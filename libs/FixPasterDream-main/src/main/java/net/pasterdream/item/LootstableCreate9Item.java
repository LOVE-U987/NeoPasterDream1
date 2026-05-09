
package net.pasterdream.item;

import net.pasterdream.procedures.LootstableCreate9Pr0Procedure;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.InteractionResult;
import net.minecraft.network.chat.Component;

import java.util.List;

public class LootstableCreate9Item extends Item {
	public LootstableCreate9Item() {
		super(new Item.Properties().stacksTo(1).fireResistant().rarity(Rarity.COMMON));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level world, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, world, list, flag);
		list.add(Component.literal("\u00A77\u6F5C\u884C\u53F3\u51FB\u5BB9\u5668\u65B9\u5757\u4EE5\u4F7F\u5176\u589E\u52A0\u6218\u5229\u54C1\u8868"));
		list.add(Component.literal("\u00A77\u6218\u5229\u54C1\u8868\uFF1A"));
		list.add(Component.literal("\u00A7e\u91CE\u9910\u7BEE\u98DF\u7269"));
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		super.useOn(context);
		LootstableCreate9Pr0Procedure.execute(context.getLevel(), context.getClickedPos().getX(), context.getClickedPos().getY(), context.getClickedPos().getZ(), context.getPlayer());
		return InteractionResult.SUCCESS;
	}
}
