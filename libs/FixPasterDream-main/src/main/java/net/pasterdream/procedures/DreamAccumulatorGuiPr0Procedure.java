package net.pasterdream.procedures;

import net.pasterdream.init.PasterdreamModItems;

import net.minecraftforge.items.ItemHandlerHelper;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;

public class DreamAccumulatorGuiPr0Procedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		if ((entity instanceof ServerPlayer _plr0 && _plr0.level() instanceof ServerLevel
				&& _plr0.getAdvancements().getOrStartProgress(_plr0.server.getAdvancements().getAdvancement(new ResourceLocation("pasterdream:achievement_c_3"))).isDone()) == false
				&& (entity instanceof Player _playerHasItem ? _playerHasItem.getInventory().contains(new ItemStack(PasterdreamModItems.DREAMNOTES_7.get())) : false) == false) {
			if (entity instanceof Player _player)
				_player.closeContainer();
			if (entity instanceof Player _player) {
				ItemStack _setstack = new ItemStack(PasterdreamModItems.DREAMNOTES_7.get());
				_setstack.setCount(1);
				ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
			}
			if (entity instanceof Player _player && !_player.level().isClientSide())
				_player.displayClientMessage(Component.literal("\u4F60\u53D1\u73B0\u4E86\u4E00\u5F20\u5BFB\u68A6\u8005\u7B14\u8BB0\uFF0C\u5E76\u6536\u8FDB\u4E86\u4F60\u7684\u80CC\u5305"), false);
		}
	}
}
