package com.pasterdream.pasterdreammod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

/**
 * 藤蔓方块 - 垂泪藤式向下生长逻辑
 *
 * 从上方悬挂向下生长，最大生长高度14格。
 * 可附着在冰块、方解石、泥土、草方块、染梦泥土等方块底面。
 */
public class Vine0Block extends Block {
    protected static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D);

    public Vine0Block(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    /**
     * 随机 tick：向下生长（类垂泪藤）
     * 检查下方是否为空，若为空且总长度不超过14，则在下方生成新的藤蔓
     */
    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (level.isEmptyBlock(pos.below())) {
            int height;
            for (height = 1; level.getBlockState(pos.above(height)).is(this); ++height);
            if (height < 14) {
                level.setBlockAndUpdate(pos.below(), this.defaultBlockState());
            }
        }
    }

    /**
     * 判断藤蔓能否存活
     * 上方必须是同种藤蔓或任意完整固体方块底面（无限制附着）
     */
    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockState aboveState = level.getBlockState(pos.above());
        // 同种藤蔓 或 上方完整固体方块的面
        return aboveState.is(this) || aboveState.isFaceSturdy(level, pos.above(), Direction.DOWN);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState,
                                   LevelAccessor level, BlockPos pos, BlockPos facingPos) {
        return !state.canSurvive(level, pos) ? Blocks.AIR.defaultBlockState()
                : super.updateShape(state, facing, facingState, level, pos, facingPos);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        List<ItemStack> drops = super.getDrops(state, params);
        if (drops.isEmpty()) {
            return List.of(new ItemStack(this));
        }
        return drops;
    }
}