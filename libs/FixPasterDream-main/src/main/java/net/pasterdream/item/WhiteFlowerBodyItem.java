
package net.pasterdream.item;

import top.theillusivec4.curios.api.type.capability.ICurioItem;
import top.theillusivec4.curios.api.SlotContext;

import net.pasterdream.procedures.WhiteFlowerBodyPr0Procedure;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.network.chat.Component;

import java.util.List;

public class WhiteFlowerBodyItem extends Item implements ICurioItem {
	public WhiteFlowerBodyItem() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level world, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, world, list, flag);
		list.add(Component.literal("\u54C1\u8D28\uFF1A\u00A7d\u53F2\u8BD7 \u2605\u2605\u2605\u2605\u2605\u2605"));
		list.add(Component.literal("\u00A77\u25AA \u00A7f\u6761\u4EF6:\u4FE1\u4EF0\u5149\u660E"));
		list.add(Component.literal("\u00A77\u25AA \u00A79\u4E0D\u518D\u4F1A\u53D7\u5230\u73AF\u5883\u9020\u6210\u7684\u964Dsan\u5F71\u54CD"));
		list.add(Component.literal("\u00A77\u00A7o-- \u6211\u6D88\u901D\u4E8E\u65E0\u5F62 \u6B64\u523B\u4EAB\u53D7\u4F60\u5E94\u5F97\u7684\u8363\u8000"));
	}

	@Override
	public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
		WhiteFlowerBodyPr0Procedure.execute(slotContext.entity().level(), slotContext.entity().getX(), slotContext.entity().getY(), slotContext.entity().getZ(), slotContext.entity(), stack);
	}
}
