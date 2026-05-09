
package net.pasterdream.item;

import net.pasterdream.procedures.RootsPaleBoneneedlePr1Procedure;
import net.pasterdream.procedures.RootsPaleBoneneedlePr0Procedure;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.network.chat.Component;

import java.util.List;

public class RootsPaleBoneneedleItem extends Item {
	public RootsPaleBoneneedleItem() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level world, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, world, list, flag);
		list.add(Component.literal("\u00A77\u8BA9\u4F60\u4ECE\u68A6\u4E2D\u6E05\u9192...\u5E76\u56DE\u5230\u6807\u8BB0\u7684\u4F4D\u7F6E"));
		list.add(Component.literal("\u00A77\u6F5C\u884C\u53F3\u51FB\u4EE5\u8BBE\u7F6E\u6807\u8BB0\u70B9"));
		list.add(Component.literal("\u00A77\u5982\u672A\u8BBE\u7F6E\u6807\u8BB0\u5C06\u9ED8\u8BA4\u8FD4\u56DE\u91CD\u751F\u70B9"));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
		InteractionResultHolder<ItemStack> ar = super.use(world, entity, hand);
		RootsPaleBoneneedlePr0Procedure.execute(world, entity.getX(), entity.getY(), entity.getZ(), entity, ar.getObject());
		return ar;
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		super.useOn(context);
		RootsPaleBoneneedlePr1Procedure.execute(context.getLevel(), context.getClickedPos().getX(), context.getClickedPos().getY(), context.getClickedPos().getZ(), context.getPlayer(), context.getItemInHand());
		return InteractionResult.SUCCESS;
	}
}
