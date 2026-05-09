
package net.pasterdream.potion;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.pasterdream.init.PasterdreamModAttributes;


public class CheerupBuffMobEffect extends MobEffect {
	private final static String uuid = "b57e603c-ae6f-44f3-b51e-b4e5b89171c9";
	public CheerupBuffMobEffect() {
		super(MobEffectCategory.BENEFICIAL, -33158);
		this.addAttributeModifier(PasterdreamModAttributes.TELEPORTATIONCD.get(), uuid, -0.1, AttributeModifier.Operation.ADDITION);
		this.addAttributeModifier(Attributes.MOVEMENT_SPEED, uuid, 0.05, AttributeModifier.Operation.ADDITION);
		this.addAttributeModifier(Attributes.ATTACK_SPEED, uuid, 0.05, AttributeModifier.Operation.ADDITION);
		this.addAttributeModifier(PasterdreamModAttributes.SKILLCD.get(), uuid, -0.1, AttributeModifier.Operation.ADDITION);
	}
}
