package net.pasterdream.procedures;

import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;

import java.util.UUID;

public class IceSpellBuffPr0Procedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		if (entity instanceof LivingEntity && ((LivingEntity) entity).getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED) != null) {
			if (!(((LivingEntity) entity).getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED)
					.hasModifier((new AttributeModifier(UUID.fromString("616bce62-3721-466d-b0d7-c7a371939df6"), "ice_spell_buff_0", (-1), AttributeModifier.Operation.ADDITION)))))
				((LivingEntity) entity).getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED)
						.addPermanentModifier((new AttributeModifier(UUID.fromString("616bce62-3721-466d-b0d7-c7a371939df6"), "ice_spell_buff_0", (-1), AttributeModifier.Operation.ADDITION)));
		}
		if (entity instanceof LivingEntity && ((LivingEntity) entity).getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE) != null) {
			if (!(((LivingEntity) entity).getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE)
					.hasModifier((new AttributeModifier(UUID.fromString("d15daef1-f99b-4397-9206-745b2ddfea75"), "ice_spell_buff_1", (-100), AttributeModifier.Operation.ADDITION)))))
				((LivingEntity) entity).getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE)
						.addPermanentModifier((new AttributeModifier(UUID.fromString("d15daef1-f99b-4397-9206-745b2ddfea75"), "ice_spell_buff_1", (-100), AttributeModifier.Operation.ADDITION)));
		}
	}
}
