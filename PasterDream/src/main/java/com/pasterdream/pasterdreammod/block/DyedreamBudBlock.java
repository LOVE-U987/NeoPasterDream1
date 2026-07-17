package com.pasterdream.pasterdreammod.block;

import com.pasterdream.pasterdreammod.registry.PDBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

/**
 * 染梦花蕾方块 —— 拥有 FACING（朝向）和 WATERLOGGED（含水）属性。
 * 可附着在六个面上，支持水下放置，并能在方解石上自然生长。
 * 适用于染梦系列植物的花蕾阶段，有三种大小变体（0/1/2）。
 *
 * @author PasterDream
 */
public class DyedreamBudBlock extends Block implements SimpleWaterloggedBlock {
    public static final DirectionProperty FACING = DirectionalBlock.FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private static final int MAX_GROWTH_CHAIN = 4;
    private final int budSize;

    /**
     * @param props   方块属性
     * @param budSize 花蕾大小，用于区分不同生长阶段的碰撞箱
     */
    public DyedreamBudBlock(Properties props, int budSize) {
        super(props.randomTicks());
        this.budSize = budSize;
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.UP)
                .setValue(WATERLOGGED, false));
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
        return state.getFluidState().isEmpty();
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return 0;
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(FACING);
        return switch (budSize) {
            case 0 -> switch (facing) {
                case DOWN -> box(3, 12, 3, 13, 16, 13);
                case UP -> box(3, 0, 3, 13, 4, 13);
                case NORTH -> box(3, 3, 12, 13, 13, 16);
                case SOUTH -> box(3, 3, 0, 13, 13, 4);
                case WEST -> box(12, 3, 3, 16, 13, 13);
                case EAST -> box(0, 3, 3, 4, 13, 13);
            };
            case 1 -> switch (facing) {
                case DOWN -> box(4, 13, 4, 12, 16, 12);
                case UP -> box(4, 0, 4, 12, 3, 12);
                case NORTH -> box(4, 4, 13, 12, 12, 16);
                case SOUTH -> box(4, 4, 0, 12, 12, 3);
                case WEST -> box(13, 4, 4, 16, 12, 12);
                case EAST -> box(0, 4, 4, 3, 12, 12);
            };
            case 2 -> switch (facing) {
                case DOWN -> box(5, 14, 5, 11, 16, 11);
                case UP -> box(5, 0, 5, 11, 2, 11);
                case NORTH -> box(5, 5, 14, 11, 11, 16);
                case SOUTH -> box(5, 5, 0, 11, 11, 2);
                case WEST -> box(14, 5, 5, 16, 11, 11);
                case EAST -> box(0, 5, 5, 2, 11, 11);
            };
            default -> box(3, 0, 3, 13, 4, 13);
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        boolean flag = context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER;
        return this.defaultBlockState()
                .setValue(FACING, context.getClickedFace())
                .setValue(WATERLOGGED, flag);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world,
                                  BlockPos currentPos, BlockPos facingPos) {
        if (state.getValue(WATERLOGGED)) {
            world.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        }
        return !state.canSurvive(world, currentPos) ? Blocks.AIR.defaultBlockState()
                : super.updateShape(state, facing, facingState, world, currentPos, facingPos);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction facing = state.getValue(FACING);
        BlockPos attachPos = pos.relative(facing.getOpposite());
        BlockState attachState = level.getBlockState(attachPos);
        return attachState.isFaceSturdy(level, attachPos, facing);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!level.isAreaLoaded(pos, 1)) return;

        if (random.nextInt(40) != 0) return;

        Direction facing = state.getValue(FACING);
        BlockPos attachPos = pos.relative(facing.getOpposite());
        BlockState attachState = level.getBlockState(attachPos);

        if (!isCalciteOrPolishedCalcite(attachState)) return;

        if (getGrowthChainLength(level, pos, facing) >= MAX_GROWTH_CHAIN) return;

        BlockPos growPos = pos.relative(facing);
        if (!level.isEmptyBlock(growPos)) return;

        level.setBlockAndUpdate(growPos, this.defaultBlockState()
                .setValue(FACING, facing)
                .setValue(WATERLOGGED, level.getFluidState(growPos).getType() == Fluids.WATER));
    }

    private int getGrowthChainLength(LevelReader level, BlockPos startPos, Direction facing) {
        int length = 0;
        BlockPos currentPos = startPos;
        while (level.getBlockState(currentPos).is(this)) {
            length++;
            currentPos = currentPos.relative(facing);
        }
        return length;
    }

    private boolean isCalciteOrPolishedCalcite(BlockState state) {
        return state.is(Blocks.CALCITE);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        return List.of(new ItemStack(this));
    }
}