package com.pasterdream.pasterdreammod.entity.mob;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.entity.damage.ConfigurableImmunityEntity;
import com.pasterdream.pasterdreammod.registry.PDArenaBossManager;
import com.pasterdream.pasterdreammod.registry.PDDimensions;
import com.pasterdream.pasterdreammod.registry.PDEntities;
import com.pasterdream.pasterdreammod.registry.PDSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

/**
 * 亚伦柯斯左手 (Aaroncos Lefthand 0) — BOSS 级飞行敌对生物
 * <p>
 * 行为：
 * <ul>
 *   <li>飞行 BOSS，500HP，免疫击退和火焰伤害</li>
 *   <li>技能循环：冲刺(3次) → 重击，受击触发剑雨技能</li>
 *   <li>HP &lt; 100 时触发鲜血锁链（召唤暗影之手 + 玩家减益）</li>
 *   <li>粉色 BOSS 血条</li>
 * </ul>
 * <p>
 * 动画：
 * <ul>
 *   <li>movement: idle / walk / fly / death</li>
 *   <li>attacking: 触发式攻击动画</li>
 *   <li>procedure: 技能动画（skill_sprint / skill_hit / skill_sword）</li>
 * </ul>
 */
public class AaroncosLefthand0Entity extends ConfigurableImmunityEntity {

    /** 暗影系实体标签（用于 AoE 伤害排除友军） */
    private static final TagKey<EntityType<?>> SHADOW_MOB_TAG =
            TagKey.create(Registries.ENTITY_TYPE,
                    ResourceLocation.fromNamespaceAndPath("pasterdream", "shadow_mob"));

    /** 攻击挥动标记（供动画系统使用） */
    private boolean swinging;
    /** 上一次挥动的时间 */
    private long lastSwing;

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
     * 构造亚伦柯斯左手实体
     *
     * @param type  实体类型
     * @param level 世界实例
     */
    public AaroncosLefthand0Entity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.xpReward = 100;
        this.setPersistenceRequired();
        this.moveControl = new FlyingMoveControl(this, 10, true);
    }

    /**
     * 获取默认纹理名称
     *
     * @return 纹理名称
     */
    @Override
    protected String getDefaultTexture() {
        return "aaroncos_lefthand_0";
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

    // ======================== 属性 ========================

    /**
     * 创建亚伦柯斯左手实体属性（BOSS 级：500HP/20攻/10甲）
     *
     * @return 属性构造器
     */
    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 500)
                .add(Attributes.ARMOR, 10)
                .add(Attributes.ATTACK_DAMAGE, 20)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.FOLLOW_RANGE, 32)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
                .add(Attributes.FLYING_SPEED, 0.25);
    }

    // ======================== AI 目标 ========================

    @Override
    protected void registerGoals() {
        // 近战攻击
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2, true));
        // 飞行追踪目标
        this.goalSelector.addGoal(2, new FlyingPursuitGoal());
        // 攻击目标
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, false, false));
        // 随机飞行
        this.goalSelector.addGoal(4, new RandomStrollGoal(this, 0.8, 20) {
            @Override
            protected Vec3 getPosition() {
                Vec3 pos = AaroncosLefthand0Entity.this.position();
                double dx = pos.x + (AaroncosLefthand0Entity.this.getRandom().nextFloat() * 2 - 1) * 16;
                double dy = pos.y + (AaroncosLefthand0Entity.this.getRandom().nextFloat() * 2 - 1) * 16;
                double dz = pos.z + (AaroncosLefthand0Entity.this.getRandom().nextFloat() * 2 - 1) * 16;
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
        // 受击触发剑雨技能（服务端且存活时）
        if (!this.level().isClientSide() && !this.isDeadOrDying()) {
            triggerSwordSkill();
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
            // 左手召唤动画持续 4 秒（80 tick），结束后激活 AI 和技能
            if (this.level() instanceof ServerLevel serverLevel) {
                queueTask(80, () -> {
                    this.isSummoning = false;
                    // 通知战斗管理器召唤完成
                    if (serverLevel.dimension().equals(PDDimensions.AARONCOS_ARENA_WORLD_LEVEL_KEY)) {
                        PDArenaBossManager.onSpawnAnimationComplete(serverLevel);
                    }
                    PasterDreamMod.LOGGER.debug("[AaroncosLefthand0] 召唤动画完成，BOSS 激活");
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

                // 死亡爆炸（威力 4，MOB 交互类型不破坏方块）
                serverLevel.explode(this, this.getX(), this.getY(), this.getZ(), 4.0f,
                        Level.ExplosionInteraction.MOB);

                // 如果死亡在亚伦柯斯竞技场维度，通知战斗管理器
                if (serverLevel.dimension().equals(PDDimensions.AARONCOS_ARENA_WORLD_LEVEL_KEY)) {
                    PDArenaBossManager.onLeftHandDeath(serverLevel);
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
        compound.putBoolean("AaroncosSwitch", this.getPersistentData().getBoolean("AaroncosSwitch"));
        compound.putInt("AaroncosSkill", this.getPersistentData().getInt("AaroncosSkill"));
        compound.putInt("AaroncosSprint", this.getPersistentData().getInt("AaroncosSprint"));
        compound.putInt("AaroncosHit", this.getPersistentData().getInt("AaroncosHit"));
        compound.putInt("AaroncosSword", this.getPersistentData().getInt("AaroncosSword"));
        compound.putBoolean("AaroncosBloodLock", this.getPersistentData().getBoolean("AaroncosBloodLock"));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("AaroncosSwitch")) {
            this.getPersistentData().putBoolean("AaroncosSwitch", compound.getBoolean("AaroncosSwitch"));
        }
        if (compound.contains("AaroncosSkill")) {
            this.getPersistentData().putInt("AaroncosSkill", compound.getInt("AaroncosSkill"));
        }
        if (compound.contains("AaroncosSprint")) {
            this.getPersistentData().putInt("AaroncosSprint", compound.getInt("AaroncosSprint"));
        }
        if (compound.contains("AaroncosHit")) {
            this.getPersistentData().putInt("AaroncosHit", compound.getInt("AaroncosHit"));
        }
        if (compound.contains("AaroncosSword")) {
            this.getPersistentData().putInt("AaroncosSword", compound.getInt("AaroncosSword"));
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
     */
    private void processPendingTasks() {
        Iterator<DelayedTask> it = pendingTasks.iterator();
        while (it.hasNext()) {
            DelayedTask task = it.next();
            if (serverTickCounter >= task.triggerTick()) {
                task.action().run();
                it.remove();
            }
        }
    }

    // ======================== 技能系统 ========================

    /**
     * 技能循环调度 —— 使用 PersistentData 管理技能状态
     * <p>
     * 技能循环逻辑（原 AaroncosLefthandSkillProcedure）：
     * <ul>
     *   <li>冲刺(3次) → skill_hit 累计</li>
     *   <li>skill_hit == 3 → 触发重击技能</li>
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

        int sprint = data.getInt("AaroncosSprint");
        int hit = data.getInt("AaroncosHit");

        // 冲刺阶段：sprint 不为 1 且不为 3 时触发
        if (sprint != 1 && sprint != 3) {
            data.putInt("AaroncosSkill", 1);
            data.putInt("AaroncosSprint", 1);
            executeSprintSkill();
            return;
        }

        // 重击阶段：skill_hit == 3 时触发
        if (hit == 3) {
            data.putInt("AaroncosSkill", 1);
            data.putInt("AaroncosHit", 4);
            executeHitSkill();
        }
    }

    /**
     * 执行冲刺技能 —— 播放冲刺动画 + 音效 + 冲锋伤害
     */
    private void executeSprintSkill() {
        CompoundTag data = this.getPersistentData();
        this.setAnimation("skill_sprint");

        // 5 tick 后播放音效（剑波 + 石裂）
        queueTask(5, () -> {
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    PDSounds.SWORD_WAVE.get(), SoundSource.HOSTILE, 1.2F, 1.0F);
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    PDSounds.STONE_BREAK_0.get(), SoundSource.HOSTILE, 1.0F, 1.0F);
        });

        // 16 tick 后锁定最近玩家并冲锋
        queueTask(16, () -> {
            Player nearest = this.level().getNearestPlayer(this, 64.0);
            if (nearest != null) {
                this.lookAt(nearest, 360.0F, 360.0F);
                Vec3 look = this.getLookAngle();
                this.setDeltaMovement(look.x * 2.8, look.y - 0.2, look.z * 2.8);
            }
        });

        // 17 tick 后爆炸粒子 + 范围伤害
        queueTask(17, () -> {
            if (this.level() instanceof ServerLevel sl) {
                sl.sendParticles(ParticleTypes.EXPLOSION, this.getX(), this.getY(), this.getZ(),
                        16, 1, 1, 1, 0.2);
            }
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.GENERIC_EXPLODE, SoundSource.HOSTILE, 1.0F, 1.0F);
            this.hurtNearbyPlayers(6.0, 7.0F);
        });

        // 24 tick 后二次爆炸
        queueTask(24, () -> {
            if (this.level() instanceof ServerLevel sl) {
                sl.sendParticles(ParticleTypes.EXPLOSION, this.getX(), this.getY(), this.getZ(),
                        16, 1, 1, 1, 0.2);
            }
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.GENERIC_EXPLODE, SoundSource.HOSTILE, 1.0F, 1.0F);
            this.hurtNearbyPlayers(6.0, 7.0F);
        });

        // 20 tick 后递增 skill_hit
        queueTask(20, () -> data.putInt("AaroncosHit", data.getInt("AaroncosHit") + 1));

        // 40 tick 后解锁技能状态
        queueTask(40, () -> data.putInt("AaroncosSkill", 0));

        // 120 tick 后重置 sprint 计数
        queueTask(120, () -> {
            if (data.getInt("AaroncosSprint") == 1) {
                data.putInt("AaroncosSprint", 0);
            }
        });
    }

    /**
     * 执行重击技能 —— 播放重击动画 + 三段式冲击波
     * <p>
     * 三段 AoE 范围递增（15/19/23 格），伤害递增（6/7/8 点），
     * 每段对范围内非暗影系 LivingEntity 造伤害并附加 CONFUSION 10 tick。
     */
    private void executeHitSkill() {
        CompoundTag data = this.getPersistentData();
        this.setAnimation("skill_hit");

        // 第一段重击（10 tick 后起跳，19 tick 后落地爆炸）—— 15 格 AoE / 6 点伤害
        queueTask(10, () -> this.setDeltaMovement(new Vec3(0, 2, 0)));
        queueTask(15, () -> this.setDeltaMovement(new Vec3(0, -10, 0)));
        queueTask(19, () -> {
            if (this.level() instanceof ServerLevel sl) {
                sl.sendParticles(ParticleTypes.EXPLOSION, this.getX(), this.getY(), this.getZ(),
                        12, 1, 1, 1, 0.5);
                sl.sendParticles(ParticleTypes.SMOKE, this.getX(), this.getY(), this.getZ(),
                        64, 2, 1, 2, 0.5);
            }
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    PDSounds.SWORD1.get(), SoundSource.HOSTILE, 1.0F, 1.0F);
            this.hurtNearbyLivingWithConfusion(15.0, 6.0F, 10);
            this.pushNearbyPlayers(0, 0.5, 0);
        });

        // 第二段重击（21 tick 后）—— 19 格 AoE / 7 点伤害
        queueTask(21, () -> this.setDeltaMovement(new Vec3(0, 3, 0)));
        queueTask(27, () -> this.setDeltaMovement(new Vec3(0, -10, 0)));
        queueTask(30, () -> {
            if (this.level() instanceof ServerLevel sl) {
                sl.sendParticles(ParticleTypes.EXPLOSION, this.getX(), this.getY(), this.getZ(),
                        16, 2, 1, 2, 0.5);
                sl.sendParticles(ParticleTypes.SMOKE, this.getX(), this.getY(), this.getZ(),
                        128, 3, 1, 3, 0.5);
            }
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    PDSounds.SWORD1.get(), SoundSource.HOSTILE, 1.1F, 1.0F);
            this.hurtNearbyLivingWithConfusion(19.0, 7.0F, 10);
            this.pushNearbyPlayers(0, 1.0, 0);
        });

        // 第三段重击（42 tick 后）—— 23 格 AoE / 8 点伤害
        queueTask(42, () -> this.setDeltaMovement(new Vec3(0, 4, 0)));
        queueTask(48, () -> this.setDeltaMovement(new Vec3(0, -10, 0)));
        queueTask(53, () -> {
            if (this.level() instanceof ServerLevel sl) {
                sl.sendParticles(ParticleTypes.EXPLOSION, this.getX(), this.getY(), this.getZ(),
                        24, 3, 1, 3, 0.5);
                sl.sendParticles(ParticleTypes.SMOKE, this.getX(), this.getY(), this.getZ(),
                        192, 4, 1, 4, 0.5);
            }
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    PDSounds.SWORD_WAVE.get(), SoundSource.HOSTILE, 1.2F, 1.0F);
            this.hurtNearbyLivingWithConfusion(23.0, 8.0F, 10);
            this.pushNearbyPlayers(0, 1.5, 0);
        });

        // 100 tick 后重置技能和 hit 计数
        queueTask(100, () -> {
            data.putInt("AaroncosSkill", 0);
            data.putInt("AaroncosHit", 0);
        });
    }

    /**
     * 受击触发的剑雨技能 —— 播放剑雨动画 + 范围混乱 + 暗影石粒子 + 多段 AoE 伤害
     * <p>
     * 行为：
     * <ul>
     *   <li>初始阶段：自身抗性提升、播放 SHADOW_SWORD 与 STONE_BREAK 音效</li>
     *   <li>初始 AoE 混乱：对 30 格内非暗影 LivingEntity 施加 CONFUSION 60 tick</li>
     *   <li>多段剑雨（57/70/83/88/95/105/112 tick）：粒子 + 16 格 AoE 8 点伤害 + SWORD_WAVE 音效</li>
     *   <li>对玩家额外施加 CONFUSION 20 tick</li>
     * </ul>
     */
    private void triggerSwordSkill() {
        CompoundTag data = this.getPersistentData();
        int skill = data.getInt("AaroncosSkill");
        boolean sw = data.getBoolean("AaroncosSwitch");
        int sword = data.getInt("AaroncosSword");

        if (skill == 0 && sw && sword != 1 && this.getHealth() > 1) {
            data.putInt("AaroncosSkill", 1);
            data.putInt("AaroncosSword", 1);

            this.setAnimation("skill_sword");

            // 抗性提升 + 下坠
            this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 120, 1, false, false));
            this.setDeltaMovement(new Vec3(0, -2, 0));

            // 音效（剑雨触发 + 石裂）
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    PDSounds.SHADOW_SWORD.get(), SoundSource.HOSTILE, 1.0F, 1.0F);
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    PDSounds.STONE_BREAK.get(), SoundSource.HOSTILE, 1.0F, 1.0F);

            // 初始 AoE 混乱效果（30 格）—— 对非暗影 LivingEntity 施加 CONFUSION 60 tick
            List<LivingEntity> initialEntities = this.level().getEntitiesOfClass(LivingEntity.class,
                    this.getBoundingBox().inflate(30.0),
                    e -> e != this && e.isAlive() && !e.getType().is(SHADOW_MOB_TAG));
            for (LivingEntity entity : initialEntities) {
                entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 60, 0, false, false));
            }

            // 15 tick 后粒子 + 缓慢
            queueTask(15, () -> {
                this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 120, 4, false, false));
                if (this.level() instanceof ServerLevel sl) {
                    sl.sendParticles(ParticleTypes.SMOKE, this.getX(), this.getY(), this.getZ(),
                            128, 1, 2, 1, 0.5);
                }
            });

            // 25 tick 后二次粒子
            queueTask(25, () -> {
                if (this.level() instanceof ServerLevel sl) {
                    sl.sendParticles(ParticleTypes.SMOKE, this.getX(), this.getY(), this.getZ(),
                            128, 1, 2, 1, 0.5);
                }
            });

            // 57 tick 后开始多段剑雨 —— 粒子 + 16 格 AoE 8 点伤害 + 音效
            for (int t : new int[]{57, 70, 83, 88, 95, 105, 112}) {
                queueTask(t, () -> {
                    if (this.level() instanceof ServerLevel sl) {
                        double px = this.getX() + (this.getRandom().nextFloat() - 0.5) * 6;
                        double pz = this.getZ() + (this.getRandom().nextFloat() - 0.5) * 6;
                        sl.sendParticles(ParticleTypes.SWEEP_ATTACK, px, this.getY() - 1, pz,
                                1, 0, 0, 0, 0);
                    }

                    // 16 格 AoE 伤害判定（排除自身与暗影系友军）
                    List<LivingEntity> entities = this.level().getEntitiesOfClass(LivingEntity.class,
                            this.getBoundingBox().inflate(16.0),
                            e -> e != this && e.isAlive() && !e.getType().is(SHADOW_MOB_TAG));
                    for (LivingEntity entity : entities) {
                        entity.hurt(this.damageSources().generic(), 8.0F);
                        // 对玩家额外施加 CONFUSION 20 tick
                        if (entity instanceof Player player) {
                            player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 20, 0, false, false));
                        }
                    }

                    // 播放剑波音效
                    this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                            PDSounds.SWORD_WAVE.get(), SoundSource.HOSTILE, 1.0F, 1.2F);
                });
            }

            // 140 tick 后解锁技能
            queueTask(140, () -> data.putInt("AaroncosSkill", 0));
            // 420 tick 后重置剑雨冷却
            queueTask(420, () -> data.putInt("AaroncosSword", 0));
        }
    }

    // ======================== 范围伤害辅助 ========================

    /**
     * 对附近玩家造成伤害（排除特殊实体）
     *
     * @param range  范围半径
     * @param damage 伤害量
     */
    private void hurtNearbyPlayers(double range, float damage) {
        AABB box = this.getBoundingBox().inflate(range);
        this.level().getEntitiesOfClass(Player.class, box, Entity::isAlive)
                .forEach(p -> p.hurt(this.damageSources().mobAttack(this), damage));
    }

    /**
     * 对附近非暗影系 LivingEntity 造成伤害并附加 CONFUSION 效果
     * <p>
     * 用于左手重击三段 AoE —— 排除自身与 shadow_mob 标签友军。
     *
     * @param range             范围半径
     * @param damage            伤害量
     * @param confusionDuration 混乱效果持续 tick
     */
    private void hurtNearbyLivingWithConfusion(double range, float damage, int confusionDuration) {
        AABB box = this.getBoundingBox().inflate(range);
        List<LivingEntity> entities = this.level().getEntitiesOfClass(LivingEntity.class, box,
                e -> e != this && e.isAlive() && !e.getType().is(SHADOW_MOB_TAG));
        for (LivingEntity entity : entities) {
            entity.hurt(this.damageSources().mobAttack(this), damage);
            entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, confusionDuration, 0, false, false));
        }
    }

    /**
     * 改变附近玩家的速度向量
     *
     * @param x x 方向
     * @param y y 方向
     * @param z z 方向
     */
    private void pushNearbyPlayers(double x, double y, double z) {
        AABB box = this.getBoundingBox().inflate(15.0);
        this.level().getEntitiesOfClass(Player.class, box, Entity::isAlive)
                .forEach(p -> p.setDeltaMovement(new Vec3(x, y, z)));
    }

    // ======================== 鲜血锁链系统 ========================

    /**
     * 检测并触发鲜血锁链 —— 当 HP < 100 且未锁定时触发
     * <p>
     * 触发效果（原 AaroncosHandBloodlockProcedure）：
     * <ul>
     *   <li>抗性提升 V</li>
     *   <li>召唤 4 个暗影之手（ShadowHand）</li>
     *   <li>80 格内玩家获得暗影/失明/缓慢 + 移动禁锢（MOVEMENT_SLOWDOWN 255 级，60 tick）</li>
     *   <li>播放 aaroncos_spawn 音效</li>
     * </ul>
     */
    private void tryBloodLock() {
        CompoundTag data = this.getPersistentData();
        if (data.getBoolean("AaroncosBloodLock") || this.getHealth() > 100) return;

        data.putBoolean("AaroncosBloodLock", true);

        // 抗性提升 V（60秒）
        this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 1200, 3, false, false));

        // 召唤 4 个暗影之手 —— 仅在 ServerLevel 中执行
        if (this.level() instanceof ServerLevel serverLevel) {
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
        }

        // 80 格内玩家获得减益效果（暗影 + 失明 + 缓慢 + 禁锢）
        AABB box = this.getBoundingBox().inflate(80.0);
        this.level().getEntitiesOfClass(Player.class, box, Entity::isAlive)
                .forEach(player -> {
                    player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 60, 0, false, false));
                    player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 1, false, false));
                    player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 60, 0, false, false));
                    // 禁锢：MOVEMENT_SLOWDOWN 255 级（amplifier=254），60 tick
                    player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 254, false, false));
                });

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
     *
     * @param state 动画状态
     * @return 播放状态
     */
    private PlayState movementPredicate(AnimationState<AaroncosLefthand0Entity> state) {
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
     *
     * @param state 动画状态
     * @return 播放状态
     */
    private PlayState attackingPredicate(AnimationState<AaroncosLefthand0Entity> state) {
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

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        super.registerControllers(controllers);
        controllers.add(new AnimationController<>(this, "movement", 4, this::movementPredicate));
        controllers.add(new AnimationController<>(this, "attacking", 4, this::attackingPredicate));
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
