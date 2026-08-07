package com.pasterdream.pasterdreammod.entity.mob;

import com.pasterdream.pasterdreammod.api.effect.atmosphere.AtmosphereEffectAPI;
import com.pasterdream.pasterdreammod.api.effect.particle.ParticleEmitterAPI;
import com.pasterdream.pasterdreammod.api.effect.particle.ParticleEmitterData;
import com.pasterdream.pasterdreammod.api.effect.particle.processors.CircleSpawnProcessor;
import com.pasterdream.pasterdreammod.api.entity.damage.ConfigurableImmunityEntity;
import com.pasterdream.pasterdreammod.registry.PDEntities;
import com.pasterdream.pasterdreammod.registry.PDSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
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
 * 亚伦柯斯之手（左手/右手）公共基类。
 * <p>
 * 抽取两只 BOSS 级飞行手的通用行为：召唤状态管理、延迟任务队列、鲜血锁链、
 * 飞行 AI、动画控制器、范围伤害辅助等。子类只需实现左右手各自的技能循环、
 * 属性数值与死亡/召唤回调。
 *
 * @see AaroncosLefthand0Entity
 * @see AaroncosRighthand0Entity
 */
public abstract class AaroncosHandEntity extends ConfigurableImmunityEntity {

    /** 暗影系实体标签（用于 AoE 伤害排除友军） */
    protected static final TagKey<EntityType<?>> SHADOW_MOB_TAG =
            TagKey.create(Registries.ENTITY_TYPE,
                    ResourceLocation.fromNamespaceAndPath("pasterdream", "shadow_mob"));

    /** 攻击挥动标记（供动画系统使用） */
    protected boolean swinging;
    /** 上一次挥动的时间 */
    protected long lastSwing;

    /**
     * 延迟任务 —— 替代原 Forge queueServerWork，纯 Java 实现
     */
    protected static record DelayedTask(int triggerTick, Runnable action) {}

    /** 挂起的延迟任务列表 */
    protected final List<DelayedTask> pendingTasks = new ArrayList<>();
    /** 服务端全局 tick 计数器（用于任务调度） */
    protected int serverTickCounter = 0;

    /** 技能系统初始化标记 */
    protected boolean skillSwitchInitialized = false;

    /** 技能锁定时长计数器（防止技能状态卡死导致 BOSS 变傻） */
    protected int skillLockTick = 0;

    /** 技能锁定卡死阈值（tick）。超过则强制解锁技能状态，防止 BOSS 永久不放大招 */
    protected static final int SKILL_LOCK_TIMEOUT = 300;

    /** 是否处于召唤动画状态（spawn 动画期间禁用 AI 和技能） */
    protected boolean isSummoning = false;

    /**
     * 构造亚伦柯斯之手实体
     *
     * @param type  实体类型
     * @param level 世界实例
     */
    protected AaroncosHandEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
        this.xpReward = 100;
        this.setPersistenceRequired();
        this.moveControl = new FlyingMoveControl(this, 10, true);
    }

    // ======================== 子类差异抽象 ========================

    /**
     * 获取手名称，用于日志输出。
     *
     * @return 手名称（如 "AaroncosLefthand0" / "AaroncosRighthand0"）
     */
    protected abstract String getHandName();

    /**
     * 获取召唤动画持续 tick 数。
     *
     * @return 召唤动画时长
     */
    protected abstract int getSpawnAnimationTicks();

    /**
     * 召唤动画完成后的回调。
     *
     * @param serverLevel 当前服务端世界
     */
    protected abstract void onSpawnAnimationComplete(ServerLevel serverLevel);

    /**
     * 死亡时的维度相关回调。
     *
     * @param serverLevel 当前服务端世界
     */
    protected abstract void onHandDeath(ServerLevel serverLevel);

    /**
     * 受击时触发的技能。
     */
    protected abstract void onHurtTriggerSkill();

    /**
     * 保存手的专属 NBT 数据。
     *
     * @param compound 待写入的 CompoundTag
     */
    protected abstract void saveHandSpecificData(CompoundTag compound);

    /**
     * 读取手的专属 NBT 数据。
     *
     * @param compound 待读取的 CompoundTag
     */
    protected abstract void readHandSpecificData(CompoundTag compound);

    /**
     * 获取近战 AI 移动速度倍率。
     *
     * @return 速度倍率
     */
    protected abstract double getMeleeAttackSpeed();

    /**
     * 获取首选战斗距离（格）。
     * <p>
     * 远程手（右手）覆写返回 &gt; 0，飞行追踪 AI 会维持该距离输出并避免贴脸；
     * 近战手（左手）保持默认 0，维持贴脸追击。
     *
     * @return 首选战斗距离，0 表示贴脸近战
     */
    protected double getPreferredCombatRange() {
        return 0.0;
    }

    /**
     * 执行技能循环。子类在此实现各自的技能调度逻辑。
     */
    protected abstract void tickSkillCycle();

    // ======================== 技能与目标辅助 ========================

    /**
     * 技能是否激活（AaroncosSkill==1：技能执行中，应暂停普攻/追击）
     * <p>
     * 由 AI goal（近战/飞行追踪）在技能执行期间拦截普攻，
     * 保证「技能 CD 好放技能、没好普攻」互斥。
     *
     * @return 技能激活返回 {@code true}
     */
    protected boolean isSkillActive() {
        return this.getPersistentData().getInt("AaroncosSkill") == 1;
    }

    /**
     * 获取当前仇恨目标（不存在或已死亡返回 {@code null}）
     * <p>
     * 技能只对仇恨目标释放：无目标时技能循环应直接返回。
     *
     * @return 存活仇恨目标，或 {@code null}
     */
    @org.jetbrains.annotations.Nullable
    protected LivingEntity getCombatTarget() {
        LivingEntity target = this.getTarget();
        return target != null && target.isAlive() ? target : null;
    }

    // ======================== 召唤状态 ========================

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

    // ======================== AI 目标 ========================

    @Override
    protected void registerGoals() {
        // 近战攻击（技能激活期间暂停普攻）
        this.goalSelector.addGoal(1, new BossMeleeAttackGoal(this, getMeleeAttackSpeed(), true));
        // 飞行追踪目标
        this.goalSelector.addGoal(2, new FlyingPursuitGoal());
        // 攻击目标（排除创造模式玩家）
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class,
                10, false, false, target -> !(target instanceof Player p && p.isCreative())));
        // 随机飞行
        this.goalSelector.addGoal(4, new RandomStrollGoal(this, 0.8, 20) {
            @Override
            protected Vec3 getPosition() {
                Vec3 pos = AaroncosHandEntity.this.position();
                double dx = pos.x + (AaroncosHandEntity.this.getRandom().nextFloat() * 2 - 1) * 16;
                double dy = pos.y + (AaroncosHandEntity.this.getRandom().nextFloat() * 2 - 1) * 16;
                double dz = pos.z + (AaroncosHandEntity.this.getRandom().nextFloat() * 2 - 1) * 16;
                return new Vec3(dx, dy, dz);
            }
        });
    }

    // ======================== 目标辅助 ========================

    /**
     * 判断实体是否为 BOSS 可攻击的玩家（排除创造模式玩家）
     * <p>
     * BOSS 及其所有技能不对创造模式玩家发动攻击，统一由此判定。
     *
     * @param entity 待判定的实体
     * @return 是否为可攻击的存活非创造玩家
     */
    protected boolean isAttackablePlayer(Entity entity) {
        return entity instanceof Player p && !p.isCreative() && p.isAlive();
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
        // 受击触发子类技能（服务端且存活时）
        if (!this.level().isClientSide() && !this.isDeadOrDying()) {
            // 普通受击不再触发全屏打击帧（避免频繁闪白，闪白保留给终结技大演出）
            onHurtTriggerSkill();
        }
        // 伤害免疫由 ConfigurableImmunityEntity 统一管理
        return super.hurt(source, amount);
    }

    // ======================== 生成 & 死亡 ========================

    @Override
    public void onAddedToLevel() {
        super.onAddedToLevel();
        // 如果处于召唤状态，不播放默认生成效果，由召唤动画控制
        if (this.isSummoning) {
            if (this.level() instanceof ServerLevel serverLevel) {
                queueTask(getSpawnAnimationTicks(), () -> {
                    this.isSummoning = false;
                    onSpawnAnimationComplete(serverLevel);
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

                onHandDeath(serverLevel);
            }
            this.remove(RemovalReason.KILLED);
            // dropExperience 由 Entity 基类在死亡时自动处理
        }
    }

    // ======================== NBT 持久化 ========================

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        CompoundTag data = this.getPersistentData();
        compound.putBoolean("AaroncosSwitch", data.getBoolean("AaroncosSwitch"));
        compound.putInt("AaroncosSkill", data.getInt("AaroncosSkill"));
        compound.putBoolean("AaroncosBloodLock", data.getBoolean("AaroncosBloodLock"));
        saveHandSpecificData(compound);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        CompoundTag data = this.getPersistentData();
        if (compound.contains("AaroncosSwitch")) {
            data.putBoolean("AaroncosSwitch", compound.getBoolean("AaroncosSwitch"));
        }
        if (compound.contains("AaroncosSkill")) {
            data.putInt("AaroncosSkill", compound.getInt("AaroncosSkill"));
        }
        if (compound.contains("AaroncosBloodLock")) {
            data.putBoolean("AaroncosBloodLock", compound.getBoolean("AaroncosBloodLock"));
        }
        readHandSpecificData(compound);
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
                CompoundTag data = this.getPersistentData();
                data.putBoolean("AaroncosSwitch", true);
                // 重进世界后重置技能中间状态：
                // pendingTasks 延迟任务队列不持久化，若在技能释放中（AaroncosSkill=1）保存，
                // 加载后 skill 卡死、且没有 queueTask 解锁，技能系统永久锁死（BOSS 变傻）。
                // 这里清零技能中间状态，仅保留一次性标记（BloodLock / TuneTotemFinale）。
                data.putInt("AaroncosSkill", 0);
                data.putInt("AaroncosMagicball", 0);
                data.putInt("AaroncosVortex", 0);
                data.putInt("AaroncosSprint", 0);
                data.putInt("AaroncosHit", 0);
                data.putInt("AaroncosSword", 0);
                // 仅在非召唤状态清空延迟任务：
                // 召唤时 onAddedToLevel 已排程「召唤结束解除无敌」任务，若在此清空
                // 会导致 isSummoning 永不解除 → BOSS 过场后仍无敌。
                if (!isSummoning) {
                    this.pendingTasks.clear();
                }
                skillSwitchInitialized = true;
            }

            // 处理延迟任务队列
            processPendingTasks();

            // 技能循环
            tickSkillCycle();

            // 技能卡死防御：若 AaroncosSkill 长时间保持 1（解锁任务丢失/被打断），
            // 强制解锁，避免 BOSS 永久不放大招"变傻"。
            CompoundTag skillData = this.getPersistentData();
            if (skillData.getInt("AaroncosSkill") == 1) {
                skillLockTick++;
                if (skillLockTick > SKILL_LOCK_TIMEOUT) {
                    skillData.putInt("AaroncosSkill", 0);
                    skillLockTick = 0;
                }
            } else {
                skillLockTick = 0;
            }

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

        // —— 持续面向目标（敌人）——
        // 关键：getLookAngle()（技能发射方向）基于 yRot 而非 yHeadRot。
        // getLookControl().setLookAt() 只更新 yHeadRot/xRot，若不同步 yRot，
        // 技能发射方向仍用身体朝向，导致技能空位。这里每 tick 把 yRot/yBodyRot
        // 同步到视线方向，确保 BOSS 持续面对敌人且发射方向正确。
        LivingEntity target = this.getTarget();
        if (target != null && target.isAlive()) {
            this.getLookControl().setLookAt(target, 30.0F, 30.0F);
            this.setYRot(this.getYHeadRot());
            this.setYBodyRot(this.getYHeadRot());
        }
    }

    // ======================== 延迟任务队列 ========================

    /**
     * 添加一个延迟任务，在指定 tick 数后执行
     *
     * @param delay  延迟 tick 数
     * @param action 要执行的操作
     */
    protected void queueTask(int delay, Runnable action) {
        this.pendingTasks.add(new DelayedTask(serverTickCounter + delay, action));
    }

    /**
     * 处理所有到期的延迟任务
     */
    protected void processPendingTasks() {
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

    // ======================== 鲜血锁链系统 ========================

    /**
     * 检测并触发鲜血锁链（狂暴）—— 当 HP &lt; 本体血量 1/3 且未锁定时触发
     * <p>
     * 触发效果：
     * <ul>
     *   <li>抗性提升 IV（60 秒）</li>
     *   <li>召唤 4 个暗影之手（ShadowHand）</li>
     *   <li>80 格内玩家获得暗影/失明/缓慢 + 禁锢效果</li>
     *   <li>播放 aaroncos_spawn 音效</li>
     * </ul>
     */
    protected void tryBloodLock() {
        CompoundTag data = this.getPersistentData();
        if (data.getBoolean("AaroncosBloodLock") || this.getHealth() > this.getMaxHealth() / 3f) return;

        data.putBoolean("AaroncosBloodLock", true);

        // 抗性提升 IV（60秒）
        this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 1200, 3, false, false));

        // 召唤 4 个暗影之手并施加玩家减益（仅在 ServerLevel 执行）
        if (this.level() instanceof ServerLevel serverLevel) {
            // 狂暴瞬间血色雾氛围（全场雾色染红，持续 6 秒后自动衰减；不触发黑白闪，留给终结技）
            AtmosphereEffectAPI.bloodFog(serverLevel, this.position(), 99.0, 0.9f, 120);

            // 狂暴瞬间灵魂粒子发射器（圆形向上喷射）
            ParticleEmitterAPI.spawn(serverLevel, this.position(), 99.0,
                    ParticleEmitterData.builder(ParticleTypes.SOUL)
                            .position(this.position().add(0, 2.5, 0))
                            .lifetime(40)
                            .particlesPerTick(6)
                            .processor(new CircleSpawnProcessor(2.5f))
                            .build());

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

            AABB box = this.getBoundingBox().inflate(80.0);
            this.level().getEntitiesOfClass(Player.class, box, this::isAttackablePlayer)
                    .forEach(player -> {
                        player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 60, 0, false, false));
                        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 1, false, false));
                        player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 60, 0, false, false));
                        // 禁锢：MOVEMENT_SLOWDOWN 255 级（amplifier=254），60 tick
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

    // ======================== 范围伤害辅助 ========================

    /**
     * 对附近可攻击玩家造成伤害（排除创造模式玩家与特殊实体）
     *
     * @param range  范围半径
     * @param damage 伤害量
     */
    protected void hurtNearbyPlayers(double range, float damage) {
        AABB box = this.getBoundingBox().inflate(range);
        this.level().getEntitiesOfClass(Player.class, box, this::isAttackablePlayer)
                .forEach(p -> p.hurt(this.damageSources().mobAttack(this), damage));
    }

    /**
     * 对附近非暗影系 LivingEntity 造成伤害并附加 CONFUSION 效果（排除创造模式玩家）
     *
     * @param range             范围半径
     * @param damage            伤害量
     * @param confusionDuration 混乱效果持续 tick
     */
    protected void hurtNearbyLivingWithConfusion(double range, float damage, int confusionDuration) {
        AABB box = this.getBoundingBox().inflate(range);
        List<LivingEntity> entities = this.level().getEntitiesOfClass(LivingEntity.class, box,
                e -> e != this && e.isAlive() && !e.getType().is(SHADOW_MOB_TAG)
                        && !(e instanceof Player p && p.isCreative()));
        for (LivingEntity entity : entities) {
            entity.hurt(this.damageSources().mobAttack(this), damage);
            entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, confusionDuration, 0, false, false));
        }
    }

    /**
     * 改变附近可攻击玩家的速度向量（排除创造模式玩家）
     *
     * @param x x 方向
     * @param y y 方向
     * @param z z 方向
     */
    protected void pushNearbyPlayers(double x, double y, double z) {
        AABB box = this.getBoundingBox().inflate(15.0);
        this.level().getEntitiesOfClass(Player.class, box, this::isAttackablePlayer)
                .forEach(p -> p.setDeltaMovement(new Vec3(x, y, z)));
    }

    // ======================== GeckoLib 动画 ========================

    /**
     * 移动状态动画控制器
     *
     * @param state 动画状态
     * @return 播放状态
     */
    private PlayState movementPredicate(AnimationState<AaroncosHandEntity> state) {
        // 注意：必须用 getSyncedAnimation()（服务端同步的 entity data），
        // 不能读 animationprocedure 本地字段——该字段只在服务端 setAnimation 时赋值，
        // 客户端永远保持 "empty"，会导致 movement 控制器持续播放并覆盖 procedure 技能动画。
        if (this.getSyncedAnimation().equals("empty")) {
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
     *
     * @param state 动画状态
     * @return 播放状态
     */
    private PlayState attackingPredicate(AnimationState<AaroncosHandEntity> state) {
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
     * <p>
     * 技能激活期间暂停追击与普攻（技能执行中不贴脸不普攻，技能播完恢复）。
     */
    protected class FlyingPursuitGoal extends Goal {
        public FlyingPursuitGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (isSkillActive() || getTarget() == null || getMoveControl().hasWanted()) {
                return false;
            }
            // 远程手：已在舒适战斗距离带内时无需移动，悬停输出（技能循环负责释放）
            double range = getPreferredCombatRange();
            if (range > 0) {
                double distSq = distanceToSqr(getTarget());
                double near = range - 3.0;
                double far = range + 8.0;
                return distSq < near * near || distSq > far * far;
            }
            return true;
        }

        @Override
        public boolean canContinueToUse() {
            return !isSkillActive() && getMoveControl().hasWanted()
                    && getTarget() != null && getTarget().isAlive();
        }

        @Override
        public void start() {
            LivingEntity target = getTarget();
            if (target == null) return;
            // 远程手：过近则先拉开到首选距离，其余情况逼近目标
            double range = getPreferredCombatRange();
            if (range > 0 && distanceToSqr(target) < (range - 3.0) * (range - 3.0)) {
                setAwayFromTarget(target, range);
                return;
            }
            Vec3 pos = target.getEyePosition(1);
            moveControl.setWantedPosition(pos.x, pos.y, pos.z, 1.0);
        }

        @Override
        public void tick() {
            LivingEntity target = getTarget();
            if (target == null || isSkillActive()) return;

            // 远程手：维持战斗距离——过近远离，过远逼近，舒适带内悬停
            double range = getPreferredCombatRange();
            if (range > 0) {
                double distSq = distanceToSqr(target);
                double near = range - 3.0;
                double far = range + 8.0;
                if (distSq < near * near) {
                    setAwayFromTarget(target, range);
                } else if (distSq > far * far) {
                    Vec3 pos = target.getEyePosition(1);
                    moveControl.setWantedPosition(pos.x, pos.y, pos.z, 1.0);
                }
                return;
            }

            // 近战手（range == 0）：贴脸追击 + 碰撞普攻
            if (getBoundingBox().intersects(target.getBoundingBox())) {
                doHurtTarget(target);
            } else if (distanceToSqr(target) < 16) {
                Vec3 pos = target.getEyePosition(1);
                moveControl.setWantedPosition(pos.x, pos.y, pos.z, 1.0);
            }
        }

        /**
         * 让 BOSS 远离目标，落点在目标水平方向 + 首选距离处（保持当前高度）
         *
         * @param target 仇恨目标
         * @param range  首选战斗距离
         */
        private void setAwayFromTarget(LivingEntity target, double range) {
            Vec3 away = AaroncosHandEntity.this.position().subtract(target.getEyePosition(1));
            away = new Vec3(away.x, 0, away.z);
            if (away.lengthSqr() < 1.0E-4) {
                // 与目标几乎同坐标时取随机水平方向，避免归一化除零
                away = new Vec3(AaroncosHandEntity.this.getRandom().nextDouble() - 0.5, 0,
                        AaroncosHandEntity.this.getRandom().nextDouble() - 0.5);
            }
            away = away.normalize();
            Vec3 destination = target.getEyePosition(1).add(away.scale(range));
            destination = new Vec3(destination.x, AaroncosHandEntity.this.getY(), destination.z);
            AaroncosHandEntity.this.moveControl.setWantedPosition(
                    destination.x, destination.y, destination.z, 1.0);
        }
    }

    /**
     * 近战攻击 goal —— 技能激活期间暂停普攻
     * <p>
     * 保证「技能 CD 好放技能、CD 没好普攻」互斥：
     * 技能执行（AaroncosSkill==1）时该 goal 不激活、不继续。
     */
    protected class BossMeleeAttackGoal extends MeleeAttackGoal {
        /**
         * 构造 BOSS 近战攻击 goal
         *
         * @param mob       BOSS 实体
         * @param speed     追击速度倍率
         * @param following 是否持续追踪目标
         */
        BossMeleeAttackGoal(AaroncosHandEntity mob, double speed, boolean following) {
            super(mob, speed, following);
        }

        @Override
        public boolean canUse() {
            if (isSkillActive()) return false;
            // 远程手：过近（低于首选距离）时禁用近战普攻，让优先级更低的
            // FlyingPursuitGoal 接管拉开距离，避免贴脸卡在普攻不放远程技能。
            double range = getPreferredCombatRange();
            if (range > 0) {
                LivingEntity target = getTarget();
                if (target == null || distanceToSqr(target) < (range - 3.0) * (range - 3.0)) {
                    return false;
                }
            }
            return super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            if (isSkillActive()) return false;
            double range = getPreferredCombatRange();
            if (range > 0) {
                LivingEntity target = getTarget();
                if (target == null || distanceToSqr(target) < (range - 3.0) * (range - 3.0)) {
                    return false;
                }
            }
            return super.canContinueToUse();
        }
    }
}
