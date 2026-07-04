package com.pasterdream.pasterdreammod.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 暗影书架方块 (Shadowshelf Block)
 * 支持水平朝向放置，可被岩浆点燃
 * 用于暗影地牢场景装饰
 */
public class ShadowshelfBlock extends HorizontalDirectionalBlock {

    public static final MapCodec<ShadowshelfBlock> CODEC = simpleCodec(ShadowshelfBlock::new);

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    /** 水平朝向属性 */
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    /**
     * 暗影书架碰撞箱（全方块）
     */
    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 16, 16);

    /**
     * 构造暗影书架方块
     * @param properties 方块属性
     */
    public ShadowshelfBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    /**
     * 创建方块状态定义，注册 FACING 属性
     * @param builder 状态构建器
     */
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    /**
     * 获取放置时的方块状态（朝向玩家反向）
     * @param context 放置上下文
     * @return 方块状态
     */
    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    /**
     * 旋转方块状态
     * @param state 当前状态
     * @param rot 旋转方式
     * @return 旋转后的状态
     */
    @Override
    public @NotNull BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    /**
     * 镜像方块状态
     * @param state 当前状态
     * @param mirrorIn 镜像方式
     * @return 镜像后的状态
     */
    @Override
    public @NotNull BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
    }

    /**
     * 获取碰撞箱形状
     * @param state 方块状态
     * @param level 世界读取器
     * @param pos 方块位置
     * @param context 碰撞上下文
     * @return 碰撞箱形状
     */
    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level,
                                         @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE;
    }
}
