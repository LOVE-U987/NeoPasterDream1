package net.pasterdream.procedures;

import net.pasterdream.init.PasterdreamModAttributes;

import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;

public class AtkPr0Procedure {
	public static void execute(Entity entity, ItemStack itemstack) {
		if (entity == null)
			return;
		itemstack.getOrCreateTag().putDouble("paster_atk", (((LivingEntity) entity).getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE).getValue() + itemstack.getEnchantmentLevel(Enchantments.SHARPNESS) * 0.5));
		itemstack.getOrCreateTag().putDouble("skill_multiplier", ((LivingEntity) entity).getAttribute(PasterdreamModAttributes.SKILLMULTIPLIER.get()).getBaseValue());
	}
}
