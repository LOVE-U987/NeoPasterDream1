package com.pasterdream.pasterdreammod.entity.projectile;

import com.pasterdream.pasterdreammod.api.entity.projectile.AbstractWandProjectileEntity;
import com.pasterdream.pasterdreammod.registry.PDEntities;
import com.pasterdream.pasterdreammod.registry.PDItems;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

/**
 * 粉红蛋投掷物 (pinkegg_projectile)
 * <p>
 * 还原自原版 PinkeggProjectileEntity：
 * <ul>
 *   <li>命中方块（PinkeggPr1Procedure）：1/5 概率在落点上方孵出粉色鸡（随机朝向）</li>
 *   <li>默认弹道参数：动能 0.65、伤害 0，发射音效 entity.egg.throw</li>
 * </ul>
 */
public class PinkeggProjectileEntity extends AbstractWandProjectileEntity {

    /** 渲染物品缓存（粉红蛋自身） */
    private ItemStack cachedItem = ItemStack.EMPTY;

    public PinkeggProjectileEntity(EntityType<? extends PinkeggProjectileEntity> type, Level level) {
        super(type, level);
    }

    @Override
    protected ItemStack projectileItem() {
        if (cachedItem == null || cachedItem.isEmpty()) {
            cachedItem = new ItemStack(PDItems.PINKEGG.get());
        }
        return cachedItem;
    }

    @Override
    protected void onHitBlock(BlockHitResult hitResult) {
        super.onHitBlock(hitResult);
        // 原版 PinkeggPr1Procedure：1/5 概率孵出粉色鸡
        BlockPos pos = hitResult.getBlockPos();
        if (Mth.nextInt(RandomSource.create(), 1, 5) == 1
                && this.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            Entity chicken = PDEntities.PINK_CHICKEN.get().spawn(serverLevel,
                    BlockPos.containing(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5),
                    MobSpawnType.MOB_SUMMONED);
            if (chicken != null) {
                chicken.setYRot(serverLevel.getRandom().nextFloat() * 360F);
            }
        }
    }

    /**
     * 沿视线投掷（原版默认参数：动能 0.65、伤害 0、击退 1）
     */
    public static PinkeggProjectileEntity shoot(Level level, LivingEntity shooter, RandomSource random) {
        PinkeggProjectileEntity projectile =
                new PinkeggProjectileEntity(PDEntities.PINKEGG_PROJECTILE.get(), level);
        configureShot(projectile, level, shooter, random, 0.65f, 0, SoundEvents.EGG_THROW);
        return projectile;
    }
}
