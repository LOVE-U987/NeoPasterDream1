
package net.pasterdream.item;

import net.pasterdream.procedures.CalleCard1Pr0Procedure;

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

public class CalleCard1Item extends Item {
	public CalleCard1Item() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level world, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, world, list, flag);
		list.add(Component.literal("\u00A76\u300E\u5893\u56ED\u300F"));
		list.add(Component.literal("\u00A7f\u25AA \u00A77\u4F7F\u7528\u5361\u724C\u65F6\uFF1A"));
		list.add(Component.literal("\u00A77\u25AA \u00A79\u5BF9\u4EE5\u81EA\u8EAB\u4E3A\u4E2D\u5FC3 5*5\u8303\u56F4\u5185\u7684\u6240\u6709\u654C\u4EBA\u9020\u6210100\u70B9\u9B54\u6CD5\u4F24\u5BB3"));
		list.add(Component.literal("\u00A77\u25AA \u00A79\u6B64\u6548\u679C\u4E0D\u5BF9\u73A9\u5BB6\u751F\u6548"));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
		InteractionResultHolder<ItemStack> ar = super.use(world, entity, hand);
		CalleCard1Pr0Procedure.execute(world, entity.getX(), entity.getY(), entity.getZ(), entity, ar.getObject());
		return ar;
	}
}
