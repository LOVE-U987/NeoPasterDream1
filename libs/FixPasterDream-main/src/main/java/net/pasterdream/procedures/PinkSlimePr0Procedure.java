package net.pasterdream.procedures;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.Entity;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Mth;

public class PinkSlimePr0Procedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		if (Mth.nextDouble(RandomSource.create(), 1, 15) > 14) {
			entity.setDeltaMovement(new Vec3((entity.getLookAngle().x / 4), 0.3, (entity.getLookAngle().z / 4)));
		}
	}
}
