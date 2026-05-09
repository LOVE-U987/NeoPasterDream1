package net.pasterdream.procedures;

import net.pasterdream.init.PasterdreamModAttributes;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;

public class MementoBuffPr1Procedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		((LivingEntity) entity).getAttribute(PasterdreamModAttributes.LUCK.get()).setBaseValue((((LivingEntity) entity).getAttribute(PasterdreamModAttributes.LUCK.get()).getBaseValue() + 10));
	}
}
