
package net.pasterdream.block;

import com.google.common.collect.Lists;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.lighting.LightEngine;
import net.minecraftforge.common.IPlantable;

import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.pasterdream.init.PasterdreamModBlocks;

import java.util.List;
import java.util.Collections;

public class DyedreamDirtBlock extends Block {
	public DyedreamDirtBlock() {
		super(BlockBehaviour.Properties.of().sound(SoundType.GRAVEL).strength(0.75f, 0.5f).randomTicks());
	}

	@Override
	public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
		return 15;
	}

	@Override
	public boolean canSustainPlant(BlockState state, BlockGetter world, BlockPos pos, Direction direction, IPlantable plantable) {
		return true;
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
		List<ItemStack> dropsOriginal = super.getDrops(state, builder);
		if (!dropsOriginal.isEmpty())
			return dropsOriginal;
		return Collections.singletonList(new ItemStack(this, 1));
	}

	@Override
	public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
		if (!canBeGrass(state, world, pos)) return;
		if (!world.isAreaLoaded(pos, 2)) return;
		world.setBlockAndUpdate(pos, PasterdreamModBlocks.DYEDREAM_GRASS.get().defaultBlockState());
	}

	private boolean canBeGrass(BlockState state, LevelReader reader, BlockPos pos) {
		List<Block> list = Lists.newArrayList(reader.getBlockState(pos.north()).getBlock(), reader.getBlockState(pos.east()).getBlock(), reader.getBlockState(pos.west()).getBlock(), reader.getBlockState(pos.south()).getBlock());
		if (!list.contains(PasterdreamModBlocks.DYEDREAM_GRASS.get())) return false;

		BlockPos blockpos = pos.above();
		BlockState blockstate = reader.getBlockState(blockpos);
		int i = LightEngine.getLightBlockInto(reader, state, pos, blockstate, blockpos, Direction.UP, blockstate.getLightBlock(reader, blockpos));
		return blockstate.isAir() && i < reader.getMaxLightLevel();
	}
}
