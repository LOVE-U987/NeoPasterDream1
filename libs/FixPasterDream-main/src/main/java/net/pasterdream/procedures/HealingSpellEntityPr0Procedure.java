package net.pasterdream.procedures;

import net.minecraft.world.entity.Entity;

public class HealingSpellEntityPr0Procedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		entity.getPersistentData().putDouble("time", 400);
	}
}
