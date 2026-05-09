
package net.pasterdream.item;

import top.theillusivec4.curios.api.type.capability.ICurioItem;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.network.chat.Component;

import java.util.List;

public class CounterRingItem extends Item implements ICurioItem {
	public CounterRingItem() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level world, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, world, list, flag);
		list.add(Component.literal("\u54C1\u8D28\uFF1A\u00A7b\u7CBE\u826F \u2605\u2605\u2605"));
		list.add(Component.literal("\u00A77\u25AA \u00A79\u6210\u529F\u56DE\u907F\u4F24\u5BB3\u65F6\u83B7\u5F97\u53CD\u51FBI (0:02)"));
		list.add(Component.literal("\u00A77\u25AA \u00A79\u671F\u95F4\u653B\u51FB\u529B+3 \u6218\u6280\u4F24\u5BB3\u500D\u7387+50% "));
	}
}
