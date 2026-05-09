package net.pasterdream.procedures;

import net.pasterdream.init.PasterdreamModEntities;
import net.pasterdream.entity.ShadowSquealGhost0Entity;
import net.pasterdream.PasterdreamMod;

import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Entity;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Mth;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;

public class ShadowSquealGhost0Pr0Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		if (new Object() {
			public int getScore(String score, Entity _ent) {
				Scoreboard _sc = _ent.level().getScoreboard();
				Objective _so = _sc.getObjective(score);
				if (_so != null)
					return _sc.getOrCreatePlayerScore(_ent.getScoreboardName(), _so).getScore();
				return 0;
			}
		}.getScore("switch", entity) != 1) {
			{
				Entity _ent = entity;
				Scoreboard _sc = _ent.level().getScoreboard();
				Objective _so = _sc.getObjective("switch");
				if (_so == null)
					_so = _sc.addObjective("switch", ObjectiveCriteria.DUMMY, Component.literal("switch"), ObjectiveCriteria.RenderType.INTEGER);
				_sc.getOrCreatePlayerScore(_ent.getScoreboardName(), _so).setScore(1);
			}
			if (world instanceof Level _level) {
				if (!_level.isClientSide()) {
					_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("pasterdream:ghost0")), SoundSource.MASTER, 1, 1);
				} else {
					_level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("pasterdream:ghost0")), SoundSource.MASTER, 1, 1, false);
				}
			}
			if (entity instanceof ShadowSquealGhost0Entity) {
				((ShadowSquealGhost0Entity) entity).setAnimation("skill");
			}
			PasterdreamMod.queueServerWork(8, () -> {
				if (world instanceof ServerLevel _level) {
					Entity entityToSpawn = PasterdreamModEntities.SHADOW_GHOST.get().spawn(_level, BlockPos.containing(x + Mth.nextDouble(RandomSource.create(), -1, 1), y, z + Mth.nextDouble(RandomSource.create(), -1, 1)), MobSpawnType.MOB_SUMMONED);
					if (entityToSpawn != null) {
						entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
					}
				}
			});
			PasterdreamMod.queueServerWork(16, () -> {
				if (world instanceof ServerLevel _level) {
					Entity entityToSpawn = PasterdreamModEntities.SHADOW_GHOST.get().spawn(_level, BlockPos.containing(x + Mth.nextDouble(RandomSource.create(), -1, 1), y, z + Mth.nextDouble(RandomSource.create(), -1, 1)), MobSpawnType.MOB_SUMMONED);
					if (entityToSpawn != null) {
						entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
					}
				}
			});
			PasterdreamMod.queueServerWork(24, () -> {
				if (world instanceof ServerLevel _level) {
					Entity entityToSpawn = PasterdreamModEntities.SHADOW_SQUEAL_GHOST.get().spawn(_level, BlockPos.containing(x + Mth.nextDouble(RandomSource.create(), -1, 1), y, z + Mth.nextDouble(RandomSource.create(), -1, 1)),
							MobSpawnType.MOB_SUMMONED);
					if (entityToSpawn != null) {
						entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
					}
				}
			});
			PasterdreamMod.queueServerWork(30, () -> {
				{
					Entity _ent = entity;
					Scoreboard _sc = _ent.level().getScoreboard();
					Objective _so = _sc.getObjective("switch");
					if (_so == null)
						_so = _sc.addObjective("switch", ObjectiveCriteria.DUMMY, Component.literal("switch"), ObjectiveCriteria.RenderType.INTEGER);
					_sc.getOrCreatePlayerScore(_ent.getScoreboardName(), _so).setScore(0);
				}
			});
		}
	}
}
