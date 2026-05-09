package com.pasterdream.pasterdreammod.entity.mob;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/**
 * 粉色史莱姆 (Pink Slime) — 会活蹦乱跳的被动生物
 * <p>
 * 行为：
 * - 被动生物，不会攻击
 * - 受击时会逃跑（PanicGoal）
 * - 免疫摔落伤害
 * - 有史莱姆风格的跳跃/受伤/死亡音效
 * <p>
 * 动画效果：
 * - 随机跳跃（约每 tick 1/15 概率，朝面向方向猛跳）
 * - 落地时产生粒子效果
 */
public class PinkSlimeEntity extends PathfinderMob {

    public PinkSlimeEntity(EntityType<PinkSlimeEntity> type, Level level) {
        super(type, level);
        this.xpReward = 1;
    }

    /**
     * 创建粉色史莱姆的属性
     * 6 血量、无攻击力、低速
     *
     * @return 属性构造器
     */
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 6)
                .add(Attributes.MOVEMENT_SPEED, 0.15)
                .add(Attributes.ARMOR, 0)
                .add(Attributes.ATTACK_DAMAGE, 0)
                .add(Attributes.FOLLOW_RANGE, 16);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.2));
        this.goalSelector.addGoal(2, new RandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
    }

    // ==================== 受伤/免疫 ====================

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypes.FALL)) return false;
        return super.hurt(source, amount);
    }

    // ==================== 音效 ====================

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.SLIME_BLOCK_PLACE;
    }

    @Override
    public void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(SoundEvents.SLIME_JUMP, 0.15f, 1.0f);
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == EntityEvent.JUMP) {
            this.playSound(SoundEvents.SLIME_JUMP, 0.15f, 1.0f);
        }
        super.handleEntityEvent(id);
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.SLIME_HURT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SLIME_DEATH;
    }

    // ==================== 每 tick 更新 ====================

    @Override
    public void baseTick() {
        super.baseTick();
        this.refreshDimensions();
        if (!level().isClientSide()) {
            doRandomJump();
            doFallParticles();
        }
    }

    /**
     * 随机跳跃行为（原 PinkSlimePr0Procedure）
     * 约 1/15 概率朝面向方向猛跳，实现史莱姆活泼的跳跃动画效果
     */
    private void doRandomJump() {
        if (random.nextInt(15) == 5 && this.onGround()) {
            Vec3 look = this.getLookAngle();
            this.setDeltaMovement(look.x * 3, 0.5, look.z * 3);
            this.hasImpulse = true;
            this.level().broadcastEntityEvent(this, EntityEvent.JUMP);
        }
    }

    /**
     * 掉落粒子效果（原 PinkSlimePr2Procedure）
     * 下落距离 > 0.6 格时产生白色粒子，模拟史莱姆弹跳时的柔软触感
     */
    private void doFallParticles() {
        if (this.fallDistance > 0.6 && level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.END_ROD,
                    this.getX(), this.getY() + 0.5, this.getZ(),
                    5, 0.2, 0.2, 0.2, 0.02);
        }
    }
}