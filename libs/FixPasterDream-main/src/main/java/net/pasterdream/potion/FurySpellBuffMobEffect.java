
package net.pasterdream.potion;

import net.pasterdream.procedures.FurySpellBuffPr1Procedure;
import net.pasterdream.procedures.FurySpellBuffPr0Procedure;

import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect;

public class FurySpellBuffMobEffect extends MobEffect {
	public FurySpellBuffMobEffect() {
		super(MobEffectCategory.BENEFICIAL, -4827668);
	}

	@Override
	public String getDescriptionId() {
		return "effect.pasterdream.fury_spell_buff";
	}

	@Override
	public void addAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
		FurySpellBuffPr0Procedure.execute(entity);
	}

	@Override
	public void removeAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
		super.removeAttributeModifiers(entity, attributeMap, amplifier);
		FurySpellBuffPr1Procedure.execute(entity);
	}

	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		return true;
	}
}
