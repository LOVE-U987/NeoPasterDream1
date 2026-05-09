package net.pasterdream.procedures;

import net.pasterdream.init.PasterdreamModItems;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffects;

public class ArmorBuffPr0Procedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		if (((entity instanceof LivingEntity _entGetArmor ? _entGetArmor.getItemBySlot(EquipmentSlot.HEAD) : ItemStack.EMPTY).getItem() == PasterdreamModItems.DYEDREAM_ARMOR_HELMET.get()) == false
				|| ((entity instanceof LivingEntity _entGetArmor ? _entGetArmor.getItemBySlot(EquipmentSlot.CHEST) : ItemStack.EMPTY).getItem() == PasterdreamModItems.DYEDREAM_ARMOR_CHESTPLATE.get()) == false
				|| ((entity instanceof LivingEntity _entGetArmor ? _entGetArmor.getItemBySlot(EquipmentSlot.LEGS) : ItemStack.EMPTY).getItem() == PasterdreamModItems.DYEDREAM_ARMOR_LEGGINGS.get()) == false
				|| ((entity instanceof LivingEntity _entGetArmor ? _entGetArmor.getItemBySlot(EquipmentSlot.FEET) : ItemStack.EMPTY).getItem() == PasterdreamModItems.DYEDREAM_ARMOR_BOOTS.get()) == false) {
			if (entity instanceof LivingEntity _entity)
				_entity.removeEffect(MobEffects.HEALTH_BOOST);
		}
	}
}
