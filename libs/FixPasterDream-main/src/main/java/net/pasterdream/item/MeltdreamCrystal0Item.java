
package net.pasterdream.item;

import net.pasterdream.procedures.MeltdreamCrystal0Pr0Procedure;

import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.InteractionResult;
import net.minecraft.network.chat.Component;

import java.util.List;

public class MeltdreamCrystal0Item extends Item {
	public MeltdreamCrystal0Item() {
		super(new Item.Properties().stacksTo(16).rarity(Rarity.EPIC));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean isFoil(ItemStack itemstack) {
		return true;
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level world, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, world, list, flag);
		list.add(Component.literal("\u00A77\u6B64\u4E16\u95F4\u4E00\u5207\u62E5\u6709\u7075\u9B42\u7684\u751F\u7269\u7686\u6709\u68A6"));
		list.add(Component.literal("\u00A77\u90A3\u4E9B\u9192\u6765\u65F6\u65E0\u6CD5\u60F3\u8D77\u7684\u7EC6\u7F15\u56DE\u5FC6"));
		list.add(Component.literal("\u00A77\u7834\u788E\u5E76\u878D\u5316 \u88AB\u57CB\u85CF\u5728\u4E16\u754C\u5404\u5904\u51DD\u7ED3\u6210\u6676"));
		list.add(Component.literal("\u00A77\u7B49\u5F85\u7740\u88AB\u65B0\u7684\u7075\u9B42\u53D1\u73B0"));
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		super.useOn(context);
		MeltdreamCrystal0Pr0Procedure.execute(context.getLevel(), context.getClickedPos().getX(), context.getClickedPos().getY(), context.getClickedPos().getZ(), context.getItemInHand());
		return InteractionResult.SUCCESS;
	}
}
