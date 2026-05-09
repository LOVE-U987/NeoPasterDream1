
package net.pasterdream.item;

import net.pasterdream.procedures.StarWishRodPr1Procedure;
import net.pasterdream.procedures.StarWishRodPr0Procedure;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.network.chat.Component;

import java.util.List;

public class StarWishRodItem extends FishingRodItem {
	public StarWishRodItem() {
		super(new Item.Properties().durability(0).fireResistant());
	}

	@Override
	public int getEnchantmentValue() {
		return 10;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
		super.use(world, entity, hand);
		ItemStack itemstack = entity.getItemInHand(hand);
		StarWishRodPr0Procedure.execute(itemstack);
		return InteractionResultHolder.sidedSuccess(itemstack, world.isClientSide());
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level world, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, world, list, flag);
		list.add(Component.literal("\u00A77\u25AA \u00A79\u9493\u9C7C\u7279\u6B8A\u5E78\u8FD0\u503C\u4E58\u533A\uFF1A220%"));
		list.add(Component.literal("\u00A77\u25AA \u00A79\u5C06\u53EF\u80FD\u9493\u5230\u66F4\u6709\u4EF7\u503C\u7684\u6DF1\u6D77\u79D8\u5B9D"));
	}

	@Override
	public void inventoryTick(ItemStack itemstack, Level world, Entity entity, int slot, boolean selected) {
		super.inventoryTick(itemstack, world, entity, slot, selected);
		StarWishRodPr1Procedure.execute(entity, itemstack);
	}
}
