package net.pasterdream.procedures;

import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.BlockPos;

public class FishermanHut0Pr0Procedure {
	public static boolean execute(LevelAccessor world, double x, double y, double z) {
		return (world.getBlockState(BlockPos.containing(x, y + 3, z))).getBlock() == Blocks.AIR && world.canSeeSkyFromBelowWater(BlockPos.containing(x, y + 3, z)) && world.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (int) x, (int) z) >= 62;
	}
}
