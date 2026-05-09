package net.pasterdream.procedures;

import net.pasterdream.init.PasterdreamModAttributes;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;

public class TranceBuffPr1Procedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		if (entity instanceof Player) {
			((LivingEntity) entity).getAttribute(PasterdreamModAttributes.TELEPORTATIONCD.get()).setBaseValue((((LivingEntity) entity).getAttribute(PasterdreamModAttributes.TELEPORTATIONCD.get()).getBaseValue() - 1));
			((LivingEntity) entity).getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED)
					.setBaseValue((((LivingEntity) entity).getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED).getBaseValue() + 0.02));
			((LivingEntity) entity).getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_SPEED)
					.setBaseValue((((LivingEntity) entity).getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_SPEED).getBaseValue() + 0.2));
			((LivingEntity) entity).getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE)
					.setBaseValue((((LivingEntity) entity).getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE).getBaseValue() + 1));
			((LivingEntity) entity).getAttribute(PasterdreamModAttributes.SKILLCD.get()).setBaseValue((((LivingEntity) entity).getAttribute(PasterdreamModAttributes.SKILLCD.get()).getBaseValue() - 0.5));
		}
	}
}
