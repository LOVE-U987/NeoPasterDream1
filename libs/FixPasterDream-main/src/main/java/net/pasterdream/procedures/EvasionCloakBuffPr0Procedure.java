package net.pasterdream.procedures;

import net.pasterdream.init.PasterdreamModParticleTypes;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.particles.SimpleParticleType;

public class EvasionCloakBuffPr0Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		if (world instanceof ServerLevel _level)
			_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.SILVER_PARTICLE.get()), x, (y + 1), z, 4, 0.4, 0.5, 0.4, 0.01);
		if (world instanceof ServerLevel _level)
			_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.DUST_0_PARTICLE.get()), x, (y + 1), z, 4, 0.4, 0.5, 0.4, 0.01);
	}
}
