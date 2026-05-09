
package net.pasterdream.potion;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect;

public class DreamwishBuffMobEffect extends MobEffect {
	public DreamwishBuffMobEffect() {
		super(MobEffectCategory.BENEFICIAL, -357146);
	}

	@Override
	public String getDescriptionId() {
		return "effect.pasterdream.dreamwish_buff";
	}

	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		return true;
	}
}
