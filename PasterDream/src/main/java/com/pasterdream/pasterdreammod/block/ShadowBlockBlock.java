package com.pasterdream.pasterdreammod.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * 阴影方块（shadow_block）
 * <p>
 * 类似沙子的下落方块：失去支撑时下落形成 FallingBlockEntity；
 * 相邻同种方块跳过相邻面渲染（连成一体），光照遮挡等级 10。
 */
public class ShadowBlockBlock extends FallingBlock {

    /** 方块 MapCodec（1.21 数据驱动序列化要求） */
    public static final MapCodec<ShadowBlockBlock> CODEC = simpleCodec(ShadowBlockBlock::new);

    /**
     * 构造阴影方块
     *
     * @param properties 方块属性
     */
    public ShadowBlockBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends FallingBlock> codec() {
        return CODEC;
    }

    @Override
    public boolean skipRendering(BlockState state, BlockState adjacentBlockState, Direction side) {
        return adjacentBlockState.getBlock() == this || super.skipRendering(state, adjacentBlockState, side);
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return 10;
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }
}
