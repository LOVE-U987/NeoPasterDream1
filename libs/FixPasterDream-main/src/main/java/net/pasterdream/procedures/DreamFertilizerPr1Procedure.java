package net.pasterdream.procedures;

import net.pasterdream.init.PasterdreamModBlocks;

import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Mth;
import net.minecraft.core.BlockPos;

import java.util.Map;

public class DreamFertilizerPr1Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		if ((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == PasterdreamModBlocks.GRASS_3.get()) {
			world.setBlock(BlockPos.containing(x, y, z), PasterdreamModBlocks.GRASS_4.get().defaultBlockState(), 3);
			world.setBlock(BlockPos.containing(x, y + 1, z), PasterdreamModBlocks.GRASS_4.get().defaultBlockState(), 3);
		}
		if ((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == PasterdreamModBlocks.FLOWER_8.get()) {
			world.setBlock(BlockPos.containing(x, y, z), PasterdreamModBlocks.FLOWER_10.get().defaultBlockState(), 3);
			world.setBlock(BlockPos.containing(x, y + 1, z), PasterdreamModBlocks.FLOWER_10.get().defaultBlockState(), 3);
		}
		if ((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == PasterdreamModBlocks.CROP_0B.get()) {
			if (Mth.nextInt(RandomSource.create(), 1, 2) == 2) {
				{
					BlockPos _bp = BlockPos.containing(x, y, z);
					BlockState _bs = PasterdreamModBlocks.CROP_0A.get().defaultBlockState();
					BlockState _bso = world.getBlockState(_bp);
					for (Map.Entry<Property<?>, Comparable<?>> entry : _bso.getValues().entrySet()) {
						Property _property = _bs.getBlock().getStateDefinition().getProperty(entry.getKey().getName());
						if (_property != null && _bs.getValue(_property) != null)
							try {
								_bs = _bs.setValue(_property, (Comparable) entry.getValue());
							} catch (Exception e) {
							}
					}
					world.setBlock(_bp, _bs, 3);
				}
			}
		}
		if ((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == PasterdreamModBlocks.CROP_2B.get()) {
			if (Mth.nextInt(RandomSource.create(), 1, 2) == 2) {
				{
					BlockPos _bp = BlockPos.containing(x, y, z);
					BlockState _bs = PasterdreamModBlocks.CROP_2A.get().defaultBlockState();
					BlockState _bso = world.getBlockState(_bp);
					for (Map.Entry<Property<?>, Comparable<?>> entry : _bso.getValues().entrySet()) {
						Property _property = _bs.getBlock().getStateDefinition().getProperty(entry.getKey().getName());
						if (_property != null && _bs.getValue(_property) != null)
							try {
								_bs = _bs.setValue(_property, (Comparable) entry.getValue());
							} catch (Exception e) {
							}
					}
					world.setBlock(_bp, _bs, 3);
				}
			}
		}
		if ((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == PasterdreamModBlocks.CROP_3B.get()) {
			if (Mth.nextInt(RandomSource.create(), 1, 2) == 2) {
				{
					BlockPos _bp = BlockPos.containing(x, y, z);
					BlockState _bs = PasterdreamModBlocks.CROP_3A.get().defaultBlockState();
					BlockState _bso = world.getBlockState(_bp);
					for (Map.Entry<Property<?>, Comparable<?>> entry : _bso.getValues().entrySet()) {
						Property _property = _bs.getBlock().getStateDefinition().getProperty(entry.getKey().getName());
						if (_property != null && _bs.getValue(_property) != null)
							try {
								_bs = _bs.setValue(_property, (Comparable) entry.getValue());
							} catch (Exception e) {
							}
					}
					world.setBlock(_bp, _bs, 3);
				}
			}
		}
	}
}
