package net.pasterdream.procedures;

import net.pasterdream.init.PasterdreamModMobEffects;
import net.pasterdream.entity.AaroncosRighthand0Entity;
import net.pasterdream.entity.AaroncosLefthand0Entity;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.tags.TagKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.Registries;

public class WhiteSwordRainPr1Procedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		if (entity instanceof AaroncosLefthand0Entity || entity instanceof AaroncosRighthand0Entity) {
			if (Math.random() <= 0.06) {
				if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
					_entity.addEffect(new MobEffectInstance(PasterdreamModMobEffects.SHADOW_SILENCE_BUFF.get(), 200, 0));
			}
		} else {
			if (entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("pasterdream:shadow_mob")))) {
				if (Math.random() <= 0.12) {
					if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
						_entity.addEffect(new MobEffectInstance(PasterdreamModMobEffects.SHADOW_SILENCE_BUFF.get(), 200, 0));
				}
			}
		}
		if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
			_entity.addEffect(new MobEffectInstance(PasterdreamModMobEffects.BIND_BUFF.get(), 40, 0));
	}
}
