
package net.pasterdream.item;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.network.chat.Component;

import java.util.List;

public class Unknownnotes0Item extends Item {
	public Unknownnotes0Item() {
		super(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level world, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, world, list, flag);
		list.add(Component.literal("\u672A\u77E5\u5185\u5BB9"));
		list.add(Component.literal("\u00A77\u9700\u8981\u4F7F\u7528\u00A7e\u7814\u7A76\u53F0\u00A77\u89E3\u6790\u7B14\u8BB0"));
	}
}
