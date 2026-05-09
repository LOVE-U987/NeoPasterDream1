
package net.pasterdream.item;

import net.pasterdream.procedures.CalleCard8Pr0Procedure;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.network.chat.Component;

import java.util.List;

public class CalleCard8Item extends Item {
	public CalleCard8Item() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level world, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, world, list, flag);
		list.add(Component.literal("\u00A76\u300E\u5723\u676F\u300F"));
		list.add(Component.literal("\u00A7f\u25AA \u00A77\u4F7F\u7528\u5361\u724C\u65F6\uFF1A"));
		list.add(Component.literal("\u00A77\u25AA \u00A79\u83B7\u5F97\u5723\u676F\u6548\u679C \u6301\u7EED120\u79D2"));
		list.add(Component.literal("\u00A77\u25AA \u00A79\u671F\u95F4\u6700\u5927\u751F\u547D\u503C+20 \u514D\u75AB\u90E8\u5206\u4E0D\u826F\u53CD\u5E94\u7C7B\u6548\u679C"));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
		InteractionResultHolder<ItemStack> ar = super.use(world, entity, hand);
		CalleCard8Pr0Procedure.execute(world, entity.getX(), entity.getY(), entity.getZ(), entity, ar.getObject());
		return ar;
	}
}
