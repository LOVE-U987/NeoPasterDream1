package com.pasterdream.pasterdreammod.entity.mob;

import com.pasterdream.pasterdreammod.registry.PDSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import com.pasterdream.pasterdreammod.api.entity.anim.ProcedureAnimationHandler;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.EnumSet;

/**
 * 暗影尖啸幽灵 (Shadow Squeal Ghost) — 飞行远程攻击幽灵生物
 * <p>
 * AI 行为：
 * <ul>
 *   <li>继承 {@link RangedAttackMob}，发射音波弹远程攻击</li>
 *   <li>三维飞行移动（FlyingMoveControl + FlyingPathNavigation + 无重力）</li>
 *   <li>兼具近战攻击和远程射击能力</li>
 *   <li>免疫火焰、摔落、仙人掌、溺水、药水伤害</li>
 * </ul>
 * <p>
 * 动画：movement(idle/walk) | attacking(attack) | procedure(触发式)
 */
public class ShadowSquealGhostEntity extends Monster implements RangedAttackMob, GeoEntity {

    /** 射击状态同步标记 */
    public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(ShadowSquealGhostEntity.class, EntityDataSerializers.BOOLEAN);
    /** 当前播放动画名称同步标记 */
    public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(ShadowSquealGhostEntity.class, EntityDataSerializers.STRING);
    /** 纹理名称同步标记（默认 "shadow_ghost"） */
    public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(ShadowSquealGhostEntity.class, EntityDataSerializers.STRING);

    /** GeckoLib 动画实例缓存 */
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    /** 攻击挥动标记 */
    private boolean swinging;
    /** 上一次挥动的时间 */
    private long lastSwing;
    /** 过程动画名称（"empty" 表示无过程动画） */
    public String animationprocedure = "empty";

    /** 客户端 procedure 动画处理器 */
    private final ProcedureAnimationHandler procAnim = new ProcedureAnimationHandler();

    /**
     * 构造暗影尖啸幽灵实体
     *
     * @param type  实体类型
     * @param level 世界实例
     */
    public ShadowSquealGhostEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.moveControl = new FlyingMoveControl(this, 10, true);
        this.setNoGravity(true);
        this.xpReward = 2;
    }

    // ======================== 同步数据 ========================

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SHOOT, false);
        builder.define(ANIMATION, "undefined");
        builder.define(TEXTURE, "shadow_ghost");
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

    // ======================== NBT 持久化 ========================

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putString("Texture", this.getTexture());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Texture")) {
            this.setTexture(compound.getString("Texture"));
        }
    }

    // ======================== 导航 ========================

    @Override
    protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation navigation = new FlyingPathNavigation(this, level);
        navigation.setCanOpenDoors(false);
        navigation.setCanFloat(true);
        navigation.setCanPassDoors(true);
        return navigation;
    }

    // ======================== 属性 ========================

    /**
     * 创建暗影尖啸幽灵的属性
     *
     * @return 属性构造器
     */
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.7)
                .add(Attributes.MAX_HEALTH, 10.0)
                .add(Attributes.ARMOR, 0.0)
                .add(Attributes.ATTACK_DAMAGE, 5.0)
                .add(Attributes.FOLLOW_RANGE, 20.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5)
                .add(Attributes.FLYING_SPEED, 0.7);
    }

    // ======================== AI 目标 ========================

    @Override
    protected void registerGoals() {
        super.registerGoals();

        // 近战攻击
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2, false));

        // 飞行追踪目标 Goal
        this.goalSelector.addGoal(2, new Goal() {
            {
                this.setFlags(EnumSet.of(Goal.Flag.MOVE));
            }

            @Override
            public boolean canUse() {
                if (ShadowSquealGhostEntity.this.getTarget() != null && !ShadowSquealGhostEntity.this.getMoveControl().hasWanted()) {
                    return true;
                }
                return false;
            }

            @Override
            public boolean canContinueToUse() {
                return ShadowSquealGhostEntity.this.getMoveControl().hasWanted()
                        && ShadowSquealGhostEntity.this.getTarget() != null
                        && ShadowSquealGhostEntity.this.getTarget().isAlive();
            }

            @Override
            public void start() {
                LivingEntity target = ShadowSquealGhostEntity.this.getTarget();
                if (target != null) {
                    Vec3 vec3d = target.getEyePosition(1);
                    ShadowSquealGhostEntity.this.moveControl.setWantedPosition(vec3d.x, vec3d.y, vec3d.z, 0.6);
                }
            }

            @Override
            public void tick() {
                LivingEntity target = ShadowSquealGhostEntity.this.getTarget();
                if (target == null) return;
                if (ShadowSquealGhostEntity.this.getBoundingBox().intersects(target.getBoundingBox())) {
                    ShadowSquealGhostEntity.this.doHurtTarget(target);
                } else {
                    double distanceSqr = ShadowSquealGhostEntity.this.distanceToSqr(target);
                    if (distanceSqr < 5) {
                        Vec3 vec3d = target.getEyePosition(1);
                        ShadowSquealGhostEntity.this.moveControl.setWantedPosition(vec3d.x, vec3d.y, vec3d.z, 0.6);
                    }
                }
            }
        });

        // 三维随机飞行
        this.goalSelector.addGoal(3, new RandomStrollGoal(this, 0.8, 20) {
            @Override
            protected Vec3 getPosition() {
                RandomSource random = ShadowSquealGhostEntity.this.getRandom();
                double dirX = ShadowSquealGhostEntity.this.getX() + ((random.nextFloat() * 2 - 1) * 16);
                double dirY = ShadowSquealGhostEntity.this.getY() + ((random.nextFloat() * 2 - 1) * 16);
                double dirZ = ShadowSquealGhostEntity.this.getZ() + ((random.nextFloat() * 2 - 1) * 16);
                return new Vec3(dirX, dirY, dirZ);
            }
        });

        // 随机张望
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));

        // 目标选择器：反击 + 主动攻击玩家
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Player.class, false, false));
        this.targetSelector.addGoal(6, new HurtByTargetGoal(this));

        // 远程攻击 Goal
        this.goalSelector.addGoal(1, new ShadowSquealGhostEntity.RangedAttackGoal(this, 1.25, 30, 12f) {
            @Override
            public boolean canContinueToUse() {
                return this.canUse();
            }
        });
    }

    /**
     * 自定义远程攻击 Goal（与 RangedAttackGoal 标准实现兼容）
     */
    public class RangedAttackGoal extends Goal {
        private final Mob mob;
        private final RangedAttackMob rangedAttackMob;
        @Nullable
        private LivingEntity target;
        private int attackTime = -1;
        private final double speedModifier;
        private int seeTime;
        private final int attackIntervalMin;
        private final int attackIntervalMax;
        private final float attackRadius;
        private final float attackRadiusSqr;

        public RangedAttackGoal(RangedAttackMob mob, double speedModifier, int attackInterval, float attackRadius) {
            this(mob, speedModifier, attackInterval, attackInterval, attackRadius);
        }

        public RangedAttackGoal(RangedAttackMob mob, double speedModifier, int attackIntervalMin, int attackIntervalMax, float attackRadius) {
            if (!(mob instanceof LivingEntity)) {
                throw new IllegalArgumentException("RangedAttackGoal requires Mob implements RangedAttackMob");
            }
            this.rangedAttackMob = mob;
            this.mob = (Mob) mob;
            this.speedModifier = speedModifier;
            this.attackIntervalMin = attackIntervalMin;
            this.attackIntervalMax = attackIntervalMax;
            this.attackRadius = attackRadius;
            this.attackRadiusSqr = attackRadius * attackRadius;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity livingentity = this.mob.getTarget();
            if (livingentity != null && livingentity.isAlive()) {
                this.target = livingentity;
                return true;
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return this.canUse() || (this.target != null && this.target.isAlive() && !this.mob.getNavigation().isDone());
        }

        @Override
        public void stop() {
            this.target = null;
            this.seeTime = 0;
            this.attackTime = -1;
            ((ShadowSquealGhostEntity) rangedAttackMob).entityData.set(SHOOT, false);
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            if (target == null) return;
            double distanceSq = this.mob.distanceToSqr(this.target.getX(), this.target.getY(), this.target.getZ());
            boolean hasLineOfSight = this.mob.getSensing().hasLineOfSight(this.target);
            if (hasLineOfSight) {
                ++this.seeTime;
            } else {
                this.seeTime = 0;
            }
            if (!(distanceSq > (double) this.attackRadiusSqr) && this.seeTime >= 5) {
                this.mob.getNavigation().stop();
            } else {
                this.mob.getNavigation().moveTo(this.target, this.speedModifier);
            }
            this.mob.getLookControl().setLookAt(this.target, 30.0F, 30.0F);
            if (--this.attackTime == 0) {
                if (!hasLineOfSight) {
                    ((ShadowSquealGhostEntity) rangedAttackMob).entityData.set(SHOOT, false);
                    return;
                }
                ((ShadowSquealGhostEntity) rangedAttackMob).entityData.set(SHOOT, true);
                float f = (float) Math.sqrt(distanceSq) / this.attackRadius;
                float f1 = Mth.clamp(f, 0.1F, 1.0F);
                this.rangedAttackMob.performRangedAttack(this.target, f1);
                this.attackTime = Mth.floor(f * (float) (this.attackIntervalMax - this.attackIntervalMin) + (float) this.attackIntervalMin);
            } else if (this.attackTime < 0) {
                this.attackTime = Mth.floor(Mth.lerp(Math.sqrt(distanceSq) / (double) this.attackRadius, (double) this.attackIntervalMin, (double) this.attackIntervalMax));
            } else {
                ((ShadowSquealGhostEntity) rangedAttackMob).entityData.set(SHOOT, false);
            }
        }
    }

    // ======================== 远程攻击实现 ========================

    /**
     * 执行远程攻击（发射音波弹）
     * <p>
     * 发射音波弹射物攻击目标，同时播放暗影尖啸音效 {@link PDSounds#SQUEAL_WAVE}。
     * 注意：此方法需要 {@code SquealWaveProjectileEntity} 配合使用，
     * 当前音波弹使用区域性音效作为临时占位实现。
     *
     * @param target 攻击目标
     * @param flval  速度修正系数
     */
    @Override
    public void performRangedAttack(LivingEntity target, float flval) {
        // 播放暗影尖啸音效
        this.level().playSound(null, this.blockPosition(),
                PDSounds.SQUEAL_WAVE.get(), SoundSource.HOSTILE, 1.0F, 1.0F);
        // 对目标造成直接伤害（音波攻击）作为临时实现
        target.hurt(this.damageSources().mobAttack(this), 4.0F);
    }

    // ======================== 受伤/免疫 ========================

    /**
     * 暗影尖啸幽灵免疫以下伤害类型：
     * <ul>
     *   <li>火焰</li>
     *   <li>药水云</li>
     *   <li>摔落</li>
     *   <li>仙人掌</li>
     *   <li>溺水</li>
     * </ul>
     *
     * @param source 伤害来源
     * @param amount 伤害值
     * @return 是否受到伤害
     */
    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypes.IN_FIRE)) return false;
        if (source.is(DamageTypes.ON_FIRE)) return false;
        if (source.is(DamageTypes.LAVA)) return false;
        if (source.getDirectEntity() instanceof net.minecraft.world.entity.projectile.ThrownPotion
                || source.getDirectEntity() instanceof net.minecraft.world.entity.AreaEffectCloud) {
            return false;
        }
        if (source.is(DamageTypes.FALL)) return false;
        if (source.is(DamageTypes.CACTUS)) return false;
        if (source.is(DamageTypes.DROWN)) return false;
        return super.hurt(source, amount);
    }

    // ======================== 飞行行为 ========================

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
        // 飞行生物，不处理摔落检测
    }

    @Override
    public void aiStep() {
        super.aiStep();
        this.updateSwingTime();
        this.setNoGravity(true);
    }

    @Override
    public void playStepSound(BlockPos pos, BlockState blockIn) {
        // 幽灵飞行无脚步声
    }

    // ======================== 尺寸刷新 ========================

    @Override
    public void baseTick() {
        super.baseTick();
        this.refreshDimensions();
    }

    // ======================== 死亡处理 ========================

    @Override
    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime == 10) {
            this.remove(RemovalReason.KILLED);
            this.dropExperience(this.getLastHurtByMob());
        }
    }

    // ======================== 动画 getter/setter ========================

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

    // ======================== GeckoLib 动画 ========================

    /**
     * 移动状态动画控制器
     *
     * @param state 动画状态
     * @return 播放状态
     */
    private PlayState movementPredicate(software.bernie.geckolib.animation.AnimationState<ShadowSquealGhostEntity> state) {
        if (this.getSyncedAnimation().equals("empty")) {
            if (state.isMoving() || !(state.getLimbSwingAmount() > -0.15F && state.getLimbSwingAmount() < 0.15F)) {
                return state.setAndContinue(RawAnimation.begin().thenLoop("walk"));
            }
            return state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
        }
        return PlayState.STOP;
    }

    /**
     * 攻击动画控制器（远程射击 + 近战挥动）
     *
     * @param state 动画状态
     * @return 播放状态
     */
    private PlayState attackingPredicate(software.bernie.geckolib.animation.AnimationState<ShadowSquealGhostEntity> state) {
        double d1 = this.getX() - this.xOld;
        double d0 = this.getZ() - this.zOld;
        float velocity = (float) Math.sqrt(d1 * d1 + d0 * d0);
        if (getAttackAnim(state.getPartialTick()) > 0f && !this.swinging) {
            this.swinging = true;
            this.lastSwing = level().getGameTime();
        }
        if (this.swinging && this.lastSwing + 7L <= level().getGameTime()) {
            this.swinging = false;
        }
        if ((this.swinging || this.entityData.get(SHOOT)) && state.getController().getAnimationState() == AnimationController.State.STOPPED) {
            state.getController().forceAnimationReset();
            return state.setAndContinue(RawAnimation.begin().thenPlay("attack"));
        }
        return PlayState.CONTINUE;
    }

    /**
     * 过程动画控制器（用于触发一次性动画）
     *
     * @param state 动画状态
     * @return 播放状态
     */
    private PlayState procedurePredicate(software.bernie.geckolib.animation.AnimationState<ShadowSquealGhostEntity> state) {
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
}