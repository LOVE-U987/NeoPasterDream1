
package net.pasterdream.item;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.network.chat.Component;

import java.util.List;

public class BubbleTeaItem extends Item {
	public BubbleTeaItem() {
		super(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON).food((new FoodProperties.Builder()).nutrition(5).saturationMod(0.7f).build()));
	}

	@Override
	public UseAnim getUseAnimation(ItemStack itemstack) {
		return UseAnim.DRINK;
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level world, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, world, list, flag);
		list.add(Component.literal("\u00A77\u8349\u8393\u9F99\u4EB2\u624B\u6599\u7406\u7684\u996E\u54C1"));
		list.add(Component.literal("\u00A77\"\u756B\u4E86\u4E00\u500B\u73CD\u73E0\u5976\u8336\u7D66\u5927\u5BB6\u559D(X\""));
	}
}
