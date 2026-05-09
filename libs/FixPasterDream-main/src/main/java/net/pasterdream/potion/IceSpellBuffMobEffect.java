
package net.pasterdream.potion;

import net.pasterdream.procedures.IceSpellBuffPr1Procedure;
import net.pasterdream.procedures.IceSpellBuffPr0Procedure;

import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect;

public class IceSpellBuffMobEffect extends MobEffect {
	public IceSpellBuffMobEffect() {
		super(MobEffectCategory.HARMFUL, -4657930);
	}

	@Override
	public String getDescriptionId() {
		return "effect.pasterdream.ice_spell_buff";
	}

	@Override
	public void addAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
		IceSpellBuffPr0Procedure.execute(entity);
	}

	@Override
	public void removeAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
		super.removeAttributeModifiers(entity, attributeMap, amplifier);
		IceSpellBuffPr1Procedure.execute(entity);
	}

	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		return true;
	}
}
