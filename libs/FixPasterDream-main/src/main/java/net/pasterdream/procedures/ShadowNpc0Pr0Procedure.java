package net.pasterdream.procedures;

import net.pasterdream.entity.ShadowNpc0Entity;
import net.pasterdream.configuration.PasterdreamConfigCommonConfiguration;
import net.pasterdream.PasterdreamMod;

import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket;
import net.minecraft.network.protocol.game.ClientboundLevelEventPacket;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.Advancement;

import java.util.List;
import java.util.Comparator;

public class ShadowNpc0Pr0Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity, Entity sourceentity) {
		if (entity == null || sourceentity == null)
			return;
		if (entity.getPersistentData().getBoolean("switch") != true) {
			if ((sourceentity instanceof ServerPlayer _plr1 && _plr1.level() instanceof ServerLevel
					&& _plr1.getAdvancements().getOrStartProgress(_plr1.server.getAdvancements().getAdvancement(new ResourceLocation("pasterdream:achievement_shadow_npc_0"))).isDone()) == false) {
				entity.getPersistentData().putBoolean("switch", true);
				if (entity instanceof ShadowNpc0Entity) {
					((ShadowNpc0Entity) entity).setAnimation("say");
				}
				{
					final Vec3 _center = new Vec3(x, y, z);
					List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(16 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
					for (Entity entityiterator : _entfound) {
						if (entityiterator instanceof Player) {
							if (entityiterator instanceof Player _player && !_player.level().isClientSide())
								_player.displayClientMessage(Component.literal("\uFF1F\uFF1F\uFF1F\uFF1A..."), false);
							PasterdreamMod.queueServerWork(40, () -> {
								if (entityiterator instanceof Player _player && !_player.level().isClientSide())
									_player.displayClientMessage(Component.literal("\uFF1F\uFF1F\uFF1F\uFF1A\u4F60\u597D...\u5DF2\u7ECF..\u5F88\u4E45\u6CA1\u4EBA\u6765\u5230\u8FD9\u91CC\u4E86"), false);
							});
							PasterdreamMod.queueServerWork(80, () -> {
								if (entityiterator instanceof Player _player && !_player.level().isClientSide())
									_player.displayClientMessage(Component.literal("\uFF1F\uFF1F\uFF1F\uFF1A\u8BF7\u4E0D\u8981\u611F\u5230\u5BB3\u6015\uFF0C\u6211\u4E0D\u50CF\u5916\u9762\u7684\u6697\u5F71\u751F\u7269\u4E00\u6837\u3002"), false);
							});
							PasterdreamMod.queueServerWork(120, () -> {
								if (entityiterator instanceof Player _player && !_player.level().isClientSide())
									_player.displayClientMessage(Component.literal("\uFF1F\uFF1F\uFF1F\uFF1A\u4F60\u5F88\u597D\u5947\u6211\u548C\u6211\u4E3A\u4EC0\u4E48\u5728\u8FD9\u91CC\uFF1F"), false);
							});
							PasterdreamMod.queueServerWork(160, () -> {
								if (entityiterator instanceof Player _player && !_player.level().isClientSide())
									_player.displayClientMessage(
											Component.literal("\uFF1F\uFF1F\uFF1F\uFF1A\u5982\u679C\u8981\u8C08\u8D77\u6765\u5173\u4E8E\u6211\u7684\u4E8B...\u7ECF\u5386\u4E86\u592A\u591A\uFF0C\u6211\u4E5F\u4E0D\u60F3\u56DE\u5FC6\u3002"), false);
							});
							PasterdreamMod.queueServerWork(200, () -> {
								if (entityiterator instanceof Player _player && !_player.level().isClientSide())
									_player.displayClientMessage(Component.literal("\uFF1F\uFF1F\uFF1F\uFF1A\u5F88\u62B1\u6B49\uFF0C\u8FD9\u4E5F\u662F\u6211\u4EEC\u7B2C\u4E00\u6B21\u89C1\u9762...\u5E0C\u671B\u4F60\u80FD\u7406\u89E3\u3002"), false);
							});
							PasterdreamMod.queueServerWork(240, () -> {
								if (entityiterator instanceof Player _player && !_player.level().isClientSide())
									_player.displayClientMessage(Component.literal("\uFF1F\uFF1F\uFF1F\uFF1A..."), false);
							});
							PasterdreamMod.queueServerWork(280, () -> {
								if (entityiterator instanceof Player _player && !_player.level().isClientSide())
									_player.displayClientMessage(
											Component.literal("\uFF1F\uFF1F\uFF1F\uFF1A\u6211\u7684\u540D\u5B57\uFF1F...\u65F6\u95F4\u8FC7\u53BB\u592A\u4E45\uFF0C\u6211\u4E5F\u5931\u53BB\u4E86\u66FE\u7ECF\u7684\u201C\u81EA\u5DF1\u201D\u3002"),
											false);
							});
							PasterdreamMod.queueServerWork(320, () -> {
								if (entityiterator instanceof Player _player && !_player.level().isClientSide())
									_player.displayClientMessage(Component.literal("\uFF1F\uFF1F\uFF1F\uFF1A\u5C31\u53EB\u6211\u201C\u65E0\u540D\u201D\u5427\u3002"), false);
							});
							PasterdreamMod.queueServerWork(360, () -> {
								if (entityiterator instanceof Player _player && !_player.level().isClientSide())
									_player.displayClientMessage(Component.literal("\u65E0\u540D\uFF1A\u8FD9\u4E9B\u7ED9\u4F60\uFF0C\u4F5C\u4E3A\u521D\u6B21\u7684\u89C1\u9762\u793C\u3002"), false);
								if (world instanceof ServerLevel _level) {
									ItemEntity entityToSpawn = new ItemEntity(_level, x, y, z, new ItemStack(Blocks.GOLD_BLOCK));
									entityToSpawn.setPickUpDelay(20);
									_level.addFreshEntity(entityToSpawn);
								}
							});
							PasterdreamMod.queueServerWork(400, () -> {
								if (entityiterator instanceof Player _player && !_player.level().isClientSide())
									_player.displayClientMessage(Component.literal(
											"\u65E0\u540D\uFF1A\u8FD8\u6709\uFF0C\u8FD9\u91CC\u5F88\u5371\u9669\u3002 \u6BCF\u6B21\u8FDB\u5165\u8FD9\u4E2A\u6697\u5F71\u5730\u7262\uFF0C\u5B83\u90FD\u4F1A\u4EA7\u751F\u4E00\u4E9B\u53D8\u5316\uFF0C\u662F\u66F4\u591A\u7684\u5A01\u80C1\u8FD8\u662F\u66F4\u591A\u7684\u5B9D\u85CF...\u6211\u4E5F\u4E0D\u5F97\u800C\u77E5\u3002"),
											false);
							});
							PasterdreamMod.queueServerWork(440, () -> {
								if (entityiterator instanceof Player _player && !_player.level().isClientSide())
									_player.displayClientMessage(Component.literal("\u65E0\u540D\uFF1A\u4ECE\u8FD9\u91CC\u4E0B\u53BB\u5C31\u662F\u51FA\u53E3\u4E86"), false);
							});
							PasterdreamMod.queueServerWork(480, () -> {
								if (entityiterator instanceof Player _player && !_player.level().isClientSide())
									_player.displayClientMessage(Component.literal("\u65E0\u540D\uFF1A..."), false);
							});
							PasterdreamMod.queueServerWork(520, () -> {
								if (entityiterator instanceof Player _player && !_player.level().isClientSide())
									_player.displayClientMessage(
											Component.literal(
													"\u65E0\u540D\uFF1A\u6700\u540E\uFF0C\u8BF7\u4E0D\u8981\u5192\u7740\u751F\u547D\u98CE\u9669\u63A2\u7D22\u8FD9\u91CC\u3002 \u4F60\u6240\u770B\u5230\u7684\u9B3C\u9B42...\u662F\u66FE\u7ECF\u7684..."),
											false);
								if (entityiterator instanceof ServerPlayer _player) {
									Advancement _adv = _player.server.getAdvancements().getAdvancement(new ResourceLocation("pasterdream:achievement_shadow_npc_0"));
									AdvancementProgress _ap = _player.getAdvancements().getOrStartProgress(_adv);
									if (!_ap.isDone()) {
										for (String criteria : _ap.getRemainingCriteria())
											_player.getAdvancements().award(_adv, criteria);
									}
								}
							});
							PasterdreamMod.queueServerWork(560, () -> {
								if (entityiterator instanceof Player _player && !_player.level().isClientSide())
									_player.displayClientMessage(Component.literal("\u00A7a\u65B0\u7684\u4E3B\u7EBF\u8FDB\u5EA6\u5DF2\u89E3\u9501"), false);
							});
						}
					}
				}
			}
			if ((sourceentity instanceof ServerPlayer _plr37 && _plr37.level() instanceof ServerLevel
					&& _plr37.getAdvancements().getOrStartProgress(_plr37.server.getAdvancements().getAdvancement(new ResourceLocation("pasterdream:achievement_shadow_npc_0"))).isDone()) == true
					&& (sourceentity instanceof ServerPlayer _plr38 && _plr38.level() instanceof ServerLevel
							&& _plr38.getAdvancements().getOrStartProgress(_plr38.server.getAdvancements().getAdvancement(new ResourceLocation("pasterdream:achievement_shadow_npc_1"))).isDone()) == false) {
				entity.getPersistentData().putBoolean("switch", true);
				if (entity instanceof ShadowNpc0Entity) {
					((ShadowNpc0Entity) entity).setAnimation("say");
				}
				{
					final Vec3 _center = new Vec3(x, y, z);
					List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(16 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
					for (Entity entityiterator : _entfound) {
						if (entityiterator instanceof Player) {
							if (entityiterator instanceof Player _player && !_player.level().isClientSide())
								_player.displayClientMessage(Component.literal("\u65E0\u540D\uFF1A..."), false);
							PasterdreamMod.queueServerWork(40, () -> {
								if (entityiterator instanceof Player _player && !_player.level().isClientSide())
									_player.displayClientMessage(Component.literal("\u65E0\u540D\uFF1A\u6211\u4EEC\u89C1\u8FC7\u4E00\u9762\uFF0C\u5BF9\u5427"), false);
							});
							PasterdreamMod.queueServerWork(80, () -> {
								if (entityiterator instanceof Player _player && !_player.level().isClientSide())
									_player.displayClientMessage(Component.literal("\u65E0\u540D\uFF1A\u53C8\u4E00\u6B21\u6765\u5230\u8FD9\u91CC...\u4F60\u662F\u4E3A\u4E86\u4EC0\u4E48"), false);
							});
							PasterdreamMod.queueServerWork(120, () -> {
								if (entityiterator instanceof Player _player && !_player.level().isClientSide())
									_player.displayClientMessage(Component.literal("\u65E0\u540D\uFF1A..."), false);
							});
							PasterdreamMod.queueServerWork(160, () -> {
								if (entityiterator instanceof Player _player && !_player.level().isClientSide())
									_player.displayClientMessage(
											Component.literal(
													"\u65E0\u540D\uFF1A\u6211\u4E3A\u4EC0\u4E48\u5728\u8FD9\u91CC\uFF1F \u56E0\u4E3A\u6211\u65E0\u6CD5\u9003\u79BB\uFF0C\u4E5F\u4E0D\u518D\u60F3\u5C1D\u8BD5\u9003\u79BB\u8FD9\u91CC\u4E86\u3002"),
											false);
							});
							PasterdreamMod.queueServerWork(200, () -> {
								if (entityiterator instanceof Player _player && !_player.level().isClientSide())
									_player.displayClientMessage(Component.literal("\u65E0\u540D\uFF1A\u6B63\u5982\u4F60\u6240\u89C1\uFF0C\u8FD9\u4E00\u5C42\uFF0C\u5E76\u975E\u88AB\u9634\u5F71\u5168\u90E8\u8986\u76D6\u3002"), false);
							});
							PasterdreamMod.queueServerWork(240, () -> {
								if (entityiterator instanceof Player _player && !_player.level().isClientSide())
									_player.displayClientMessage(Component.literal("\u65E0\u540D\uFF1A\u56E0\u4E3A\u8FD9\u662F\u6211\u4EC5\u5B58\u7684\u56DE\u5FC6\uFF0C\u6211\u66FE\u770B\u5230\u8FC7\u7684\u4E16\u754C\u3002"), false);
							});
							PasterdreamMod.queueServerWork(280, () -> {
								if (entityiterator instanceof Player _player && !_player.level().isClientSide())
									_player.displayClientMessage(
											Component.literal(
													"\u65E0\u540D\uFF1A\u6211\u8111\u6D77\u4E2D\u7684\u60F3\u6CD5...\u8BA9\u8FD9\u91CC\u7684\u90E8\u5206\u6210\u4E3A\u4E86\u201C\u73B0\u5B9E\u201D\uFF0C\u8FD9\u5F88\u5947\u5999 \u5BF9\u5427\u3002"),
											false);
							});
							PasterdreamMod.queueServerWork(320, () -> {
								if (entityiterator instanceof Player _player && !_player.level().isClientSide())
									_player.displayClientMessage(Component.literal("\u65E0\u540D\uFF1A..."), false);
							});
							PasterdreamMod.queueServerWork(360, () -> {
								if (entityiterator instanceof Player _player && !_player.level().isClientSide())
									_player.displayClientMessage(Component.literal("\u65E0\u540D\uFF1A\u60F3\u5E2E\u6211\u51FA\u53BB\uFF1F"), false);
							});
							PasterdreamMod.queueServerWork(400, () -> {
								if (entityiterator instanceof Player _player && !_player.level().isClientSide())
									_player.displayClientMessage(Component.literal("\u65E0\u540D\uFF1A\u94A5\u5319\u5C31\u5728\u8FD9\u91CC\uFF0C\u51FA\u53E3\u4E5F\u5C31\u5728\u8FD9\u4E00\u5C42\u7684\u4E0B\u65B9\uFF0C\u4F60\u660E\u767D\u7684\u3002"),
											false);
							});
							PasterdreamMod.queueServerWork(440, () -> {
								if (entityiterator instanceof Player _player && !_player.level().isClientSide())
									_player.displayClientMessage(
											Component.literal("\u65E0\u540D\uFF1A\u6211\u5728\u8FD9\u91CC...\u4E5F\u662F\u56E0\u4E3A\u6211\u53D7\u5230\u4E00\u4E2A\u8BC5\u5492\uFF0C\u5B88\u62A4\u627F\u8BFA\u7684\u8BC5\u5492\u3002"), false);
							});
							PasterdreamMod.queueServerWork(480, () -> {
								if (entityiterator instanceof Player _player && !_player.level().isClientSide())
									_player.displayClientMessage(Component.literal("\u65E0\u540D\uFF1A\u4F46\u6211\u5E76\u4E0D\u53CD\u611F... \u8FD9\u4E8B\u51FA\u6709\u56E0\u3002"), false);
							});
							PasterdreamMod.queueServerWork(520, () -> {
								if (entityiterator instanceof Player _player && !_player.level().isClientSide())
									_player.displayClientMessage(Component.literal("\u65E0\u540D\uFF1A\u79BB\u5F00\u8FD9\u91CC\u5427...\u4E00\u76F4\u5F85\u5728\u8FD9\u91CC\u53EA\u4F1A\u4FB5\u8680\u4F60\u7684\u7CBE\u795E..."), false);
							});
							PasterdreamMod.queueServerWork(560, () -> {
								if (entityiterator instanceof Player _player && !_player.level().isClientSide())
									_player.displayClientMessage(Component.literal("\u65E0\u540D\uFF1A\u5728\u4F60\u8FD8\u53EF\u4EE5\u56DE\u5BB6\u7684\u65F6\u5019..."), false);
							});
							PasterdreamMod.queueServerWork(600, () -> {
								if (entityiterator instanceof Player _player && !_player.level().isClientSide())
									_player.displayClientMessage(Component.literal("\u00A7a\u65B0\u7684\u4E3B\u7EBF\u8FDB\u5EA6\u5DF2\u89E3\u9501"), false);
								if (entityiterator instanceof ServerPlayer _player) {
									Advancement _adv = _player.server.getAdvancements().getAdvancement(new ResourceLocation("pasterdream:achievement_shadow_npc_1"));
									AdvancementProgress _ap = _player.getAdvancements().getOrStartProgress(_adv);
									if (!_ap.isDone()) {
										for (String criteria : _ap.getRemainingCriteria())
											_player.getAdvancements().award(_adv, criteria);
									}
								}
							});
						}
					}
				}
			}
			if ((sourceentity instanceof ServerPlayer _plr75 && _plr75.level() instanceof ServerLevel
					&& _plr75.getAdvancements().getOrStartProgress(_plr75.server.getAdvancements().getAdvancement(new ResourceLocation("pasterdream:achievement_shadow_npc_1"))).isDone()) == true
					&& (sourceentity instanceof ServerPlayer _plr76 && _plr76.level() instanceof ServerLevel
							&& _plr76.getAdvancements().getOrStartProgress(_plr76.server.getAdvancements().getAdvancement(new ResourceLocation("pasterdream:achievement_shadow_npc_2"))).isDone()) == false
					|| (sourceentity instanceof ServerPlayer _plr77 && _plr77.level() instanceof ServerLevel
							&& _plr77.getAdvancements().getOrStartProgress(_plr77.server.getAdvancements().getAdvancement(new ResourceLocation("pasterdream:achievement_shadow_npc_3"))).isDone()) == false
							&& (sourceentity instanceof ServerPlayer _plr78 && _plr78.level() instanceof ServerLevel
									&& _plr78.getAdvancements().getOrStartProgress(_plr78.server.getAdvancements().getAdvancement(new ResourceLocation("pasterdream:achievement_shadow_npc_2"))).isDone()) == true) {
				entity.getPersistentData().putBoolean("switch", true);
				if (entity instanceof ShadowNpc0Entity) {
					((ShadowNpc0Entity) entity).setAnimation("say");
				}
				{
					final Vec3 _center = new Vec3(x, y, z);
					List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(16 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
					for (Entity entityiterator : _entfound) {
						if (entityiterator instanceof Player) {
							if (entityiterator instanceof Player _player && !_player.level().isClientSide())
								_player.displayClientMessage(Component.literal("\u65E0\u540D\uFF1A\u4F60\u4E3A\u4F55\u5982\u6B64\u6267\u7740..."), false);
							PasterdreamMod.queueServerWork(40, () -> {
								if (entityiterator instanceof Player _player && !_player.level().isClientSide())
									_player.displayClientMessage(Component.literal("\u65E0\u540D\uFF1A\u8BF7\u4E0D\u8981\u518D\u5C1D\u8BD5\u7EE7\u7EED\u5411\u4E0B\u4E86\uFF0C\u8FD9\u4F1A\u5371\u53CA\u4F60\u7684\u6027\u547D\u3002"), false);
							});
							PasterdreamMod.queueServerWork(80, () -> {
								if (entityiterator instanceof Player _player && !_player.level().isClientSide())
									_player.displayClientMessage(Component.literal(
											"\u65E0\u540D\uFF1A\u8FD9\u79CD\u6E34\u6C42\u529B\u91CF\u7684\u60F3\u6CD5\u53EA\u4F1A\u8BA9\u6697\u5F71\u66F4\u5BB9\u6613\u63A7\u5236\u4F60\u7684\u7CBE\u795E...\u5728\u4F60\u653E\u677E\u4E0B\u6765\u7684\u77AC\u95F4\u3002"),
											false);
							});
							PasterdreamMod.queueServerWork(120, () -> {
								if (entityiterator instanceof Player _player && !_player.level().isClientSide())
									_player.displayClientMessage(Component.literal("\u65E0\u540D\uFF1A\u6211\u4E0D\u80FD\u8BA9\u4F60\u7EE7\u7EED\u7559\u5728\u8FD9\u91CC\u4E86...\u8BF7\u79BB\u5F00\u5427\u3002"), false);
							});
							PasterdreamMod.queueServerWork(150, () -> {
								if (entityiterator instanceof Player _player && !_player.level().isClientSide())
									_player.displayClientMessage(Component.literal("\u00A7a\u65B0\u7684\u4E3B\u7EBF\u8FDB\u5EA6\u5DF2\u89E3\u9501"), false);
								if (entityiterator instanceof ServerPlayer _player) {
									Advancement _adv = _player.server.getAdvancements().getAdvancement(new ResourceLocation("pasterdream:achievement_shadow_npc_2"));
									AdvancementProgress _ap = _player.getAdvancements().getOrStartProgress(_adv);
									if (!_ap.isDone()) {
										for (String criteria : _ap.getRemainingCriteria())
											_player.getAdvancements().award(_adv, criteria);
									}
								}
								if (world instanceof Level _level) {
									if (!_level.isClientSide()) {
										_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("pasterdream:portal")), SoundSource.NEUTRAL, 1, 1);
									} else {
										_level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("pasterdream:portal")), SoundSource.NEUTRAL, 1, 1, false);
									}
								}
							});
							PasterdreamMod.queueServerWork(160, () -> {
								if (PasterdreamConfigCommonConfiguration.SHADOW_NPC_THIRD_DIALOGUE_AFTER_TP_PLAYER_BACK_TO_OVERWORLD.get() == true) {
									if (entityiterator instanceof ServerPlayer _player && !_player.level().isClientSide()) {
										ResourceKey<Level> destinationType = Level.OVERWORLD;
										if (_player.level().dimension() == destinationType)
											return;
										ServerLevel nextLevel = _player.server.getLevel(destinationType);
										if (nextLevel != null) {
											_player.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.WIN_GAME, 0));
											_player.teleportTo(nextLevel, _player.getX(), _player.getY(), _player.getZ(), _player.getYRot(), _player.getXRot());
											_player.connection.send(new ClientboundPlayerAbilitiesPacket(_player.getAbilities()));
											for (MobEffectInstance _effectinstance : _player.getActiveEffects())
												_player.connection.send(new ClientboundUpdateMobEffectPacket(_player.getId(), _effectinstance));
											_player.connection.send(new ClientboundLevelEventPacket(1032, BlockPos.ZERO, 0, false));
										}
									}
									{
										Entity _ent = entityiterator;
										_ent.teleportTo(
												((entityiterator instanceof ServerPlayer _player && !_player.level().isClientSide())
														? ((_player.getRespawnDimension().equals(_player.level().dimension()) && _player.getRespawnPosition() != null) ? _player.getRespawnPosition().getX() : _player.level().getLevelData().getXSpawn())
														: 0),
												((entityiterator instanceof ServerPlayer _player && !_player.level().isClientSide())
														? ((_player.getRespawnDimension().equals(_player.level().dimension()) && _player.getRespawnPosition() != null) ? _player.getRespawnPosition().getY() : _player.level().getLevelData().getYSpawn())
														: 0),
												((entityiterator instanceof ServerPlayer _player && !_player.level().isClientSide())
														? ((_player.getRespawnDimension().equals(_player.level().dimension()) && _player.getRespawnPosition() != null) ? _player.getRespawnPosition().getZ() : _player.level().getLevelData().getZSpawn())
														: 0));
										if (_ent instanceof ServerPlayer _serverPlayer)
											_serverPlayer.connection.teleport(((entityiterator instanceof ServerPlayer _player && !_player.level().isClientSide())
													? ((_player.getRespawnDimension().equals(_player.level().dimension()) && _player.getRespawnPosition() != null) ? _player.getRespawnPosition().getX() : _player.level().getLevelData().getXSpawn())
													: 0),
													((entityiterator instanceof ServerPlayer _player && !_player.level().isClientSide())
															? ((_player.getRespawnDimension().equals(_player.level().dimension()) && _player.getRespawnPosition() != null)
																	? _player.getRespawnPosition().getY()
																	: _player.level().getLevelData().getYSpawn())
															: 0),
													((entityiterator instanceof ServerPlayer _player && !_player.level().isClientSide())
															? ((_player.getRespawnDimension().equals(_player.level().dimension()) && _player.getRespawnPosition() != null)
																	? _player.getRespawnPosition().getZ()
																	: _player.level().getLevelData().getZSpawn())
															: 0),
													_ent.getYRot(), _ent.getXRot());
									}
								}
							});
						}
					}
				}
			}
			if ((sourceentity instanceof ServerPlayer _plr101 && _plr101.level() instanceof ServerLevel
					&& _plr101.getAdvancements().getOrStartProgress(_plr101.server.getAdvancements().getAdvancement(new ResourceLocation("pasterdream:achievement_shadow_npc_3"))).isDone()) == true
					&& (sourceentity instanceof ServerPlayer _plr102 && _plr102.level() instanceof ServerLevel
							&& _plr102.getAdvancements().getOrStartProgress(_plr102.server.getAdvancements().getAdvancement(new ResourceLocation("pasterdream:achievement_shadow_npc_4"))).isDone()) == false) {
				entity.getPersistentData().putBoolean("switch", true);
				if (entity instanceof ShadowNpc0Entity) {
					((ShadowNpc0Entity) entity).setAnimation("say");
				}
				{
					final Vec3 _center = new Vec3(x, y, z);
					List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(16 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
					for (Entity entityiterator : _entfound) {
						if (entityiterator instanceof Player) {
							if (entityiterator instanceof Player _player && !_player.level().isClientSide())
								_player.displayClientMessage(Component.literal("\u65E0\u540D\uFF1A\u4F60\u5DF2\u7ECF...\u89C1\u5230\u5B83\u4EEC\u51FA\u73B0\u5728\u4E86\u4F60\u7684\u4E16\u754C\uFF1F"), false);
							PasterdreamMod.queueServerWork(40, () -> {
								if (entityiterator instanceof Player _player && !_player.level().isClientSide())
									_player.displayClientMessage(Component.literal("\u65E0\u540D\uFF1A\u86F0\u4F0F\u4E8E\u9ED1\u6697\uFF0C\u5728\u6CA1\u6709\u5149\u4EAE\u7684\u5730\u65B9\uFF0C\u5B83\u4EEC\u4F3A\u673A\u800C\u52A8..."), false);
							});
							PasterdreamMod.queueServerWork(80, () -> {
								if (entityiterator instanceof Player _player && !_player.level().isClientSide())
									_player.displayClientMessage(
											Component.literal("\u65E0\u540D\uFF1A\u5F71\u5B50...\u9ED1\u6697...\u5B83\u4EEC\u662F\u8FD9\u4E9B\u7684\u5316\u8EAB\uFF0C\u8FD8\u662F\u706F\u5F71\u4E4B\u4E0B\u7684\u53E6\u4E00\u79CD\u5B58\u5728\uFF1F"),
											false);
							});
							PasterdreamMod.queueServerWork(120, () -> {
								if (entityiterator instanceof Player _player && !_player.level().isClientSide())
									_player.displayClientMessage(Component.literal("\u65E0\u540D\uFF1A\u5B83\u4EEC\u662F\u5B9E\u9645\u5B58\u5728\u7684\u7269\u8D28\uFF1F\u8FD8\u53EA\u662F\u4EC5\u5B58\u4E8E\u4EBA\u4EEC\u601D\u60F3\u7684\u68A6..."),
											false);
							});
							PasterdreamMod.queueServerWork(160, () -> {
								if (entityiterator instanceof Player _player && !_player.level().isClientSide())
									_player.displayClientMessage(Component.literal("\u65E0\u540D\uFF1A\u5373\u4F7F\u8EAB\u4E34\u6B64\u5730\u7684\u6211\u4E5F\u4E0D\u660E\u786E\u8FD9\u4E00\u5207\u7684\u7B54\u6848..."), false);
							});
							PasterdreamMod.queueServerWork(200, () -> {
								if (entityiterator instanceof Player _player && !_player.level().isClientSide())
									_player.displayClientMessage(Component.literal("\u65E0\u540D\uFF1A\u54C8...\u62B1\u6B49\uFF0C\u6211\u5570\u55E6\u4E86\u3002\u95EE\u6211\u66FE\u963B\u6B62\u4F60\u7684\u539F\u56E0\uFF1F"), false);
							});
							PasterdreamMod.queueServerWork(240, () -> {
								if (entityiterator instanceof Player _player && !_player.level().isClientSide())
									_player.displayClientMessage(Component.literal(
											"\u65E0\u540D\uFF1A\u53EF\u6CA1\u51E0\u4E2A\u4EBA\u80FD\u89C1\u5230\u8FD9\u4E9B\u602A\u7269\u4E4B\u540E\u8FD8\u6D3B\u7740\u7684\uFF0C\u4F46\u73B0\u5728\u770B\u6765\u4F60\u4E0E\u5E38\u4EBA\u4E0D\u4E00\u6837\u3002"),
											false);
							});
							PasterdreamMod.queueServerWork(280, () -> {
								if (entityiterator instanceof Player _player && !_player.level().isClientSide())
									_player.displayClientMessage(Component.literal("\u65E0\u540D\uFF1A\u6211\u80FD\u611F\u53D7\u5230...\u4F60\u53EF\u4EE5\u8FD0\u7528\u90A3\u4EFD\u529B\u91CF\uFF0C\u6765\u81EA\u4F60\u7684\u7CBE\u795E\u3002"), false);
							});
							PasterdreamMod.queueServerWork(320, () -> {
								if (entityiterator instanceof Player _player && !_player.level().isClientSide())
									_player.displayClientMessage(Component.literal(
											"\u65E0\u540D\uFF1A\u5982\u679C\u6211\u6CA1\u731C\u9519\uFF0C\u4F60\u662F\u4E3B\u52A8\u6765\u5230\u8FD9\u91CC\u7684\u5427\uFF0C\u800C\u4E0D\u662F\u88AB\u90A3\u4E9B\u4F1A\u52A8\u7684\u5F71\u5B50\u62C9\u8FDB\u6765..."),
											false);
							});
							PasterdreamMod.queueServerWork(360, () -> {
								if (entityiterator instanceof Player _player && !_player.level().isClientSide())
									_player.displayClientMessage(Component.literal("\u65E0\u540D\uFF1A..."), false);
							});
							PasterdreamMod.queueServerWork(400, () -> {
								if (entityiterator instanceof Player _player && !_player.level().isClientSide())
									_player.displayClientMessage(
											Component.literal(
													"\u65E0\u540D\uFF1A\u5176\u5B9E...\u8FD9\u4E9B\u5F71\u5B50\u4F1A\u524D\u5F80\u4F60\u7684\u4E16\u754C\u4E5F\u8BB8\u548C\u6211\u6709\u5173\uFF0C\u5C31\u5728\u8FD9\u5C42\u5730\u7262\u4E4B\u4E0B..."),
											false);
							});
							PasterdreamMod.queueServerWork(440, () -> {
								if (entityiterator instanceof Player _player && !_player.level().isClientSide())
									_player.displayClientMessage(Component.literal("\u65E0\u540D\uFF1A\u4E0D\u8FC7...\u6211\u9700\u8981\u53BB\u51C6\u5907\u4E00\u4E0B\uFF0C\u4E5F\u8BF7\u628A\u4F60\u7684\u60F3\u6CD5\u544A\u8BC9\u6211\u3002"), false);
							});
							PasterdreamMod.queueServerWork(480, () -> {
								if (entityiterator instanceof Player _player && !_player.level().isClientSide())
									_player.displayClientMessage(Component.literal("\u65E0\u540D\uFF1A\u5728\u6211\u4EEC\u4E0B\u6B21\u89C1\u9762..."), false);
							});
							PasterdreamMod.queueServerWork(500, () -> {
								if (entityiterator instanceof Player _player && !_player.level().isClientSide())
									_player.displayClientMessage(Component.literal("\u00A7a\u65B0\u7684\u4E3B\u7EBF\u8FDB\u5EA6\u5DF2\u89E3\u9501"), false);
								if (entityiterator instanceof ServerPlayer _player) {
									Advancement _adv = _player.server.getAdvancements().getAdvancement(new ResourceLocation("pasterdream:achievement_shadow_npc_4"));
									AdvancementProgress _ap = _player.getAdvancements().getOrStartProgress(_adv);
									if (!_ap.isDone()) {
										for (String criteria : _ap.getRemainingCriteria())
											_player.getAdvancements().award(_adv, criteria);
									}
								}
							});
						}
					}
				}
			}
			if ((sourceentity instanceof ServerPlayer _plr135 && _plr135.level() instanceof ServerLevel
					&& _plr135.getAdvancements().getOrStartProgress(_plr135.server.getAdvancements().getAdvancement(new ResourceLocation("pasterdream:achievement_shadow_npc_4"))).isDone()) == true
					&& (sourceentity instanceof ServerPlayer _plr136 && _plr136.level() instanceof ServerLevel
							&& _plr136.getAdvancements().getOrStartProgress(_plr136.server.getAdvancements().getAdvancement(new ResourceLocation("pasterdream:achievement_shadow_npc_5"))).isDone()) == false) {
				entity.getPersistentData().putBoolean("switch", true);
				if (entity instanceof ShadowNpc0Entity) {
					((ShadowNpc0Entity) entity).setAnimation("say");
				}
				{
					final Vec3 _center = new Vec3(x, y, z);
					List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(16 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
					for (Entity entityiterator : _entfound) {
						if (entityiterator instanceof Player) {
							if (entityiterator instanceof Player _player && !_player.level().isClientSide())
								_player.displayClientMessage(Component.literal("\u65E0\u540D\uFF1A\u770B\u6765\u4F60\u51C6\u5907\u597D\u4E86..."), false);
							PasterdreamMod.queueServerWork(40, () -> {
								if (entityiterator instanceof Player _player && !_player.level().isClientSide())
									_player.displayClientMessage(Component.literal(
											"\u65E0\u540D\uFF1A\u5728\u8FD9\u5C42\u4E0B\u9762\uFF0C\u5176\u5B9E\u662F\u4E00\u4F4D\u5DF2\u6545\u4E4B\u4EBA\u7684\u575F\u5893\uFF0C\u6211\u7684\u5B58\u5728\u4E5F\u662F\u4E3A\u4E86\u4FE1\u5B88\u90A3\u4EFD\u627F\u8BFA\u3002"),
											false);
							});
							PasterdreamMod.queueServerWork(80, () -> {
								if (entityiterator instanceof Player _player && !_player.level().isClientSide())
									_player.displayClientMessage(Component.literal(
											"\u65E0\u540D\uFF1A\u201C\u5982\u679C\u6211\u5931\u8D25\u4E86\uFF0C\u5C31\u8BF7\u628A\u6211\u57CB\u846C\u5728\u8FD9\u91CC\uFF0C\u6C38\u8FDC\u201D \u8FD9\u662F\u4ED6\u6240\u7559\u4E0B\u7684\u9057\u8A00\u3002"),
											false);
							});
							PasterdreamMod.queueServerWork(120, () -> {
								if (entityiterator instanceof Player _player && !_player.level().isClientSide())
									_player.displayClientMessage(Component.literal(
											"\u65E0\u540D\uFF1A\u4ED6\u7684\u5C38\u4F53\u88AB\u653E\u5728\u66AE\u5F71\u957F\u5E8A\u4E0A\uFF0C\u56E0\u4E3A\u8F6C\u79FB\u7075\u9B42\u7684\u5B9E\u9A8C\uFF0C\u90A3\u5177\u8089\u4F53\u5316\u4E3A\u4E86\u6D41\u6DCC\u7684\u9634\u5F71\uFF0C\u5145\u65A5\u6EE1\u90A3\u4E2A\u623F\u95F4\u3002"),
											false);
							});
							PasterdreamMod.queueServerWork(160, () -> {
								if (entityiterator instanceof Player _player && !_player.level().isClientSide())
									_player.displayClientMessage(Component.literal(
											"\u65E0\u540D\uFF1A\u201C\u53EA\u6709\u771F\u6B63\u7684\u5931\u53BB\uFF0C\u624D\u80FD\u4F53\u4F1A\u5230\u5B58\u5728\u7684\u610F\u4E49\u201D \u5728\u6211\u7684\u68A6\u91CC\uFF0C\u8FD9\u662F\u4ED6\u7684\u58F0\u97F3\u3002"),
											false);
							});
							PasterdreamMod.queueServerWork(200, () -> {
								if (entityiterator instanceof Player _player && !_player.level().isClientSide())
									_player.displayClientMessage(Component.literal(
											"\u65E0\u540D\uFF1A\u5982\u679C\u4F60\u6267\u610F\u8981\u6597\u4E89\u4E0B\u53BB...\u90A3\u4E48\u5C31\u518D\u6B21\u89E6\u6478\u4E0B\u65B9\u7684\u95E8\u6249\u5427\uFF0C\u5982\u679C\u4F60\u5F97\u5230\u4E86\u8BA4\u53EF\uFF0C\u95E8\u81EA\u7136\u4F1A\u6253\u5F00\u3002"),
											false);
							});
							PasterdreamMod.queueServerWork(240, () -> {
								if (entityiterator instanceof Player _player && !_player.level().isClientSide())
									_player.displayClientMessage(Component.literal("\u65E0\u540D\uFF1A\u53BB\u8F7B\u629A\u66AE\u5F71\u957F\u5E8A\uFF0C\u505A\u51FA\u4F60\u5185\u5FC3\u7684\u7B2C\u4E00\u9009\u62E9\uFF0C\u4E0D\u8BA1\u7ED3\u679C\u3002"),
											false);
							});
							PasterdreamMod.queueServerWork(260, () -> {
								if (entityiterator instanceof Player _player && !_player.level().isClientSide())
									_player.displayClientMessage(Component.literal("\u00A7a\u524D\u5F80\u4E0B\u5C42\u6253\u5F00\u89E6\u6478\u5927\u95E8\u7684\u201C\u773C\u775B\u201D"), false);
								if (entityiterator instanceof ServerPlayer _player) {
									Advancement _adv = _player.server.getAdvancements().getAdvancement(new ResourceLocation("pasterdream:achievement_shadow_npc_5"));
									AdvancementProgress _ap = _player.getAdvancements().getOrStartProgress(_adv);
									if (!_ap.isDone()) {
										for (String criteria : _ap.getRemainingCriteria())
											_player.getAdvancements().award(_adv, criteria);
									}
								}
							});
						}
					}
				}
			}
		}
	}
}
