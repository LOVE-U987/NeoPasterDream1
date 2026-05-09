package net.pasterdream.procedures;

import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.pasterdream.init.PasterdreamModAttributes;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;

public class SnowVowBuffPr1Procedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		AttributeInstance instance = ((LivingEntity) entity).getAttribute(PasterdreamModAttributes.LUCK.get());
		if(instance != null)
		{
			instance.setBaseValue(instance.getBaseValue() - 3);
		}
	}
}
