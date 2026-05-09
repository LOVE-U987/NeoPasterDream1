package net.pasterdream.procedures;

import net.minecraft.world.entity.Entity;

public class PinkSlimePr1Procedure {
	public static boolean execute(Entity entity) {
		if (entity == null)
			return false;
		return entity.isAlive();
	}
}
