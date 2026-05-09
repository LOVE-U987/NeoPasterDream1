package net.pasterdream.procedures;

import net.pasterdream.init.PasterdreamModMobEffects;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;

public class QymArmorPr3Procedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		if (entity instanceof LivingEntity _entity)
			_entity.removeEffect(PasterdreamModMobEffects.TELEPORTATION_BUFF.get());
	}
}
