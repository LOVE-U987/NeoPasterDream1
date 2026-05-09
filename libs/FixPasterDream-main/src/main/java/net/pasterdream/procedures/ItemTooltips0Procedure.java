package net.pasterdream.procedures;

import net.pasterdream.init.PasterdreamModItems;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.tags.ItemTags;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.Screen;

import javax.annotation.Nullable;

import java.util.List;

@Mod.EventBusSubscriber
public class ItemTooltips0Procedure {
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
        boolean check_shift = false;
		if (itemstack.getItem() == PasterdreamModItems.DREAM_FERTILIZER.get()) {
			if (Screen.hasShiftDown()) {
				tooltip.add(Component.literal("\u00A7f\u25AA \u00A77\u7B80\u4ECB"));
				tooltip.add(Component.literal("\u00A77\u53EF\u4EE5\u50AC\u719F\u5E15\u65AF\u7279\u4E4B\u68A6\u7684\u90E8\u5206\u4F5C\u7269"));
				tooltip.add(Component.literal("\u00A77\u5E76\u5177\u6709\u548C\u9AA8\u7C89\u76F8\u540C\u7684\u50AC\u719F\u529F\u6548"));
			} else {
                check_shift = true;
			}
		}
		if (itemstack.getItem() == PasterdreamModItems.STORAGE_BAG_0.get()) {
			if (Screen.hasShiftDown()) {
				tooltip.add(Component.literal("\u00A7f\u25AA \u00A77\u7B80\u4ECB"));
				tooltip.add(Component.literal("\u00A77\u8FD0\u7528\u672B\u5F71\u7684\u529B\u91CF\u5B9E\u73B0\u6B21\u5143\u5B58\u50A8"));
				tooltip.add(Component.literal("\u00A77\u6CA1\u6709\u672B\u5F71\u7BB1\u7684\u4F20\u8F93\u7269\u54C1\u80FD\u529B"));
			} else {
                check_shift = true;
            }
		}
		if (itemstack.getItem() == PasterdreamModItems.CALLE_CARD_0.get()) {
			if (Screen.hasShiftDown()) {
				tooltip.add(Component.literal("\u00A7f\u25AA \u00A77\u7B80\u4ECB"));
				tooltip.add(Component.literal("\u00A77\u53F3\u952E\u62BD\u53D6\u5361\u724C\u4EE5\u5C55\u73B0\u9884\u8A00\u7ED3\u679C"));
				tooltip.add(Component.literal("\u00A77\u5361\u724C\u5747\u4E3A\u6D88\u8017\u54C1"));
				tooltip.add(Component.literal("\u00A7e\u00A7l\u00A7o\u00A7m\u716E\u996D\u5A46\u7684\u9EC4\u91D1\u79C1\u8D27"));
			} else {
                check_shift = true;
            }
		}
		if (itemstack.getItem() == Items.MILK_BUCKET) {
			if (Screen.hasShiftDown()) {
				tooltip.add(Component.literal("\u00A7f\u25AA \u00A77PasterDream\u505A\u51FA\u7684\u4FEE\u6539\uFF1A"));
				tooltip.add(Component.literal("\u00A77\u725B\u5976\u5C06\u65E0\u6CD5\u6E05\u9664PasterDream\u7684\u90E8\u5206\u836F\u6C34\u6548\u679C"));
			} else {
                check_shift = true;
            }
		}
		if (itemstack.is(ItemTags.create(new ResourceLocation("forge:structure_block")))) {
			if (Screen.hasShiftDown()) {
				tooltip.add(Component.literal("\u00A7f\u25AA \u00A77\u7B80\u4ECB"));
				tooltip.add(Component.literal("\u00A77\u4EC5\u62E5\u6709\u5267\u60C5\u7684\u7ED3\u6784\u624D\u4F1A\u6709\u5BF9\u5E94\u7684\u7ED3\u6784\u751F\u6210\u5757"));
				tooltip.add(Component.literal("\u00A77\u653E\u7F6E\u65B9\u5757\u6216\u65B9\u5757\u66F4\u65B0\u523B\u5373\u53EF\u7ACB\u5373\u751F\u6210\u7ED3\u6784"));
				tooltip.add(Component.literal("\u00A77\u751F\u6210\u4F1A\u9876\u53BB\u6240\u6709\u5360\u5730\u65B9\u5757 \u4E0D\u8981\u5728\u5BB6\u4F7F\u7528\uFF01"));
			} else {
                check_shift = true;
            }
		}
		if (itemstack.is(ItemTags.create(new ResourceLocation("forge:embryo_curios")))) {
			if (Screen.hasShiftDown()) {
				tooltip.add(Component.literal("\u00A7f\u25AA \u00A77\u7B80\u4ECB"));
				tooltip.add(Component.literal("\u00A77\u949B\u91D1\u5C5E\u548C\u7075\u9B42\u7CBE\u534E\u800C\u5236\u4F5C\u7684\u9970\u54C1\u539F\u80DA"));
				tooltip.add(Component.literal("\u00A77\u5229\u7528\u949B\u7684\u751F\u547D\u4EB2\u548C\u6027\u548C\u7075\u9B42\u7CBE\u534E\u7684\u7CBE\u795E\u8FDE\u643A\u4F5C\u7528"));
				tooltip.add(Component.literal("\u00A77\u4F7F\u5F97\u53EF\u4EE5\u901A\u8FC7\u7528\u7269\u8D28\u5B9E\u4F53\u5236\u4F5C\u6210\u7684\u9970\u54C1\u6765\u5F3A\u5316\u81EA\u8EAB"));
			} else {
                check_shift = true;
            }
		}
		if (itemstack.is(ItemTags.create(new ResourceLocation("pasterdream:paster_treasure")))) {
			if (Screen.hasShiftDown()) {
				tooltip.add(Component.literal("\u00A7f\u25AA \u00A77\u7B80\u4ECB"));
				tooltip.add(Component.literal("\u00A77\u5B9D\u85CF\u7C7B\u7269\u54C1 \u65E0\u6CD5\u901A\u8FC7\u5408\u6210\u83B7\u53D6"));
			} else {
                check_shift = true;
            }
		}
		if (itemstack.is(ItemTags.create(new ResourceLocation("pasterdream:pastercurios")))) {
			if (Screen.hasShiftDown()) {
				tooltip.add(Component.literal("\u00A7f\u25AA \u00A77\u7B80\u4ECB"));
				tooltip.add(Component.literal("\u00A77\u6B64\u79CD\u5C5E\u6027\u52A0\u6210\u7C7B\u9970\u54C1\u540C\u79CD\u53EA\u53EF\u4F69\u6234\u4E00\u679A"));
				tooltip.add(Component.literal("\u00A77\u540C\u79CD\u4F46\u4E0D\u540C\u7B49\u7EA7\u7684\u9970\u54C1\u53EF\u540C\u65F6\u4F69\u6234"));
			} else {
                check_shift = true;
            }
		}
        if(check_shift)
        {
            tooltip.add(Component.translatable("item.pasterdream.curios.tiptopushshift", "shift"));
        }
	}
}
