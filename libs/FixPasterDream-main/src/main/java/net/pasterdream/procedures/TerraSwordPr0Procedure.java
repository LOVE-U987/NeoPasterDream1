package net.pasterdream.procedures;

import net.pasterdream.init.PasterdreamModEntities;
import net.pasterdream.entity.TerraswordWaveEntity;
import net.pasterdream.configuration.PasterdreamConfigCommonConfiguration;
import net.pasterdream.PasterdreamMod;

import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Entity;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;

import java.util.List;
import java.util.Comparator;

public class TerraSwordPr0Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity, ItemStack itemstack) {
		if (entity == null)
			return;
		if (PasterdreamConfigCommonConfiguration.BAN_TERRA_SWORD.get() == true) {
			if (entity instanceof Player _player && !_player.level().isClientSide())
				_player.displayClientMessage(Component.literal("\u00A74\u6B64\u7269\u54C1\u5DF2\u88AB\u7981\u7528"), true);
		} else {
			if (itemstack.getOrCreateTag().getDouble("skill") >= 1) {
				if (world instanceof ServerLevel _level) {
					Entity entityToSpawn = PasterdreamModEntities.TERRASWORD_WAVE.get().spawn(_level, BlockPos.containing(x + entity.getLookAngle().x * 1, y + 1.5, z + entity.getLookAngle().z * 1), MobSpawnType.MOB_SUMMONED);
					if (entityToSpawn != null) {
						entityToSpawn.setYRot(entity.getYRot());
						entityToSpawn.setYBodyRot(entity.getYRot());
						entityToSpawn.setYHeadRot(entity.getYRot());
						entityToSpawn.setXRot(entity.getXRot());
						entityToSpawn.setDeltaMovement((entity.getLookAngle().x * 2), (entity.getLookAngle().y * 2), (entity.getLookAngle().z * 2));
					}
				}
			}
			if (itemstack.getOrCreateTag().getDouble("skill") == 1) {
				if (world instanceof Level _level) {
					if (!_level.isClientSide()) {
						_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("pasterdream:sword_wave")), SoundSource.PLAYERS, (float) 0.8, 1);
					} else {
						_level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("pasterdream:sword_wave")), SoundSource.PLAYERS, (float) 0.8, 1, false);
					}
				}
				{
					final Vec3 _center = new Vec3((x + entity.getLookAngle().x * 1), (y + 1.5), (z + entity.getLookAngle().z * 1));
					List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(4 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
					for (Entity entityiterator : _entfound) {
						if (entityiterator instanceof TerraswordWaveEntity) {
							if (entityiterator instanceof TerraswordWaveEntity) {
								((TerraswordWaveEntity) entityiterator).setAnimation("1");
							}
							entityiterator.getPersistentData().putDouble("paster_atk", (itemstack.getOrCreateTag().getDouble("paster_atk")));
							{
								Entity _ent = entity;
								Scoreboard _sc = _ent.level().getScoreboard();
								Objective _so = _sc.getObjective("animation");
								if (_so == null)
									_so = _sc.addObjective("animation", ObjectiveCriteria.DUMMY, Component.literal("animation"), ObjectiveCriteria.RenderType.INTEGER);
								_sc.getOrCreatePlayerScore(_ent.getScoreboardName(), _so).setScore(1);
							}
						}
					}
				}
				PasterdreamMod.queueServerWork(1, () -> {
					itemstack.getOrCreateTag().putDouble("skill", 2);
				});
			}
			if (itemstack.getOrCreateTag().getDouble("skill") == 2) {
				if (world instanceof Level _level) {
					if (!_level.isClientSide()) {
						_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("pasterdream:sword_wave")), SoundSource.PLAYERS, (float) 0.9, (float) 1.2);
					} else {
						_level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("pasterdream:sword_wave")), SoundSource.PLAYERS, (float) 0.9, (float) 1.2, false);
					}
				}
				{
					final Vec3 _center = new Vec3((x + entity.getLookAngle().x * 1), (y + 1.5), (z + entity.getLookAngle().z * 1));
					List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(3 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
					for (Entity entityiterator : _entfound) {
						if (entityiterator instanceof TerraswordWaveEntity) {
							if (entityiterator instanceof TerraswordWaveEntity) {
								((TerraswordWaveEntity) entityiterator).setAnimation("2");
							}
							entityiterator.getPersistentData().putDouble("paster_atk", (itemstack.getOrCreateTag().getDouble("paster_atk")));
							{
								Entity _ent = entity;
								Scoreboard _sc = _ent.level().getScoreboard();
								Objective _so = _sc.getObjective("animation");
								if (_so == null)
									_so = _sc.addObjective("animation", ObjectiveCriteria.DUMMY, Component.literal("animation"), ObjectiveCriteria.RenderType.INTEGER);
								_sc.getOrCreatePlayerScore(_ent.getScoreboardName(), _so).setScore(2);
							}
						}
					}
				}
				PasterdreamMod.queueServerWork(1, () -> {
					itemstack.getOrCreateTag().putDouble("skill", 3);
				});
			}
			if (itemstack.getOrCreateTag().getDouble("skill") == 3) {
				if (world instanceof Level _level) {
					if (!_level.isClientSide()) {
						_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("pasterdream:sword_wave")), SoundSource.PLAYERS, 1, (float) 1.4);
					} else {
						_level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("pasterdream:sword_wave")), SoundSource.PLAYERS, 1, (float) 1.4, false);
					}
				}
				{
					final Vec3 _center = new Vec3((x + entity.getLookAngle().x * 1), (y + 1.5), (z + entity.getLookAngle().z * 1));
					List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(3 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
					for (Entity entityiterator : _entfound) {
						if (entityiterator instanceof TerraswordWaveEntity) {
							if (entityiterator instanceof TerraswordWaveEntity) {
								((TerraswordWaveEntity) entityiterator).setAnimation("3");
							}
							entityiterator.getPersistentData().putDouble("paster_atk", (itemstack.getOrCreateTag().getDouble("paster_atk") * 2));
							{
								Entity _ent = entity;
								Scoreboard _sc = _ent.level().getScoreboard();
								Objective _so = _sc.getObjective("animation");
								if (_so == null)
									_so = _sc.addObjective("animation", ObjectiveCriteria.DUMMY, Component.literal("animation"), ObjectiveCriteria.RenderType.INTEGER);
								_sc.getOrCreatePlayerScore(_ent.getScoreboardName(), _so).setScore(3);
							}
						}
					}
				}
				PasterdreamMod.queueServerWork(1, () -> {
					itemstack.getOrCreateTag().putDouble("skill", 0);
				});
			}
		}
	}
}
