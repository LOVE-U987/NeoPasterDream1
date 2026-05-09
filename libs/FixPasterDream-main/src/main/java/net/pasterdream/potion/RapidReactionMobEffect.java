
package net.pasterdream.potion;

import net.pasterdream.procedures.RapidReactionPr1Procedure;
import net.pasterdream.procedures.RapidReactionPr0Procedure;

import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect;

public class RapidReactionMobEffect extends MobEffect {
	public RapidReactionMobEffect() {
		super(MobEffectCategory.BENEFICIAL, -9315102);
	}

	@Override
	public String getDescriptionId() {
		return "effect.pasterdream.rapid_reaction";
	}

	@Override
	public void addAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
		RapidReactionPr0Procedure.execute(entity);
	}

	@Override
	public void removeAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
		super.removeAttributeModifiers(entity, attributeMap, amplifier);
		RapidReactionPr1Procedure.execute(entity);
	}

	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		return true;
	}
}
