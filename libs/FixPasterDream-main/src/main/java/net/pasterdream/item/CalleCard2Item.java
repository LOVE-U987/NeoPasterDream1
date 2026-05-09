
package net.pasterdream.item;

import net.pasterdream.procedures.CalleCard2Pr0Procedure;

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

public class CalleCard2Item extends Item {
	public CalleCard2Item() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level world, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, world, list, flag);
		list.add(Component.literal("\u00A76\u300E\u6267\u5251\u300F"));
		list.add(Component.literal("\u00A7f\u25AA \u00A77\u4F7F\u7528\u5361\u724C\u65F6\uFF1A"));
		list.add(Component.literal("\u00A77\u25AA \u00A79\u83B7\u5F97\u6012\u6C14\u7206\u53D1\u6548\u679C \u6301\u7EED120\u79D2"));
		list.add(Component.literal("\u00A77\u25AA \u00A79\u671F\u95F4\u653B\u51FB\u529B+3 \u653B\u51FB\u901F\u5EA6+0.2 \u6218\u6280\u4F24\u5BB3\u500D\u7387+50% \u6280\u80FD\u51B7\u5374-10%"));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
		InteractionResultHolder<ItemStack> ar = super.use(world, entity, hand);
		CalleCard2Pr0Procedure.execute(world, entity.getX(), entity.getY(), entity.getZ(), entity, ar.getObject());
		return ar;
	}
}
