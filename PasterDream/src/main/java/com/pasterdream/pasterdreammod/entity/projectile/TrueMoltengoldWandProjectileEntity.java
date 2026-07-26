package com.pasterdream.pasterdreammod.entity.projectile;

import com.pasterdream.pasterdreammod.registry.PDBlocks;
import com.pasterdream.pasterdreammod.registry.PDEntities;
import com.pasterdream.pasterdreammod.registry.PDItems;
import com.pasterdream.pasterdreammod.registry.PDSounds;
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
 * 唤星者法杖法球 (true_moltengold_wand_projectile)
 * <p>
 * 还原自原版 TrueMoltengoldWandProjectileEntity：
 * <ul>
 *   <li>飞行拖尾：熔岩粒子（MoltengoldWandPr2Procedure）</li>
 *   <li>命中实体/方块：唤星冲击（TrueMoltengoldWandPr0Procedure）——
 *       龙息爆炸音效、落点上方点火、y+2 处放置唤星照明（starcall_block）、
 *       露天时 20% 概率在 y+11 处放置唤星裂隙（starcall_crack）并播放裂隙音效</li>
 *   <li>默认弹道参数：动能 1.6、伤害 2，发射音效 block.fire.extinguish</li>
 * </ul>
 */
public class TrueMoltengoldWandProjectileEntity extends AbstractWandProjectileEntity {

    /** 渲染物品缓存（魔法石） */
    private ItemStack cachedItem = ItemStack.EMPTY;

    public TrueMoltengoldWandProjectileEntity(EntityType<? extends TrueMoltengoldWandProjectileEntity> type, Level level) {
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
        // 原版对实体命中以投射物当前坐标为效果中心
        starcallImpact(this.level(), this.getX(), this.getY(), this.getZ(), 0.2);
    }

    @Override
    protected void onHitBlock(BlockHitResult hitResult) {
        super.onHitBlock(hitResult);
        BlockPos pos = hitResult.getBlockPos();
        starcallImpact(this.level(), pos.getX(), pos.getY(), pos.getZ(), 0.2);
    }

    /**
     * 唤星冲击（原版 TrueMoltengoldWandPr0 / TruestMoltengoldWandPr0 共通逻辑）
     * <ol>
     *   <li>龙息爆炸音效（0.5）</li>
     *   <li>落点上方（y+1）点火：仅当落点非空气且上方为空气</li>
     *   <li>y+2 为空气时放置唤星照明方块（starcall_block）</li>
     *   <li>y+2 露天且随机数 ≤ crackChance 时，在 y+11 放置唤星裂隙（starcall_crack）
     *       并在 y+1 播放 starcall_crack 音效（1.5）</li>
     * </ol>
     *
     * @param level       世界
     * @param x           效果中心 X
     * @param y           效果中心 Y
     * @param z           效果中心 Z
     * @param crackChance 唤星裂隙概率（唤星者 0.2 / 『亚勒兹』 0.5）
     */
    static void starcallImpact(Level level, double x, double y, double z, double crackChance) {
        BlockPos base = BlockPos.containing(x, y, z);
        if (!level.isClientSide()) {
            level.playSound(null, base, SoundEvents.DRAGON_FIREBALL_EXPLODE, SoundSource.NEUTRAL, 0.5f, 1.0f);
        }
        BlockPos above1 = BlockPos.containing(x, y + 1, z);
        if (level.getBlockState(above1).getBlock() == Blocks.AIR
                && level.getBlockState(base).getBlock() != Blocks.AIR) {
            level.setBlock(above1, Blocks.FIRE.defaultBlockState(), 3);
        }
        BlockPos above2 = BlockPos.containing(x, y + 2, z);
        if (level.getBlockState(above2).getBlock() == Blocks.AIR) {
            level.setBlock(above2, PDBlocks.STARCALL_BLOCK.get().defaultBlockState(), 3);
        }
        if (level.canSeeSkyFromBelowWater(above2) && Math.random() <= crackChance) {
            level.setBlock(BlockPos.containing(x, y + 11, z),
                    PDBlocks.STARCALL_CRACK.get().defaultBlockState(), 3);
            if (!level.isClientSide()) {
                level.playSound(null, above1, PDSounds.STARCALL_CRACK.get(),
                        SoundSource.NEUTRAL, 1.5f, 1.0f);
            }
        }
    }

    /**
     * 沿视线发射（原版默认参数：动能 1.6、伤害 2、击退 1）
     */
    public static TrueMoltengoldWandProjectileEntity shoot(Level level, LivingEntity shooter, RandomSource random) {
        TrueMoltengoldWandProjectileEntity projectile =
                new TrueMoltengoldWandProjectileEntity(PDEntities.TRUE_MOLTENGOLD_WAND_PROJECTILE.get(), level);
        configureShot(projectile, level, shooter, random, 1.6f, 2, SoundEvents.FIRE_EXTINGUISH);
        return projectile;
    }
}
