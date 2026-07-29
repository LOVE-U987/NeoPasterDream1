package com.pasterdream.pasterdreammod.entity.projectile;

import com.pasterdream.pasterdreammod.api.entity.projectile.AbstractWandProjectileEntity;
import com.pasterdream.pasterdreammod.registry.PDEntities;
import com.pasterdream.pasterdreammod.registry.PDItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

/**
 * 炙焰金杖法球 (moltengold_wand_projectile)
 * <p>
 * 还原自原版 MoltengoldWandProjectileEntity：
 * <ul>
 *   <li>飞行拖尾：熔岩粒子（MoltengoldWandPr2Procedure）</li>
 *   <li>命中实体：龙息爆炸音效 0.6（MoltengoldWandPr3Procedure）</li>
 *   <li>命中方块：龙息爆炸音效 0.5，并在落点上方点燃火焰（MoltengoldWandPr0Procedure）</li>
 *   <li>默认弹道参数：动能 1.5、伤害 1.5，发射音效 block.fire.extinguish</li>
 * </ul>
 */
public class MoltengoldWandProjectileEntity extends AbstractWandProjectileEntity {

    /** 渲染物品缓存（魔法石） */
    private ItemStack cachedItem = ItemStack.EMPTY;

    public MoltengoldWandProjectileEntity(EntityType<? extends MoltengoldWandProjectileEntity> type, Level level) {
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
        // 原版 MoltengoldWandPr2Procedure：熔岩拖尾
        this.level().addParticle(ParticleTypes.LAVA, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
    }

    @Override
    protected void onHitEntity(EntityHitResult hitResult) {
        super.onHitEntity(hitResult);
        // 原版 MoltengoldWandPr3Procedure：命中实体播放龙息爆炸音效（0.6）
        if (!this.level().isClientSide()) {
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.DRAGON_FIREBALL_EXPLODE, SoundSource.NEUTRAL, 0.6f, 1.0f);
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult hitResult) {
        super.onHitBlock(hitResult);
        // 原版 MoltengoldWandPr0Procedure：龙息爆炸音效（0.5）+ 落点上方点火
        BlockPos pos = hitResult.getBlockPos();
        igniteAbove(this.level(), pos);
    }

    /**
     * 命中方块的共通效果：龙息爆炸音效（0.5）并在方块上方点燃火焰
     * <p>
     * 与原版一致：仅当命中方块非空气且其上方为空气时放置火。
     *
     * @param level 世界
     * @param pos   命中方块坐标
     */
    static void igniteAbove(Level level, BlockPos pos) {
        if (!level.isClientSide()) {
            level.playSound(null, pos, SoundEvents.DRAGON_FIREBALL_EXPLODE, SoundSource.NEUTRAL, 0.5f, 1.0f);
        }
        if (level.getBlockState(pos.above()).getBlock() == Blocks.AIR
                && level.getBlockState(pos).getBlock() != Blocks.AIR) {
            level.setBlock(pos.above(), Blocks.FIRE.defaultBlockState(), 3);
        }
    }

    /**
     * 沿视线发射（原版默认参数：动能 1.5、伤害 1.5、击退 1）
     */
    public static MoltengoldWandProjectileEntity shoot(Level level, LivingEntity shooter, RandomSource random) {
        MoltengoldWandProjectileEntity projectile =
                new MoltengoldWandProjectileEntity(PDEntities.MOLTENGOLD_WAND_PROJECTILE.get(), level);
        configureShot(projectile, level, shooter, random, 1.5f, 1.5, SoundEvents.FIRE_EXTINGUISH);
        return projectile;
    }
}
