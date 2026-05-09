package net.pasterdream.procedures;

import net.pasterdream.init.PasterdreamModAttributes;

import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;

import java.util.UUID;

public class FurySpellBuffPr0Procedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		if (!(((LivingEntity) entity).getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE)
				.hasModifier((new AttributeModifier(UUID.fromString("c65d47fe-4c96-4fe5-beb2-f2e013872602"), "fury_spell_buff_0", 4, AttributeModifier.Operation.ADDITION)))))
			((LivingEntity) entity).getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE)
					.addPermanentModifier((new AttributeModifier(UUID.fromString("c65d47fe-4c96-4fe5-beb2-f2e013872602"), "fury_spell_buff_0", 4, AttributeModifier.Operation.ADDITION)));
		if (!(((LivingEntity) entity).getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED)
				.hasModifier((new AttributeModifier(UUID.fromString("41e71773-5d7a-4dbf-aa59-9c42f7ebc610"), "fury_spell_buff_1", 0.05, AttributeModifier.Operation.ADDITION)))))
			((LivingEntity) entity).getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED)
					.addPermanentModifier((new AttributeModifier(UUID.fromString("41e71773-5d7a-4dbf-aa59-9c42f7ebc610"), "fury_spell_buff_1", 0.05, AttributeModifier.Operation.ADDITION)));
		if (!(((LivingEntity) entity).getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_SPEED)
				.hasModifier((new AttributeModifier(UUID.fromString("7e9bd630-c8a1-4a5b-b4a9-76182d2a4973"), "fury_spell_buff_2", 3, AttributeModifier.Operation.ADDITION)))))
			((LivingEntity) entity).getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_SPEED)
					.addPermanentModifier((new AttributeModifier(UUID.fromString("7e9bd630-c8a1-4a5b-b4a9-76182d2a4973"), "fury_spell_buff_2", 3, AttributeModifier.Operation.ADDITION)));
		if (!(((LivingEntity) entity).getAttribute(PasterdreamModAttributes.SKILLCD.get())
				.hasModifier((new AttributeModifier(UUID.fromString("dd65e457-7b9f-40ac-849d-3335e1375864"), "fury_spell_buff_3", (-0.3), AttributeModifier.Operation.ADDITION)))))
			((LivingEntity) entity).getAttribute(PasterdreamModAttributes.SKILLCD.get())
					.addPermanentModifier((new AttributeModifier(UUID.fromString("dd65e457-7b9f-40ac-849d-3335e1375864"), "fury_spell_buff_3", (-0.3), AttributeModifier.Operation.ADDITION)));
		if (!(((LivingEntity) entity).getAttribute(PasterdreamModAttributes.TELEPORTATIONCD.get())
				.hasModifier((new AttributeModifier(UUID.fromString("dde69ce2-de94-4863-820a-c10c30def7be"), "fury_spell_buff_4", (-0.3), AttributeModifier.Operation.ADDITION)))))
			((LivingEntity) entity).getAttribute(PasterdreamModAttributes.TELEPORTATIONCD.get())
					.addPermanentModifier((new AttributeModifier(UUID.fromString("dde69ce2-de94-4863-820a-c10c30def7be"), "fury_spell_buff_4", (-0.3), AttributeModifier.Operation.ADDITION)));
	}
}
