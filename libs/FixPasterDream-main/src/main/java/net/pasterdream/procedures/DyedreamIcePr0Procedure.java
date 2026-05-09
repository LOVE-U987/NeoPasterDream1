package net.pasterdream.procedures;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.BlockPos;

public class DyedreamIcePr0Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		double generator_distance = 0;
		if (world.getBlockState(BlockPos.containing(x, y, z)).getLightEmission(world, BlockPos.containing(x, y, z)) >= 13) {
			world.setBlock(BlockPos.containing(x, y, z), Blocks.WATER.defaultBlockState(), 3);
		}
	}
}
