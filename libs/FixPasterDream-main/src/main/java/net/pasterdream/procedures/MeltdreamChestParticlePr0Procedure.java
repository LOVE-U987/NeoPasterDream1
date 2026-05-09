package net.pasterdream.procedures;

import net.pasterdream.init.PasterdreamModParticleTypes;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.particles.SimpleParticleType;

public class MeltdreamChestParticlePr0Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		if (world instanceof ServerLevel _level)
			_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.WHITE_STAR_PARTICLE.get()), (x + 0.5), (y + 0.3), (z + 0.5), 3, 0.4, 0.3, 0.4, 0);
	}
}
