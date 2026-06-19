package com.pasterdream.pasterdreammod.entity.mob;

import com.pasterdream.pasterdreammod.registry.PDParticles;
import com.pasterdream.pasterdreammod.registry.PDItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

/**
 * 孢子实体 (SporeEntity) —— 染梦世界中飞行飘散的丛林孢子
 * <p>
 * 行为特点：
 * <ul>
 *   <li>小型飞行生物，继承 {@link PathfinderMob}，不使用 GeckoLib</li>
 *   <li>1 点生命值，无盔甲，飞行速度 0.3</li>
 *   <li>三维飞行移动（{@link FlyingMoveControl} + {@link FlyingPathNavigation}）</li>
 *   <li>不会受到摔落、仙人掌、箭矢伤害</li>
 *   <li>死亡掉落 {@link PDItems#JUNGLE_SPORE}（丛林孢子）</li>
 *   <li>每 tick 有 50% 概率产生孢子粒子，0.01% 概率掉落丛林孢子后自毁</li>
 * </ul>
 * <p>
 * 原模组对照：FixPasterDream 的 {@code SporeEntityEntity}（PathfinderMob，无 GeckoLib）
 */
public class SporeEntityEntity extends PathfinderMob {

    /**
     * 构造孢子实体
     *
     * @param type  实体类型
     * @param level 世界实例
     */
    public SporeEntityEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
        this.moveControl = new FlyingMoveControl(this, 10, true);
        this.setNoGravity(true);
        this.xpReward = 0;
    }

    // ======================== 属性 ========================

    /**
     * 创建孢子实体的属性
     * <p>
     * 1 HP、0 盔甲、速度 0.3、无攻击力、追踪 16 格、飞行速度 0.3
     *
     * @return 属性构造器
     */
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 1.0)
                .add(Attributes.ARMOR, 0.0)
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.ATTACK_DAMAGE, 0.0)
                .add(Attributes.FOLLOW_RANGE, 16.0)
                .add(Attributes.FLYING_SPEED, 0.3);
    }

    // ======================== 导航 ========================

    /**
     * 创建飞行导航
     *
     * @param level 世界实例
     * @return 飞行路径导航
     */
    @Override
    protected @NotNull PathNavigation createNavigation(Level level) {
        FlyingPathNavigation navigation = new FlyingPathNavigation(this, level);
        navigation.setCanOpenDoors(false);
        navigation.setCanFloat(true);
        navigation.setCanPassDoors(true);
        return navigation;
    }

    // ======================== AI 目标 ========================

    @Override
    protected void registerGoals() {
        super.registerGoals();
        // 三维飞行随机游荡 —— 在 ±16 范围随机选目标坐标
        this.goalSelector.addGoal(1, new RandomStrollGoal(this, 0.8, 20) {
            @Override
            protected Vec3 getPosition() {
                RandomSource random = SporeEntityEntity.this.getRandom();
                double dirX = SporeEntityEntity.this.getX() + ((random.nextFloat() * 2 - 1) * 16);
                double dirY = SporeEntityEntity.this.getY() + ((random.nextFloat() * 2 - 1) * 16);
                double dirZ = SporeEntityEntity.this.getZ() + ((random.nextFloat() * 2 - 1) * 16);
                return new Vec3(dirX, dirY, dirZ);
            }
        });
        this.goalSelector.addGoal(2, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(3, new FloatGoal(this));
        this.goalSelector.addGoal(4, new LeapAtTargetGoal(this, 0.5f));
    }

    // ======================== 伤害免疫 ========================

    @Override
    public boolean hurt(DamageSource source, float amount) {
        // 免疫箭矢伤害
        if (source.getDirectEntity() instanceof AbstractArrow) return false;
        // 免疫摔落伤害
        if (source.is(DamageTypes.FALL)) return false;
        // 免疫仙人掌伤害
        if (source.is(DamageTypes.CACTUS)) return false;
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
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void doPush(Entity entity) {
        // 不可被推动
    }

    @Override
    protected void pushEntities() {
        // 不可推动其他实体
    }

    @Override
    public void aiStep() {
        super.aiStep();
        this.setNoGravity(true);
    }

    // ======================== 声音 ========================

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvent.createVariableRangeEvent(
                ResourceLocation.parse("entity.player.hurt_sweet_berry_bush"));
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvent.createVariableRangeEvent(
                ResourceLocation.parse("entity.player.hurt_sweet_berry_bush"));
    }

    // ======================== 死亡掉落 ========================

    @Override
    protected void dropCustomDeathLoot(ServerLevel serverLevel, DamageSource source, boolean recentlyHitIn) {
        super.dropCustomDeathLoot(serverLevel, source, recentlyHitIn);
        this.spawnAtLocation(new ItemStack(PDItems.JUNGLE_SPORE.get()));
    }

    // ======================== 每 tick 更新 ========================

    @Override
    public void baseTick() {
        super.baseTick();
        if (level().isClientSide()) return;

        // 50% 概率产生 3 个孢子粒子
        if (random.nextDouble() <= 0.5) {
            if (level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles((SimpleParticleType) PDParticles.SPORE_PARTICLE.particleType(),
                        getX(), getY(), getZ(),
                        3, 0.4, 0.4, 0.4, 0.01);
            }
        }

        // 0.01% 概率掉落一个丛林孢子后销毁实体（自爆效果）
        if (random.nextDouble() <= 0.0001) {
            if (level() instanceof ServerLevel serverLevel) {
                // 自爆时爆发大量孢子粒子
                serverLevel.sendParticles((SimpleParticleType) PDParticles.SPORE_PARTICLE.particleType(),
                        getX(), getY(), getZ(),
                        20, 1.5, 1.5, 1.5, 0.2);
                // 掉落丛林孢子
                ItemEntity itemEntity = new ItemEntity(serverLevel, getX(), getY(), getZ(),
                        new ItemStack(PDItems.JUNGLE_SPORE.get()));
                itemEntity.setPickUpDelay(10);
                serverLevel.addFreshEntity(itemEntity);
                // 播放孢子爆裂音效
                serverLevel.playSound(null, BlockPos.containing(getX(), getY(), getZ()),
                        SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES,
                        SoundSource.NEUTRAL, 1.0f, 1.5f);
            }
            this.discard();
        }
    }
}