
package net.pasterdream.potion;

import net.pasterdream.procedures.TurnbackCloakBuffPr0Procedure;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect;

public class TurnbackCloakBuffMobEffect extends MobEffect {
	public TurnbackCloakBuffMobEffect() {
		super(MobEffectCategory.BENEFICIAL, -1);
	}

	@Override
	public String getDescriptionId() {
		return "effect.pasterdream.turnback_cloak_buff";
	}

	@Override
	public void applyEffectTick(LivingEntity entity, int amplifier) {
		TurnbackCloakBuffPr0Procedure.execute(entity.level(), entity.getX(), entity.getY(), entity.getZ());
	}

	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		return true;
	}
}
