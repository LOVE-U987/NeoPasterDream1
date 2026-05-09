package net.pasterdream.procedures;

import net.minecraft.world.entity.Entity;

public class PinkslimeBlockPr0Procedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		entity.fallDistance = 3;
	}
}
