package net.pasterdream.procedures;

import top.theillusivec4.curios.api.CuriosApi;

import net.pasterdream.init.PasterdreamModMobEffects;
import net.pasterdream.init.PasterdreamModItems;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffectInstance;

public class TeleportationPr2Procedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
			_entity.addEffect(new MobEffectInstance(PasterdreamModMobEffects.EVASION_BUFF.get(), 2, 0, false, false));
		if (entity instanceof LivingEntity lv ? CuriosApi.getCuriosHelper().findEquippedCurio(PasterdreamModItems.CROSS_NECKLACE.get(), lv).isPresent() : false == true) {
			if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
				_entity.addEffect(new MobEffectInstance(PasterdreamModMobEffects.EVASION_BUFF.get(), 6, 0, false, false));
		}
		if (entity instanceof LivingEntity lv ? CuriosApi.getCuriosHelper().findEquippedCurio(PasterdreamModItems.BOBOJI_CURIO.get(), lv).isPresent() : false == true) {
			if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
				_entity.addEffect(new MobEffectInstance(PasterdreamModMobEffects.BOBOJI_BUFF.get(), 16, 0, false, false));
		}
		if (entity instanceof LivingEntity _livEnt5 && _livEnt5.hasEffect(PasterdreamModMobEffects.EVASION_CLOAK_BUFF.get())) {
			if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
				_entity.addEffect(new MobEffectInstance(PasterdreamModMobEffects.EVASION_BUFF.get(), 12, 0, false, false));
		}
	}
}
