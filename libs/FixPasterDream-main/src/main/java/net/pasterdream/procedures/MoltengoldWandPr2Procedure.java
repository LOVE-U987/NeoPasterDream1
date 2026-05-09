package net.pasterdream.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.particles.ParticleTypes;

public class MoltengoldWandPr2Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		world.addParticle(ParticleTypes.LAVA, x, y, z, 0, 0, 0);
	}
}
