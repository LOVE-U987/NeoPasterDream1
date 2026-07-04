package com.pasterdream.pasterdreammod.entity.mob;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.api.entity.anim.ProcedureAnimationHandler;
import com.pasterdream.pasterdreammod.entity.damage.ConfigurableImmunityEntity;
import com.pasterdream.pasterdreammod.entity.projectile.ShadowMagicballEntity;
import com.pasterdream.pasterdreammod.registry.PDArenaBossManager;
import com.pasterdream.pasterdreammod.registry.PDDimensions;
import com.pasterdream.pasterdreammod.registry.PDEntities;
import com.pasterdream.pasterdreammod.registry.PDSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

/**
 * 亚伦柯斯右手 (Aaroncos Righthand 0) — BOSS 级飞行敌对生物
 * <p>
 * 行为：
 * <ul>
 *   <li>飞行 BOSS，500HP，免疫击退和火焰伤害</li>
 *   <li>技能循环：魔法弹(3次) → 涡流，受击触发调音图腾召唤</li>
 *   <li>HP &lt; 100 时触发鲜血锁链（玩家减益）</li>
 *   <li>粉色 BOSS 血条</li>
 * </ul>
 * <p>
 * 动画：
 * <ul>
 *   <li>movement: idle / walk / fly / death</li>
 *   <li>attacking: 触发式攻击动画</li>
 *   <li>procedure: 技能动画（skill_magicball / skill_vortex / skill_tunetotem）</li>
 * </ul>
 */
public class AaroncosRighthand0Entity extends ConfigurableImmunityEntity implements GeoEntity {

    /** 暗影生物标签（用于区分友军/敌军，避免误伤同阵营实体） */
    private static final TagKey<EntityType<?>> SHADOW_MOB_TAG =
            TagKey.create(Registries.ENTITY_TYPE,
                    ResourceLocation.fromNamespaceAndPath("pasterdream", "shadow_mob"));

    private static final EntityDataAccessor<Boolean> SHOOT =
            SynchedEntityData.defineId(AaroncosRighthand0Entity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<String> ANIMATION =
            SynchedEntityData.defineId(AaroncosRighthand0Entity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> TEXTURE =
            SynchedEntityData.defineId(AaroncosRighthand0Entity.class, EntityDataSerializers.STRING);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final ProcedureAnimationHandler procAnim = new ProcedureAnimationHandler();

    /** 攻击挥动标记（供动画系统使用） */
    private boolean swinging;
    /** 上一次挥动的时间 */
    private long lastSwing;
    /** 过程动画名称（"empty" 表示无过程动画） */
    public String animationprocedure = "empty";

    // ==================== 延迟任务队列 ====================

    /**
     * 延迟任务 —— 替代原 Forge queueServerWork，纯 Java 实现
     */
    private static record DelayedTask(int triggerTick, Runnable action) {}

    /** 挂起的延迟任务列表 */
    private final List<DelayedTask> pendingTasks = new ArrayList<>();
    /** 服务端全局 tick 计数器（用于任务调度） */
    private int serverTickCounter = 0;

    /** 技能系统初始化标记 */
    private boolean skillSwitchInitialized = false;

    /** 是否处于召唤动画状态（spawn 动画期间禁用 AI 和技能） */
    private boolean isSummoning = false;

    /**
     * 构造亚伦柯斯右手实体
     *
     * @param type  实体类型
     * @param level 世界实例
     */
    public AaroncosRighthand0Entity(EntityType<? extends ConfigurableImmunityEntity> type, Level level) {
        super(type, level);
        this.xpReward = 100;
        this.setPersistenceRequired();
        this.moveControl = new FlyingMoveControl(this, 10, true);
    }

    /**
     * 设置是否处于召唤状态
     * <p>
     * 召唤状态下 BOSS 播放 spawn 动画，AI 和技能被禁用。
     *
     * @param summoning 是否处于召唤状态
     */
    public void setSummoning(boolean summoning) {
        this.isSummoning = summoning;
        if (summoning) {
            this.setAnimation("spawn");
        }
    }

    /**
     * 检查是否处于召唤状态
     *
     * @return 是否处于召唤状态
     */
    public boolean isSummoning() {
        return isSummoning;
    }

    // ======================== 同步数据 ========================

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SHOOT, false);
        builder.define(ANIMATION, "undefined");
        builder.define(TEXTURE, "aaroncos_righthand_0");
    }

    /**
     * 设置纹理名称
     *
     * @param texture 纹理名称
     */
    public void setTexture(String texture) {
        this.entityData.set(TEXTURE, texture);
    }

    /**
     * 获取当前纹理名称
     *
     * @return 纹理名称
     */
    public String getTexture() {
        return this.entityData.get(TEXTURE);
    }

    /**
     * 获取同步的动画名称
     *
     * @return 动画名称
     */
    public String getSyncedAnimation() {
        return this.entityData.get(ANIMATION);
    }

    /**
     * 设置同步的动画名称
     *
     * @param animation 动画名称
     */
    public void setAnimation(String animation) {
        this.entityData.set(ANIMATION, animation);
    }

    // ======================== 属性 ========================

    /**
     * 创建亚伦柯斯右手实体属性（BOSS 级：500HP/18攻/4甲）
     *
     * @return 属性构造器
     */
    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 500)
                .add(Attributes.ARMOR, 4)
                .add(Attributes.ATTACK_DAMAGE, 18)
                .add(Attributes.MOVEMENT_SPEED, 0.4)
                .add(Attributes.FOLLOW_RANGE, 32)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
                .add(Attributes.FLYING_SPEED, 0.4);
    }

    // ======================== AI 目标 ========================

    @Override
    protected void registerGoals() {
        // 近战攻击
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0, true));
        // 飞行追踪目标
        this.goalSelector.addGoal(2, new FlyingPursuitGoal());
        // 攻击目标
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, false, false));
        // 随机飞行
        this.goalSelector.addGoal(4, new RandomStrollGoal(this, 0.8, 20) {
            @Override
            protected Vec3 getPosition() {
                Vec3 pos = AaroncosRighthand0Entity.this.position();
                double dx = pos.x + (AaroncosRighthand0Entity.this.getRandom().nextFloat() * 2 - 1) * 16;
                double dy = pos.y + (AaroncosRighthand0Entity.this.getRandom().nextFloat() * 2 - 1) * 16;
                double dz = pos.z + (AaroncosRighthand0Entity.this.getRandom().nextFloat() * 2 - 1) * 16;
                return new Vec3(dx, dy, dz);
            }
        });
    }

    // ======================== 免疫 ========================

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public boolean causeFallDamage(float l, float d, DamageSource source) {
        return false;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        // 召唤状态下无敌，不受伤害
        if (isSummoning) {
            return false;
        }
        // 受击触发调音图腾技能（服务端且存活时）
        if (!this.level().isClientSide() && !this.isDeadOrDying()) {
            triggerTuneTotemSkill();
        }
        // 伤害免疫由 ConfigurableImmunityEntity + EntityImmunitySetup 统一管理
        return super.hurt(source, amount);
    }

    // ======================== 生成 & 死亡 ========================

    @Override
    public void onAddedToLevel() {
        super.onAddedToLevel();
        // 如果处于召唤状态，不播放默认生成效果，由召唤动画控制
        if (this.isSummoning) {
            // 召唤动画持续 2 秒（40 tick），结束后激活 AI 和技能
            if (this.level() instanceof ServerLevel serverLevel) {
                queueTask(40, () -> {
                    this.isSummoning = false;
                    // 通知战斗管理器召唤完成
                    if (serverLevel.dimension().equals(PDDimensions.AARONCOS_ARENA_WORLD_LEVEL_KEY)) {
                        PDArenaBossManager.onSpawnAnimationComplete(serverLevel);
                    }
                    PasterDreamMod.LOGGER.debug("[AaroncosRighthand0] ✨ 召唤动画完成，BOSS 激活");
                });
            }
        } else {
            // 非召唤状态生成（调试或其他方式），播放默认生成效果
            if (this.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.EXPLOSION, this.getX(), this.getY(), this.getZ(),
                        16, 1, 1, 1, 0.2);
                serverLevel.playSound(null, this.getX(), this.getY(), this.getZ(),
                        PDSounds.AARONCOS_SPAWN.get(), SoundSource.HOSTILE, 1.0F, 1.0F);
            }
        }
    }

    @Override
    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime == 40) {
            // 死亡爆炸 + 烟雾粒子
            if (this.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.EXPLOSION, this.getX(), this.getY(), this.getZ(),
                        32, 2, 2, 2, 0.3);
                serverLevel.sendParticles(ParticleTypes.SMOKE, this.getX(), this.getY(), this.getZ(),
                        64, 2, 2, 2, 0.5);

                // 🎯 新增：死亡爆炸（威力 4，不破坏方块，由 MOB 交互类型决定破坏行为）
                serverLevel.explode(this, this.getX(), this.getY(), this.getZ(),
                        4.0f, Level.ExplosionInteraction.MOB);

                // 🎯 如果死亡在亚伦柯斯竞技场维度，通知战斗管理器
                if (serverLevel.dimension().equals(PDDimensions.AARONCOS_ARENA_WORLD_LEVEL_KEY)) {
                    PDArenaBossManager.onRightHandDeath(serverLevel);
                }
            }
            this.remove(RemovalReason.KILLED);
            // dropExperience 由 Entity 基类在死亡时自动处理
        }
    }

    // ======================== NBT 持久化 ========================

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putString("Texture", this.getTexture());
        compound.putBoolean("AaroncosSwitch", this.getPersistentData().getBoolean("AaroncosSwitch"));
        compound.putInt("AaroncosSkill", this.getPersistentData().getInt("AaroncosSkill"));
        compound.putInt("AaroncosMagicball", this.getPersistentData().getInt("AaroncosMagicball"));
        compound.putInt("AaroncosVortex", this.getPersistentData().getInt("AaroncosVortex"));
        compound.putInt("AaroncosTuneTotem", this.getPersistentData().getInt("AaroncosTuneTotem"));
        compound.putBoolean("AaroncosBloodLock", this.getPersistentData().getBoolean("AaroncosBloodLock"));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Texture")) {
            this.setTexture(compound.getString("Texture"));
        }
        if (compound.contains("AaroncosSwitch")) {
            this.getPersistentData().putBoolean("AaroncosSwitch", compound.getBoolean("AaroncosSwitch"));
        }
        if (compound.contains("AaroncosSkill")) {
            this.getPersistentData().putInt("AaroncosSkill", compound.getInt("AaroncosSkill"));
        }
        if (compound.contains("AaroncosMagicball")) {
            this.getPersistentData().putInt("AaroncosMagicball", compound.getInt("AaroncosMagicball"));
        }
        if (compound.contains("AaroncosVortex")) {
            this.getPersistentData().putInt("AaroncosVortex", compound.getInt("AaroncosVortex"));
        }
        if (compound.contains("AaroncosTuneTotem")) {
            this.getPersistentData().putInt("AaroncosTuneTotem", compound.getInt("AaroncosTuneTotem"));
        }
        if (compound.contains("AaroncosBloodLock")) {
            this.getPersistentData().putBoolean("AaroncosBloodLock", compound.getBoolean("AaroncosBloodLock"));
        }
    }

    // ======================== 每 tick 更新 ========================

    @Override
    public void baseTick() {
        super.baseTick();
        this.refreshDimensions();
        if (!this.level().isClientSide()) {
            serverTickCounter++;

            // 首次 tick 初始化技能开关
            if (!skillSwitchInitialized) {
                this.getPersistentData().putBoolean("AaroncosSwitch", true);
                skillSwitchInitialized = true;
            }

            // 处理延迟任务队列
            processPendingTasks();

            // 技能循环
            tickSkillCycle();

            // 鲜血锁链检测
            tryBloodLock();
        }
    }

    @Override
    public void aiStep() {
        // 召唤状态下禁用 AI（清除目标、停止移动）
        if (isSummoning) {
            this.setNoGravity(true);
            this.setTarget(null);
            this.setDeltaMovement(Vec3.ZERO);
            return;
        }
        super.aiStep();
        this.setNoGravity(true);
        this.updateSwingTime();
    }

    // ======================== 延迟任务队列 ========================

    /**
     * 添加一个延迟任务，在指定 tick 数后执行
     *
     * @param delay  延迟 tick 数
     * @param action 要执行的操作
     */
    private void queueTask(int delay, Runnable action) {
        this.pendingTasks.add(new DelayedTask(serverTickCounter + delay, action));
    }

    /**
     * 处理所有到期的延迟任务
     * <p>
     * 先收集到期任务并从列表移除，再统一执行。
     * 避免任务内部调用 queueTask 时触发 ConcurrentModificationException。
     */
    private void processPendingTasks() {
        List<DelayedTask> toExecute = new ArrayList<>();
        Iterator<DelayedTask> it = pendingTasks.iterator();
        while (it.hasNext()) {
            DelayedTask task = it.next();
            if (serverTickCounter >= task.triggerTick()) {
                toExecute.add(task);
                it.remove();
            }
        }
        // 遍历结束后再执行，任务内部的 queueTask 不会触发并发修改
        for (DelayedTask task : toExecute) {
            task.action().run();
        }
    }

    // ======================== 技能系统 ========================

    /**
     * 技能循环调度 —— 使用 PersistentData 管理技能状态
     * <p>
     * 技能循环逻辑（原 AaroncosRighthandSkillProcedure）：
     * <ul>
     *   <li>魔法弹(3次, 间隔约20+40 tick) → vortex 累计</li>
     *   <li>vortex == 3 → 触发涡流技能</li>
     *   <li>受击 → 触发调音图腾技能</li>
     * </ul>
     * <p>
     * 召唤状态下技能系统被禁用。
     */
    private void tickSkillCycle() {
        // 召唤状态下禁用技能
        if (isSummoning) return;

        CompoundTag data = this.getPersistentData();
        boolean sw = data.getBoolean("AaroncosSwitch");
        int skill = data.getInt("AaroncosSkill");

        if (!sw || skill == 1) return;

        int magicball = data.getInt("AaroncosMagicball");
        int vortex = data.getInt("AaroncosVortex");

        // 魔法弹阶段：magicball 不为 1 且不为 3 时触发
        if (magicball != 1 && magicball != 3) {
            data.putInt("AaroncosSkill", 1);
            data.putInt("AaroncosMagicball", 1);
            executeMagicballSkill();
            return;
        }

        // 涡流阶段：vortex == 3 时触发
        if (vortex == 3) {
            data.putInt("AaroncosSkill", 1);
            data.putInt("AaroncosVortex", 4);
            executeVortexSkill();
        }
    }

    /**
     * 执行魔法弹技能 —— 播放魔法弹动画 + 音效 + 发射魔法弹
     * <p>
     * 流程：
     * <ul>
     *   <li>0 tick：触发 skill_magicball 动画</li>
     *   <li>5 tick：播放蓄力音效（SKILL0 + STONE_BREAK_0）</li>
     *   <li>35 tick：锁定最近玩家 + 爆炸粒子 + 发射 ShadowMagicballEntity 弹幕</li>
     *   <li>20 tick：累加 vortex 计数</li>
     *   <li>40 tick：解锁技能状态</li>
     *   <li>90 tick：重置 magicball 计数</li>
     * </ul>
     */
    private void executeMagicballSkill() {
        CompoundTag data = this.getPersistentData();
        this.setAnimation("skill_magicball");

        // 5 tick 后播放蓄力音效（SKILL0 + STONE_BREAK_0）
        queueTask(5, () -> {
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    PDSounds.SKILL0.get(), SoundSource.HOSTILE, 1.0F, 1.0F);
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    PDSounds.STONE_BREAK_0.get(), SoundSource.HOSTILE, 1.0F, 1.0F);
        });

        // 35 tick 后锁定最近玩家 + 爆炸效果 + 发射魔法弹
        queueTask(35, () -> {
            // 锁定最近玩家
            Player nearest = this.level().getNearestPlayer(this, 64.0);
            if (nearest != null) {
                this.lookAt(nearest, 360.0F, 360.0F);
            }

            // 爆炸粒子
            if (this.level() instanceof ServerLevel sl) {
                sl.sendParticles(ParticleTypes.EXPLOSION, this.getX(), this.getY(), this.getZ(),
                        16, 1, 1, 1, 0.2);
                sl.sendParticles(ParticleTypes.SMOKE, this.getX(), this.getY(), this.getZ(),
                        64, 1, 1, 1, 0.2);
            }
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    PDSounds.SKILL1.get(), SoundSource.HOSTILE, 1.0F, 1.0F);

            // 发射方向上的粒子（保留视觉余韵）
            Vec3 look = this.getLookAngle();
            double tx = this.getX() + look.x * 1.5;
            double ty = this.getY() + look.y;
            double tz = this.getZ() + look.z * 1.5;

            if (this.level() instanceof ServerLevel sl) {
                sl.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, tx, ty, tz,
                        8, 0.3, 0.3, 0.3, 0.1);
            }

            // 🎯 实际生成 ShadowMagicballEntity 弹幕实体
            if (this.level() instanceof ServerLevel sl) {
                ShadowMagicballEntity magicball = PDEntities.SHADOW_MAGICBALL.get().create(sl);
                if (magicball != null) {
                    magicball.moveTo(this.getX() + look.x * 1.5,
                            this.getY() + look.y * 1.5,
                            this.getZ() + look.z * 1.5);
                    magicball.setDeltaMovement(look.x * 3, look.y * 2, look.z * 3);
                    sl.addFreshEntity(magicball);
                }
            }
        });

        // 20 tick 后递增 vortex 计数
        queueTask(20, () -> data.putInt("AaroncosVortex", data.getInt("AaroncosVortex") + 1));

        // 40 tick 后解锁技能状态
        queueTask(40, () -> data.putInt("AaroncosSkill", 0));

        // 90 tick 后重置 magicball 计数
        queueTask(90, () -> {
            if (data.getInt("AaroncosMagicball") == 1) {
                data.putInt("AaroncosMagicball", 0);
            }
        });
    }

    /**
     * 执行涡流技能 —— 播放涡流动画 + 范围减益 + 暗影漩涡方块
     * <p>
     * 流程：
     * <ul>
     *   <li>0 tick：触发 skill_vortex 动画 + 下坠 + 缓慢效果</li>
     *   <li>42 tick：涡流爆发，生成暗影漩涡方块（每个玩家脚下 + BOSS 脚下） + 伤害 + 减益 + 音效</li>
     *   <li>120 tick：重置技能和 vortex 计数</li>
     * </ul>
     */
    private void executeVortexSkill() {
        CompoundTag data = this.getPersistentData();
        this.setAnimation("skill_vortex");

        // 下坠
        this.setDeltaMovement(new Vec3(0, -5, 0));
        this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 4, false, false));

        // 42 tick 后涡流爆发：范围伤害 + 粒子 + 玩家减益 + 暗影漩涡方块
        queueTask(42, () -> {
            if (this.level() instanceof ServerLevel sl) {
                sl.sendParticles(ParticleTypes.EXPLOSION, this.getX(), this.getY(), this.getZ(),
                        24, 2, 1, 2, 0.3);
                sl.sendParticles(ParticleTypes.SMOKE, this.getX(), this.getY(), this.getZ(),
                        128, 2, 1, 2, 0.5);

                // 在 BOSS 位置生成暗影漩涡方块
                BlockPos bossPos = BlockPos.containing(this.getX(), this.getY(), this.getZ());
                sl.setBlockAndUpdate(bossPos, com.pasterdream.pasterdreammod.registry.PDBlocks.SHADOW_VORTEX.get().defaultBlockState());
            }

            // 64 格内玩家受到涡流影响
            AABB box = this.getBoundingBox().inflate(64.0);
            this.level().getEntitiesOfClass(Player.class, box, Entity::isAlive)
                    .forEach(p -> {
                        p.hurt(this.damageSources().mobAttack(this), 4.0F);
                        p.setDeltaMovement(new Vec3(0, 0.2, 0));
                        // 附加缓慢效果
                        p.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 2, false, false));

                        // 在玩家位置生成暗影漩涡方块
                        if (this.level() instanceof ServerLevel sl) {
                            BlockPos playerPos = BlockPos.containing(p.getX(), p.getY(), p.getZ());
                            sl.setBlockAndUpdate(playerPos, com.pasterdream.pasterdreammod.registry.PDBlocks.SHADOW_VORTEX.get().defaultBlockState());
                        }
                    });

            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    PDSounds.SHADOW_VORTEX.get(), SoundSource.HOSTILE, 0.8F, 1.0F);
        });

        // 120 tick 后重置技能和 vortex 计数
        queueTask(120, () -> {
            data.putInt("AaroncosSkill", 0);
            data.putInt("AaroncosVortex", 0);
        });
    }

    /**
     * 受击触发的调音图腾技能 —— 播放调音图腾动画 + 召唤调音图腾 + 范围减益
     * <p>
     * 流程：
     * <ul>
     *   <li>0 tick：触发 skill_tunetotem 动画 + 抗性提升 + 下坠 + 范围混乱 + STONE_BREAK/SKILL2 音效</li>
     *   <li>10 tick：自身缓慢 + 粒子</li>
     *   <li>15 tick：二次粒子</li>
     *   <li>21 tick：召唤 ShadowTuneTotemEntity + 后跳</li>
     *   <li>120 tick：解锁技能状态</li>
     *   <li>600 tick：重置调音图腾冷却</li>
     * </ul>
     */
    private void triggerTuneTotemSkill() {
        CompoundTag data = this.getPersistentData();
        int skill = data.getInt("AaroncosSkill");
        boolean sw = data.getBoolean("AaroncosSwitch");
        int totem = data.getInt("AaroncosTuneTotem");

        if (skill == 0 && sw && totem != 1 && this.getHealth() > 1) {
            data.putInt("AaroncosSkill", 1);
            data.putInt("AaroncosTuneTotem", 1);

            this.setAnimation("skill_tunetotem");

            // 抗性提升 + 下坠
            this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 40, 1, false, false));
            this.setDeltaMovement(new Vec3(0, -2, 0));

            // 🎯 初始 AoE 混乱效果：15 格内非暗影标签 LivingEntity 施加 CONFUSION 60 tick
            List<LivingEntity> entities = this.level().getEntitiesOfClass(LivingEntity.class,
                    this.getBoundingBox().inflate(15.0),
                    e -> e != this && e.isAlive() && !e.getType().is(SHADOW_MOB_TAG));
            for (LivingEntity entity : entities) {
                entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 60, 0, false, false));
            }

            // 音效（SKILL2 + STONE_BREAK）
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    PDSounds.SKILL2.get(), SoundSource.HOSTILE, 1.0F, 1.0F);
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    PDSounds.STONE_BREAK.get(), SoundSource.HOSTILE, 1.0F, 1.0F);

            // 10t 后粒子 + 自身缓慢
            queueTask(10, () -> {
                this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 120, 4, false, false));
                if (this.level() instanceof ServerLevel sl) {
                    sl.sendParticles(ParticleTypes.SMOKE, this.getX(), this.getY(), this.getZ(),
                            32, 1, 0, 1, 0.5);
                }
            });

            // 15t 后二次粒子
            queueTask(15, () -> {
                if (this.level() instanceof ServerLevel sl) {
                    sl.sendParticles(ParticleTypes.SMOKE, this.getX(), this.getY(), this.getZ(),
                            32, 1, 0, 1, 0.5);
                }
            });

            // 21t 后召唤调音图腾 + 后跳
            queueTask(21, () -> {
                // 🎯 召唤 ShadowTuneTotemEntity（前方 2 格）
                if (this.level() instanceof ServerLevel serverLevel) {
                    ShadowTuneTotemEntity tuneTotem = PDEntities.SHADOW_TUNE_TOTEM.get().create(serverLevel);
                    if (tuneTotem != null) {
                        tuneTotem.moveTo(this.getX() + this.getLookAngle().x * 2,
                                this.getY(),
                                this.getZ() + this.getLookAngle().z * 2);
                        tuneTotem.setYRot(this.getRandom().nextFloat() * 360.0F);
                        serverLevel.addFreshEntity(tuneTotem);
                    }
                }
                // 后跳
                Vec3 look = this.getLookAngle();
                this.setDeltaMovement(new Vec3(look.x * (-1), 0, look.z * (-1)));
            });

            // 120 tick 后解锁技能
            queueTask(120, () -> data.putInt("AaroncosSkill", 0));
            // 600 tick 后重置调音图腾冷却
            queueTask(600, () -> data.putInt("AaroncosTuneTotem", 0));
        }
    }

    // ======================== 范围伤害辅助 ========================

    /**
     * 对附近玩家造成伤害
     *
     * @param range  范围半径
     * @param damage 伤害量
     */
    private void hurtNearbyPlayers(double range, float damage) {
        AABB box = this.getBoundingBox().inflate(range);
        this.level().getEntitiesOfClass(Player.class, box, Entity::isAlive)
                .forEach(p -> p.hurt(this.damageSources().mobAttack(this), damage));
    }

    // ======================== 鲜血锁链系统 ========================

    /**
     * 检测并触发鲜血锁链 —— 当 HP < 100 且未锁定时触发
     * <p>
     * 触发效果（原 AaroncosHandBloodlockProcedure）：
     * <ul>
     *   <li>抗性提升 V</li>
     *   <li>召唤 4 个 ShadowHandEntity 暗影之手</li>
     *   <li>80 格内玩家获得暗影/失明/缓慢(255级)禁锢效果</li>
     *   <li>播放 aaroncos_spawn 音效</li>
     * </ul>
     */
    private void tryBloodLock() {
        CompoundTag data = this.getPersistentData();
        if (data.getBoolean("AaroncosBloodLock") || this.getHealth() > 100) return;

        data.putBoolean("AaroncosBloodLock", true);

        // 抗性提升 V（60秒）
        this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 1200, 3, false, false));

        // 仅服务端执行实体召唤与玩家减益
        if (this.level() instanceof ServerLevel serverLevel) {
            // 🎯 召唤 4 个暗影之手（随机散布在 BOSS 周围）
            for (int i = 0; i < 4; i++) {
                ShadowHandEntity shadowHand = PDEntities.SHADOW_HAND.get().create(serverLevel);
                if (shadowHand != null) {
                    double offsetX = (this.getRandom().nextDouble() - 0.5) * 6;
                    double offsetY = this.getRandom().nextDouble() * 2;
                    double offsetZ = (this.getRandom().nextDouble() - 0.5) * 6;
                    shadowHand.moveTo(this.getX() + offsetX, this.getY() + offsetY, this.getZ() + offsetZ);
                    serverLevel.addFreshEntity(shadowHand);
                }
            }

            // 80 格内玩家获得减益效果（暗影 + 失明 + 缓慢 + 禁锢）
            AABB box = this.getBoundingBox().inflate(80.0);
            this.level().getEntitiesOfClass(Player.class, box, Entity::isAlive)
                    .forEach(player -> {
                        player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 60, 0, false, false));
                        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 1, false, false));
                        player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 60, 0, false, false));
                        // 🎯 禁锢效果：MOVEMENT_SLOWDOWN 255 级（amplifier=254）60 tick
                        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 254, false, false));
                    });
        }

        // 播放鲜血锁链音效
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                PDSounds.AARONCOS_SPAWN.get(), SoundSource.HOSTILE, 1.0F, 1.0F);
    }

    // ======================== 物理 ========================

    @Override
    protected PathNavigation createNavigation(Level world) {
        return new FlyingPathNavigation(this, world);
    }

    @Override
    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    @Override
    public void setNoGravity(boolean ignored) {
        super.setNoGravity(true);
    }

    @Override
    public boolean isPersistenceRequired() {
        return true;
    }

    // ======================== GeckoLib 动画 ========================

    /**
     * 移动状态动画控制器
     * 根据移动状态切换 idle / walk / fly / death 动画
     */
    private PlayState movementPredicate(AnimationState<AaroncosRighthand0Entity> state) {
        if (this.animationprocedure.equals("empty")) {
            if ((state.isMoving() || !(state.getLimbSwingAmount() > -0.15F && state.getLimbSwingAmount() < 0.15F)) && this.onGround()) {
                return state.setAndContinue(RawAnimation.begin().thenLoop("walk"));
            }
            if (this.isDeadOrDying()) {
                return state.setAndContinue(RawAnimation.begin().thenPlay("death"));
            }
            if (!this.onGround()) {
                return state.setAndContinue(RawAnimation.begin().thenLoop("fly"));
            }
            return state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
        }
        return PlayState.STOP;
    }

    /**
     * 攻击动画控制器
     * 在实体挥动时触发 attack 动画
     */
    private PlayState attackingPredicate(AnimationState<AaroncosRighthand0Entity> state) {
        if (getAttackAnim(state.getPartialTick()) > 0f && !this.swinging) {
            this.swinging = true;
            this.lastSwing = level().getGameTime();
        }
        if (this.swinging && this.lastSwing + 7L <= level().getGameTime()) {
            this.swinging = false;
        }
        if (this.swinging && state.getController().getAnimationState() == AnimationController.State.STOPPED) {
            state.getController().forceAnimationReset();
            return state.setAndContinue(RawAnimation.begin().thenPlay("attack"));
        }
        return PlayState.CONTINUE;
    }

    /**
     * 过程动画控制器（用于技能动画）
     */
    private PlayState procedurePredicate(AnimationState<AaroncosRighthand0Entity> state) {
        return procAnim.predicate(state,
                level().isClientSide(),
                this::getSyncedAnimation,
                () -> setAnimation("empty"));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "movement", 4, this::movementPredicate));
        controllers.add(new AnimationController<>(this, "attacking", 4, this::attackingPredicate));
        controllers.add(new AnimationController<>(this, "procedure", 4, this::procedurePredicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    // ======================== 飞行追踪 AI ========================

    /**
     * 飞行追踪目标的 AI 目标 —— 使 BOSS 持续向目标移动并在碰撞箱相交时攻击
     */
    private class FlyingPursuitGoal extends Goal {
        public FlyingPursuitGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return getTarget() != null && !getMoveControl().hasWanted();
        }

        @Override
        public boolean canContinueToUse() {
            return getMoveControl().hasWanted() && getTarget() != null && getTarget().isAlive();
        }

        @Override
        public void start() {
            LivingEntity target = getTarget();
            if (target != null) {
                Vec3 pos = target.getEyePosition(1);
                moveControl.setWantedPosition(pos.x, pos.y, pos.z, 1.0);
            }
        }

        @Override
        public void tick() {
            LivingEntity target = getTarget();
            if (target == null) return;

            if (getBoundingBox().intersects(target.getBoundingBox())) {
                doHurtTarget(target);
            } else if (distanceToSqr(target) < 16) {
                Vec3 pos = target.getEyePosition(1);
                moveControl.setWantedPosition(pos.x, pos.y, pos.z, 1.0);
            }
        }
    }
}
