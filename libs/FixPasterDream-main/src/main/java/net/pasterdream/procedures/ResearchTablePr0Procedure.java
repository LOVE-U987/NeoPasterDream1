package net.pasterdream.procedures;

import net.pasterdream.init.PasterdreamModItems;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.util.RandomSource;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;

import java.util.concurrent.atomic.AtomicReference;

public class ResearchTablePr0Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		if ((new Object() {
			public ItemStack getItemStack(LevelAccessor world, BlockPos pos, int slotid) {
				AtomicReference<ItemStack> _retval = new AtomicReference<>(ItemStack.EMPTY);
				BlockEntity _ent = world.getBlockEntity(pos);
				if (_ent != null)
					_ent.getCapability(ForgeCapabilities.ITEM_HANDLER, null).ifPresent(capability -> _retval.set(capability.getStackInSlot(slotid).copy()));
				return _retval.get();
			}
		}.getItemStack(world, BlockPos.containing(x, y, z), 0)).getItem() == PasterdreamModItems.PEN_AND_INK.get()) {
			if ((new Object() {
				public ItemStack getItemStack(LevelAccessor world, BlockPos pos, int slotid) {
					AtomicReference<ItemStack> _retval = new AtomicReference<>(ItemStack.EMPTY);
					BlockEntity _ent = world.getBlockEntity(pos);
					if (_ent != null)
						_ent.getCapability(ForgeCapabilities.ITEM_HANDLER, null).ifPresent(capability -> _retval.set(capability.getStackInSlot(slotid).copy()));
					return _retval.get();
				}
			}.getItemStack(world, BlockPos.containing(x, y, z), 4)).getItem() == PasterdreamModItems.UNKNOWNNOTES_0.get() && (new Object() {
				public ItemStack getItemStack(LevelAccessor world, BlockPos pos, int slotid) {
					AtomicReference<ItemStack> _retval = new AtomicReference<>(ItemStack.EMPTY);
					BlockEntity _ent = world.getBlockEntity(pos);
					if (_ent != null)
						_ent.getCapability(ForgeCapabilities.ITEM_HANDLER, null).ifPresent(capability -> _retval.set(capability.getStackInSlot(slotid).copy()));
					return _retval.get();
				}
			}.getItemStack(world, BlockPos.containing(x, y, z), 5)).getItem() == Blocks.AIR.asItem()) {
				if (entity instanceof ServerPlayer _plr6 && _plr6.level() instanceof ServerLevel
						&& _plr6.getAdvancements().getOrStartProgress(_plr6.server.getAdvancements().getAdvancement(new ResourceLocation("pasterdream:achievement_shadow_start"))).isDone()) {
					{
						BlockEntity _ent = world.getBlockEntity(BlockPos.containing(x, y, z));
						if (_ent != null) {
							final int _slotid = 0;
							final int _amount = 1;
							_ent.getCapability(ForgeCapabilities.ITEM_HANDLER, null).ifPresent(capability -> {
								if (capability instanceof IItemHandlerModifiable) {
									ItemStack _stk = capability.getStackInSlot(_slotid).copy();
									if (_stk.hurt(_amount, RandomSource.create(), null)) {
										_stk.shrink(1);
										_stk.setDamageValue(0);
									}
									((IItemHandlerModifiable) capability).setStackInSlot(_slotid, _stk);
								}
							});
						}
					}
					{
						BlockEntity _ent = world.getBlockEntity(BlockPos.containing(x, y, z));
						if (_ent != null) {
							final int _slotid = 4;
							final int _amount = 1;
							_ent.getCapability(ForgeCapabilities.ITEM_HANDLER, null).ifPresent(capability -> {
								if (capability instanceof IItemHandlerModifiable) {
									ItemStack _stk = capability.getStackInSlot(_slotid).copy();
									_stk.shrink(_amount);
									((IItemHandlerModifiable) capability).setStackInSlot(_slotid, _stk);
								}
							});
						}
					}
					if (world instanceof Level _level) {
						if (!_level.isClientSide()) {
							_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("item.book.page_turn")), SoundSource.BLOCKS, (float) 1.2, 1);
						} else {
							_level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("item.book.page_turn")), SoundSource.BLOCKS, (float) 1.2, 1, false);
						}
					}
					if (entity instanceof ServerPlayer _plr10 && _plr10.level() instanceof ServerLevel
							&& _plr10.getAdvancements().getOrStartProgress(_plr10.server.getAdvancements().getAdvancement(new ResourceLocation("pasterdream:achievement_hide_14"))).isDone() && entity instanceof ServerPlayer _plr11
							&& _plr11.level() instanceof ServerLevel && _plr11.getAdvancements().getOrStartProgress(_plr11.server.getAdvancements().getAdvancement(new ResourceLocation("pasterdream:achievement_hide_15"))).isDone()) {
						if (entity instanceof Player _player)
							_player.closeContainer();
						if (entity instanceof Player _player && !_player.level().isClientSide())
							_player.displayClientMessage(Component.literal("\u7B14\u8BB0\u4E0A\u7684\u7814\u7A76\u5185\u5BB9\u4F60\u90FD\u5DF2\u7ECF\u4E86\u89E3\uFF0C\u4F46\u8FD8\u662F\u80FD\u7ED9\u4F60\u5E26\u6765\u4E9B\u8BB8\u542F\u53D1"), false);
						if (entity instanceof Player _player && !_player.level().isClientSide())
							_player.displayClientMessage(Component.literal("\u7ECF\u9A8C\u503C+50"), false);
						if (entity instanceof Player _player)
							_player.giveExperiencePoints(50);
					}
					if (entity instanceof ServerPlayer _plr16 && _plr16.level() instanceof ServerLevel
							&& _plr16.getAdvancements().getOrStartProgress(_plr16.server.getAdvancements().getAdvancement(new ResourceLocation("pasterdream:achievement_hide_14"))).isDone() && !(entity instanceof ServerPlayer _plr17
									&& _plr17.level() instanceof ServerLevel && _plr17.getAdvancements().getOrStartProgress(_plr17.server.getAdvancements().getAdvancement(new ResourceLocation("pasterdream:achievement_hide_15"))).isDone())) {
						{
							BlockEntity _ent = world.getBlockEntity(BlockPos.containing(x, y, z));
							if (_ent != null) {
								final int _slotid = 5;
								final ItemStack _setstack = new ItemStack(PasterdreamModItems.DREAMNOTES_13.get());
								_setstack.setCount(1);
								_ent.getCapability(ForgeCapabilities.ITEM_HANDLER, null).ifPresent(capability -> {
									if (capability instanceof IItemHandlerModifiable)
										((IItemHandlerModifiable) capability).setStackInSlot(_slotid, _setstack);
								});
							}
						}
						if (entity instanceof Player _player && !_player.level().isClientSide())
							_player.displayClientMessage(Component.literal("\u6B8B\u7834\u7684\u7B14\u8BB0\u88AB\u4F60\u91CD\u65B0\u6392\u5217\uFF0C\u6A21\u7CCA\u7684\u6587\u5B57\u548C\u56FE\u6848\u88AB\u91CD\u65B0\u52FE\u52D2"), false);
						if (entity instanceof Player _player && !_player.level().isClientSide())
							_player.displayClientMessage(Component.literal("\u5DF2\u83B7\u5F97\u65B0\u7684\u5BFB\u68A6\u8005\u7B14\u8BB0"), false);
					}
					if (entity instanceof ServerPlayer _plr21 && _plr21.level() instanceof ServerLevel
							&& _plr21.getAdvancements().getOrStartProgress(_plr21.server.getAdvancements().getAdvancement(new ResourceLocation("pasterdream:achievement_hide_13"))).isDone() && !(entity instanceof ServerPlayer _plr22
									&& _plr22.level() instanceof ServerLevel && _plr22.getAdvancements().getOrStartProgress(_plr22.server.getAdvancements().getAdvancement(new ResourceLocation("pasterdream:achievement_hide_14"))).isDone())) {
						{
							BlockEntity _ent = world.getBlockEntity(BlockPos.containing(x, y, z));
							if (_ent != null) {
								final int _slotid = 5;
								final ItemStack _setstack = new ItemStack(PasterdreamModItems.DREAMNOTES_12.get());
								_setstack.setCount(1);
								_ent.getCapability(ForgeCapabilities.ITEM_HANDLER, null).ifPresent(capability -> {
									if (capability instanceof IItemHandlerModifiable)
										((IItemHandlerModifiable) capability).setStackInSlot(_slotid, _setstack);
								});
							}
						}
						if (entity instanceof Player _player && !_player.level().isClientSide())
							_player.displayClientMessage(Component.literal("\u6B8B\u7834\u7684\u7B14\u8BB0\u88AB\u4F60\u91CD\u65B0\u6392\u5217\uFF0C\u6A21\u7CCA\u7684\u6587\u5B57\u548C\u56FE\u6848\u88AB\u91CD\u65B0\u52FE\u52D2"), false);
						if (entity instanceof Player _player && !_player.level().isClientSide())
							_player.displayClientMessage(Component.literal("\u5DF2\u83B7\u5F97\u65B0\u7684\u5BFB\u68A6\u8005\u7B14\u8BB0"), false);
					}
					if (entity instanceof ServerPlayer _plr26 && _plr26.level() instanceof ServerLevel
							&& _plr26.getAdvancements().getOrStartProgress(_plr26.server.getAdvancements().getAdvancement(new ResourceLocation("pasterdream:achievement_hide_12"))).isDone() && !(entity instanceof ServerPlayer _plr27
									&& _plr27.level() instanceof ServerLevel && _plr27.getAdvancements().getOrStartProgress(_plr27.server.getAdvancements().getAdvancement(new ResourceLocation("pasterdream:achievement_hide_13"))).isDone())) {
						{
							BlockEntity _ent = world.getBlockEntity(BlockPos.containing(x, y, z));
							if (_ent != null) {
								final int _slotid = 5;
								final ItemStack _setstack = new ItemStack(PasterdreamModItems.BLUEPRINT_0.get());
								_setstack.setCount(1);
								_ent.getCapability(ForgeCapabilities.ITEM_HANDLER, null).ifPresent(capability -> {
									if (capability instanceof IItemHandlerModifiable)
										((IItemHandlerModifiable) capability).setStackInSlot(_slotid, _setstack);
								});
							}
						}
						if (entity instanceof Player _player && !_player.level().isClientSide())
							_player.displayClientMessage(Component.literal("\u6B8B\u7834\u7684\u7B14\u8BB0\u88AB\u4F60\u91CD\u65B0\u6392\u5217\uFF0C\u6A21\u7CCA\u7684\u6587\u5B57\u548C\u56FE\u6848\u88AB\u91CD\u65B0\u52FE\u52D2"), false);
						if (entity instanceof Player _player && !_player.level().isClientSide())
							_player.displayClientMessage(Component.literal("\u5DF2\u83B7\u5F97\u65B0\u7684\u5BFB\u68A6\u8005\u7B14\u8BB0"), false);
					}
					if (entity instanceof ServerPlayer _plr31 && _plr31.level() instanceof ServerLevel
							&& _plr31.getAdvancements().getOrStartProgress(_plr31.server.getAdvancements().getAdvancement(new ResourceLocation("pasterdream:achievement_hide_11"))).isDone() && !(entity instanceof ServerPlayer _plr32
									&& _plr32.level() instanceof ServerLevel && _plr32.getAdvancements().getOrStartProgress(_plr32.server.getAdvancements().getAdvancement(new ResourceLocation("pasterdream:achievement_hide_12"))).isDone())) {
						{
							BlockEntity _ent = world.getBlockEntity(BlockPos.containing(x, y, z));
							if (_ent != null) {
								final int _slotid = 5;
								final ItemStack _setstack = new ItemStack(PasterdreamModItems.DREAMNOTES_11.get());
								_setstack.setCount(1);
								_ent.getCapability(ForgeCapabilities.ITEM_HANDLER, null).ifPresent(capability -> {
									if (capability instanceof IItemHandlerModifiable)
										((IItemHandlerModifiable) capability).setStackInSlot(_slotid, _setstack);
								});
							}
						}
						if (entity instanceof Player _player && !_player.level().isClientSide())
							_player.displayClientMessage(Component.literal("\u6B8B\u7834\u7684\u7B14\u8BB0\u88AB\u4F60\u91CD\u65B0\u6392\u5217\uFF0C\u6A21\u7CCA\u7684\u6587\u5B57\u548C\u56FE\u6848\u88AB\u91CD\u65B0\u52FE\u52D2"), false);
						if (entity instanceof Player _player && !_player.level().isClientSide())
							_player.displayClientMessage(Component.literal("\u5DF2\u83B7\u5F97\u65B0\u7684\u5BFB\u68A6\u8005\u7B14\u8BB0"), false);
					}
					if (entity instanceof ServerPlayer _plr36 && _plr36.level() instanceof ServerLevel
							&& _plr36.getAdvancements().getOrStartProgress(_plr36.server.getAdvancements().getAdvancement(new ResourceLocation("pasterdream:achievement_shadow_start"))).isDone() && !(entity instanceof ServerPlayer _plr37
									&& _plr37.level() instanceof ServerLevel && _plr37.getAdvancements().getOrStartProgress(_plr37.server.getAdvancements().getAdvancement(new ResourceLocation("pasterdream:achievement_hide_11"))).isDone())) {
						{
							BlockEntity _ent = world.getBlockEntity(BlockPos.containing(x, y, z));
							if (_ent != null) {
								final int _slotid = 5;
								final ItemStack _setstack = new ItemStack(PasterdreamModItems.DREAMNOTES_10.get());
								_setstack.setCount(1);
								_ent.getCapability(ForgeCapabilities.ITEM_HANDLER, null).ifPresent(capability -> {
									if (capability instanceof IItemHandlerModifiable)
										((IItemHandlerModifiable) capability).setStackInSlot(_slotid, _setstack);
								});
							}
						}
						if (entity instanceof Player _player && !_player.level().isClientSide())
							_player.displayClientMessage(Component.literal("\u6B8B\u7834\u7684\u7B14\u8BB0\u88AB\u4F60\u91CD\u65B0\u6392\u5217\uFF0C\u6A21\u7CCA\u7684\u6587\u5B57\u548C\u56FE\u6848\u88AB\u91CD\u65B0\u52FE\u52D2"), false);
						if (entity instanceof Player _player && !_player.level().isClientSide())
							_player.displayClientMessage(Component.literal("\u5DF2\u83B7\u5F97\u65B0\u7684\u5BFB\u68A6\u8005\u7B14\u8BB0"), false);
					}
				} else {
					if (entity instanceof Player _player && !_player.level().isClientSide())
						_player.displayClientMessage(Component.literal("\u7F3A\u5C11\u524D\u7F6E\u77E5\u8BC6 \u4F60\u8FD8\u65E0\u6CD5\u89E3\u8BFB\u7B14\u8BB0\u7684\u5185\u5BB9"), true);
					if (world instanceof Level _level) {
						if (!_level.isClientSide()) {
							_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("item.book.put")), SoundSource.BLOCKS, 1, 1);
						} else {
							_level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("item.book.put")), SoundSource.BLOCKS, 1, 1, false);
						}
					}
				}
			}
		} else {
			if (entity instanceof Player _player && !_player.level().isClientSide())
				_player.displayClientMessage(Component.literal("\u7F3A\u5C11\u7B14\u4E0E\u58A8"), false);
		}
	}
}
