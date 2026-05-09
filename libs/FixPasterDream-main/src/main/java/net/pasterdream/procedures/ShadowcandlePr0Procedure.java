package net.pasterdream.procedures;

import net.pasterdream.init.PasterdreamModParticleTypes;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.particles.SimpleParticleType;

public class ShadowcandlePr0Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		world.addParticle((SimpleParticleType) (PasterdreamModParticleTypes.SOUL_PARTICLE.get()), (x + 0.5), (y + 0.7), (z + 0.5), 0, 0.1, 0);
	}
}
