
package net.pasterdream.item;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.network.chat.Component;

import java.util.List;

public class TitaniumUpgradeItem extends Item {
	public TitaniumUpgradeItem() {
		super(new Item.Properties().stacksTo(16).rarity(Rarity.COMMON));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level world, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, world, list, flag);
		list.add(Component.literal("\u00A77\u7528\u4E8E\u5728\u953B\u9020\u53F0\u5904\u5347\u7EA7\u88C5\u5907"));
		list.add(Component.literal("\u00A77\u53EF\u4EE5\u4FDD\u7559\u88C5\u5907\u7684\u539F\u6709\u5F3A\u5316\u5C5E\u6027"));
	}
}
