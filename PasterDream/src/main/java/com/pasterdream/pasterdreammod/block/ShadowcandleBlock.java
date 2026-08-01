package com.pasterdream.pasterdreammod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * 暗影蜡烛（shadowcandle）
 * <p>
 * 细长蜡烛造型：碰撞箱 6/6 ~ 10/10，高 8 格像素（半格），
 * 光线可穿透、视觉形状为空（不遮挡光照）。
 */
public class ShadowcandleBlock extends Block {

    /** 蜡烛碰撞箱：x/z 0.375~0.625，y 0~0.5 */
    private static final VoxelShape CANDLE_SHAPE = box(6, 0, 6, 10, 8, 10);

    /**
     * 构造暗影蜡烛方块
     *
     * @param properties 方块属性
     */
    public ShadowcandleBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return CANDLE_SHAPE;
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
