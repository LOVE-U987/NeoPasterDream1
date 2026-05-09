package net.pasterdream.procedures;

import net.pasterdream.network.PasterdreamModVariables;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.nbt.CompoundTag;

public class WorkshopNbtcopy0Procedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		if (PasterdreamModVariables.weapon_workshop_item.getOrCreateTag().getBoolean("paster_attack_damage") == true) {
			(entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getOrCreateTag().putBoolean("paster_attack_damage", true);
			(entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getOrCreateTag().putDouble("paster_attack_damage_number",
					(PasterdreamModVariables.weapon_workshop_item.getOrCreateTag().getDouble("paster_attack_damage_number")));
		}
		if (PasterdreamModVariables.weapon_workshop_item.getOrCreateTag().getBoolean("paster_attack_speed") == true) {
			(entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getOrCreateTag().putBoolean("paster_attack_speed", true);
			(entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getOrCreateTag().putDouble("paster_attack_speed_number",
					(PasterdreamModVariables.weapon_workshop_item.getOrCreateTag().getDouble("paster_attack_speed_number")));
		}
		if (PasterdreamModVariables.weapon_workshop_item.getOrCreateTag().getBoolean("paster_movement_speed") == true) {
			(entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getOrCreateTag().putBoolean("paster_movement_speed", true);
			(entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getOrCreateTag().putDouble("paster_movement_speed_number",
					(PasterdreamModVariables.weapon_workshop_item.getOrCreateTag().getDouble("paster_movement_speed_number")));
		}
		if (PasterdreamModVariables.weapon_workshop_item.getOrCreateTag().getBoolean("paster_luck") == true) {
			(entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getOrCreateTag().putBoolean("paster_luck", true);
			(entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getOrCreateTag().putDouble("paster_luck_number", (PasterdreamModVariables.weapon_workshop_item.getOrCreateTag().getDouble("paster_luck_number")));
		}
		PasterdreamModVariables.weapon_workshop_item = new ItemStack(Blocks.AIR);
		{
			CompoundTag _nbtTag = new ItemStack(Blocks.AIR).getTag();
			if (_nbtTag != null)
				PasterdreamModVariables.weapon_workshop_item.setTag(_nbtTag.copy());
		}
	}
}
