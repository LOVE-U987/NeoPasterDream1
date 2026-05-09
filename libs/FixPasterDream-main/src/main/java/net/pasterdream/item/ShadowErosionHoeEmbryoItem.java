
package net.pasterdream.item;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.network.chat.Component;

import java.util.List;

public class ShadowErosionHoeEmbryoItem extends Item {
	public ShadowErosionHoeEmbryoItem() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level world, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, world, list, flag);
		list.add(Component.literal("\u00A77\u672A\u52A0\u5DE5\u539F\u80DA"));
		list.add(Component.literal("\u00A77\u9700\u8981\u5728\u7CBE\u94F8\u5DE5\u574A\u5185\u5B8C\u6210\u540E\u7EED\u953B\u9020"));
	}
}
