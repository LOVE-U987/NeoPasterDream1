package net.pasterdream.procedures;

import net.pasterdream.init.PasterdreamModParticleTypes;
import net.pasterdream.init.PasterdreamModItems;
import net.pasterdream.PasterdreamMod;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.items.ItemHandlerHelper;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Mth;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.BlockPos;

public class CalleCard0Pr0Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity, ItemStack itemstack) {
		if (entity == null)
			return;
		itemstack.getOrCreateTag().putDouble("number", (Mth.nextInt(RandomSource.create(), 1, 9)));
		if (world instanceof ServerLevel _level)
			_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.CALLE_PARTICLE.get()), x, y, z, 128, 1, 2, 1, 0.1);
		if (world instanceof Level _level) {
			if (!_level.isClientSide()) {
				_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("pasterdream:card0")), SoundSource.PLAYERS, (float) 1.5, 1);
			} else {
				_level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("pasterdream:card0")), SoundSource.PLAYERS, (float) 1.5, 1, false);
			}
		}
		if (entity instanceof Player _player && !_player.level().isClientSide())
			_player.displayClientMessage(Component.literal("\u00A76\u4F60\u624B\u4E2D\u7684\u5361\u83B1\u5360\u535C\u724C\u5DF2\u663E\u793A\u6240\u9884\u8A00\u7684\u4E00\u9762"), false);
		itemstack.shrink(1);
		if (itemstack.getOrCreateTag().getDouble("number") == 1) {
			if (entity instanceof Player _player && !_player.level().isClientSide())
				_player.displayClientMessage(Component.literal("\u00A76\u9884\u8A00\u5361\u724C\u4E3A\uFF1A\u300E\u5893\u56ED\u300F"), false);
			if (entity instanceof Player _player && !_player.level().isClientSide())
				_player.displayClientMessage(Component.literal("\u00A76\u00A7l\u300E\u5893\u56ED\u300F"), true);
			PasterdreamMod.queueServerWork(2, () -> {
				if (entity instanceof Player _player) {
					ItemStack _setstack = new ItemStack(PasterdreamModItems.CALLE_CARD_1.get());
					_setstack.setCount(1);
					ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
				}
			});
		}
		if (itemstack.getOrCreateTag().getDouble("number") == 2) {
			if (entity instanceof Player _player && !_player.level().isClientSide())
				_player.displayClientMessage(Component.literal("\u00A76\u9884\u8A00\u5361\u724C\u4E3A\uFF1A\u300E\u6267\u5251\u300F"), false);
			if (entity instanceof Player _player && !_player.level().isClientSide())
				_player.displayClientMessage(Component.literal("\u00A76\u00A7l\u300E\u6267\u5251\u300F"), true);
			PasterdreamMod.queueServerWork(2, () -> {
				if (entity instanceof Player _player) {
					ItemStack _setstack = new ItemStack(PasterdreamModItems.CALLE_CARD_2.get());
					_setstack.setCount(1);
					ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
				}
			});
		}
		if (itemstack.getOrCreateTag().getDouble("number") == 3) {
			if (entity instanceof Player _player && !_player.level().isClientSide())
				_player.displayClientMessage(Component.literal("\u00A76\u9884\u8A00\u5361\u724C\u4E3A\uFF1A\u300E\u75BE\u884C\u300F"), false);
			if (entity instanceof Player _player && !_player.level().isClientSide())
				_player.displayClientMessage(Component.literal("\u00A76\u00A7l\u300E\u75BE\u884C\u300F"), true);
			PasterdreamMod.queueServerWork(2, () -> {
				if (entity instanceof Player _player) {
					ItemStack _setstack = new ItemStack(PasterdreamModItems.CALLE_CARD_3.get());
					_setstack.setCount(1);
					ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
				}
			});
		}
		if (itemstack.getOrCreateTag().getDouble("number") == 4) {
			if (entity instanceof Player _player && !_player.level().isClientSide())
				_player.displayClientMessage(Component.literal("\u00A76\u9884\u8A00\u5361\u724C\u4E3A\uFF1A\u300E\u5B88\u62A4\u300F"), false);
			if (entity instanceof Player _player && !_player.level().isClientSide())
				_player.displayClientMessage(Component.literal("\u00A76\u00A7l\u300E\u5B88\u62A4\u300F"), true);
			PasterdreamMod.queueServerWork(2, () -> {
				if (entity instanceof Player _player) {
					ItemStack _setstack = new ItemStack(PasterdreamModItems.CALLE_CARD_4.get());
					_setstack.setCount(1);
					ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
				}
			});
		}
		if (itemstack.getOrCreateTag().getDouble("number") == 5) {
			if (entity instanceof Player _player && !_player.level().isClientSide())
				_player.displayClientMessage(Component.literal("\u00A76\u9884\u8A00\u5361\u724C\u4E3A\uFF1A\u300E\u5BF9\u7ACB\u300F"), false);
			if (entity instanceof Player _player && !_player.level().isClientSide())
				_player.displayClientMessage(Component.literal("\u00A76\u00A7l\u300E\u5BF9\u7ACB\u300F"), true);
			PasterdreamMod.queueServerWork(2, () -> {
				if (entity instanceof Player _player) {
					ItemStack _setstack = new ItemStack(PasterdreamModItems.CALLE_CARD_5.get());
					_setstack.setCount(1);
					ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
				}
			});
		}
		if (itemstack.getOrCreateTag().getDouble("number") == 6) {
			if (entity instanceof Player _player && !_player.level().isClientSide())
				_player.displayClientMessage(Component.literal("\u00A76\u9884\u8A00\u5361\u724C\u4E3A\uFF1A\u300E\u7F6A\u6076\u300F"), false);
			if (entity instanceof Player _player && !_player.level().isClientSide())
				_player.displayClientMessage(Component.literal("\u00A76\u00A7l\u300E\u7F6A\u6076\u300F"), true);
			PasterdreamMod.queueServerWork(2, () -> {
				if (entity instanceof Player _player) {
					ItemStack _setstack = new ItemStack(PasterdreamModItems.CALLE_CARD_6.get());
					_setstack.setCount(1);
					ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
				}
			});
		}
		if (itemstack.getOrCreateTag().getDouble("number") == 7) {
			if (entity instanceof Player _player && !_player.level().isClientSide())
				_player.displayClientMessage(Component.literal("\u00A76\u9884\u8A00\u5361\u724C\u4E3A\uFF1A\u300E\u5747\u8861\u300F"), false);
			if (entity instanceof Player _player && !_player.level().isClientSide())
				_player.displayClientMessage(Component.literal("\u00A76\u00A7l\u300E\u5747\u8861\u300F"), true);
			PasterdreamMod.queueServerWork(2, () -> {
				if (entity instanceof Player _player) {
					ItemStack _setstack = new ItemStack(PasterdreamModItems.CALLE_CARD_7.get());
					_setstack.setCount(1);
					ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
				}
			});
		}
		if (itemstack.getOrCreateTag().getDouble("number") == 8) {
			if (entity instanceof Player _player && !_player.level().isClientSide())
				_player.displayClientMessage(Component.literal("\u00A76\u9884\u8A00\u5361\u724C\u4E3A\uFF1A\u300E\u5723\u676F\u300F"), false);
			if (entity instanceof Player _player && !_player.level().isClientSide())
				_player.displayClientMessage(Component.literal("\u00A76\u00A7l\u300E\u5723\u676F\u300F"), true);
			PasterdreamMod.queueServerWork(2, () -> {
				if (entity instanceof Player _player) {
					ItemStack _setstack = new ItemStack(PasterdreamModItems.CALLE_CARD_8.get());
					_setstack.setCount(1);
					ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
				}
			});
		}
		if (itemstack.getOrCreateTag().getDouble("number") == 9) {
			if (entity instanceof Player _player && !_player.level().isClientSide())
				_player.displayClientMessage(Component.literal("\u00A76\u9884\u8A00\u5361\u724C\u4E3A\uFF1A\u300E\u6DF7\u4E71\u300F"), false);
			if (entity instanceof Player _player && !_player.level().isClientSide())
				_player.displayClientMessage(Component.literal("\u00A76\u00A7l\u300E\u6DF7\u4E71\u300F"), true);
			PasterdreamMod.queueServerWork(2, () -> {
				if (entity instanceof Player _player) {
					ItemStack _setstack = new ItemStack(PasterdreamModItems.CALLE_CARD_9.get());
					_setstack.setCount(1);
					ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
				}
			});
		}
	}
}
