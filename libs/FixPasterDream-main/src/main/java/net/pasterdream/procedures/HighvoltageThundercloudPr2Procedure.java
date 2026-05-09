package net.pasterdream.procedures;

import net.minecraft.world.level.LevelAccessor;

public class HighvoltageThundercloudPr2Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		LightningSpellPr0Procedure.execute(world, x, (y - 4), z);
	}
}
