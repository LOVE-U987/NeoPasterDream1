package net.pasterdream.procedures;

import net.pasterdream.init.PasterdreamModGameRules;

import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.sounds.SoundSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;

public class WindVanePr0Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		if (entity instanceof Player _player && !_player.level().isClientSide())
			_player.displayClientMessage(Component.literal((("\u89D2\u5EA6\uFF1A" + new java.text.DecimalFormat("##.##").format(entity.getXRot())) + "" + (" \u65B9\u5411\uFF1A" + new java.text.DecimalFormat("##.##").format(entity.getYRot())))),
					true);
		if (world instanceof Level _level) {
			if (!_level.isClientSide()) {
				_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("pasterdream:ding_0")), SoundSource.PLAYERS, 1, 1);
			} else {
				_level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("pasterdream:ding_0")), SoundSource.PLAYERS, 1, 1, false);
			}
		}
		if ((world.getLevelData().getGameRules().getInt(PasterdreamModGameRules.PASTERDREAM_WIND_DIRECTION)) == 0) {
			if (entity instanceof Player _player && !_player.level().isClientSide())
				_player.displayClientMessage(Component.literal("\u5F53\u524D\u98CE\u5411\uFF1A\u00A7a\u5317\u98CE"), false);
		}
		if ((world.getLevelData().getGameRules().getInt(PasterdreamModGameRules.PASTERDREAM_WIND_DIRECTION)) == 1) {
			if (entity instanceof Player _player && !_player.level().isClientSide())
				_player.displayClientMessage(Component.literal("\u5F53\u524D\u98CE\u5411\uFF1A\u00A7a\u4E1C\u5317\u98CE"), false);
		}
		if ((world.getLevelData().getGameRules().getInt(PasterdreamModGameRules.PASTERDREAM_WIND_DIRECTION)) == 2) {
			if (entity instanceof Player _player && !_player.level().isClientSide())
				_player.displayClientMessage(Component.literal("\u5F53\u524D\u98CE\u5411\uFF1A\u00A7a\u4E1C\u98CE"), false);
		}
		if ((world.getLevelData().getGameRules().getInt(PasterdreamModGameRules.PASTERDREAM_WIND_DIRECTION)) == 3) {
			if (entity instanceof Player _player && !_player.level().isClientSide())
				_player.displayClientMessage(Component.literal("\u5F53\u524D\u98CE\u5411\uFF1A\u00A7a\u4E1C\u5357\u98CE"), false);
		}
		if ((world.getLevelData().getGameRules().getInt(PasterdreamModGameRules.PASTERDREAM_WIND_DIRECTION)) == 4) {
			if (entity instanceof Player _player && !_player.level().isClientSide())
				_player.displayClientMessage(Component.literal("\u5F53\u524D\u98CE\u5411\uFF1A\u00A7a\u5357\u98CE"), false);
		}
		if ((world.getLevelData().getGameRules().getInt(PasterdreamModGameRules.PASTERDREAM_WIND_DIRECTION)) == 5) {
			if (entity instanceof Player _player && !_player.level().isClientSide())
				_player.displayClientMessage(Component.literal("\u5F53\u524D\u98CE\u5411\uFF1A\u00A7a\u897F\u5357\u98CE"), false);
		}
		if ((world.getLevelData().getGameRules().getInt(PasterdreamModGameRules.PASTERDREAM_WIND_DIRECTION)) == 6) {
			if (entity instanceof Player _player && !_player.level().isClientSide())
				_player.displayClientMessage(Component.literal("\u5F53\u524D\u98CE\u5411\uFF1A\u00A7a\u897F\u98CE"), false);
		}
		if ((world.getLevelData().getGameRules().getInt(PasterdreamModGameRules.PASTERDREAM_WIND_DIRECTION)) == 7) {
			if (entity instanceof Player _player && !_player.level().isClientSide())
				_player.displayClientMessage(Component.literal("\u5F53\u524D\u98CE\u5411\uFF1A\u00A7a\u897F\u5317\u98CE"), false);
		}
	}
}
