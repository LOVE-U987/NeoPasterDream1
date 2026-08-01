package com.pasterdream.pasterdreammod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * 干裂粘土层·薄盘基类（claypan_0 / claypan_2）
 * <p>
 * 扁平方盘造型：碰撞箱 0/0/0 ~ 16/16，高 3 格像素（3/16 格），
 * 光线可穿透、视觉形状为空（不遮挡光照）。
 */
public class ClaypanPlateBlock extends Block {

    /** 薄盘碰撞箱：x/z 满格，y 0~3/16 */
    private static final VoxelShape PAN_SHAPE = box(0, 0, 0, 16, 3, 16);

    /**
     * 构造薄盘方块
     *
     * @param properties 方块属性
     */
    public ClaypanPlateBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return PAN_SHAPE;
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return true;
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return 0;
    }
}
