package net.pasterdream.procedures;

import net.pasterdream.capability.SanCapability;
import net.pasterdream.init.PasterdreamModMobEffects;
import net.pasterdream.init.PasterdreamModItems;
import net.pasterdream.init.PasterdreamModBlocks;
import net.pasterdream.init.PasterdreamModAttributes;
import net.pasterdream.PasterdreamMod;

import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket;
import net.minecraft.network.protocol.game.ClientboundLevelEventPacket;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.BlockPos;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class SleepPr0Procedure {
	@SubscribeEvent
	public static void onPlayerInBed(PlayerSleepInBedEvent event) {
		execute(event, event.getEntity().level(), event.getPos().getX(), event.getPos().getY(), event.getPos().getZ(), event.getEntity());
	}

	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		execute(null, world, x, y, z, entity);
	}

	private static void execute(@Nullable Event event, LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		if ((world instanceof Level _lvl0 && _lvl0.isDay()) == false) {
			if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
				_entity.addEffect(new MobEffectInstance(PasterdreamModMobEffects.REST_BUFF.get(), 3600, 0, false, false));
			if ((entity instanceof ServerPlayer _plr2 && _plr2.level() instanceof ServerLevel
					&& _plr2.getAdvancements().getOrStartProgress(_plr2.server.getAdvancements().getAdvancement(new ResourceLocation("pasterdream:achievement_start"))).isDone()) == true
					&& (entity instanceof ServerPlayer _plr3 && _plr3.level() instanceof ServerLevel
							&& _plr3.getAdvancements().getOrStartProgress(_plr3.server.getAdvancements().getAdvancement(new ResourceLocation("pasterdream:achievement_a_0"))).isDone()) == false) {
				PasterdreamMod.queueServerWork(110, () -> {
					if (entity instanceof Player _player) {
						ItemStack _setstack = new ItemStack(PasterdreamModItems.DREAMNOTES_1.get());
						_setstack.setCount(1);
						ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
					}
					if (entity instanceof Player _player && !_player.level().isClientSide())
						_player.displayClientMessage(Component.literal(
								"\u00A75\u4F60\u5728\u7761\u68A6\u4E2D\u60CA\u9192\uFF0C\u80CC\u540E\u5192\u51FA\u4E86\u4E9B\u8BB8\u51B7\u6C57\uFF0C\u4F60\u56DE\u5FC6\u8D77\u81EA\u5DF1\u68A6\u89C1\u4E86\u5F80\u65E5\u63A2\u7D22\u4E2D\u9047\u89C1\u7684\u5947\u602A\u88C2\u9699\uFF0C\u5728\u6162\u6162\u9760\u8FD1\u5E76\u51DD\u89C6\u7740\u4F60\u3002"),
								false);
					if (entity instanceof Player _player && !_player.level().isClientSide())
						_player.displayClientMessage(Component.literal(
								"\u00A75\u4F60\u62FF\u8D77\u9644\u8FD1\u7684\u6750\u6599\u8FC5\u901F\u5730\u628A\u8FD9\u4E9B\u68A6\u5883\u8BB0\u5F55\u4E0B\u6765\uFF0C\u5728\u8FD9\u4E4B\u540E\u4F60\u5C31\u5931\u53BB\u4E86\u5BF9\u8FD9\u6BB5\u68A6\u5883\u7684\u8BB0\u5FC6\u3002"),
								false);
					if (entity instanceof Player _player && !_player.level().isClientSide())
						_player.displayClientMessage(Component.literal("\u00A75\u6211\u5FC5\u987B\u77E5\u9053\u53D1\u751F\u4E86\u4EC0\u4E48..."), false);
				});
			}
			if ((entity instanceof LivingEntity _livEnt9 && _livEnt9.hasEffect(PasterdreamModMobEffects.DREAMWISH_BUFF.get())) == true) {
				PasterdreamMod.queueServerWork(60, () -> {
					if (entity instanceof ServerPlayer _player && !_player.level().isClientSide()) {
						ResourceKey<Level> destinationType = ResourceKey.create(Registries.DIMENSION, new ResourceLocation("pasterdream:dyedream_world"));
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
						Entity _ent = entity;
						_ent.teleportTo(0.5, 108, 0.5);
						if (_ent instanceof ServerPlayer _serverPlayer)
							_serverPlayer.connection.teleport(0.5, 108, 0.5, _ent.getYRot(), _ent.getXRot());
					}
				});
			}
			if ((entity instanceof ServerPlayer _plr13 && _plr13.level() instanceof ServerLevel
					&& _plr13.getAdvancements().getOrStartProgress(_plr13.server.getAdvancements().getAdvancement(new ResourceLocation("pasterdream:achievement_hide_7"))).isDone()) == true
					&& (entity instanceof ServerPlayer _plr14 && _plr14.level() instanceof ServerLevel
							&& _plr14.getAdvancements().getOrStartProgress(_plr14.server.getAdvancements().getAdvancement(new ResourceLocation("pasterdream:achievement_hide_8"))).isDone()) == false) {
				PasterdreamMod.queueServerWork(110, () -> {
					if (entity instanceof Player _player) {
						ItemStack _setstack = new ItemStack(PasterdreamModItems.DREAMNOTES_8.get());
						_setstack.setCount(1);
						ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
					}
					if (entity instanceof Player _player && !_player.level().isClientSide())
						_player.displayClientMessage(Component.literal(
								"\u00A75\u4F60\u5728\u7761\u68A6\u4E2D\u60CA\u9192\uFF0C\u80CC\u540E\u5192\u51FA\u4E86\u4E9B\u8BB8\u51B7\u6C57\uFF0C\u4F60\u56DE\u5FC6\u8D77\u81EA\u5DF1\u68A6\u89C1\u4E86\u5F80\u65E5\u63A2\u7D22\u4E2D\u9047\u89C1\u7684\u5947\u602A\u88C2\u9699\uFF0C\u5728\u6162\u6162\u9760\u8FD1\u5E76\u51DD\u89C6\u7740\u4F60\u3002"),
								false);
					if (entity instanceof Player _player && !_player.level().isClientSide())
						_player.displayClientMessage(Component.literal(
								"\u00A75\u4F60\u62FF\u8D77\u9644\u8FD1\u7684\u6750\u6599\u8FC5\u901F\u5730\u628A\u8FD9\u4E9B\u68A6\u5883\u8BB0\u5F55\u4E0B\u6765\uFF0C\u5728\u8FD9\u4E4B\u540E\u4F60\u5C31\u5931\u53BB\u4E86\u5BF9\u8FD9\u6BB5\u68A6\u5883\u7684\u8BB0\u5FC6\u3002"),
								false);
					if (entity instanceof Player _player && !_player.level().isClientSide())
						_player.displayClientMessage(Component.literal("\u00A75\u6211\u5FC5\u987B\u77E5\u9053\u53D1\u751F\u4E86\u4EC0\u4E48..."), false);
				});
			}
			if ((world.getBlockState(BlockPos.containing(x, y + 2, z))).getBlock() == PasterdreamModBlocks.TWILIGHT_LANTERN.get()) {
				if ((new Object() {
					public boolean getValue(LevelAccessor world, BlockPos pos, String tag) {
						BlockEntity blockEntity = world.getBlockEntity(pos);
						if (blockEntity != null)
							return blockEntity.getPersistentData().getBoolean(tag);
						return false;
					}
				}.getValue(world, BlockPos.containing(x, y + 2, z), "key")) == true && entity instanceof ServerPlayer _plr23 && _plr23.level() instanceof ServerLevel
						&& _plr23.getAdvancements().getOrStartProgress(_plr23.server.getAdvancements().getAdvancement(new ResourceLocation("pasterdream:achievement_hide_9"))).isDone()) {
					PasterdreamMod.queueServerWork(95, () -> {
						world.setBlock(BlockPos.containing(x, y, z), Blocks.AIR.defaultBlockState(), 3);
						world.setBlock(BlockPos.containing(x, y, z), Blocks.BLACK_BED.defaultBlockState(), 3);
                        if(entity instanceof Player pl)
                        {
                            SanCapability.addPlayerSanWithCheck(pl, -10);
                        }
						WorldSpawnPr1Procedure.execute(world, entity);
					});
				}
			}
		}
	}
}
