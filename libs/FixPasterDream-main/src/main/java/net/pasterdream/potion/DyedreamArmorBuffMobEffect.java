
package net.pasterdream.potion;

import net.pasterdream.procedures.HealthboostArmorPr0Procedure;
import net.pasterdream.procedures.ArmorBuffPr0Procedure;

import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect;

public class DyedreamArmorBuffMobEffect extends MobEffect {
	public DyedreamArmorBuffMobEffect() {
		super(MobEffectCategory.BENEFICIAL, -1);
	}

	@Override
	public String getDescriptionId() {
		return "effect.pasterdream.dyedream_armor_buff";
	}

	@Override
	public void applyEffectTick(LivingEntity entity, int amplifier) {
		HealthboostArmorPr0Procedure.execute(entity);
	}

	@Override
	public void removeAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
		super.removeAttributeModifiers(entity, attributeMap, amplifier);
		ArmorBuffPr0Procedure.execute(entity);
	}

	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		return true;
	}
}
