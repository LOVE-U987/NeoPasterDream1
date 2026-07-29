package com.pasterdream.pasterdreammod.block;

import com.mojang.serialization.MapCodec;
import com.pasterdream.pasterdreammod.api.block.HorizontalWaterloggedBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

/**
 * 陶罐 (clay_pot_0)
 * <p>
 * 阴影维度地表装饰容器方块，水平朝向 + 可含水。
 * 破坏后按战利品表随机掉落杂物（丝绸之触则掉落自身）。
 * 形状为中央 6x11x6 的罐体（还原自原版 ClayPot0Block）。
 */
public class ClayPot0Block extends HorizontalWaterloggedBlock {

    /** 方块 MapCodec（1.21 数据驱动序列化要求） */
    public static final MapCodec<ClayPot0Block> CODEC = simpleCodec(ClayPot0Block::new);

    /** 罐体碰撞形状：四个水平朝向完全一致 */
    private static final VoxelShape SHAPE = box(5, 0, 5, 11, 11, 11);

    /**
     * 构造陶罐
     *
     * @param properties 方块属性
     */
    public ClayPot0Block(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level,
                                        @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE;
    }
}
