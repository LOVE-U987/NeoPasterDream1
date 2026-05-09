
package net.pasterdream.potion;

import net.pasterdream.procedures.GrailBuffPr2Procedure;
import net.pasterdream.procedures.GrailBuffPr1Procedure;
import net.pasterdream.procedures.GrailBuffPr0Procedure;

import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect;

public class GrailBuffMobEffect extends MobEffect {
	public GrailBuffMobEffect() {
		super(MobEffectCategory.BENEFICIAL, -6341);
	}

	@Override
	public String getDescriptionId() {
		return "effect.pasterdream.grail_buff";
	}

	@Override
	public void addAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
		GrailBuffPr0Procedure.execute(entity);
	}

	@Override
	public void applyEffectTick(LivingEntity entity, int amplifier) {
		GrailBuffPr2Procedure.execute(entity);
	}

	@Override
	public void removeAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
		super.removeAttributeModifiers(entity, attributeMap, amplifier);
		GrailBuffPr1Procedure.execute(entity);
	}

	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		return true;
	}
}
