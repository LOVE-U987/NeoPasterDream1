package net.pasterdream.procedures;

import net.pasterdream.init.PasterdreamModMobEffects;
import net.pasterdream.init.PasterdreamModAttributes;

import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;

import java.util.UUID;

public class TailwindBuffPr0Procedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		if (entity instanceof LivingEntity _entity)
			_entity.removeEffect(PasterdreamModMobEffects.DEADWIND_BUFF.get());
		if ((entity instanceof LivingEntity _livEnt && _livEnt.hasEffect(PasterdreamModMobEffects.TAILWIND_BUFF.get()) ? _livEnt.getEffect(PasterdreamModMobEffects.TAILWIND_BUFF.get()).getAmplifier() : 0) == 0) {
			if (entity instanceof LivingEntity && ((LivingEntity) entity).getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED) != null) {
				if (!(((LivingEntity) entity).getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED)
						.hasModifier((new AttributeModifier(UUID.fromString("a70291bb-c451-4b26-b160-55f7b12c32ed"), "tailwind_buff_0", 0.03, AttributeModifier.Operation.ADDITION)))))
					((LivingEntity) entity).getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED)
							.addPermanentModifier((new AttributeModifier(UUID.fromString("a70291bb-c451-4b26-b160-55f7b12c32ed"), "tailwind_buff_0", 0.03, AttributeModifier.Operation.ADDITION)));
			}
			if (entity instanceof LivingEntity && ((LivingEntity) entity).getAttribute(PasterdreamModAttributes.TELEPORTATIONRANGE.get()) != null) {
				if (!(((LivingEntity) entity).getAttribute(PasterdreamModAttributes.TELEPORTATIONRANGE.get())
						.hasModifier((new AttributeModifier(UUID.fromString("78c6cb5b-3f93-4e1c-a22f-6a3f2a3ed1ff"), "tailwind_buff_1", 1, AttributeModifier.Operation.ADDITION)))))
					((LivingEntity) entity).getAttribute(PasterdreamModAttributes.TELEPORTATIONRANGE.get())
							.addPermanentModifier((new AttributeModifier(UUID.fromString("78c6cb5b-3f93-4e1c-a22f-6a3f2a3ed1ff"), "tailwind_buff_1", 1, AttributeModifier.Operation.ADDITION)));
			}
			if (entity instanceof LivingEntity && ((LivingEntity) entity).getAttribute(PasterdreamModAttributes.TELEPORTATIONCD.get()) != null) {
				if (!(((LivingEntity) entity).getAttribute(PasterdreamModAttributes.TELEPORTATIONCD.get())
						.hasModifier((new AttributeModifier(UUID.fromString("5aa8de78-2c53-4e9a-9205-87e71fa62ea1"), "tailwind_buff_2", 0.7, AttributeModifier.Operation.MULTIPLY_BASE)))))
					((LivingEntity) entity).getAttribute(PasterdreamModAttributes.TELEPORTATIONCD.get())
							.addPermanentModifier((new AttributeModifier(UUID.fromString("5aa8de78-2c53-4e9a-9205-87e71fa62ea1"), "tailwind_buff_2", 0.7, AttributeModifier.Operation.MULTIPLY_BASE)));
			}
		} else {
			if (entity instanceof LivingEntity && ((LivingEntity) entity).getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED) != null) {
				if (!(((LivingEntity) entity).getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED)
						.hasModifier((new AttributeModifier(UUID.fromString("a70291bb-c451-4b26-b160-55f7b12c32ed"), "tailwind_buff_0", 0.04, AttributeModifier.Operation.ADDITION)))))
					((LivingEntity) entity).getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED)
							.addPermanentModifier((new AttributeModifier(UUID.fromString("a70291bb-c451-4b26-b160-55f7b12c32ed"), "tailwind_buff_0", 0.04, AttributeModifier.Operation.ADDITION)));
			}
			if (entity instanceof LivingEntity && ((LivingEntity) entity).getAttribute(PasterdreamModAttributes.TELEPORTATIONRANGE.get()) != null) {
				if (!(((LivingEntity) entity).getAttribute(PasterdreamModAttributes.TELEPORTATIONRANGE.get())
						.hasModifier((new AttributeModifier(UUID.fromString("78c6cb5b-3f93-4e1c-a22f-6a3f2a3ed1ff"), "tailwind_buff_1", 1.5, AttributeModifier.Operation.ADDITION)))))
					((LivingEntity) entity).getAttribute(PasterdreamModAttributes.TELEPORTATIONRANGE.get())
							.addPermanentModifier((new AttributeModifier(UUID.fromString("78c6cb5b-3f93-4e1c-a22f-6a3f2a3ed1ff"), "tailwind_buff_1", 1.5, AttributeModifier.Operation.ADDITION)));
			}
			if (entity instanceof LivingEntity && ((LivingEntity) entity).getAttribute(PasterdreamModAttributes.TELEPORTATIONCD.get()) != null) {
				if (!(((LivingEntity) entity).getAttribute(PasterdreamModAttributes.TELEPORTATIONCD.get())
						.hasModifier((new AttributeModifier(UUID.fromString("5aa8de78-2c53-4e9a-9205-87e71fa62ea1"), "tailwind_buff_2", 0.6, AttributeModifier.Operation.MULTIPLY_BASE)))))
					((LivingEntity) entity).getAttribute(PasterdreamModAttributes.TELEPORTATIONCD.get())
							.addPermanentModifier((new AttributeModifier(UUID.fromString("5aa8de78-2c53-4e9a-9205-87e71fa62ea1"), "tailwind_buff_2", 0.6, AttributeModifier.Operation.MULTIPLY_BASE)));
			}
		}
	}
}
