package net.pasterdream.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.BlockPos;

public class OpenAir0Procedure {
	public static boolean execute(LevelAccessor world, double x, double y, double z) {
		return world.canSeeSkyFromBelowWater(BlockPos.containing(x, y + 1, z)) == true;
	}
}
