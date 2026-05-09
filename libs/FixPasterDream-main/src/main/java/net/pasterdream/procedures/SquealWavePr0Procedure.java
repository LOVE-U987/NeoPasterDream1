package net.pasterdream.procedures;

import net.pasterdream.init.PasterdreamModParticleTypes;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.particles.SimpleParticleType;

public class SquealWavePr0Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		world.addParticle((SimpleParticleType) (PasterdreamModParticleTypes.SQUEAL_WAVE_PARTICLE.get()), x, y, z, 0, 0, 0);
	}
}
