
package net.pasterdream.block;

import net.pasterdream.init.PasterdreamModBlocks;

import net.minecraftforge.common.PlantType;
import net.minecraftforge.common.ForgeHooks;

import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.SugarCaneBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.util.RandomSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;

import java.util.List;
import java.util.Collections;

public class Vine0Block extends SugarCaneBlock {
	public Vine0Block() {
		super(BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).randomTicks().sound(SoundType.CAVE_VINES).instabreak().noCollission().offsetType(BlockBehaviour.OffsetType.XZ).pushReaction(PushReaction.DESTROY));
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		Vec3 offset = state.getOffset(world, pos);
		return box(1, 0, 1, 15, 16, 15).move(offset.x, offset.y, offset.z);
	}

	@Override
	public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return 100;
	}

	@Override
	public void appendHoverText(ItemStack itemstack, BlockGetter world, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, world, list, flag);
	}

	@Override
	public int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return 60;
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
		List<ItemStack> dropsOriginal = super.getDrops(state, builder);
		if (!dropsOriginal.isEmpty())
			return dropsOriginal;
		return Collections.singletonList(new ItemStack(this));
	}

	@Override
	public boolean canSurvive(BlockState blockstate, LevelReader worldIn, BlockPos pos) {
		BlockPos blockpos = pos.below();
		BlockState groundState = worldIn.getBlockState(blockpos);
		return groundState.is(this) || groundState.is(PasterdreamModBlocks.DYEDREAM_ICE.get()) || groundState.is(PasterdreamModBlocks.DYEDREAM_PACKED_ICE.get()) || groundState.is(Blocks.CALCITE)
				|| groundState.is(PasterdreamModBlocks.DYEDREAM_GRASS.get()) || groundState.is(PasterdreamModBlocks.DYEDREAM_DIRT.get());
	}

	@Override
	public PlantType getPlantType(BlockGetter world, BlockPos pos) {
		return PlantType.CAVE;
	}

	@Override
	public void randomTick(BlockState blockstate, ServerLevel world, BlockPos blockpos, RandomSource random) {
		if (world.isEmptyBlock(blockpos.above())) {
			int i = 1;
			for (; world.getBlockState(blockpos.below(i)).is(this); ++i);
			if (i < 14) {
				int j = blockstate.getValue(AGE);
				if (ForgeHooks.onCropsGrowPre(world, blockpos, blockstate, true)) {
					if (j == 15) {
						world.setBlockAndUpdate(blockpos.above(), defaultBlockState());
						world.setBlock(blockpos, blockstate.setValue(AGE, 0), 4);
					} else
						world.setBlock(blockpos, blockstate.setValue(AGE, j + 1), 4);
				}
			}
		}
	}
}
