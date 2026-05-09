
package net.pasterdream.item;

import top.theillusivec4.curios.api.type.capability.ICurioItem;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.network.chat.Component;

import java.util.List;

public class TerraCharmItem extends Item implements ICurioItem {
	public TerraCharmItem() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level world, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, world, list, flag);
		list.add(Component.literal("\u54C1\u8D28\uFF1A\u00A7d\u53F2\u8BD7 \u2605\u2605\u2605\u2605\u2605\u2605"));
		list.add(Component.literal("\u00A77\u25AA \u00A79\u5927\u5730\u4E4B\u5203\u6218\u6280\u7684\u51B7\u5374\u65F6\u95F4-60%"));
		list.add(Component.literal("\u00A77\u25AA \u00A79\u5251\u6C14\u7684\u653B\u51FB\u529B\u5C5E\u6027\u52A0\u6210\u500D\u7387+30%"));
		list.add(Component.literal("\u00A77\u25AA \u00A79\u6CF0\u62C9\u5251\u6280\u6D88\u8017\u7684\u878D\u68A6\u80FD\u91CF-0.2"));
	}
}
