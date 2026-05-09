package net.pasterdream.procedures;

import net.pasterdream.init.PasterdreamModParticleTypes;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.particles.ParticleTypes;

public class SquealWaveWandPr0Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		world.addParticle((SimpleParticleType) (PasterdreamModParticleTypes.SQUEAL_WAVE_PARTICLE.get()), x, y, z, 0, 0, 0);
		if (world instanceof ServerLevel _level)
			_level.sendParticles(ParticleTypes.SCULK_SOUL, x, y, z, 2, 0.1, 0.1, 0.1, 0);
	}
}
