package net.pasterdream.procedures;

import net.pasterdream.init.PasterdreamModParticleTypes;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.particles.SimpleParticleType;

public class LightFireflyGlassJarPr0Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		if (world instanceof ServerLevel _level)
			_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.LIGHT_FIREFLY_GLASS_JAR_PARTICLE.get()), (x + 0.5), (y + 0.4), (z + 0.5), 4, 0.1, 0.15, 0.1, 0.01);
	}
}
