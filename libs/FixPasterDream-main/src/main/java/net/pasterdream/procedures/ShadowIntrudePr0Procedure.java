package net.pasterdream.procedures;

import net.pasterdream.init.PasterdreamModMobEffects;
import net.pasterdream.init.PasterdreamModEntities;
import net.pasterdream.entity.WeakenessTerrorbeakEntity;

import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Mth;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.Advancement;

public class ShadowIntrudePr0Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		if (!(world instanceof Level _lvl0 && _lvl0.isDay()) && (world instanceof Level _lvl ? _lvl.dimension() : Level.OVERWORLD) == Level.OVERWORLD) {
			if (entity.getPersistentData().getBoolean("shadow_intrude") != true) {
				if (world.getMaxLocalRawBrightness(BlockPos.containing(x, y, z)) <= 7 && Math.random() < 0.0005) {
					entity.getPersistentData().putBoolean("shadow_intrude", true);
					entity.getPersistentData().putBoolean("shadow_intrude_end", false);
					if (entity instanceof Player _player && !_player.level().isClientSide())
						_player.displayClientMessage(Component.literal("\u00A75\u4F60\u611F\u5230\u4E00\u4E1D\u51C9\u610F\uFF0C\u773C\u524D\u88AB\u7B3C\u7F69\u4E86\u4E00\u5C42\u9ED1\u96FE"), false);
					if (entity instanceof Player _player && !_player.level().isClientSide())
						_player.displayClientMessage(Component.literal("\u00A75\u9644\u8FD1\u7684\u7684\u5F71\u5B50\u5F00\u59CB\u8815\u52A8"), false);
					if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
						_entity.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 100, 0, false, false));
					if (world instanceof Level _level) {
						if (!_level.isClientSide()) {
							_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("pasterdream:shadow_door")), SoundSource.NEUTRAL, 1, 1);
						} else {
							_level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("pasterdream:shadow_door")), SoundSource.NEUTRAL, 1, 1, false);
						}
					}
				}
			} else {
				if (Math.random() < 0.02 && entity.getPersistentData().getBoolean("shadow_intrude_end") != true) {
					entity.getPersistentData().putDouble("nearly_random_coord_x", (Mth.nextInt(RandomSource.create(), -9, 9)));
					entity.getPersistentData().putDouble("nearly_random_coord_z", (Mth.nextInt(RandomSource.create(), -9, 9)));
					if (world.isEmptyBlock(BlockPos.containing(x + entity.getPersistentData().getDouble("nearly_random_coord_x"), y + 2, z + entity.getPersistentData().getDouble("nearly_random_coord_z")))
							&& world.isEmptyBlock(BlockPos.containing(x + entity.getPersistentData().getDouble("nearly_random_coord_x"), y + 1, z + entity.getPersistentData().getDouble("nearly_random_coord_z")))
							&& !world.isEmptyBlock(BlockPos.containing(x + entity.getPersistentData().getDouble("nearly_random_coord_x"), y - 1, z + entity.getPersistentData().getDouble("nearly_random_coord_z")))) {
						if (world instanceof ServerLevel _level) {
							Entity entityToSpawn = PasterdreamModEntities.WEAKENESS_TERRORBEAK.get().spawn(_level,
									BlockPos.containing(x + entity.getPersistentData().getDouble("nearly_random_coord_x"), y, z + entity.getPersistentData().getDouble("nearly_random_coord_z")), MobSpawnType.MOB_SUMMONED);
							if (entityToSpawn != null) {
								entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
							}
						}
						if (world instanceof ServerLevel _level) {
							Entity entityToSpawn = PasterdreamModEntities.SHADOW_HAND.get().spawn(_level,
									BlockPos.containing(x + entity.getPersistentData().getDouble("nearly_random_coord_x"), y, z + entity.getPersistentData().getDouble("nearly_random_coord_z")), MobSpawnType.MOB_SUMMONED);
							if (entityToSpawn != null) {
								entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
							}
						}
						entity.getPersistentData().putDouble("shadow_intrude_number", (entity.getPersistentData().getDouble("shadow_intrude_number") + 1));
						if (entity.getPersistentData().getDouble("shadow_intrude_number") > 4) {
							if (Math.random() >= 0.5) {
								entity.getPersistentData().putBoolean("shadow_intrude_end", true);
							}
						}
						if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
							_entity.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 100, 0));
					}
				}
			}
			if (entity.getPersistentData().getBoolean("shadow_intrude_end") == true && entity.getPersistentData().getBoolean("shadow_intrude") == true) {
				if (!(!world.getEntitiesOfClass(WeakenessTerrorbeakEntity.class, AABB.ofSize(new Vec3(x, y, z), 32, 32, 32), e -> true).isEmpty())) {
					entity.getPersistentData().putBoolean("shadow_intrude", false);
					entity.getPersistentData().putBoolean("shadow_intrude_end", false);
					if (entity instanceof Player _player && !_player.level().isClientSide())
						_player.displayClientMessage(Component.literal("\u00A75\u5F71\u5B50\u5F52\u4E8E\u5E73\u606F..."), false);
					if (!(entity instanceof ServerPlayer _plr43 && _plr43.level() instanceof ServerLevel
							&& _plr43.getAdvancements().getOrStartProgress(_plr43.server.getAdvancements().getAdvancement(new ResourceLocation("pasterdream:achievement_shadow_npc_3"))).isDone())) {
						if (entity instanceof ServerPlayer _player) {
							Advancement _adv = _player.server.getAdvancements().getAdvancement(new ResourceLocation("pasterdream:achievement_shadow_npc_3"));
							AdvancementProgress _ap = _player.getAdvancements().getOrStartProgress(_adv);
							if (!_ap.isDone()) {
								for (String criteria : _ap.getRemainingCriteria())
									_player.getAdvancements().award(_adv, criteria);
							}
						}
						if (entity instanceof Player _player && !_player.level().isClientSide())
							_player.displayClientMessage(Component.literal("\u00A77\u4F60\u5BF9\u8FD9\u4E9B\u6697\u5F71\u751F\u7269\u4F1A\u6765\u5230\u8FD9\u91CC\u611F\u5230\u7591\u60D1"), false);
						if (entity instanceof Player _player && !_player.level().isClientSide())
							_player.displayClientMessage(Component.literal("\u00A77\u6216\u8BB8\u6211\u4EEC\u5E94\u8BE5\u518D\u53BB\u627E\u4E00\u6B21\u65E0\u540D..."), false);
						if (entity instanceof LivingEntity _entity)
							_entity.removeEffect(PasterdreamModMobEffects.SHADOW_SPYON_BUFF.get());
					}
				}
			}
		}
		if (world instanceof Level _lvl48 && _lvl48.isDay() && entity.getPersistentData().getBoolean("shadow_intrude") == true) {
			entity.getPersistentData().putBoolean("shadow_intrude", false);
			entity.getPersistentData().putBoolean("shadow_intrude_end", false);
			if (entity instanceof Player _player && !_player.level().isClientSide())
				_player.displayClientMessage(Component.literal("\u00A75\u5F71\u5B50\u5F52\u4E8E\u5E73\u606F..."), false);
			if (!(entity instanceof ServerPlayer _plr53 && _plr53.level() instanceof ServerLevel
					&& _plr53.getAdvancements().getOrStartProgress(_plr53.server.getAdvancements().getAdvancement(new ResourceLocation("pasterdream:achievement_shadow_npc_3"))).isDone())) {
				if (entity instanceof ServerPlayer _player) {
					Advancement _adv = _player.server.getAdvancements().getAdvancement(new ResourceLocation("pasterdream:achievement_shadow_npc_3"));
					AdvancementProgress _ap = _player.getAdvancements().getOrStartProgress(_adv);
					if (!_ap.isDone()) {
						for (String criteria : _ap.getRemainingCriteria())
							_player.getAdvancements().award(_adv, criteria);
					}
				}
				if (entity instanceof Player _player && !_player.level().isClientSide())
					_player.displayClientMessage(Component.literal("\u00A77\u4F60\u5BF9\u8FD9\u4E9B\u6697\u5F71\u751F\u7269\u4F1A\u6765\u5230\u8FD9\u91CC\u611F\u5230\u7591\u60D1"), false);
				if (entity instanceof Player _player && !_player.level().isClientSide())
					_player.displayClientMessage(Component.literal("\u00A77\u6216\u8BB8\u6211\u4EEC\u5E94\u8BE5\u518D\u53BB\u627E\u4E00\u6B21\u65E0\u540D..."), false);
				if (entity instanceof LivingEntity _entity)
					_entity.removeEffect(PasterdreamModMobEffects.SHADOW_SPYON_BUFF.get());
			}
		}
	}
}
