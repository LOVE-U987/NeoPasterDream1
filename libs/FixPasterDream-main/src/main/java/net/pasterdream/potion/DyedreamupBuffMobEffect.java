
package net.pasterdream.potion;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect;

public class DyedreamupBuffMobEffect extends MobEffect {
	public DyedreamupBuffMobEffect() {
		super(MobEffectCategory.BENEFICIAL, -32550);
	}

	@Override
	public String getDescriptionId() {
		return "effect.pasterdream.dyedreamup_buff";
	}

	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		return true;
	}
}
