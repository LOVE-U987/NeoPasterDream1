
package net.pasterdream.potion;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect;

public class DyedreamPerfumeBuffMobEffect extends MobEffect {
	public DyedreamPerfumeBuffMobEffect() {
		super(MobEffectCategory.BENEFICIAL, -1407043);
	}

	@Override
	public String getDescriptionId() {
		return "effect.pasterdream.dyedream_perfume_buff";
	}

	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		return true;
	}
}
