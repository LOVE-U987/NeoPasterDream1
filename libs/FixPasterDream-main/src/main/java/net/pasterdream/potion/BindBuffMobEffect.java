
package net.pasterdream.potion;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect;

public class BindBuffMobEffect extends MobEffect {
	public BindBuffMobEffect() {
		super(MobEffectCategory.HARMFUL, -6180680);
	}

	@Override
	public String getDescriptionId() {
		return "effect.pasterdream.bind_buff";
	}

	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		return true;
	}
}
