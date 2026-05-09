package net.pasterdream.procedures;

import net.pasterdream.init.PasterdreamModEntities;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;

public class TwilightLanternPr2Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		if (world instanceof ServerLevel _level) {
			Entity entityToSpawn = PasterdreamModEntities.SHADOW_GHOST.get().spawn(_level, BlockPos.containing(x + 6, y + 2, z + 0), MobSpawnType.MOB_SUMMONED);
			if (entityToSpawn != null) {
			}
		}
		if (world instanceof ServerLevel _level) {
			Entity entityToSpawn = PasterdreamModEntities.SHADOW_GHOST.get().spawn(_level, BlockPos.containing(x - 6, y + 2, z + 0), MobSpawnType.MOB_SUMMONED);
			if (entityToSpawn != null) {
			}
		}
		if (world instanceof ServerLevel _level) {
			Entity entityToSpawn = PasterdreamModEntities.SHADOW_GHOST.get().spawn(_level, BlockPos.containing(x + 0, y + 2, z - 6), MobSpawnType.MOB_SUMMONED);
			if (entityToSpawn != null) {
			}
		}
		if (world instanceof ServerLevel _level) {
			Entity entityToSpawn = PasterdreamModEntities.SHADOW_GHOST.get().spawn(_level, BlockPos.containing(x - 0, y + 2, z - 6), MobSpawnType.MOB_SUMMONED);
			if (entityToSpawn != null) {
			}
		}
		if (world instanceof ServerLevel _level) {
			Entity entityToSpawn = PasterdreamModEntities.SHADOW_SQUEAL_GHOST.get().spawn(_level, BlockPos.containing(x + 8, y + 1, z + 8), MobSpawnType.MOB_SUMMONED);
			if (entityToSpawn != null) {
			}
		}
		if (world instanceof ServerLevel _level) {
			Entity entityToSpawn = PasterdreamModEntities.SHADOW_SQUEAL_GHOST.get().spawn(_level, BlockPos.containing(x - 8, y + 1, z + 8), MobSpawnType.MOB_SUMMONED);
			if (entityToSpawn != null) {
			}
		}
		if (world instanceof ServerLevel _level) {
			Entity entityToSpawn = PasterdreamModEntities.SHADOW_SQUEAL_GHOST.get().spawn(_level, BlockPos.containing(x + 8, y + 1, z - 8), MobSpawnType.MOB_SUMMONED);
			if (entityToSpawn != null) {
			}
		}
		if (world instanceof ServerLevel _level) {
			Entity entityToSpawn = PasterdreamModEntities.SHADOW_SQUEAL_GHOST.get().spawn(_level, BlockPos.containing(x - 8, y + 1, z - 8), MobSpawnType.MOB_SUMMONED);
			if (entityToSpawn != null) {
			}
		}
		if (world instanceof ServerLevel _level) {
			Entity entityToSpawn = EntityType.WITHER_SKELETON.spawn(_level, BlockPos.containing(x + 9, y - 1, z + 1), MobSpawnType.MOB_SUMMONED);
			if (entityToSpawn != null) {
			}
		}
		if (world instanceof ServerLevel _level) {
			Entity entityToSpawn = EntityType.WITHER_SKELETON.spawn(_level, BlockPos.containing(x - 9, y - 1, z + 1), MobSpawnType.MOB_SUMMONED);
			if (entityToSpawn != null) {
			}
		}
		if (world instanceof ServerLevel _level) {
			Entity entityToSpawn = EntityType.WITHER_SKELETON.spawn(_level, BlockPos.containing(x + 0, y - 1, z + 9), MobSpawnType.MOB_SUMMONED);
			if (entityToSpawn != null) {
			}
		}
		if (world instanceof ServerLevel _level) {
			Entity entityToSpawn = EntityType.WITHER_SKELETON.spawn(_level, BlockPos.containing(x + 0, y - 1, z - 9), MobSpawnType.MOB_SUMMONED);
			if (entityToSpawn != null) {
			}
		}
	}
}
