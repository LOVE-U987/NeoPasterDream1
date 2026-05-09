
package net.pasterdream.item;

import top.theillusivec4.curios.api.type.capability.ICurioItem;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.network.chat.Component;

import java.util.List;

public class CalaisSpiceBottleCurioItem extends Item implements ICurioItem {
	public CalaisSpiceBottleCurioItem() {
		super(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level world, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, world, list, flag);
		list.add(Component.literal("\u54C1\u8D28\uFF1A\u00A7b\u7CBE\u826F \u2605\u2605\u2605"));
		list.add(Component.literal("\u00A77\u25AA \u00A79\u98DF\u7528\u98DF\u7269\u65F6\u53EF\u4EE5\u968F\u673A\u83B7\u5F97\u4E00\u79CD\u989D\u5916\u589E\u76CA"));
		list.add(Component.literal("\u00A77\u25AA \u00A79\u6062\u590Dsan\u503C \u90E8\u5206\u6B63\u9762\u836F\u6C34\u6548\u679C \u989D\u5916\u9971\u548C"));
		list.add(Component.literal("\u00A77\u25AA \u00A79\u83B7\u5F97\u7684\u589E\u76CA\u5F3A\u5EA6\u4E0E\u98DF\u7528\u98DF\u7269\u7684\u9971\u98DF/\u9971\u548C\u6210\u6B63\u76F8\u5173"));
	}
}
