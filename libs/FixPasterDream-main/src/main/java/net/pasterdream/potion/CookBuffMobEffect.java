
package net.pasterdream.potion;

import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.pasterdream.init.PasterdreamModAttributes;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect;

public class CookBuffMobEffect extends MobEffect {
    static String uuid = "de214503-66e4-4d52-84c6-0a856b297f87";
    public CookBuffMobEffect() {
		super(MobEffectCategory.BENEFICIAL, -1186157);
        this.addAttributeModifier(PasterdreamModAttributes.SAN_VARIABILITY.get(), uuid, 1.2, AttributeModifier.Operation.ADDITION);
	}
    public double getAttributeModifierValue(int amplifier, AttributeModifier modifier) {
        return modifier.getAmount() * (double)(amplifier + 1);
    }
}
