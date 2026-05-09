
package net.pasterdream.item;

import top.theillusivec4.curios.api.type.capability.ICurioItem;
import top.theillusivec4.curios.api.SlotContext;

import net.pasterdream.procedures.BrightButterflyCurioPr0Procedure;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.network.chat.Component;

import java.util.List;

public class BrightButterflyCurioItem extends Item implements ICurioItem {
	public BrightButterflyCurioItem() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level world, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, world, list, flag);
		list.add(Component.literal("\u54C1\u8D28\uFF1A\u00A75\u4E0A\u53E4 \u2605\u2605\u2605\u2605\u2605"));
		list.add(Component.literal("\u00A77\u25AA \u00A79\u81EA\u8EAB\u5904\u4E8E\u4F4E\u4EAE\u5EA6\u73AF\u5883\u4E0B\u83B7\u5F97\u591C\u89C6"));
		list.add(Component.literal("\u00A77\u25AA \u00A79\u514D\u75AB\u9ED1\u6697\u6548\u679C"));
	}

	@Override
	public void curioTick(SlotContext slotContext, ItemStack stack) {
		BrightButterflyCurioPr0Procedure.execute(slotContext.entity().level(), slotContext.entity().getX(), slotContext.entity().getY(), slotContext.entity().getZ(), slotContext.entity());
	}
}
