package net.pasterdream.procedures;

import net.minecraft.world.entity.Entity;

public class ShadowGolemPr1Procedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		if (entity.getPersistentData().getDouble("time") < 189) {
			entity.getPersistentData().putDouble("time", (entity.getPersistentData().getDouble("time") + 10));
		}
	}
}
