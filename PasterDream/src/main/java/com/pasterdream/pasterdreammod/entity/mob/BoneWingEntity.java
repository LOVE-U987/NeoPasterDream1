package com.pasterdream.pasterdreammod.entity.mob;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.entity.projectile.BoneWingFireBallProjectileEntity;
import com.pasterdream.pasterdreammod.registry.PDSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import com.pasterdream.pasterdreammod.api.entity.anim.ProcedureAnimationHandler;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.EnumSet;

/**
 * 骨翼 (Bone Wing) — 飞行敌对生物
 * <p>
 * 使用远程攻击（射出火球），免疫摔落伤害
 * 在天空中自由飞行巡逻
 */
public class BoneWingEntity extends Monster implements RangedAttackMob, GeoEntity {

    private static final EntityDataAccessor<Boolean> SHOOT =
            SynchedEntityData.defineId(BoneWingEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<String> ANIMATION =
            SynchedEntityData.defineId(BoneWingEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> TEXTURE =
            SynchedEntityData.defineId(BoneWingEntity.class, EntityDataSerializers.STRING);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    /** 当前动画标识（用于 procedure 控制器） */
    public String animationprocedure = "empty";

    /** 客户端 procedure 动画处理器 */
    private final ProcedureAnimationHandler procAnim = new ProcedureAnimationHandler();
    private boolean swinging;
    private boolean lastloop;
    private long lastSwing;

    /**
     * 构造骨翼实体
     *
     * @param type  实体类型
     * @param level 世界实例
     */
    public BoneWingEntity(EntityType<BoneWingEntity> type, Level level) {
        super(type, level);
        this.xpReward = 12;
        this.moveControl = new FlyingMoveControl(this, 10, true);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SHOOT, false);
        builder.define(ANIMATION, "undefined");
        builder.define(TEXTURE, "bone_wing");
    }

    /**
     * 设置纹理
     *
     * @param texture 纹理名称
     */
    public void setTexture(String texture) {
        this.entityData.set(TEXTURE, texture);
    }

    /**
     * 获取纹理名称
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
     * 设置同步动画
     *
     * @param animation 动画名称
     */
    public void setAnimation(String animation) {
        this.entityData.set(ANIMATION, animation);
        this.animationprocedure = animation;
    }

    // ==================== 导航/移动 ====================

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new FlyingPathNavigation(this, level);
    }

    @Override
    public void setNoGravity(boolean ignored) {
        super.setNoGravity(true);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        this.setNoGravity(true);
    }

    // ==================== 属性 ====================

    /**
     * 创建骨翼的属性
     * 20 血、0 护甲、飞行速度 0.4
     *
     * @return 属性构造器
     */
    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 20)
                .add(Attributes.ARMOR, 0)
                .add(Attributes.ATTACK_DAMAGE, 3)
                .add(Attributes.MOVEMENT_SPEED, 0.4)
                .add(Attributes.FOLLOW_RANGE, 20)
                .add(Attributes.FLYING_SPEED, 0.4);
    }

    // ==================== AI 目标 ====================

    @Override
    protected void registerGoals() {
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, false, false));
        this.goalSelector.addGoal(2, new RandomStrollGoal(this, 1, 20) {
            @Override
            protected Vec3 getPosition() {
                RandomSource random = BoneWingEntity.this.getRandom();
                double dirX = BoneWingEntity.this.getX() + ((random.nextFloat() * 2 - 1) * 16);
                double dirY = BoneWingEntity.this.getY() + ((random.nextFloat() * 2 - 1) * 16);
                double dirZ = BoneWingEntity.this.getZ() + ((random.nextFloat() * 2 - 1) * 16);
                return new Vec3(dirX, dirY, dirZ);
            }
        });
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(4, new BoneWingEntity.RangedAttackGoal(this, 1.25, 85, 12f) {
            @Override
            public boolean canContinueToUse() {
                return this.canUse();
            }
        });
    }

    // ==================== 远程攻击 AI ====================

    /**
     * 骨翼远程攻击目标
     * 自定义弓箭攻击目标变体，使用 SHOOT 同步数据触发攻击动画
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

        public RangedAttackGoal(RangedAttackMob rangedAttackMob, double speedModifier, int attackInterval, float attackRadius) {
            this(rangedAttackMob, speedModifier, attackInterval, attackInterval, attackRadius);
        }

        public RangedAttackGoal(RangedAttackMob rangedAttackMob, double speedModifier, int attackIntervalMin, int attackIntervalMax, float attackRadius) {
            if (!(rangedAttackMob instanceof LivingEntity)) {
                throw new IllegalArgumentException("ArrowAttackGoal requires Mob implements RangedAttackMob");
            }
            this.rangedAttackMob = rangedAttackMob;
            this.mob = (Mob) rangedAttackMob;
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
            ((BoneWingEntity) rangedAttackMob).entityData.set(SHOOT, false);
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
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
                    ((BoneWingEntity) rangedAttackMob).entityData.set(SHOOT, false);
                    return;
                }
                ((BoneWingEntity) rangedAttackMob).entityData.set(SHOOT, true);
                float f = (float) Math.sqrt(distanceSq) / this.attackRadius;
                float f1 = Mth.clamp(f, 0.1F, 1.0F);
                this.rangedAttackMob.performRangedAttack(this.target, f1);
                this.attackTime = Mth.floor(f * (float) (this.attackIntervalMax - this.attackIntervalMin) + (float) this.attackIntervalMin);
            } else if (this.attackTime < 0) {
                this.attackTime = Mth.floor(Mth.lerp(Math.sqrt(distanceSq) / (double) this.attackRadius, (double) this.attackIntervalMin, (double) this.attackIntervalMax));
            } else {
                ((BoneWingEntity) rangedAttackMob).entityData.set(SHOOT, false);
            }
        }
    }

    // ==================== 受伤/免疫 ====================

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {
        // 飞行实体无摔落伤害
    }

    // ==================== 音效 ====================

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.WITHER_SKELETON_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.WITHER_SKELETON_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.WITHER_SKELETON_DEATH;
    }

    // ==================== NBT 持久化 ====================

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

    // ==================== 远程攻击实现 ====================

    /**
     * 执行远程攻击（发射骨翼火球）
     * <p>
     * 火球弹射物自带发射音效 {@link PDSounds#BONE_WING_FIRE_BALL}。
     *
     * @param target   攻击目标
     * @param velocity 速度修正系数
     */
    @Override
    public void performRangedAttack(LivingEntity target, float velocity) {
        // 发射骨翼火球（shoot 方法内部已包含音效播放）
        BoneWingFireBallProjectileEntity.shoot(this, target);
    }

    // ==================== 每 tick ====================

    @Override
    public void baseTick() {
        super.baseTick();
        this.refreshDimensions();
    }

    @Override
    protected void tickDeath() {
        ++this.deathTime;
        // 死亡时产生火焰和烟雾粒子（原BoneWingPr0Procedure逻辑）
        if (this.level().isClientSide()) {
            RandomSource random = this.getRandom();
            for (int i = 0; i < 5; i++) {
                this.level().addParticle(ParticleTypes.FLAME,
                        this.getX() + (random.nextDouble() - 0.5) * this.getBbWidth(),
                        this.getY() + random.nextDouble() * this.getBbHeight(),
                        this.getZ() + (random.nextDouble() - 0.5) * this.getBbWidth(),
                        0, 0.1, 0);
                this.level().addParticle(ParticleTypes.SMOKE,
                        this.getX() + (random.nextDouble() - 0.5) * this.getBbWidth(),
                        this.getY() + random.nextDouble() * this.getBbHeight(),
                        this.getZ() + (random.nextDouble() - 0.5) * this.getBbWidth(),
                        0, 0.05, 0);
            }
        }
        if (this.deathTime == 20) {
            this.remove(RemovalReason.KILLED);
            this.dropExperience(this.getLastHurtByMob());
        }
    }

    // ==================== GeckoLib 动画 ====================

    private PlayState movementPredicate(AnimationState<BoneWingEntity> state) {
        if (this.getSyncedAnimation().equals("empty")) {
            if ((state.isMoving() || !(state.getLimbSwingAmount() > -0.15F && state.getLimbSwingAmount() < 0.15F)) && this.onGround()) {
                return state.setAndContinue(RawAnimation.begin().thenLoop("fly"));
            }
            if (this.isSprinting()) {
                return state.setAndContinue(RawAnimation.begin().thenLoop("fly"));
            }
            if (!this.onGround()) {
                return state.setAndContinue(RawAnimation.begin().thenLoop("fly"));
            }
            return state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
        }
        return PlayState.STOP;
    }

    private PlayState attackingPredicate(AnimationState<BoneWingEntity> state) {
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

    private PlayState procedurePredicate(AnimationState<BoneWingEntity> state) {
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