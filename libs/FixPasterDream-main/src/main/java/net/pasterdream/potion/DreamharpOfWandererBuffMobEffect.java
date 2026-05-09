
package net.pasterdream.potion;

import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.pasterdream.init.PasterdreamModAttributes;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect;

public class DreamharpOfWandererBuffMobEffect extends MobEffect {
    static String uuid = "d7b2bd54-dee3-47f6-b20b-b5556b221228";
	public DreamharpOfWandererBuffMobEffect() {
		super(MobEffectCategory.BENEFICIAL, -1934926);
        this.addAttributeModifier(PasterdreamModAttributes.SAN_VARIABILITY.get(), uuid, 2.4, AttributeModifier.Operation.ADDITION);
        this.addAttributeModifier(Attributes.MAX_HEALTH, uuid, 4, AttributeModifier.Operation.ADDITION);
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED, uuid, 0.01, AttributeModifier.Operation.MULTIPLY_BASE);

    }
    @Override
    public void addAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
        super.addAttributeModifiers(entity,attributeMap,amplifier);
        entity.heal(Math.max(4 << (amplifier + 1), 0));
    }
}
