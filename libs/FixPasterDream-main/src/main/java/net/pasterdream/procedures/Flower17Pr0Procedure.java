package net.pasterdream.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.particles.ParticleTypes;

public class Flower17Pr0Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		if (world instanceof ServerLevel _level)
			_level.sendParticles(ParticleTypes.SNOWFLAKE, (x + 0.5), (y + 0.5), (z + 0.5), 16, 0.5, 0.5, 0.5, 0.05);
		for (int index0 = 0; index0 < 8; index0++) {
			Flower17Pr1Procedure.execute(world, x, y, z);
		}
	}
}
