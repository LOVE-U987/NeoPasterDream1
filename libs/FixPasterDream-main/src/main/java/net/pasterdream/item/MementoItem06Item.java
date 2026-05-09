
package net.pasterdream.item;

import net.pasterdream.procedures.MementoItemPr0Procedure;

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

public class MementoItem06Item extends Item {
	public MementoItem06Item() {
		super(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level world, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, world, list, flag);
		list.add(Component.literal("\u00A77\u25AA \u00A79\u4F7F\u7528\u540E\u5E78\u8FD0+10\u6301\u7EED3\u5206\u949F"));
		list.add(Component.literal("\u00A77\u25AA \u00A79\u5373\u65F6\u6062\u590Dsan+10 \u878D\u68A6\u80FD\u91CF+10"));
		list.add(Component.literal("\u00A77someoneice\u7684\u4E13\u5C5E\u9057\u7269"));
		list.add(Component.literal("\u00A76PasterDream\u5F00\u53D1\u8005"));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
		InteractionResultHolder<ItemStack> ar = super.use(world, entity, hand);
		MementoItemPr0Procedure.execute(world, entity.getX(), entity.getY(), entity.getZ(), entity, ar.getObject());
		return ar;
	}
}
