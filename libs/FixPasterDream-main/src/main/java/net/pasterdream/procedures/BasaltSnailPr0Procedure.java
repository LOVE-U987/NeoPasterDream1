package net.pasterdream.procedures;

import net.pasterdream.entity.BasaltSnailEntity;
import net.pasterdream.PasterdreamMod;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;

public class BasaltSnailPr0Procedure {
	public static void execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		PasterdreamMod.queueServerWork(5, () -> {
			if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
				_entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 170, 3, false, false));
			if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
				_entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 170, 4, false, false));
		});
		if (entity instanceof BasaltSnailEntity) {
			((BasaltSnailEntity) entity).setAnimation("empty");
		}
		if (entity instanceof BasaltSnailEntity) {
			((BasaltSnailEntity) entity).setAnimation("inoutshell");
		}
	}
}
