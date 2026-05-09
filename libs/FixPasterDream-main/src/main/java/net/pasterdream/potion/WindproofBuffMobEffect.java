
package net.pasterdream.potion;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect;

public class WindproofBuffMobEffect extends MobEffect {
	public WindproofBuffMobEffect() {
		super(MobEffectCategory.BENEFICIAL, -4396554);
	}

	@Override
	public String getDescriptionId() {
		return "effect.pasterdream.windproof_buff";
	}

	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		return true;
	}
}
