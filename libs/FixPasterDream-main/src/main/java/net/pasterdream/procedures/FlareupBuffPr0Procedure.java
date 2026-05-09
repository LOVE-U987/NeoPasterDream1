package net.pasterdream.procedures;

import net.pasterdream.init.PasterdreamModAttributes;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;

public class FlareupBuffPr0Procedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		((LivingEntity) entity).getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE)
				.setBaseValue((((LivingEntity) entity).getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE).getBaseValue() + 3));
		((LivingEntity) entity).getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_SPEED)
				.setBaseValue((((LivingEntity) entity).getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_SPEED).getBaseValue() + 0.2));
		((LivingEntity) entity).getAttribute(PasterdreamModAttributes.SKILLMULTIPLIER.get()).setBaseValue((((LivingEntity) entity).getAttribute(PasterdreamModAttributes.SKILLMULTIPLIER.get()).getBaseValue() + 0.5));
		((LivingEntity) entity).getAttribute(PasterdreamModAttributes.SKILLCD.get()).setBaseValue((((LivingEntity) entity).getAttribute(PasterdreamModAttributes.SKILLCD.get()).getBaseValue() - 0.1));
	}
}
