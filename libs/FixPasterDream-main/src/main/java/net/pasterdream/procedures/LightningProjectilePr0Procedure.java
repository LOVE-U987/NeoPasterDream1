package net.pasterdream.procedures;

import net.pasterdream.init.PasterdreamModParticleTypes;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.particles.ParticleTypes;

public class LightningProjectilePr0Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		world.addParticle((SimpleParticleType) (PasterdreamModParticleTypes.LIGHTNING_PARTICLE.get()), x, y, z, 0, 0, 0);
		world.addParticle(ParticleTypes.ELECTRIC_SPARK, x, y, z, 0, 0, 0);
	}
}
