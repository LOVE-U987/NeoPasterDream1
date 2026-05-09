package net.pasterdream.procedures;

import net.pasterdream.init.PasterdreamModGameRules;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;

public class ShadowMobSpawnProcedure {
	public static boolean execute(LevelAccessor world) {
		return world.getLevelData().getGameRules().getBoolean(PasterdreamModGameRules.PASTERDREAMDEBUGMODE) == true && !(world instanceof Level _lvl1 && _lvl1.isDay());
	}
}
