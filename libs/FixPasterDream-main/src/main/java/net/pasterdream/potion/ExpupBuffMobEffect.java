
package net.pasterdream.potion;

import net.pasterdream.procedures.ExpupBuffPr0Procedure;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect;

public class ExpupBuffMobEffect extends MobEffect {
	public ExpupBuffMobEffect() {
		super(MobEffectCategory.BENEFICIAL, -5508267);
	}

	@Override
	public String getDescriptionId() {
		return "effect.pasterdream.expup_buff";
	}

	@Override
	public void applyEffectTick(LivingEntity entity, int amplifier) {
		ExpupBuffPr0Procedure.execute(entity);
	}

	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		return true;
	}
}
