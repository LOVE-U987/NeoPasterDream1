
package net.pasterdream.potion;

import net.pasterdream.procedures.LethargyBuffPr1Procedure;
import net.pasterdream.procedures.LethargyBuffPr0Procedure;

import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect;

public class LethargyBuffMobEffect extends MobEffect {
	public LethargyBuffMobEffect() {
		super(MobEffectCategory.HARMFUL, -2907486);
	}

	@Override
	public String getDescriptionId() {
		return "effect.pasterdream.lethargy_buff";
	}

	@Override
	public void addAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
		LethargyBuffPr0Procedure.execute(entity);
	}

	@Override
	public void removeAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
		super.removeAttributeModifiers(entity, attributeMap, amplifier);
		LethargyBuffPr1Procedure.execute(entity);
	}

	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		return true;
	}
}
