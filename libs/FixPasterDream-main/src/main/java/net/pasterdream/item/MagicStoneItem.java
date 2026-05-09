
package net.pasterdream.item;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.network.chat.Component;

import java.util.List;

public class MagicStoneItem extends Item {
	public MagicStoneItem() {
		super(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level world, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, world, list, flag);
		list.add(Component.literal("\u00A77\u00A7o\u54EA\u4E2A\u9B54\u6CD5\u5E08\u7684\u515C\u91CC\u4E0D\u4F1A\u8E39\u51E0\u5757\u9B54\u6CD5\u77F3\u5462\uFF1F"));
	}
}
