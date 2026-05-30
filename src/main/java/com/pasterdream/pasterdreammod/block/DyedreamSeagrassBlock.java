package com.pasterdream.pasterdreammod.block;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.storage.loot.LootParams;
import java.util.List;

/**
 * 染梦海草方块——水下植物，只允许放置于水中。
 * 继承 BushBlock 获得植物类摆放行为，同时实现 SimpleWaterloggedBlock 支持 Waterlogged。
 */
public class DyedreamSeagrassBlock extends BushBlock implements SimpleWaterloggedBlock {
    public static final MapCodec<DyedreamSeagrassBlock> CODEC = simpleCodec(properties -> new DyedreamSeagrassBlock());
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    protected static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);

    public DyedreamSeagrassBlock() {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.PLANT)
                .noCollission()
                .noOcclusion()
                .instabreak()
                .sound(SoundType.GRASS)
                .pushReaction(net.minecraft.world.level.material.PushReaction.DESTROY));
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, false));
    }

    @Override
    protected MapCodec<? extends BushBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluid = context.getLevel().getFluidState(context.getClickedPos());
        boolean isWater = fluid.getType() == Fluids.WATER;
        return this.defaultBlockState().setValue(WATERLOGGED, isWater);
    }

    @Override
    protected boolean mayPlaceOn(BlockState groundState, BlockGetter level, BlockPos pos) {
        return groundState.is(Blocks.SAND)
            || groundState.is(Blocks.GRAVEL)
            || groundState.is(Blocks.DIRT)
            || groundState.is(Blocks.CLAY)
            || groundState.is(net.minecraft.world.level.block.Blocks.MUD)
            || groundState.is(Blocks.WATER)
            || groundState.getFluidState().is(Fluids.WATER);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        boolean isWaterlogged = state.getValue(WATERLOGGED);
        if (!isWaterlogged) {
            return false;
        }
        BlockPos below = pos.below();
        BlockState groundState = level.getBlockState(below);
        return this.mayPlaceOn(groundState, level, below);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos pos, BlockPos facingPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return super.updateShape(state, facing, facingState, level, pos, facingPos);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        return List.of(new ItemStack(this));
    }
}