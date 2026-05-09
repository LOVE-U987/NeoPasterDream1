package net.pasterdream.procedures;

import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.LevelAccessor;

public class TravelerHouse0Pr0Procedure {
	public static boolean execute(LevelAccessor world, double x, double z) {
		return world.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (int) x, (int) z) <= 80;
	}
}
