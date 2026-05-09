
package net.pasterdream.potion;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect;

public class ShadowSilenceBuffMobEffect extends MobEffect {
	public ShadowSilenceBuffMobEffect() {
		super(MobEffectCategory.NEUTRAL, -3355444);
	}

	@Override
	public String getDescriptionId() {
		return "effect.pasterdream.shadow_silence_buff";
	}

	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		return true;
	}
}
