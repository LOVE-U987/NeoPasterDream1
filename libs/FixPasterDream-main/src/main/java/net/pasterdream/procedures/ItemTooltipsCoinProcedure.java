package net.pasterdream.procedures;

import net.pasterdream.init.PasterdreamModItems;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.Screen;

import javax.annotation.Nullable;

import java.util.List;

@Mod.EventBusSubscriber
public class ItemTooltipsCoinProcedure {
	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void onItemTooltip(ItemTooltipEvent event) {
		execute(event, event.getItemStack(), event.getToolTip());
	}

	public static void execute(ItemStack itemstack, List<Component> tooltip) {
		execute(null, itemstack, tooltip);
	}

	private static void execute(@Nullable Event event, ItemStack itemstack, List<Component> tooltip) {
		if (tooltip == null)
			return;
		if (itemstack.getItem() == PasterdreamModItems.DREAM_COIN_0.get()) {
			tooltip.add(Component.literal((new java.text.DecimalFormat("\u00A77\u25AA \u00A76\u4EF7\u503C\u9762\u989D\uFF1A\u00A7l##").format(itemstack.getCount()))));
			if (Screen.hasShiftDown()) {
				tooltip.add(Component.literal("\u00A7f\u25AA \u00A77\u7B80\u4ECB"));
				tooltip.add(Component.literal("\u00A77\u624B\u630110\u4E2A\u53CA\u4EE5\u4E0A\u7269\u54C1\u65F6\uFF0C\u53F3\u952E\u5408\u6210\u4E3A\u878D\u68A6\u661F\u5E01\u5806"));
			} else {
				tooltip.add(Component.literal("\u00A77\u6309\u4F4F[\u00A7fshift\u00A77]\u952E\u4EE5\u67E5\u770B\u8BE6\u60C5"));
			}
		}
		if (itemstack.getItem() == PasterdreamModItems.DREAM_COIN_1.get()) {
			tooltip.add(Component.literal((new java.text.DecimalFormat("\u00A77\u25AA \u00A76\u4EF7\u503C\u9762\u989D\uFF1A\u00A7l###").format(itemstack.getCount() * 10))));
			if (Screen.hasShiftDown()) {
				tooltip.add(Component.literal("\u00A7f\u25AA \u00A77\u7B80\u4ECB"));
				tooltip.add(Component.literal("\u00A77\u53F3\u952E\u4F7F\u7528\u4EE5\u62C6\u5206\u4E3A10\u4E2A\u878D\u68A6\u661F\u5E01"));
			} else {
				tooltip.add(Component.literal("\u00A77\u6309\u4F4F[\u00A7fshift\u00A77]\u952E\u4EE5\u67E5\u770B\u8BE6\u60C5"));
			}
		}
	}
}
