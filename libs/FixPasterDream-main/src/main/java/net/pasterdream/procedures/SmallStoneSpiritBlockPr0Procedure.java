package net.pasterdream.procedures;

import net.pasterdream.init.PasterdreamModEntities;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.BlockPos;

public class SmallStoneSpiritBlockPr0Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		if (world instanceof ServerLevel _level) {
			Entity entityToSpawn = PasterdreamModEntities.SMALL_STONE_SPIRIT.get().spawn(_level, BlockPos.containing(x + 0.5, y + 0.5, z + 0.5), MobSpawnType.MOB_SUMMONED);
			if (entityToSpawn != null) {
			}
		}
		if (world instanceof ServerLevel _level)
			_level.sendParticles(ParticleTypes.ASH, (x + 0.5), (y + 0.5), (z + 0.5), 12, 0.5, 0.5, 0.5, 0.05);
		if (world instanceof ServerLevel _level)
			_level.sendParticles(ParticleTypes.CLOUD, (x + 0.5), (y + 0.5), (z + 0.5), 12, 0.5, 0.5, 0.5, 0.05);
	}
}
