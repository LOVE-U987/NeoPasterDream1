
package net.pasterdream.potion;

import net.pasterdream.procedures.MementoBuffPr1Procedure;
import net.pasterdream.procedures.MementoBuffPr0Procedure;

import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect;

public class MementoBuffMobEffect extends MobEffect {
	public MementoBuffMobEffect() {
		super(MobEffectCategory.BENEFICIAL, -8970);
	}

	@Override
	public String getDescriptionId() {
		return "effect.pasterdream.memento_buff";
	}

	@Override
	public void addAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
		MementoBuffPr0Procedure.execute(entity);
	}

	@Override
	public void removeAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
		super.removeAttributeModifiers(entity, attributeMap, amplifier);
		MementoBuffPr1Procedure.execute(entity);
	}

	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		return true;
	}
}
