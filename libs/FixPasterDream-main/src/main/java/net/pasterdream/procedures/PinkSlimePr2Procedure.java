package net.pasterdream.procedures;

import net.pasterdream.init.PasterdreamModParticleTypes;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.particles.SimpleParticleType;

public class PinkSlimePr2Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		if (world instanceof ServerLevel _level)
			_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.DREAMFERTILITER_PARTICLE.get()), x, y, z, 5, (Math.random() / 3), (-0.5), (Math.random() / 3), 0.05);
	}
}
