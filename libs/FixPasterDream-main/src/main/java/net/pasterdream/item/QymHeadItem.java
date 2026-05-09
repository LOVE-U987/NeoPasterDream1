
package net.pasterdream.item;

import top.theillusivec4.curios.api.type.capability.ICurioItem;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.network.chat.Component;

import java.util.List;

public class QymHeadItem extends Item implements ICurioItem {
	public QymHeadItem() {
		super(new Item.Properties().stacksTo(1).fireResistant().rarity(Rarity.COMMON));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level world, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, world, list, flag);
		list.add(Component.literal("\u54C1\u8D28\uFF1A\u00A76\u795E\u8FF9 \u2605\u2605\u2605\u2605\u2605\u2605\u2605\u2605"));
		list.add(Component.literal("\u00A77\u25AA \u00A79\u65BD\u6CD5\u5C06\u65E0\u89C6\u51B7\u5374\u65F6\u95F4"));
		list.add(Component.literal("\u00A77\u25AA \u00A79\u91CA\u653E\u90E8\u5206\u6CD5\u672F\u4E0D\u518D\u4F1A\u53D7\u5230\u4EFB\u4F55\u8D1F\u9762\u6548\u679C\u5F71\u54CD"));
		list.add(Component.literal("\u00A77\u00A7o\u5982\u679C\u4F60\u7784\u51C6\u591C\u7A7A\u7684\u661F\u661F\uFF0C\u5373\u4F7F\u8FF7\u5931\uFF0C\u4E5F\u662F\u5728\u661F\u8FB0\u4E4B\u95F4\u3002"));
	}
}
