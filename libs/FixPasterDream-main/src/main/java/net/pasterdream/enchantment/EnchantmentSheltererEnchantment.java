
package net.pasterdream.enchantment;

import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.damagesource.DamageSource;

public class EnchantmentSheltererEnchantment extends Enchantment {
	public EnchantmentSheltererEnchantment(EquipmentSlot... slots) {
		super(Enchantment.Rarity.VERY_RARE, EnchantmentCategory.ARMOR, slots);
	}

	@Override
	public int getMaxLevel() {
		return 3;
	}

	@Override
	public int getDamageProtection(int level, DamageSource source) {
		return level * 2;
	}

	@Override
	public boolean isTreasureOnly() {
		return true;
	}
}
