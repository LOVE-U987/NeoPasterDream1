
package net.pasterdream.item;

import net.pasterdream.procedures.CalleCard7Pr0Procedure;

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

public class CalleCard7Item extends Item {
	public CalleCard7Item() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level world, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, world, list, flag);
		list.add(Component.literal("\u00A76\u300E\u5E73\u8861\u300F"));
		list.add(Component.literal("\u00A7f\u25AA \u00A77\u4F7F\u7528\u5361\u724C\u65F6\uFF1A"));
		list.add(Component.literal("\u00A77\u25AA \u00A79\u5F53\u524D\u81EA\u8EAB\u62E5\u6709\u7684\u90E8\u5206\u836F\u6C34\u6548\u679C\u7684\u7B49\u7EA7\u7FFB\u500D \u4F46\u6301\u7EED\u65F6\u95F4\u7F29\u77ED\u81F3\u4E00\u534A"));
		list.add(Component.literal("\u00A77\u25AA \u00A79\u4EC5\u5BF9\u90E8\u5206\u6709\u7B49\u7EA7\u52A0\u6210\u7684\u6548\u679C\u751F\u6548"));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
		InteractionResultHolder<ItemStack> ar = super.use(world, entity, hand);
		CalleCard7Pr0Procedure.execute(world, entity.getX(), entity.getY(), entity.getZ(), entity, ar.getObject());
		return ar;
	}
}
