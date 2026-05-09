
package net.pasterdream.item;

import net.pasterdream.procedures.MeltdreamToolPr0Procedure;
import net.pasterdream.init.PasterdreamModItems;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.chat.Component;

import java.util.List;

public class MeltdreamShovelItem extends ShovelItem {
	public MeltdreamShovelItem() {
		super(new Tier() {
			public int getUses() {
				return 1728;
			}

			public float getSpeed() {
				return 14f;
			}

			public float getAttackDamageBonus() {
				return 5f;
			}

			public int getLevel() {
				return 5;
			}

			public int getEnchantmentValue() {
				return 12;
			}

			public Ingredient getRepairIngredient() {
				return Ingredient.of(new ItemStack(PasterdreamModItems.MELTDREAM_CRYSTAL_0.get()));
			}
		}, 1, -3f, new Item.Properties().fireResistant());
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level world, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, world, list, flag);
		list.add(Component.literal("\u00A7f\u624B\u6301\u5DE5\u5177\u65F6"));
		list.add(Component.literal("\u00A7f\u25AA \u00A77\u878D\u68A6\u4FEE\u8865\uFF1A0.01E/1\u8010\u4E45"));
	}

	@Override
	public void inventoryTick(ItemStack itemstack, Level world, Entity entity, int slot, boolean selected) {
		super.inventoryTick(itemstack, world, entity, slot, selected);
		if (selected)
			MeltdreamToolPr0Procedure.execute(entity, itemstack);
	}
}
