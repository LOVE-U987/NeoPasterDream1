package com.pasterdream.pasterdreammod.entity.projectile;

import com.pasterdream.pasterdreammod.registry.PDEntities;
import com.pasterdream.pasterdreammod.registry.PDSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

/**
 * 骨翼火球弹射物实体 (BoneWingFireBallProjectileEntity)
 * <p>
 * 继承 {@link AbstractArrow} 并实现 {@link ItemSupplier}，由骨翼/灰烬骨翼通过 {@code performRangedAttack} 发射。
 * 命中目标时着火 100 秒，每 tick 生成火焰+烟雾粒子效果。
 * 移植自原模组 FixPasterDream，移除了 MoltengoldWandPr3Procedure（使用原版龙息爆炸音效替代）。
 *
 * @see AbstractArrow
 * @see ItemSupplier
 */
public class BoneWingFireBallProjectileEntity extends AbstractArrow implements ItemSupplier {

    /** 弹射物渲染使用的物品（火焰弹） */
    public static final ItemStack PROJECTILE_ITEM = new ItemStack(Items.FIRE_CHARGE);

    /**
     * 构造一个骨翼火球弹射物实体
     *
     * @param type  实体类型
     * @param level 所在世界
     */
    public BoneWingFireBallProjectileEntity(EntityType<? extends BoneWingFireBallProjectileEntity> type, Level level) {
        super(type, level);
    }

    /**
     * 构造一个由指定发射者发射的骨翼火球弹射物
     * <p>1.21.1 中 AbstractArrow 的 LivingEntity 构造需要额外传入 {@link ItemStack} 参数。</p>
     *
     * @param type   实体类型
     * @param entity 发射者
     * @param level  所在世界
     */
    public BoneWingFireBallProjectileEntity(EntityType<? extends BoneWingFireBallProjectileEntity> type, LivingEntity entity, Level level) {
        super(type, entity, level, PROJECTILE_ITEM, PROJECTILE_ITEM);
    }

    @Override
    public ItemStack getItem() {
        return PROJECTILE_ITEM;
    }

    @Override
    protected ItemStack getPickupItem() {
        return PROJECTILE_ITEM;
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return PROJECTILE_ITEM;
    }

    @Override
    protected void doPostHurtEffects(LivingEntity entity) {
        super.doPostHurtEffects(entity);
        entity.setArrowCount(entity.getArrowCount() - 1);
    }

    @Override
    public void onHitEntity(EntityHitResult entityHitResult) {
        super.onHitEntity(entityHitResult);
        // 命中时播放龙息爆炸音效（替代原模组 MoltengoldWandPr3Procedure）
        if (!this.level().isClientSide()) {
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.DRAGON_FIREBALL_EXPLODE, SoundSource.NEUTRAL, 0.6f, 1.0f);
        } else {
            this.level().playLocalSound(this.getX(), this.getY(), this.getZ(),
                    SoundEvents.DRAGON_FIREBALL_EXPLODE, SoundSource.NEUTRAL, 0.6f, 1.0f, false);
        }
    }

    @Override
    public void tick() {
        super.tick();
        // 每 tick 生成火焰+烟雾粒子（等效原模组 BoneWingFireBallPr0Procedure）
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.FLAME, this.getX(), this.getY(), this.getZ(),
                    5, 0.12, 0.12, 0.12, 0.01);
            serverLevel.sendParticles(ParticleTypes.SMOKE, this.getX(), this.getY(), this.getZ(),
                    8, 0.15, 0.15, 0.15, 0.02);
        }
        // 落地时自动销毁
        if (this.inGround) {
            this.discard();
        }
    }

    /**
     * 发射骨翼火球（默认参数）
     *
     * @param level  世界
     * @param entity 发射者
     * @param random 随机源
     * @return 发射的弹射物实体
     */
    public static BoneWingFireBallProjectileEntity shoot(Level level, LivingEntity entity, RandomSource random) {
        return shoot(level, entity, random, 0.8f, 9.0, 1);
    }

    /**
     * 发射骨翼火球（自定义参数射击）
     * <p>注意：1.21.1 中 AbstractArrow 的 {@code setKnockback} 方法已被移除，此方法不再设置击退值。</p>
     *
     * @param level     世界
     * @param entity    发射者
     * @param random    随机源
     * @param power     速度倍率
     * @param damage    基础伤害
     * @param knockback 击退等级（保留参数但不再设置，1.21.1 中 AbstractArrow 不含此 setter）
     * @return 发射的弹射物实体
     */
    public static BoneWingFireBallProjectileEntity shoot(Level level, LivingEntity entity, RandomSource random,
                                                          float power, double damage, int knockback) {
        var entityType = PDEntities.BONE_WING_FIRE_BALL_PROJECTILE.get();
        BoneWingFireBallProjectileEntity entityarrow = new BoneWingFireBallProjectileEntity(entityType, entity, level);
        entityarrow.shoot(entity.getViewVector(1).x, entity.getViewVector(1).y, entity.getViewVector(1).z,
                power * 2, 0);
        entityarrow.setSilent(true);
        entityarrow.setCritArrow(false);
        entityarrow.setBaseDamage(damage);
        entityarrow.setRemainingFireTicks(100 * 20);
        level.addFreshEntity(entityarrow);
        level.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                PDSounds.BONE_WING_FIRE_BALL.get(), SoundSource.PLAYERS, 1,
                1f / (random.nextFloat() * 0.5f + 1) + (power / 2));
        return entityarrow;
    }

    /**
     * 发射骨翼火球（追踪目标）
     *
     * @param entity 发射者
     * @param target 目标实体
     * @return 发射的弹射物实体
     */
    public static BoneWingFireBallProjectileEntity shoot(LivingEntity entity, LivingEntity target) {
        var entityType = PDEntities.BONE_WING_FIRE_BALL_PROJECTILE.get();
        BoneWingFireBallProjectileEntity entityarrow = new BoneWingFireBallProjectileEntity(entityType, entity, entity.level());
        double dx = target.getX() - entity.getX();
        double dy = target.getY() + target.getEyeHeight() - 1.1;
        double dz = target.getZ() - entity.getZ();
        entityarrow.shoot(dx, dy - entityarrow.getY() + Math.hypot(dx, dz) * 0.2F, dz, 0.8f * 2, 12.0F);
        entityarrow.setSilent(true);
        entityarrow.setBaseDamage(9);
        entityarrow.setCritArrow(false);
        entityarrow.setRemainingFireTicks(100 * 20);
        entity.level().addFreshEntity(entityarrow);
        entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                PDSounds.BONE_WING_FIRE_BALL.get(), SoundSource.PLAYERS, 1,
                1f / (RandomSource.create().nextFloat() * 0.5f + 1));
        return entityarrow;
    }
}