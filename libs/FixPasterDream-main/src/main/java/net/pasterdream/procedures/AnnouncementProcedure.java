package net.pasterdream.procedures;

import net.pasterdream.init.PasterdreamModItems;
import net.pasterdream.configuration.PasterdreamConfigCommonConfiguration;

import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.entity.player.PlayerEvent;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.Advancement;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class AnnouncementProcedure {
	@SubscribeEvent
	public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
		execute(event, event.getEntity());
	}

	public static void execute(Entity entity) {
		execute(null, entity);
	}

	private static void execute(@Nullable Event event, Entity entity) {
		if (entity == null)
			return;
		if (PasterdreamConfigCommonConfiguration.MOD_ACCOUOCEMENT.get() == true) {
			if (entity instanceof Player _player && !_player.level().isClientSide())
				_player.displayClientMessage(Component.literal("\u00A77\u25AA \u00A7fPasterDream Mod \u300A\u5E15\u65AF\u7279\u4E4B\u68A6\u300B"), false);
			if (entity instanceof Player _player && !_player.level().isClientSide())
				_player.displayClientMessage(Component.literal("\u00A77\u25AA \u00A7f\u73B0\u5904\u4E8E\u672A\u5B8C\u5DE5\u7684\u6D4B\u8BD5\u9636\u6BB5\uFF0C\u5982\u6709\u95EE\u9898\u6216\u5EFA\u8BAE\u8BF7\u8054\u7CFB\u6211\uFF01"), false);
			if (entity instanceof Player _player && !_player.level().isClientSide())
				_player.displayClientMessage(Component.literal("\u00A77\u25AA \u00A7f\u7248\u672C:beta0.3.5.7 \u5B8C\u6210\u8FDB\u5EA6 < 26% By \u5F02\u661F\u4E4B\u5C18"), false);
			if (entity instanceof Player _player && !_player.level().isClientSide())
				_player.displayClientMessage(Component.literal("\u00A77\u25AA \u00A7f\u6309[\u00A7eC\u00A7f]\u53EF\u4EE5\u4F7F\u7528\u00A7b\u77AC\u8EAB\u672F"), false);
			if (entity instanceof Player _player && !_player.level().isClientSide())
				_player.displayClientMessage(Component.literal("\u00A77\u25AA \u00A7fLanguages other than \u00A7esimplified Chinese \u00A77are not supported at present."), false);
		}
		if ((entity instanceof ServerPlayer _plr6 && _plr6.level() instanceof ServerLevel
				&& _plr6.getAdvancements().getOrStartProgress(_plr6.server.getAdvancements().getAdvancement(new ResourceLocation("pasterdream:achievement_hide_6"))).isDone()) == false) {
			if (entity instanceof Player _player) {
				ItemStack _setstack = new ItemStack(PasterdreamModItems.DREAMNOTES_0.get());
				_setstack.setCount(1);
				ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
			}
			if (entity instanceof ServerPlayer _player) {
				Advancement _adv = _player.server.getAdvancements().getAdvancement(new ResourceLocation("pasterdream:achievement_hide_6"));
				AdvancementProgress _ap = _player.getAdvancements().getOrStartProgress(_adv);
				if (!_ap.isDone()) {
					for (String criteria : _ap.getRemainingCriteria())
						_player.getAdvancements().award(_adv, criteria);
				}
			}
		}
	}
}
