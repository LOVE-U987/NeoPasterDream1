package com.pasterdream.pasterdreammod.entity.mob;

import com.pasterdream.pasterdreammod.api.entity.base.GeckoLibMobEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;

/**
 * 水母 (Jellyfish) — 染梦海洋中闪闪发光的水母
 * <p>
 * 行为：
 * - 被动水生动物，GeckoLib 动画实体
 * - 悬浮水中，自动上浮
 * - 发出萤光粒子（GLOW）
 * - 受伤时喷墨（GLOW_SQUID_INK 粒子）
 * - 完整的动画状态机（movement）
 * - 死亡 10 tick 后移除
 * <p>
 * 渲染：GeckoLib 动画实体，默认纹理 "jellyfish"
 */
public class JellyfishEntity extends GeckoLibMobEntity {

    /**
     * 构造水母实体
     *
     * @param type  实体类型
     * @param level 世界实例
     */
    public JellyfishEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
        this.moveControl = new FlyingMoveControl(this, 10, true);
        this.setNoGravity(true);
        this.xpReward = 0;
    }

    /**
     * 返回默认纹理名称
     *
     * @return 默认纹理 "jellyfish"
     */
    @Override
    protected String getDefaultTexture() {
        return "jellyfish";
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
    }

    // ==================== 属性 ====================

    /**
     * 创建水母的属性
     * 10 血量、0.2 移动速度、0.2 飞行速度、3 攻击力、16 追踪范围
     *
     * @return 属性构造器
     */
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 10)
                .add(Attributes.MOVEMENT_SPEED, 0.2)
                .add(Attributes.ARMOR, 0)
                .add(Attributes.ATTACK_DAMAGE, 3)
                .add(Attributes.FOLLOW_RANGE, 16)
                .add(Attributes.FLYING_SPEED, 0.2);
    }

    // ==================== AI ====================

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new RandomStrollGoal(this, 0.8, 20) {
            @Override
            protected Vec3 getPosition() {
                RandomSource random = JellyfishEntity.this.getRandom();
                double dirX = JellyfishEntity.this.getX() + ((random.nextFloat() * 2 - 1) * 16);
                double dirY = JellyfishEntity.this.getY() + ((random.nextFloat() * 2 - 1) * 16);
                double dirZ = JellyfishEntity.this.getZ() + ((random.nextFloat() * 2 - 1) * 16);
                return new Vec3(dirX, dirY, dirZ);
            }
        });
        this.goalSelector.addGoal(2, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(3, new LeapAtTargetGoal(this, 0.5F));
    }

    // ==================== 飞行导航 ====================

    @Override
    protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation nav = new FlyingPathNavigation(this, level);
        nav.setCanOpenDoors(false);
        nav.setCanFloat(true);
        nav.setCanPassDoors(true);
        return nav;
    }

    // ==================== 音效 ====================

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.SLIME_HURT_SMALL;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SLIME_DEATH_SMALL;
    }

    @Override
    public boolean causeFallDamage(float l, float d, DamageSource source) {
        return false;
    }

    @Override
    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
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

    // ==================== 受伤 ====================

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!level().isClientSide() && level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.GLOW_SQUID_INK,
                    this.getX(), this.getY() - 0.2, this.getZ(),
                    5, 0.1, 0.2, 0.1, 0.1);
        }
        return super.hurt(source, amount);
    }

    // ==================== 每 tick 更新 ====================

    @Override
    public void baseTick() {
        super.baseTick();
        if (!level().isClientSide() && level() instanceof ServerLevel serverLevel) {
            // 90% 概率触发上浮与粒子（原 JellyfishPr0Procedure）
            if (random.nextFloat() < 0.9F) {
                BlockPos below = this.blockPosition().below(2);
                if (!level().getBlockState(below).isAir()) {
                    this.setDeltaMovement(new Vec3(0, 0.1, 0));
                }
                serverLevel.sendParticles(ParticleTypes.GLOW,
                        this.getX(), this.getY(), this.getZ(),
                        1, 0.15, 0.5, 0.15, 0.01);
            }
        }
        this.refreshDimensions();
    }

    @Override
    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime == 10) {
            this.remove(RemovalReason.KILLED);
        }
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

    // ==================== GeckoLib 动画 ====================

    private PlayState movementPredicate(AnimationState<JellyfishEntity> state) {
        if (this.getSyncedAnimation().equals("empty")) {
            if ((state.isMoving() || !(state.getLimbSwingAmount() > -0.15F && state.getLimbSwingAmount() < 0.15F))
                    && this.onGround()) {
                return state.setAndContinue(RawAnimation.begin().thenLoop("move"));
            }
            if (this.isInWaterOrBubble()) {
                return state.setAndContinue(RawAnimation.begin().thenLoop("move"));
            }
            if (!this.onGround()) {
                return state.setAndContinue(RawAnimation.begin().thenLoop("move"));
            }
            return state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
        }
        return PlayState.STOP;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        super.registerControllers(controllers);
        controllers.add(new AnimationController<>(this, "movement", 5, this::movementPredicate));
    }
}
