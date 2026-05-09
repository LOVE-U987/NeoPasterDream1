package net.pasterdream.procedures;

import net.minecraft.world.entity.Entity;

public class SnowVowBuffPr2Procedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		entity.clearFire();
		entity.setTicksFrozen(0);
	}
}
