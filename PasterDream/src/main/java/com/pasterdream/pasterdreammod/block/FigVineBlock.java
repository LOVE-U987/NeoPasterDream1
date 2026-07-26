package com.pasterdream.pasterdreammod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * 无花果藤 (fig_vine)
 * <p>
 * 风泊树冠层藤蔓植被，无碰撞体积，可燃（燃烧度 20），
 * 破坏后按战利品表掉落无花果（fig）。
 * 形状为顶部悬挂的 16x15x16 藤幕（还原自原版 FigVineBlock）。
 * <p>
 * 上方变空气时 neighborChanged 自毁（原版 WindmoorLeavesPr0）。
 */
public class FigVineBlock extends Block {

    /** 藤幕形状：自 y=1 至 y=16 的整面 */
    private static final VoxelShape SHAPE = box(0, 1, 0, 16, 16, 16);

    /**
     * 构造无花果藤
     *
     * @param properties 方块属性
     */
    public FigVineBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    /**
     * 视觉形状为空（不遮挡视线，与原版一致）
     */
    @Override
    protected VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return true;
    }

    /**
     * 可燃度 20（与原版一致）
     */
    @Override
    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 20;
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);
        if (level.getBlockState(pos.above()).isAir()) {
            level.removeBlock(pos, false);
        }
    }
}
