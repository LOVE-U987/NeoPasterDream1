package com.pasterdream.pasterdreammod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

/**
 * 染梦灯笼方块。
 * <p>悬挂式灯笼：可放置于方块上方或悬挂于方块下方（hanging 状态），
 * 支持含水（waterlogged）。发出 15 级光照的粉色水晶质感装饰方块，
 * 实现参考原版 {@link net.minecraft.world.level.block.LanternBlock}。</p>
 */
public class DyedreamLanternBlock extends Block implements SimpleWaterloggedBlock {

    /** 悬挂状态属性 */
    public static final BooleanProperty HANGING = BlockStateProperties.HANGING;
    /** 含水状态属性 */
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    /** 放置于方块上方时的碰撞箱（灯笼本体 + 顶部提手） */
    protected static final VoxelShape AABB = Shapes.or(
            Block.box(5.0, 0.0, 5.0, 11.0, 7.0, 11.0),
            Block.box(6.0, 7.0, 6.0, 10.0, 9.0, 10.0));
    /** 悬挂于方块下方时的碰撞箱 */
    protected static final VoxelShape HANGING_AABB = Shapes.or(
            Block.box(5.0, 1.0, 5.0, 11.0, 8.0, 11.0),
            Block.box(6.0, 8.0, 6.0, 10.0, 10.0, 10.0));

    /**
     * 创建染梦灯笼方块实例。
     *
     * @param properties 方块行为属性
     */
    public DyedreamLanternBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(HANGING, false).setValue(WATERLOGGED, false));
    }

    /**
     * 获取染梦灯笼的默认方块属性。
     * <p>玻璃音效、硬度 0.3、15 级光照、无遮挡、自发光、非红石导体。</p>
     *
     * @return 配置好的方块属性
     */
    public static BlockBehaviour.Properties lanternProps() {
        return BlockBehaviour.Properties.of()
                .instrument(NoteBlockInstrument.HAT)
                .sound(SoundType.GLASS)
                .strength(0.3F)
                .lightLevel(s -> 15)
                .noOcclusion()
                .hasPostProcess((bs, br, bp) -> true)
                .emissiveRendering((bs, br, bp) -> true)
                .isRedstoneConductor((bs, br, bp) -> false);
    }

    /**
     * 根据放置位置与朝向决定灯笼是悬挂还是放置。
     *
     * @param context 放置上下文
     * @return 生成的方块状态；若无法放置则返回 null
     */
    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
        for (Direction direction : context.getNearestLookingDirections()) {
            if (direction.getAxis() == Direction.Axis.Y) {
                BlockState blockstate = this.defaultBlockState().setValue(HANGING, direction == Direction.UP);
                if (blockstate.canSurvive(context.getLevel(), context.getClickedPos())) {
                    return blockstate.setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
                }
            }
        }
        return null;
    }

    /**
     * 根据悬挂状态返回对应碰撞箱。
     *
     * @param state 当前方块状态
     * @param level 世界读取接口
     * @param pos 方块位置
     * @param context 碰撞上下文
     * @return 悬挂时用悬挂碰撞箱，否则用普通碰撞箱
     */
    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(HANGING) ? HANGING_AABB : AABB;
    }

    /**
     * 注册方块状态属性。
     *
     * @param builder 状态定义构建器
     */
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HANGING, WATERLOGGED);
    }

    /**
     * 判断灯笼能否在当前位置存活（需要悬挂点或支撑面）。
     *
     * @param state 当前方块状态
     * @param level 世界读取接口
     * @param pos 方块位置
     * @return 能否存活
     */
    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction direction = getConnectedDirection(state).getOpposite();
        return Block.canSupportCenter(level, pos.relative(direction), direction.getOpposite());
    }

    /**
     * 获取灯笼的连接方向。
     *
     * @param state 当前方块状态
     * @return 悬挂时为下方，否则为上方
     */
    protected static Direction getConnectedDirection(BlockState state) {
        return state.getValue(HANGING) ? Direction.DOWN : Direction.UP;
    }

    /**
     * 邻居方块变化时更新状态；悬挂点被破坏则灯笼掉落为空气。
     *
     * @param state 当前方块状态
     * @param direction 变化方向
     * @param neighborState 邻居方块状态
     * @param level 世界写入接口
     * @param pos 方块位置
     * @param neighborPos 邻居位置
     * @return 更新后的方块状态
     */
    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                     LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return getConnectedDirection(state).getOpposite() == direction && !state.canSurvive(level, pos)
                ? Blocks.AIR.defaultBlockState()
                : super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    /**
     * 获取方块状态的流体状态（含水时返回水源）。
     *
     * @param state 当前方块状态
     * @return 流体状态
     */
    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    /**
     * 灯笼不可作为寻路通行方块。
     *
     * @param state 当前方块状态
     * @param pathComputationType 寻路类型
     * @return 恒为 false
     */
    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }
}
