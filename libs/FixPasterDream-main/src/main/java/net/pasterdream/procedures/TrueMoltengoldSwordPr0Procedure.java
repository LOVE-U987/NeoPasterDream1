package net.pasterdream.procedures;

import net.minecraft.world.entity.Entity;

public class TrueMoltengoldSwordPr0Procedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		if (entity.isOnFire() == true) {
			entity.setSecondsOnFire((int) (entity.getRemainingFireTicks() * 0.05 + 2));
		} else {
			entity.setSecondsOnFire(4);
		}
	}
}
