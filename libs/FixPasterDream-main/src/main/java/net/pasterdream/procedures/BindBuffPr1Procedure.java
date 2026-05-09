package net.pasterdream.procedures;

import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;

import java.util.UUID;

public class BindBuffPr1Procedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		if (entity instanceof LivingEntity && ((LivingEntity) entity).getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED) != null) {
			((LivingEntity) entity).getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED)
					.removePermanentModifier((new AttributeModifier(UUID.fromString("fae1cce0-b4c7-4848-b428-62c4a21ba6a3"), "bind_buff_0", (-1), AttributeModifier.Operation.ADDITION)).getId());
		}
	}
}
