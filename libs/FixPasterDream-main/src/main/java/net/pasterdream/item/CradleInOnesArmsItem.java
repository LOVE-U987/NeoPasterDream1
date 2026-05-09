
package net.pasterdream.item;

import net.pasterdream.procedures.CradleInOnesArmsPr0Procedure;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.InteractionResult;
import net.minecraft.network.chat.Component;

import java.util.List;

public class CradleInOnesArmsItem extends Item {
	public CradleInOnesArmsItem() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level world, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, world, list, flag);
		list.add(Component.literal("\u00A77\u25AA \u00A79\u5BF912*12\u533A\u57DF\u8303\u56F4\u5185\u751F\u6210\u72D0\u706B\u7ACB\u573A"));
		list.add(Component.literal("\u00A77\u25AA \u00A79\u4F7F\u9664\u73A9\u5BB6\u5916\u7684\u751F\u7269\u53D7\u523020%\u7684\u6613\u4F24\u548C\u7F13\u6162\u6548\u679C"));
		list.add(Component.literal("\u00A77\u25AA \u00A79\u7ED9\u4E88\u73A9\u5BB6\u751F\u547D\u6062\u590D"));
		list.add(Component.literal("\u00A77\u25AA \u00A79\u6301\u7EED\u65F6\u95F4 20\u79D2"));
		list.add(Component.literal("\u00A7f\u25AA \u00A74\u878D\u68A6\u80FD\u91CF -5"));
		list.add(Component.literal("\u00A77\u25AA \u00A79\u51B7\u5374\u65F6\u95F4 8 \u79D2"));
		list.add(Component.literal("\u00A7o\u00A77 -- Alirea\u5B9A\u5236\u7269\u54C1"));
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		super.useOn(context);
		CradleInOnesArmsPr0Procedure.execute(context.getLevel(), context.getClickedPos().getX(), context.getClickedPos().getY(), context.getClickedPos().getZ(), context.getPlayer());
		return InteractionResult.SUCCESS;
	}
}
