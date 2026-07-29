package com.pasterdream.pasterdreammod.entity.projectile;

import com.pasterdream.pasterdreammod.api.entity.projectile.AbstractWandProjectileEntity;
import com.pasterdream.pasterdreammod.registry.PDAttributes;
import com.pasterdream.pasterdreammod.registry.PDEntities;
import com.pasterdream.pasterdreammod.registry.PDItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

/**
 * 『亚勒兹』唤星法球 (truest_moltengold_wand_projectile)
 * <p>
 * 还原自原版 TruestMoltengoldWandProjectileEntity：
 * <ul>
 *   <li>飞行拖尾：熔岩粒子（MoltengoldWandPr2Procedure）</li>
 *   <li>命中方块：唤星冲击（TruestMoltengoldWandPr0Procedure，裂隙概率 50%）</li>
 *   <li>命中实体：唤星冲击 + 法术强度 ≥1 时向目标四周散射 2+法术强度 个炙焰法球
 *       （伤害 5，TruestMoltengoldWandPr1Procedure）</li>
 *   <li>默认弹道参数：动能 1.7、伤害 2.7，发射音效 block.fire.extinguish</li>
 * </ul>
 */
public class TruestMoltengoldWandProjectileEntity extends AbstractWandProjectileEntity {

    /** 渲染物品缓存（魔法石） */
    private ItemStack cachedItem = ItemStack.EMPTY;

    public TruestMoltengoldWandProjectileEntity(EntityType<? extends TruestMoltengoldWandProjectileEntity> type, Level level) {
        super(type, level);
    }

    @Override
    protected ItemStack projectileItem() {
        if (cachedItem == null || cachedItem.isEmpty()) {
            cachedItem = new ItemStack(PDItems.MAGIC_STONE.get());
        }
        return cachedItem;
    }

    @Override
    protected void onTickEffect() {
        this.level().addParticle(ParticleTypes.LAVA, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
    }

    @Override
    protected void onHitEntity(EntityHitResult hitResult) {
        super.onHitEntity(hitResult);
        // 原版 TruestMoltengoldWandPr1Procedure：唤星冲击（50% 裂隙）+ 法术强度散射
        TrueMoltengoldWandProjectileEntity.starcallImpact(
                this.level(), this.getX(), this.getY(), this.getZ(), 0.5);
        Entity target = hitResult.getEntity();
        if (this.getOwner() instanceof Player owner
                && this.level() instanceof ServerLevel serverLevel) {
            AttributeInstance magicPower = owner.getAttribute(PDAttributes.MAGICPOWER);
            if (magicPower != null && magicPower.getBaseValue() >= 1) {
                int count = (int) (2 + magicPower.getBaseValue());
                for (int i = 0; i < count; i++) {
                    // 原版：以命中目标为中心向四周散射炙焰法球（伤害 5、静音）
                    MoltengoldWandProjectileEntity spray = new MoltengoldWandProjectileEntity(
                            PDEntities.MOLTENGOLD_WAND_PROJECTILE.get(), serverLevel);
                    spray.setBaseDamage(5);
                    spray.setSilent(true);
                    spray.setPos(target.getX(), target.getY() + 0.5, target.getZ());
                    spray.shoot(Mth.nextDouble(RandomSource.create(), -1, 1), 1,
                            Mth.nextDouble(RandomSource.create(), -1, 1), 0.5f, 0.5f);
                    serverLevel.addFreshEntity(spray);
                }
            }
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult hitResult) {
        super.onHitBlock(hitResult);
        BlockPos pos = hitResult.getBlockPos();
        // 原版 TruestMoltengoldWandPr0Procedure：唤星冲击（50% 裂隙）
        TrueMoltengoldWandProjectileEntity.starcallImpact(
                this.level(), pos.getX(), pos.getY(), pos.getZ(), 0.5);
    }

    /**
     * 沿视线发射（原版默认参数：动能 1.7、伤害 2.7、击退 1）
     */
    public static TruestMoltengoldWandProjectileEntity shoot(Level level, LivingEntity shooter, RandomSource random) {
        TruestMoltengoldWandProjectileEntity projectile =
                new TruestMoltengoldWandProjectileEntity(PDEntities.TRUEST_MOLTENGOLD_WAND_PROJECTILE.get(), level);
        configureShot(projectile, level, shooter, random, 1.7f, 2.7, SoundEvents.FIRE_EXTINGUISH);
        return projectile;
    }
}
