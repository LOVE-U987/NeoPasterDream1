
package net.pasterdream.item;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.network.chat.Component;

import java.util.List;

public class SorbentItem extends Item {
	public SorbentItem() {
		super(new Item.Properties().durability(2700).rarity(Rarity.COMMON));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level world, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, world, list, flag);
		list.add(Component.literal("\u00A77\u4E00\u79CD\u9AD8\u5206\u5B50\u6CB8\u77F3\u5206\u5B50\u7B5B\u5438\u9644\u5242"));
		list.add(Component.literal("\u00A77\u7528\u4E8E\u8FC7\u6EE4\u7A7A\u6C14\u548C\u8403\u53D6"));
	}
}
