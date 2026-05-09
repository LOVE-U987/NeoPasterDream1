
package net.pasterdream.potion;

import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.pasterdream.init.PasterdreamModAttributes;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect;

public class CounterattackBuffMobEffect extends MobEffect {
	private final static String uuid = "304e7468-21b2-4ef2-b6e4-a7b8358d782b";

	public CounterattackBuffMobEffect() {
		super(MobEffectCategory.BENEFICIAL, -7426603);
		this.addAttributeModifier(Attributes.ATTACK_DAMAGE, uuid, 3, AttributeModifier.Operation.ADDITION);
		this.addAttributeModifier(PasterdreamModAttributes.SKILLMULTIPLIER.get(), uuid, 0.5, AttributeModifier.Operation.ADDITION);
	}

	@Override
	public String getDescriptionId() {
		return "effect.pasterdream.counterattack_buff";
	}

	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		return true;
	}
}
