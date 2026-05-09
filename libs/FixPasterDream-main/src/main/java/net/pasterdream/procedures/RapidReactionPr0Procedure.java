package net.pasterdream.procedures;

import net.pasterdream.init.PasterdreamModAttributes;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;

public class RapidReactionPr0Procedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		((LivingEntity) entity).getAttribute(PasterdreamModAttributes.TELEPORTATIONCD.get()).setBaseValue((((LivingEntity) entity).getAttribute(PasterdreamModAttributes.TELEPORTATIONCD.get()).getBaseValue() - 0.2));
	}
}
