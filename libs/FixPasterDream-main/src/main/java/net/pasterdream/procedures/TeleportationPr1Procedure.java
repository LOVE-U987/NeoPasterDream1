package net.pasterdream.procedures;

import net.pasterdream.init.PasterdreamModAttributes;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;

public class TeleportationPr1Procedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		if (entity.isShiftKeyDown() == false) {
			if (entity.isInWater()) {
				entity.setDeltaMovement(new Vec3((entity.getLookAngle().x * ((LivingEntity) entity).getAttribute(PasterdreamModAttributes.TELEPORTATIONRANGE.get()).getValue()), (entity.getLookAngle().y),
						(entity.getLookAngle().z * ((LivingEntity) entity).getAttribute(PasterdreamModAttributes.TELEPORTATIONRANGE.get()).getValue())));
			} else {
				entity.setDeltaMovement(new Vec3((entity.getLookAngle().x * ((LivingEntity) entity).getAttribute(PasterdreamModAttributes.TELEPORTATIONRANGE.get()).getValue()), (entity.getLookAngle().y * 0.1 + 0.4),
						(entity.getLookAngle().z * ((LivingEntity) entity).getAttribute(PasterdreamModAttributes.TELEPORTATIONRANGE.get()).getValue())));
			}
		} else {
			entity.setDeltaMovement(new Vec3((entity.getLookAngle().x * (-1) * ((LivingEntity) entity).getAttribute(PasterdreamModAttributes.TELEPORTATIONRANGE.get()).getValue()), (entity.getLookAngle().y * (-0.1) + 0.4),
					(entity.getLookAngle().z * (-1) * ((LivingEntity) entity).getAttribute(PasterdreamModAttributes.TELEPORTATIONRANGE.get()).getValue())));
		}
	}
}
