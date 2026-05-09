
package net.pasterdream.item;

import top.theillusivec4.curios.api.type.capability.ICurioItem;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.network.chat.Component;

import java.util.List;

public class GhostFaceHeadItem extends Item implements ICurioItem {
	public GhostFaceHeadItem() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level world, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, world, list, flag);
		list.add(Component.literal("\u54C1\u8D28\uFF1A\u00A7b\u7CBE\u826F \u2605\u2605\u2605"));
		list.add(Component.literal("\u00A77\u25AA \u00A79\u9B42\u5578\u6CD5\u6756\u65BD\u6CD5\u4E0D\u518D\u6D88\u8017\u878D\u68A6\u80FD\u91CF\u548C\u7CBE\u795E\u503C"));
		list.add(Component.literal("\u00A77\u25AA \u00A79\u9B42\u5578\u6CD5\u6756\u547D\u4E2D\u76EE\u6807\u670910%\u6982\u7387\u53EC\u5524\u51A4\u9B42\u52A9\u6218"));
	}
}
