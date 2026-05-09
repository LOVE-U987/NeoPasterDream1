package net.pasterdream.procedures;

import net.minecraft.world.entity.Entity;

public class MoltengoldSwordPr0Procedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		entity.setSecondsOnFire(3);
	}
}
