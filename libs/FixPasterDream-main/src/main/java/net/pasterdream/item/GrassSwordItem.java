
package net.pasterdream.item;

import net.pasterdream.procedures.GrassSwordPr1Procedure;
import net.pasterdream.procedures.GrassSwordPr0Procedure;
import net.pasterdream.init.PasterdreamModItems;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.chat.Component;

import java.util.List;

public class GrassSwordItem extends SwordItem {
	public GrassSwordItem() {
		super(new Tier() {
			public int getUses() {
				return 874;
			}

			public float getSpeed() {
				return 1f;
			}

			public float getAttackDamageBonus() {
				return 3f;
			}

			public int getLevel() {
				return 1;
			}

			public int getEnchantmentValue() {
				return 16;
			}

			public Ingredient getRepairIngredient() {
				return Ingredient.of(new ItemStack(PasterdreamModItems.JUNGLE_SPORE.get()));
			}
		}, 3, -2.5f, new Item.Properties());
	}

	@Override
	public boolean hurtEnemy(ItemStack itemstack, LivingEntity entity, LivingEntity sourceentity) {
		boolean retval = super.hurtEnemy(itemstack, entity, sourceentity);
		GrassSwordPr0Procedure.execute(entity.level(), entity, itemstack);
		return retval;
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level world, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, world, list, flag);
		list.add(Component.literal("\u9576\u5D4C\uFF1A\u00A77\u65E0"));
		list.add(Component.literal("\u00A77\u25AA \u00A79\u653B\u51FB\u65BD\u52A0\u4E2D\u6BD2\u6548\u679C"));
	}

	@Override
	public void inventoryTick(ItemStack itemstack, Level world, Entity entity, int slot, boolean selected) {
		super.inventoryTick(itemstack, world, entity, slot, selected);
		if (selected)
			GrassSwordPr1Procedure.execute(world, entity.getX(), entity.getY(), entity.getZ(), entity, itemstack);
	}
}
