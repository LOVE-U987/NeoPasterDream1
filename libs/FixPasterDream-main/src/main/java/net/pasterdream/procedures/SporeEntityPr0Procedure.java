package net.pasterdream.procedures;

import net.pasterdream.init.PasterdreamModParticleTypes;
import net.pasterdream.init.PasterdreamModItems;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.particles.SimpleParticleType;

public class SporeEntityPr0Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		if (Math.random() <= 0.5) {
			if (world instanceof ServerLevel _level)
				_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.SPORE_PARTICLE.get()), x, y, z, 3, 0.4, 0.4, 0.4, 0.01);
		}
		if (Math.random() <= 0.0001) {
			if (world instanceof ServerLevel _level) {
				ItemEntity entityToSpawn = new ItemEntity(_level, x, y, z, new ItemStack(PasterdreamModItems.JUNGLE_SPORE.get()));
				entityToSpawn.setPickUpDelay(10);
				_level.addFreshEntity(entityToSpawn);
			}
			if (!entity.level().isClientSide())
				entity.discard();
		}
	}
}
