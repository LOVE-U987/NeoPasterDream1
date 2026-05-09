
package net.pasterdream.item;

import top.theillusivec4.curios.api.type.capability.ICurioItem;
import top.theillusivec4.curios.api.SlotContext;

import net.pasterdream.procedures.CeciliaCarePr0Procedure;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.network.chat.Component;

import java.util.List;

public class CeciliacareCharmItem extends Item implements ICurioItem {
	public CeciliacareCharmItem() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level world, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, world, list, flag);
		list.add(Component.literal("\u54C1\u8D28\uFF1A\u00A75\u4E0A\u53E4 \u2605\u2605\u2605\u2605\u2605"));
		list.add(Component.literal("\u00A77\u25AA \u00A79\u5F53\u524D\u751F\u547D\u503C\u4F4E\u4E8E\u6700\u5927\u751F\u547D\u503C\u768415%\u65F6\u53EF\u88AB\u89E6\u53D1"));
		list.add(Component.literal("\u00A77\u25AA \u00A79\u83B7\u5F975\u79D2\u7684\u65E0\u654C\u65F6\u95F4"));
		list.add(Component.literal("\u00A77\u25AA \u00A79\u5E76\u57285\u79D2\u5185\u5FEB\u901F\u6062\u590D\u751F\u547D 10\u79D2\u5185\u589E\u52A0\u79FB\u901F\u548C\u8DF3\u8DC3\u9AD8\u5EA6"));
		list.add(Component.literal("\u00A77\u25AA \u00A79\u7ACB\u523B\u91CD\u7F6E\u77AC\u8EAB\u672F\u7684cd\u65F6\u95F4"));
		list.add(Component.literal("\u00A77\u00A7o--\u6211\u4F1A\u5B88\u62A4\u4F60\uFF0C\u76F4\u5230\u6C38\u8FDC..."));
	}

	@Override
	public void curioTick(SlotContext slotContext, ItemStack stack) {
		CeciliaCarePr0Procedure.execute(slotContext.entity().level(), slotContext.entity().getX(), slotContext.entity().getY(), slotContext.entity().getZ(), slotContext.entity(), stack);
	}
}
