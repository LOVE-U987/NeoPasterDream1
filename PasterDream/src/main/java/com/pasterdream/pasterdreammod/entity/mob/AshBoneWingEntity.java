package com.pasterdream.pasterdreammod.entity.mob;

import com.pasterdream.pasterdreammod.api.entity.base.GeckoLibMobEntity;
import com.pasterdream.pasterdreammod.entity.projectile.BoneWingFireBallProjectileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;

import java.util.EnumSet;

/**
 * 灰烬骨翼 (Ash Bone Wing) — 更强的飞行敌对生物
 * <p>
 * 32 血、3 护甲、攻击间隔更短（35 tick）
 * 比普通骨翼更难对付的变种
 */
public class AshBoneWingEntity extends GeckoLibMobEntity implements RangedAttackMob {

    /** 攻击挥动标记（供动画系统使用） */
    private boolean swinging;
    private boolean lastloop;
    /** 上一次挥动的时间 */
    private long lastSwing;

    /**
     * 构造灰烬骨翼实体
     *
     * @param type  实体类型
     * @param level 世界实例
     */
    public AshBoneWingEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
        this.xpReward = 12;
        this.moveControl = new FlyingMoveControl(this, 10, true);
    }

    /**
     * 返回默认纹理名称
     *
     * @return 默认纹理 "ash_bone_wing"
     */
    @Override
    protected String getDefaultTexture() {
        return "ash_bone_wing";
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
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
     * 创建灰烬骨翼的属性
     * 32 血、3 护甲、比普通骨翼更耐打
     *
     * @return 属性构造器
     */
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 32)
                .add(Attributes.ARMOR, 3)
                .add(Attributes.ATTACK_DAMAGE, 3)
                .add(Attributes.MOVEMENT_SPEED, 0.4)
                .add(Attributes.FOLLOW_RANGE, 20)
                .add(Attributes.FLYING_SPEED, 0.4);
    }

    // ==================== AI 目标 ====================

    @Override
    protected void registerGoals() {
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, false, false));
        this.goalSelector.addGoal(2, new RandomStrollGoal(this, 0.8, 20) {
            @Override
            protected Vec3 getPosition() {
                RandomSource random = AshBoneWingEntity.this.getRandom();
                double dirX = AshBoneWingEntity.this.getX() + ((random.nextFloat() * 2 - 1) * 16);
                double dirY = AshBoneWingEntity.this.getY() + ((random.nextFloat() * 2 - 1) * 16);
                double dirZ = AshBoneWingEntity.this.getZ() + ((random.nextFloat() * 2 - 1) * 16);
                return new Vec3(dirX, dirY, dirZ);
            }
        });
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(4, new AshBoneWingEntity.RangedAttackGoal(this, 1.25, 35, 12f) {
            @Override
            public boolean canContinueToUse() {
                return this.canUse();
            }
        });
    }

    // ==================== 远程攻击 AI ====================

    /**
     * 灰烬骨翼远程攻击目标
     * 相比骨翼，攻击间隔更短（35tick vs 85tick）
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
            ((AshBoneWingEntity) rangedAttackMob).entityData.set(SHOOT, false);
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
                    ((AshBoneWingEntity) rangedAttackMob).entityData.set(SHOOT, false);
                    return;
                }
                ((AshBoneWingEntity) rangedAttackMob).entityData.set(SHOOT, true);
                float f = (float) Math.sqrt(distanceSq) / this.attackRadius;
                float f1 = Mth.clamp(f, 0.1F, 1.0F);
                this.rangedAttackMob.performRangedAttack(this.target, f1);
                this.attackTime = Mth.floor(f * (float) (this.attackIntervalMax - this.attackIntervalMin) + (float) this.attackIntervalMin);
            } else if (this.attackTime < 0) {
                this.attackTime = Mth.floor(Mth.lerp(Math.sqrt(distanceSq) / (double) this.attackRadius, (double) this.attackIntervalMin, (double) this.attackIntervalMax));
            } else {
                ((AshBoneWingEntity) rangedAttackMob).entityData.set(SHOOT, false);
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
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
    }

    // ==================== 远程攻击实现 ====================

    @Override
    public void performRangedAttack(LivingEntity target, float velocity) {
        // 发射骨翼火球弹射物
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

    private PlayState movementPredicate(AnimationState<AshBoneWingEntity> state) {
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

    private PlayState attackingPredicate(AnimationState<AshBoneWingEntity> state) {
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

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        super.registerControllers(controllers);
        controllers.add(new AnimationController<>(this, "movement", 4, this::movementPredicate));
        controllers.add(new AnimationController<>(this, "attacking", 4, this::attackingPredicate));
    }
}
