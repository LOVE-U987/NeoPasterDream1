package net.pasterdream.procedures;

import net.minecraft.world.entity.Entity;

public class PaperPlanePr1Procedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		entity.getPersistentData().putDouble("player_tailwind_force", 0);
		entity.getPersistentData().putDouble("player_deadwind_force", 0);
	}
}
