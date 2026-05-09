
package net.pasterdream.potion;

import net.pasterdream.procedures.FlareupBuffPr1Procedure;
import net.pasterdream.procedures.FlareupBuffPr0Procedure;

import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect;

public class FlareupBuffMobEffect extends MobEffect {
	public FlareupBuffMobEffect() {
		super(MobEffectCategory.BENEFICIAL, -31417);
	}

	@Override
	public String getDescriptionId() {
		return "effect.pasterdream.flareup_buff";
	}

	@Override
	public void addAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
		FlareupBuffPr0Procedure.execute(entity);
	}

	@Override
	public void removeAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
		super.removeAttributeModifiers(entity, attributeMap, amplifier);
		FlareupBuffPr1Procedure.execute(entity);
	}

	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		return true;
	}
}
