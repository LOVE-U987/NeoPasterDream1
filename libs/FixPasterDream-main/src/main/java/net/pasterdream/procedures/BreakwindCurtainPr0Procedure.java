package net.pasterdream.procedures;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.Entity;

public class BreakwindCurtainPr0Procedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		if (!entity.onGround()) {
			entity.setDeltaMovement(new Vec3((entity.getLookAngle().x * 5), (entity.getLookAngle().y), (entity.getLookAngle().z * 5)));
		}
	}
}
