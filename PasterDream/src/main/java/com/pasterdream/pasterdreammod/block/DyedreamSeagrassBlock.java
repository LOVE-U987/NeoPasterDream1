package com.pasterdream.pasterdreammod.block;

import com.mojang.serialization.MapCodec;
import com.pasterdream.pasterdreammod.registry.PDBlocks;

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
 * 染梦海草方块——水下植物，仅允许放置于水底的固体方块上。
 * 继承 BushBlock 获得植物类摆放行为，同时实现 SimpleWaterloggedBlock 支持 Waterlogged。
 * 关键约束：
 * - 必须 WATERLOGGED=true（被水完全浸泡）
 * - 下方必须是固体方块（沙、土、黏土等），不能悬浮在水中
 * - 不检查上方是否有水，以兼容浅水区自然生成
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
        // 海草必须种植在固体方块上（原版 + 染梦维度方块），不允许悬浮在水中
        return groundState.is(Blocks.SAND)
            || groundState.is(Blocks.GRAVEL)
            || groundState.is(Blocks.DIRT)
            || groundState.is(Blocks.CLAY)
            || groundState.is(net.minecraft.world.level.block.Blocks.MUD)
            || groundState.is(PDBlocks.DYEDREAM_SAND.get())
            || groundState.is(PDBlocks.DYEDREAM_DIRT.get())
            || groundState.is(PDBlocks.DYEDREAM_BLOCK.get());
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        // 必须被水浸泡
        if (!state.getValue(WATERLOGGED)) {
            return false;
        }
        BlockPos below = pos.below();
        BlockState groundState = level.getBlockState(below);
        // 下方必须是固体方块（不能是水），防止悬浮
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