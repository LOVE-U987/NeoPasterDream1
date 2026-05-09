
package net.pasterdream.item;

import net.pasterdream.procedures.CalleCard6Pr0Procedure;

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

public class CalleCard6Item extends Item {
	public CalleCard6Item() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level world, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, world, list, flag);
		list.add(Component.literal("\u00A76\u300E\u7F6A\u6076\u300F"));
		list.add(Component.literal("\u00A7f\u25AA \u00A77\u4F7F\u7528\u5361\u724C\u65F6\uFF1A"));
		list.add(Component.literal("\u00A77\u25AA \u00A79\u5BF9\u4EE5\u81EA\u8EAB\u4E3A\u4E2D\u5FC3 \u5F15\u71C319*19\u8303\u56F4\u5185\u7684\u6240\u6709\u4EA1\u7075\u751F\u726915\u79D2\u5E76\u9020\u621020\u70B9\u706B\u7130\u4F24\u5BB3"));
		list.add(Component.literal("\u00A77\u25AA \u00A79\u5982\u679C\u6B64\u5B9E\u4F53\u4E3A\u50F5\u5C38\u6751\u6C11 \u5219\u4E0D\u4F1A\u53D7\u5230\u4F24\u5BB3\u4E14\u8F6C\u5316\u4E3A\u6751\u6C11"));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
		InteractionResultHolder<ItemStack> ar = super.use(world, entity, hand);
		CalleCard6Pr0Procedure.execute(world, entity.getX(), entity.getY(), entity.getZ(), entity, ar.getObject());
		return ar;
	}
}
