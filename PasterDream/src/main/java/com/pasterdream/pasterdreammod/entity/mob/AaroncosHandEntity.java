package com.pasterdream.pasterdreammod.entity.mob;

import com.pasterdream.pasterdreammod.entity.damage.ConfigurableImmunityEntity;
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
     * 执行技能循环。子类在此实现各自的技能调度逻辑。
     */
    protected abstract void tickSkillCycle();

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
        // 近战攻击
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, getMeleeAttackSpeed(), true));
        // 飞行追踪目标
        this.goalSelector.addGoal(2, new FlyingPursuitGoal());
        // 攻击目标
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, false, false));
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
     * 检测并触发鲜血锁链 —— 当 HP < 100 且未锁定时触发
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
        if (data.getBoolean("AaroncosBloodLock") || this.getHealth() > 100) return;

        data.putBoolean("AaroncosBloodLock", true);

        // 抗性提升 IV（60秒）
        this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 1200, 3, false, false));

        // 召唤 4 个暗影之手并施加玩家减益（仅在 ServerLevel 执行）
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

            AABB box = this.getBoundingBox().inflate(80.0);
            this.level().getEntitiesOfClass(Player.class, box, Entity::isAlive)
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
     * 对附近玩家造成伤害（排除特殊实体）
     *
     * @param range  范围半径
     * @param damage 伤害量
     */
    protected void hurtNearbyPlayers(double range, float damage) {
        AABB box = this.getBoundingBox().inflate(range);
        this.level().getEntitiesOfClass(Player.class, box, Entity::isAlive)
                .forEach(p -> p.hurt(this.damageSources().mobAttack(this), damage));
    }

    /**
     * 对附近非暗影系 LivingEntity 造成伤害并附加 CONFUSION 效果
     *
     * @param range             范围半径
     * @param damage            伤害量
     * @param confusionDuration 混乱效果持续 tick
     */
    protected void hurtNearbyLivingWithConfusion(double range, float damage, int confusionDuration) {
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
    protected void pushNearbyPlayers(double x, double y, double z) {
        AABB box = this.getBoundingBox().inflate(15.0);
        this.level().getEntitiesOfClass(Player.class, box, Entity::isAlive)
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
     */
    protected class FlyingPursuitGoal extends Goal {
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
