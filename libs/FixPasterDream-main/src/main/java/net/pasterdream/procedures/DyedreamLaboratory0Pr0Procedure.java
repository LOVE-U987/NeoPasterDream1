package net.pasterdream.procedures;

import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.BlockPos;

public class DyedreamLaboratory0Pr0Procedure {
	public static boolean execute(LevelAccessor world, double x, double y, double z) {
		return world.isEmptyBlock(BlockPos.containing(x, y - 22, z)) == false && world.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (int) x, (int) z) <= 76;
	}
}
