
package net.pasterdream.item;

import top.theillusivec4.curios.api.type.capability.ICurioItem;

import net.pasterdream.procedures.TurnbackCloakPr1Procedure;

import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.network.chat.Component;

import java.util.List;

public class TurnbackCloakItem extends Item implements ICurioItem {
	public TurnbackCloakItem() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean isFoil(ItemStack itemstack) {
		return TurnbackCloakPr1Procedure.execute(itemstack);
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level world, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, world, list, flag);
		list.add(Component.literal("\u54C1\u8D28\uFF1A\u00A7c\u4F20\u8BF4 \u2605\u2605\u2605\u2605\u2605\u2605\u2605"));
		list.add(Component.literal("\u00A77\u25AA \u00A7f\u9ED8\u8BA4\u6309[\u00A7bZ\u00A7f]\u542F\u7528\u8863\u88C5"));
		list.add(Component.literal("\u00A77\u25AA \u00A79\u8863\u88C5\u542F\u7528\u540E\u53EF\u4EE5\u572890\u79D2\u5185\u81EA\u52A8\u56DE\u907F6\u6B21\u4F24\u5BB3"));
		list.add(Component.literal("\u00A77\u25AA \u00A79\u6210\u529F\u56DE\u907F\u4F24\u5BB3\u540E\u83B7\u5F97 \u53CD\u51FBI\uFF080:02\uFF09"));
		list.add(Component.literal("\u00A77\u25AA \u00A79\u51B7\u5374\u65F6\u95F4\uFF1A300\u79D2"));
		list.add(Component.literal("\u00A77\u25AA \u00A74\u878D\u68A6\u80FD\u91CF\u6D88\u8017\uFF1A10"));
	}
}
