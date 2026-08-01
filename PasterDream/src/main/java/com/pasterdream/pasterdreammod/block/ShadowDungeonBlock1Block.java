package com.pasterdream.pasterdreammod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;

/**
 * 暗影地牢砖 1 — 带顶底花纹砖（shadow_dungeon_block_1）
 * <p>
 * 带 {@code axis} 轴向状态：按放置点击面确定朝向，可被 90° 旋转
 * 工具切换 x/z 轴向；完全挡光（getLightBlock 15）。
 */
public class ShadowDungeonBlock1Block extends Block {

    /** 轴向状态属性 */
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.AXIS;

    /**
     * 构造暗影地牢砖 1
     *
     * @param properties 方块属性
     */
    public ShadowDungeonBlock1Block(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(AXIS, Direction.Axis.Y));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AXIS);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(AXIS, context.getClickedFace().getAxis());
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rot) {
        if (rot == Rotation.CLOCKWISE_90 || rot == Rotation.COUNTERCLOCKWISE_90) {
            if (state.getValue(AXIS) == Direction.Axis.X) {
                return state.setValue(AXIS, Direction.Axis.Z);
            } else if (state.getValue(AXIS) == Direction.Axis.Z) {
                return state.setValue(AXIS, Direction.Axis.X);
            }
        }
        return state;
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return 15;
    }
}
