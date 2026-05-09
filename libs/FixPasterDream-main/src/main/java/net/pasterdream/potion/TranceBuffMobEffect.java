
package net.pasterdream.potion;

import net.pasterdream.procedures.TranceBuffPr1Procedure;
import net.pasterdream.procedures.TranceBuffPr0Procedure;

import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect;

public class TranceBuffMobEffect extends MobEffect {
	public TranceBuffMobEffect() {
		super(MobEffectCategory.HARMFUL, -4427155);
	}

	@Override
	public String getDescriptionId() {
		return "effect.pasterdream.trance_buff";
	}

	@Override
	public void addAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
		TranceBuffPr0Procedure.execute(entity);
	}

	@Override
	public void removeAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
		super.removeAttributeModifiers(entity, attributeMap, amplifier);
		TranceBuffPr1Procedure.execute(entity);
	}

	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		return true;
	}
}
