
package net.pasterdream.potion;

import net.pasterdream.procedures.EvasionCloakBuffPr0Procedure;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect;

public class EvasionCloakBuffMobEffect extends MobEffect {
	public EvasionCloakBuffMobEffect() {
		super(MobEffectCategory.BENEFICIAL, -5509124);
	}

	@Override
	public String getDescriptionId() {
		return "effect.pasterdream.evasion_cloak_buff";
	}

	@Override
	public void applyEffectTick(LivingEntity entity, int amplifier) {
		EvasionCloakBuffPr0Procedure.execute(entity.level(), entity.getX(), entity.getY(), entity.getZ());
	}

	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		return true;
	}
}
