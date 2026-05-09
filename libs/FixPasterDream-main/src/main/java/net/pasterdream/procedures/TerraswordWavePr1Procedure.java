package net.pasterdream.procedures;

import net.pasterdream.init.PasterdreamModMobEffects;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffectInstance;

public class TerraswordWavePr1Procedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
			_entity.addEffect(new MobEffectInstance(PasterdreamModMobEffects.CLOUDMIST_BUFF.get(), 25, 0, false, false));
		TerraswordWavePr2Procedure.execute(entity);
	}
}
