
package net.pasterdream.item;

import net.pasterdream.procedures.TimeHourglassPr1Procedure;
import net.pasterdream.procedures.TimeHourglassPr0Procedure;

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

public class TimeHourglassItem extends Item {
	public TimeHourglassItem() {
		super(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level world, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, world, list, flag);
		list.add(Component.literal("\u00A77\u5BF9\u7A7A\u6C14\u4F7F\u7528\u4EE5\u8DF3\u8DC3\u4E16\u754C\u65F6\u95F4\u66F4\u66FF\u663C\u591C"));
		list.add(Component.literal("\u00A77\u6F5C\u884C\u5BF9\u90E8\u5206\u8BA1\u65F6\u68A6\u5883\u65B9\u5757\u4F7F\u7528\u4EE5\u77AC\u65F6\u5B8C\u6210\u4E00\u4E2A\u9636\u6BB5"));
		list.add(Component.literal("\u00A77\u53EF\u4EE5\u5237\u65B0\u5730\u7262\u51B7\u5374"));
		list.add(Component.literal("\u00A77\u6F5C\u884C\u53F3\u51FB\u65B9\u5757\u4EE5\u4F7F\u7528"));
		list.add(Component.literal("\u00A77\u00A7o-- \u6C99\u6F0F\u5C06\u4F1A\u8BB0\u5F97 \u6211\u4EEC\u6240\u9057\u5FD8\u7684\u65F6\u5149"));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
		InteractionResultHolder<ItemStack> ar = super.use(world, entity, hand);
		TimeHourglassPr0Procedure.execute(world, entity.getX(), entity.getY(), entity.getZ(), entity);
		return ar;
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		super.useOn(context);
		TimeHourglassPr1Procedure.execute(context.getLevel(), context.getClickedPos().getX(), context.getClickedPos().getY(), context.getClickedPos().getZ(), context.getPlayer());
		return InteractionResult.SUCCESS;
	}
}
