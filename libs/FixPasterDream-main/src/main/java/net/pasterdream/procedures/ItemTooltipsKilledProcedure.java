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

import javax.annotation.Nullable;

import java.util.List;

@Mod.EventBusSubscriber
public class ItemTooltipsKilledProcedure {
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
		if (itemstack.getItem() == PasterdreamModItems.GRASS_SWORD.get()) {
			tooltip.add(Component.literal((new java.text.DecimalFormat("\u00A77\u6210\u957F\u6240\u9700\u5269\u4F59\u51FB\u6740\u6570\uFF1A###").format(200 - itemstack.getOrCreateTag().getDouble("killed")))));
		}
	}
}
