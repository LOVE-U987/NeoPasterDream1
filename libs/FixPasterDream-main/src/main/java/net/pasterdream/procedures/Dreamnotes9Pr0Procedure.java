package net.pasterdream.procedures;

import net.pasterdream.init.PasterdreamModItems;
import net.pasterdream.init.PasterdreamModGameRules;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.items.ItemHandlerHelper;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.Advancement;

public class Dreamnotes9Pr0Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity, ItemStack itemstack) {
		if (entity == null)
			return;
		if ((entity instanceof ServerPlayer _plr0 && _plr0.level() instanceof ServerLevel
				&& _plr0.getAdvancements().getOrStartProgress(_plr0.server.getAdvancements().getAdvancement(new ResourceLocation("pasterdream:achievement_hide_10"))).isDone()) == true
				&& (entity instanceof ServerPlayer _plr1 && _plr1.level() instanceof ServerLevel
						&& _plr1.getAdvancements().getOrStartProgress(_plr1.server.getAdvancements().getAdvancement(new ResourceLocation("pasterdream:achievement_b_0"))).isDone()) == true) {
			itemstack.getOrCreateTag().putBoolean("switch", true);
			itemstack.getOrCreateTag().putDouble("x", (((world.getLevelData().getGameRules().getInt(PasterdreamModGameRules.RANDOMCOORDX)) * 20 + (world.getLevelData().getGameRules().getInt(PasterdreamModGameRules.RANDOMCOORDX))) - 22));
			itemstack.getOrCreateTag().putDouble("z", (((world.getLevelData().getGameRules().getInt(PasterdreamModGameRules.RANDOMCOORDZ)) * 20 + (world.getLevelData().getGameRules().getInt(PasterdreamModGameRules.RANDOMCOORDZ))) - 21));
			if (entity instanceof Player _player && !_player.level().isClientSide())
				_player.displayClientMessage(Component.literal("\u7B14\u8BB0\u7684\u80CC\u9762\u523B\u5370\u8FD9\u4E00\u4E2A\u5750\u6807"), false);
		}
		if ((entity instanceof ServerPlayer _plr13 && _plr13.level() instanceof ServerLevel
				&& _plr13.getAdvancements().getOrStartProgress(_plr13.server.getAdvancements().getAdvancement(new ResourceLocation("pasterdream:achievement_hide_10"))).isDone()) == false
				&& (entity instanceof ServerPlayer _plr14 && _plr14.level() instanceof ServerLevel
						&& _plr14.getAdvancements().getOrStartProgress(_plr14.server.getAdvancements().getAdvancement(new ResourceLocation("pasterdream:achievement_b_0"))).isDone()) == true) {
			if (entity instanceof ServerPlayer _player) {
				Advancement _adv = _player.server.getAdvancements().getAdvancement(new ResourceLocation("pasterdream:achievement_hide_10"));
				AdvancementProgress _ap = _player.getAdvancements().getOrStartProgress(_adv);
				if (!_ap.isDone()) {
					for (String criteria : _ap.getRemainingCriteria())
						_player.getAdvancements().award(_adv, criteria);
				}
			}
			if (entity instanceof Player _player && !_player.level().isClientSide())
				_player.displayClientMessage(Component.literal("\u4F60\u4E60\u5F97\u4E86\u65B0\u7684\u77E5\u8BC6 \u5173\u4E8E\u00A7a[\u4FB5\u67D3\u6559\u5802]\u00A7f\uFF0C\u65B0\u7684\u8FDB\u5EA6\u5DF2\u89E3\u9501"), false);
			if (world instanceof Level _level) {
				if (!_level.isClientSide()) {
					_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("ui.toast.challenge_complete")), SoundSource.NEUTRAL, 1, 1);
				} else {
					_level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("ui.toast.challenge_complete")), SoundSource.NEUTRAL, 1, 1, false);
				}
			}
			itemstack.getOrCreateTag().putBoolean("switch", true);
			if (entity instanceof Player _player) {
				ItemStack _setstack = new ItemStack(PasterdreamModItems.CALLE_CARD_0.get());
				_setstack.setCount(1);
				ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
			}
			if (entity instanceof Player _player && !_player.level().isClientSide())
				_player.displayClientMessage(Component.literal("\u4F60\u5728\u5BF9\u6298\u7684\u7B14\u8BB0\u91CC\u53D1\u73B0\u4E86\u4E00\u5F20\u5361\u7247"), false);
			itemstack.getOrCreateTag().putDouble("x", (((world.getLevelData().getGameRules().getInt(PasterdreamModGameRules.RANDOMCOORDX)) * 20 + (world.getLevelData().getGameRules().getInt(PasterdreamModGameRules.RANDOMCOORDX))) - 22));
			itemstack.getOrCreateTag().putDouble("z", (((world.getLevelData().getGameRules().getInt(PasterdreamModGameRules.RANDOMCOORDZ)) * 20 + (world.getLevelData().getGameRules().getInt(PasterdreamModGameRules.RANDOMCOORDZ))) - 21));
			NotesExpupPr0Procedure.execute(entity);
			if (entity instanceof Player _player && !_player.level().isClientSide())
				_player.displayClientMessage(Component.literal("\u7B14\u8BB0\u7684\u80CC\u9762\u523B\u5370\u8FD9\u4E00\u4E2A\u5750\u6807"), false);
			if (entity instanceof Player _player && !_player.level().isClientSide())
				_player.displayClientMessage(
						Component.literal((("X:" + new java.text.DecimalFormat("####").format(itemstack.getOrCreateTag().getDouble("x"))) + "" + ("Z:" + new java.text.DecimalFormat("####").format(itemstack.getOrCreateTag().getDouble("z"))))), false);
		}
		if (entity instanceof Player _plr ? _plr.getAbilities().instabuild : false) {
			itemstack.getOrCreateTag().putBoolean("switch", true);
			itemstack.getOrCreateTag().putDouble("x", (((world.getLevelData().getGameRules().getInt(PasterdreamModGameRules.RANDOMCOORDX)) * 20 + (world.getLevelData().getGameRules().getInt(PasterdreamModGameRules.RANDOMCOORDX))) - 0));
			itemstack.getOrCreateTag().putDouble("z", (((world.getLevelData().getGameRules().getInt(PasterdreamModGameRules.RANDOMCOORDZ)) * 20 + (world.getLevelData().getGameRules().getInt(PasterdreamModGameRules.RANDOMCOORDZ))) - 0));
			if (entity instanceof Player _player && !_player.level().isClientSide())
				_player.displayClientMessage(Component.literal("\u7B14\u8BB0\u7684\u80CC\u9762\u523B\u5370\u8FD9\u4E00\u4E2A\u5750\u6807"), false);
			if (entity instanceof Player _player && !_player.level().isClientSide())
				_player.displayClientMessage(
						Component.literal((("X:" + new java.text.DecimalFormat("####").format(itemstack.getOrCreateTag().getDouble("x"))) + "" + ("Z:" + new java.text.DecimalFormat("####").format(itemstack.getOrCreateTag().getDouble("z"))))), false);
		}
	}
}
