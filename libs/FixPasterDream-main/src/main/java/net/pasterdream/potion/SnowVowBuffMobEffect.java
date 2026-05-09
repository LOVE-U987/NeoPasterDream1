
package net.pasterdream.potion;

import net.pasterdream.procedures.SnowVowBuffPr2Procedure;
import net.pasterdream.procedures.SnowVowBuffPr1Procedure;
import net.pasterdream.procedures.SnowVowBuffPr0Procedure;

import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect;

public class SnowVowBuffMobEffect extends MobEffect {
	public SnowVowBuffMobEffect() {
		super(MobEffectCategory.BENEFICIAL, -7684397);
	}

	@Override
	public String getDescriptionId() {
		return "effect.pasterdream.snow_vow_buff";
	}

	@Override
	public void addAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
		SnowVowBuffPr0Procedure.execute(entity);
	}

	@Override
	public void applyEffectTick(LivingEntity entity, int amplifier) {
		SnowVowBuffPr2Procedure.execute(entity);
	}

	@Override
	public void removeAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
		super.removeAttributeModifiers(entity, attributeMap, amplifier);
		SnowVowBuffPr1Procedure.execute(entity);
	}

	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		return true;
	}
}
