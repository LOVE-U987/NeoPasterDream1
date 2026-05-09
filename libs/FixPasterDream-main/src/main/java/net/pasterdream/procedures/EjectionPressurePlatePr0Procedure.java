package net.pasterdream.procedures;

import net.pasterdream.PasterdreamMod;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;

public class EjectionPressurePlatePr0Procedure {
	public static void execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		PasterdreamMod.queueServerWork(2, () -> {
			entity.setDeltaMovement(new Vec3(0, 0.8, 0));
		});
		entity.fallDistance = 3;
	}
}
