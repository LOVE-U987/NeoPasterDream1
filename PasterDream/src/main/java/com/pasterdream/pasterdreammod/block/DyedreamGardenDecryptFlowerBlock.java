package com.pasterdream.pasterdreammod.block;

import com.pasterdream.pasterdreammod.api.util.ServerScheduler;
import com.pasterdream.pasterdreammod.registry.PDBlocks;
import com.pasterdream.pasterdreammod.registry.PDDimensions;
import com.pasterdream.pasterdreammod.registry.PDParticles;
import com.pasterdream.pasterdreammod.registry.PDSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.phys.BlockHitResult;

/**
 * 花园解密之花 (flower_11)
 * <p>
 * 原版 {@code Flower11Block} + {@code GardenDecryptionPr0Procedure} 的 1.21.1 移植：
 * 在<b>染梦世界</b>中，双格花下方 3 格处放有染梦书桌（{@code dyedream_desk}），
 * 且周围 2 格呈花阵布局（四角 {@code flower_13}/{@code crop_3a}/{@code flower_8}/{@code crop_2a}，
 * 四边 {@code grass_3}）时，右键任意半格均可触发解密：
 * 花阵销毁 → 裂纹/尘埃粒子 + 梦境音效 → 延迟 2 tick 后花 11 变花 12（迷梦冶梦莲），书桌保留。
 * <p>
 * 解密核心见 {@link #tryDecrypt(Level, BlockPos)}。
 */
public class DyedreamGardenDecryptFlowerBlock extends DyedreamDoublePlantBlock {

    /**
     * 构造花园解密之花
     *
     * @param properties 方块属性
     */
    public DyedreamGardenDecryptFlowerBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!level.isClientSide) {
            tryDecrypt(level, pos);
        }
        return ItemInteractionResult.SUCCESS;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hitResult) {
        if (!level.isClientSide) {
            tryDecrypt(level, pos);
        }
        return InteractionResult.SUCCESS;
    }

    /**
     * 花园解密核心逻辑（对应原版 {@code GardenDecryptionPr0Procedure}）。
     * <p>
     * 校验顺序与参数与原版逐项一致：
     * <ol>
     *   <li>必须在染梦世界维度，且归一化后的下半格下方 3 格为染梦书桌；</li>
     *   <li>周围 2 格 8 个位置为指定花阵（四角花 + 四边 {@code grass_3}）；</li>
     *   <li>立即产生裂纹粒子 ×32 + 尘埃粒子 ×128，并播放 {@code dream0}；</li>
     *   <li>1 tick 后销毁花阵；</li>
     *   <li>2 tick 后花 11 完整替换为花 12（双格），书桌保留（相对原版的有意差异）。</li>
     * </ol>
     *
     * @param level 世界（服务端）
     * @param pos   被点击的花 11 坐标（上下半格均可）
     * @return 是否成功触发解密
     */
    public static boolean tryDecrypt(Level level, BlockPos pos) {
        if (level.isClientSide) {
            return false;
        }
        // 点击上半格时归一化到下半格，保证书桌与花阵按同一 y 层校验
        BlockState clickedState = level.getBlockState(pos);
        if (clickedState.hasProperty(DoublePlantBlock.HALF)
                && clickedState.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.UPPER) {
            pos = pos.below();
        }
        final BlockPos plantPos = pos;
        // ① 染梦世界 + 下方 3 格为染梦书桌
        if (!PDDimensions.isDyedreamWorld(level)) {
            return false;
        }
        if (!level.getBlockState(pos.below(3)).is(PDBlocks.DYEDREAM_DESK.get())) {
            return false;
        }
        // ② 周围 8 格花阵校验（坐标与原版 GardenDecryptionPr0Procedure 一致）
        if (!isBlock(level, pos.offset(-2, 0, -2), PDBlocks.FLOWER_13.get())
                || !isBlock(level, pos.offset(2, 0, -2), PDBlocks.CROP_3A.get())
                || !isBlock(level, pos.offset(2, 0, 2), PDBlocks.FLOWER_8.get())
                || !isBlock(level, pos.offset(-2, 0, 2), PDBlocks.CROP_2A.get())
                || !isBlock(level, pos.offset(2, 0, 0), PDBlocks.GRASS_3.get())
                || !isBlock(level, pos.offset(-2, 0, 0), PDBlocks.GRASS_3.get())
                || !isBlock(level, pos.offset(0, 0, 2), PDBlocks.GRASS_3.get())
                || !isBlock(level, pos.offset(0, 0, -2), PDBlocks.GRASS_3.get())) {
            return false;
        }

        // ③ 延迟 1 tick 销毁 8 格花阵（原版 queueServerWork(1)）
        ServerScheduler.schedule(1, () -> {
            level.destroyBlock(plantPos.offset(2, 0, 2), false);
            level.destroyBlock(plantPos.offset(2, 0, -2), false);
            level.destroyBlock(plantPos.offset(-2, 0, -2), false);
            level.destroyBlock(plantPos.offset(-2, 0, 2), false);
            level.destroyBlock(plantPos.offset(2, 0, 0), false);
            level.destroyBlock(plantPos.offset(-2, 0, 0), false);
            level.destroyBlock(plantPos.offset(0, 0, 2), false);
            level.destroyBlock(plantPos.offset(0, 0, -2), false);
        });

        // 粒子：裂纹 ×32（高度 +1）+ 尘埃 ×128（大范围扩散）
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles((SimpleParticleType) PDParticles.CRACK_0_PARTICLE.particleType(),
                    pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, 32, 0.5, 1, 0.5, 0.05);
            serverLevel.sendParticles((SimpleParticleType) PDParticles.DUST_0_PARTICLE.particleType(),
                    pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, 128, 2, 2, 2, 0.1);
        }
        // 声音：梦境音效 dream0
        level.playSound(null, pos, PDSounds.DREAM0.get(), SoundSource.BLOCKS, 1.0F, 1.0F);

        // ④ 延迟 2 tick：花 11 → 花 12（完整双格替换），书桌保留（原版 queueServerWork(2)）
        ServerScheduler.schedule(2, () -> replaceWithFlower12(level, plantPos));
        return true;
    }

    /**
     * 将花 11 原位完整替换为花 12（迷梦冶梦莲）。
     * <p>
     * 双格植物必须成对替换：先由被点击半格归一化出下半格，再用
     * {@link DoublePlantBlock#placeAt} 同时写入上下两半。更新标志必须带
     * {@link Block#UPDATE_KNOWN_SHAPE}，跳过本次替换的形状更新；否则旧半格仍是花 11 时，
     * 原版 {@link DoublePlantBlock#updateShape} 会将其判为失效并清空，最终整株消失。
     *
     * @param level 世界（服务端）
     * @param pos   花 11 坐标（被点击的半格）
     */
    private static void replaceWithFlower12(Level level, BlockPos pos) {
        BlockState oldState = level.getBlockState(pos);
        DoubleBlockHalf half = oldState.getValue(DoublePlantBlock.HALF);
        BlockPos lowerPos = half == DoubleBlockHalf.LOWER ? pos : pos.below();
        DoublePlantBlock.placeAt(level, PDBlocks.FLOWER_12.get().defaultBlockState(), lowerPos,
                Block.UPDATE_ALL | Block.UPDATE_KNOWN_SHAPE);
    }

    /**
     * 判断指定位置是否为目标方块
     *
     * @param level 世界
     * @param pos   目标坐标
     * @param block 期望方块
     * @return 匹配返回 true
     */
    private static boolean isBlock(Level level, BlockPos pos, Block block) {
        return level.getBlockState(pos).is(block);
    }
}
