package net.pasterdream.procedures;

import net.pasterdream.init.PasterdreamModMobEffects;
import net.pasterdream.entity.ShadowHandEntity;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;

public class ShadowHandPr0Procedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		if (!(entity instanceof LivingEntity _livEnt0 && _livEnt0.hasEffect(PasterdreamModMobEffects.SHADOW_SILENCE_BUFF.get()))) {
			entity.setDeltaMovement(new Vec3((entity.getLookAngle().x * 1.2), (entity.getLookAngle().y - 0.5), (entity.getLookAngle().z * 1.2)));
			if (entity instanceof ShadowHandEntity) {
				((ShadowHandEntity) entity).setAnimation("attack");
			}
		}
	}
}
