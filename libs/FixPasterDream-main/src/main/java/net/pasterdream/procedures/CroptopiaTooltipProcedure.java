package net.pasterdream.procedures;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.world.item.ItemStack;
import net.minecraft.tags.ItemTags;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;

import java.util.List;

@Mod.EventBusSubscriber
public class CroptopiaTooltipProcedure {
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
			if (itemstack.is(ItemTags.create(new ResourceLocation("croptopia:advancements_food_big"))) || itemstack.is(ItemTags.create(new ResourceLocation("pasterdream:delight_cooked_tag")))) {
			tooltip.add(Component.literal("\u00A77\u98DF\u7528\u540E\u83B7\u5F97\u6548\u679C: (\u00A7d\u00A7o\u6E90\u81EAPasterDream\u00A77)"));
			tooltip.add(Component.literal("\u00A77\u25AA \u00A79\u5373\u65F6\u6062\u590Dsan+1"));
			tooltip.add(Component.literal("\u00A77\u25AA \u00A79\u6599\u7406I (1:00)"));
		}
	}
}
