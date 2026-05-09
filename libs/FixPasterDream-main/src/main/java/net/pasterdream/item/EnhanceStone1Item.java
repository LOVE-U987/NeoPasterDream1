
package net.pasterdream.item;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.network.chat.Component;

import java.util.List;

public class EnhanceStone1Item extends Item {
	public EnhanceStone1Item() {
		super(new Item.Properties().stacksTo(16).rarity(Rarity.COMMON));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level world, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, world, list, flag);
		list.add(Component.literal("\u00A77\u7528\u4E8E\u5728\u7CBE\u94F8\u5DE5\u574A\u4E2D\u5F3A\u5316\u5408\u6210\u539F\u80DA"));
		list.add(Component.literal("\u00A77\u25AA \u00A79\u5E78\u8FD0+1~+2"));
		list.add(Component.literal("\u00A74\u5B9E\u9A8C\u6027\u6D4B\u8BD5\uFF01\u5982\u6709\u95EE\u9898\u8BF7\u53CD\u9988\uFF01"));
	}
}
