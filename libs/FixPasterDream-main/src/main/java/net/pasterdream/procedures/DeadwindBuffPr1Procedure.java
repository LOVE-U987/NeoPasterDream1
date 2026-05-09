package net.pasterdream.procedures;

import net.pasterdream.init.PasterdreamModMobEffects;
import net.pasterdream.init.PasterdreamModAttributes;

import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;

import java.util.UUID;

public class DeadwindBuffPr1Procedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		if ((entity instanceof LivingEntity _livEnt && _livEnt.hasEffect(PasterdreamModMobEffects.DEADWIND_BUFF.get()) ? _livEnt.getEffect(PasterdreamModMobEffects.DEADWIND_BUFF.get()).getAmplifier() : 0) == 0) {
			if (entity instanceof LivingEntity && ((LivingEntity) entity).getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED) != null) {
				((LivingEntity) entity).getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED)
						.removePermanentModifier((new AttributeModifier(UUID.fromString("ec261013-84a4-41b1-99ca-89754fd5f1e8"), "deadwind_buff_0", (-0.02), AttributeModifier.Operation.ADDITION)).getId());
			}
			if (entity instanceof LivingEntity && ((LivingEntity) entity).getAttribute(PasterdreamModAttributes.TELEPORTATIONRANGE.get()) != null) {
				((LivingEntity) entity).getAttribute(PasterdreamModAttributes.TELEPORTATIONRANGE.get())
						.removePermanentModifier((new AttributeModifier(UUID.fromString("8c15c7dd-9ea5-4d86-a4ae-b4d10a7a7b36"), "deadwind_buff_1", (-0.3), AttributeModifier.Operation.ADDITION)).getId());
			}
			if (entity instanceof LivingEntity && ((LivingEntity) entity).getAttribute(PasterdreamModAttributes.TELEPORTATIONCD.get()) != null) {
				((LivingEntity) entity).getAttribute(PasterdreamModAttributes.TELEPORTATIONCD.get())
						.removePermanentModifier((new AttributeModifier(UUID.fromString("7c9687e5-912e-42e5-a473-c2ada8758c36"), "deadwind_buff_2", 1.5, AttributeModifier.Operation.MULTIPLY_BASE)).getId());
			}
		} else {
			if (entity instanceof LivingEntity && ((LivingEntity) entity).getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED) != null) {
				((LivingEntity) entity).getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED)
						.removePermanentModifier((new AttributeModifier(UUID.fromString("ec261013-84a4-41b1-99ca-89754fd5f1e8"), "deadwind_buff_0", (-0.03), AttributeModifier.Operation.ADDITION)).getId());
			}
			if (entity instanceof LivingEntity && ((LivingEntity) entity).getAttribute(PasterdreamModAttributes.TELEPORTATIONRANGE.get()) != null) {
				((LivingEntity) entity).getAttribute(PasterdreamModAttributes.TELEPORTATIONRANGE.get())
						.removePermanentModifier((new AttributeModifier(UUID.fromString("8c15c7dd-9ea5-4d86-a4ae-b4d10a7a7b36"), "deadwind_buff_1", (-0.5), AttributeModifier.Operation.ADDITION)).getId());
			}
			if (entity instanceof LivingEntity && ((LivingEntity) entity).getAttribute(PasterdreamModAttributes.TELEPORTATIONCD.get()) != null) {
				((LivingEntity) entity).getAttribute(PasterdreamModAttributes.TELEPORTATIONCD.get())
						.removePermanentModifier((new AttributeModifier(UUID.fromString("7c9687e5-912e-42e5-a473-c2ada8758c36"), "deadwind_buff_2", 2, AttributeModifier.Operation.MULTIPLY_BASE)).getId());
			}
		}
	}
}
