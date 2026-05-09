package net.pasterdream.procedures;

import net.pasterdream.init.PasterdreamModGameRules;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.BlockPos;

public class BigBubblePr0Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		if (world.getLevelData().getGameRules().getBoolean(PasterdreamModGameRules.PASTERDREAMDEBUGMODE) == false) {
			world.destroyBlock(BlockPos.containing(x, y, z), false);
		}
	}
}
