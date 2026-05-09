package net.pasterdream.procedures;

import net.pasterdream.init.PasterdreamModMobEffects;
import net.pasterdream.init.PasterdreamModEntities;
import net.pasterdream.init.PasterdreamModBlocks;
import net.pasterdream.entity.AaroncosRighthand0Entity;
import net.pasterdream.entity.AaroncosLefthand0Entity;
import net.pasterdream.PasterdreamMod;

import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.GameType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.LivingEntity;
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
import java.util.ArrayList;

public class AaroncoshandspawnblockPr1Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		if ((new Object() {
			public boolean getValue(LevelAccessor world, BlockPos pos, String tag) {
				BlockEntity blockEntity = world.getBlockEntity(pos);
				if (blockEntity != null)
					return blockEntity.getPersistentData().getBoolean(tag);
				return false;
			}
		}.getValue(world, BlockPos.containing(x, y, z), "switch")) == true) {
			if (new Object() {
				public double getValue(LevelAccessor world, BlockPos pos, String tag) {
					BlockEntity blockEntity = world.getBlockEntity(pos);
					if (blockEntity != null)
						return blockEntity.getPersistentData().getDouble(tag);
					return -1;
				}
			}.getValue(world, BlockPos.containing(x, y, z), "time0") >= 150) {
				if (!world.isClientSide()) {
					BlockPos _bp = BlockPos.containing(x, y, z);
					BlockEntity _blockEntity = world.getBlockEntity(_bp);
					BlockState _bs = world.getBlockState(_bp);
					if (_blockEntity != null)
						_blockEntity.getPersistentData().putDouble("time0", 0);
					if (world instanceof Level _level)
						_level.sendBlockUpdated(_bp, _bs, _bs, 3);
				}
				if (world instanceof Level _level) {
					if (!_level.isClientSide()) {
						_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("pasterdream:aaroncos_music")), SoundSource.WEATHER, 1, 1);
					} else {
						_level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("pasterdream:aaroncos_music")), SoundSource.WEATHER, 1, 1, false);
					}
				}
			} else {
				if (!world.isClientSide()) {
					BlockPos _bp = BlockPos.containing(x, y, z);
					BlockEntity _blockEntity = world.getBlockEntity(_bp);
					BlockState _bs = world.getBlockState(_bp);
					if (_blockEntity != null)
						_blockEntity.getPersistentData().putDouble("time0", (new Object() {
							public double getValue(LevelAccessor world, BlockPos pos, String tag) {
								BlockEntity blockEntity = world.getBlockEntity(pos);
								if (blockEntity != null)
									return blockEntity.getPersistentData().getDouble(tag);
								return -1;
							}
						}.getValue(world, BlockPos.containing(x, y, z), "time0") + 1));
					if (world instanceof Level _level)
						_level.sendBlockUpdated(_bp, _bs, _bs, 3);
				}
			}
			if (new Object() {
				public double getValue(LevelAccessor world, BlockPos pos, String tag) {
					BlockEntity blockEntity = world.getBlockEntity(pos);
					if (blockEntity != null)
						return blockEntity.getPersistentData().getDouble(tag);
					return -1;
				}
			}.getValue(world, BlockPos.containing(x, y, z), "time0") == 50) {
				if (world instanceof ServerLevel _level) {
					Entity entityToSpawn = PasterdreamModEntities.TERRORBEAK.get().spawn(_level, BlockPos.containing(x, y, z + 12), MobSpawnType.MOB_SUMMONED);
					if (entityToSpawn != null) {
						entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
					}
				}
				if (world instanceof ServerLevel _level) {
					Entity entityToSpawn = PasterdreamModEntities.TERRORBEAK.get().spawn(_level, BlockPos.containing(x, y, z - 12), MobSpawnType.MOB_SUMMONED);
					if (entityToSpawn != null) {
						entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
					}
				}
			}
			if (new Object() {
				public double getValue(LevelAccessor world, BlockPos pos, String tag) {
					BlockEntity blockEntity = world.getBlockEntity(pos);
					if (blockEntity != null)
						return blockEntity.getPersistentData().getDouble(tag);
					return -1;
				}
			}.getValue(world, BlockPos.containing(x, y, z), "time0") == 100) {
				if (world instanceof ServerLevel _level) {
					Entity entityToSpawn = PasterdreamModEntities.TERRORBEAK.get().spawn(_level, BlockPos.containing(x, y, z + 12), MobSpawnType.MOB_SUMMONED);
					if (entityToSpawn != null) {
						entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
					}
				}
				if (world instanceof ServerLevel _level) {
					Entity entityToSpawn = PasterdreamModEntities.TERRORBEAK.get().spawn(_level, BlockPos.containing(x, y, z - 12), MobSpawnType.MOB_SUMMONED);
					if (entityToSpawn != null) {
						entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
					}
				}
			}
			if (new Object() {
				public double getValue(LevelAccessor world, BlockPos pos, String tag) {
					BlockEntity blockEntity = world.getBlockEntity(pos);
					if (blockEntity != null)
						return blockEntity.getPersistentData().getDouble(tag);
					return -1;
				}
			}.getValue(world, BlockPos.containing(x, y, z), "time0") == 150) {
				if (world instanceof ServerLevel _level) {
					Entity entityToSpawn = PasterdreamModEntities.TERRORBEAK.get().spawn(_level, BlockPos.containing(x, y, z + 12), MobSpawnType.MOB_SUMMONED);
					if (entityToSpawn != null) {
						entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
					}
				}
				if (world instanceof ServerLevel _level) {
					Entity entityToSpawn = PasterdreamModEntities.TERRORBEAK.get().spawn(_level, BlockPos.containing(x, y, z - 12), MobSpawnType.MOB_SUMMONED);
					if (entityToSpawn != null) {
						entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
					}
				}
			}
			if (!(!world.getEntitiesOfClass(AaroncosLefthand0Entity.class, AABB.ofSize(new Vec3(x, y, z), 99, 99, 99), e -> true).isEmpty())
					&& !(!world.getEntitiesOfClass(AaroncosRighthand0Entity.class, AABB.ofSize(new Vec3(x, y, z), 99, 99, 99), e -> true).isEmpty())) {
				world.setBlock(BlockPos.containing(x, y - 1, z), PasterdreamModBlocks.AARONCOS_HAND_CHEST.get().defaultBlockState(), 3);
				world.destroyBlock(BlockPos.containing(x, y, z), false);
				for (Entity entityiterator : new ArrayList<>(world.players())) {
					if (entityiterator instanceof ServerPlayer _player) {
						Advancement _adv = _player.server.getAdvancements().getAdvancement(new ResourceLocation("pasterdream:achievement_shadow_e_0"));
						AdvancementProgress _ap = _player.getAdvancements().getOrStartProgress(_adv);
						if (!_ap.isDone()) {
							for (String criteria : _ap.getRemainingCriteria())
								_player.getAdvancements().award(_adv, criteria);
						}
					}
					if (entityiterator instanceof LivingEntity _entity)
						_entity.removeEffect(PasterdreamModMobEffects.SHADOW_SPYON_BUFF.get());
				}
				PasterdreamMod.queueServerWork(10, () -> {
					for (Entity entityiterator : new ArrayList<>(world.players())) {
						if (entityiterator instanceof Player _player && !_player.level().isClientSide())
							_player.displayClientMessage(Component.literal("\u79BB\u5F00\u5012\u8BA1\u65F6 20\u79D2"), true);
					}
				});
				PasterdreamMod.queueServerWork(210, () -> {
					for (Entity entityiterator : new ArrayList<>(world.players())) {
						if (entityiterator instanceof Player _player && !_player.level().isClientSide())
							_player.displayClientMessage(Component.literal("\u79BB\u5F00\u5012\u8BA1\u65F6 10\u79D2"), true);
					}
				});
				PasterdreamMod.queueServerWork(310, () -> {
					for (Entity entityiterator : new ArrayList<>(world.players())) {
						if (entityiterator instanceof Player _player && !_player.level().isClientSide())
							_player.displayClientMessage(Component.literal("\u79BB\u5F00\u5012\u8BA1\u65F6 5\u79D2"), true);
					}
				});
				PasterdreamMod.queueServerWork(350, () -> {
					for (Entity entityiterator : new ArrayList<>(world.players())) {
						if (entityiterator instanceof Player _player && !_player.level().isClientSide())
							_player.displayClientMessage(Component.literal("\u79BB\u5F00\u5012\u8BA1\u65F6 3\u79D2"), true);
					}
				});
				PasterdreamMod.queueServerWork(400, () -> {
					for (Entity entityiterator : new ArrayList<>(world.players())) {
						if (entityiterator instanceof Player _player && !_player.level().isClientSide())
							_player.displayClientMessage(Component.literal("\u79BB\u5F00\u5012\u8BA1\u65F6 1\u79D2"), true);
					}
				});
				PasterdreamMod.queueServerWork(410, () -> {
					for (Entity entityiterator : new ArrayList<>(world.players())) {
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
						if (entityiterator instanceof ServerPlayer _player)
							_player.setGameMode(GameType.SURVIVAL);
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
								_serverPlayer.connection.teleport(
										((entityiterator instanceof ServerPlayer _player && !_player.level().isClientSide())
												? ((_player.getRespawnDimension().equals(_player.level().dimension()) && _player.getRespawnPosition() != null) ? _player.getRespawnPosition().getX() : _player.level().getLevelData().getXSpawn())
												: 0),
										((entityiterator instanceof ServerPlayer _player && !_player.level().isClientSide())
												? ((_player.getRespawnDimension().equals(_player.level().dimension()) && _player.getRespawnPosition() != null) ? _player.getRespawnPosition().getY() : _player.level().getLevelData().getYSpawn())
												: 0),
										((entityiterator instanceof ServerPlayer _player && !_player.level().isClientSide())
												? ((_player.getRespawnDimension().equals(_player.level().dimension()) && _player.getRespawnPosition() != null) ? _player.getRespawnPosition().getZ() : _player.level().getLevelData().getZSpawn())
												: 0),
										_ent.getYRot(), _ent.getXRot());
						}
					}
					{
						final Vec3 _center = new Vec3(x, y, z);
						List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(75 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
						for (Entity entityiterator : _entfound) {
							if (!(entityiterator instanceof Player)) {
								if (!entityiterator.level().isClientSide())
									entityiterator.discard();
							}
						}
					}
				});
			}
		}
	}
}
