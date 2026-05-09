
package net.pasterdream.potion;

import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.common.ForgeMod;
import net.pasterdream.init.PasterdreamModAttributes;
import net.pasterdream.procedures.InsandBuffPr0Procedure;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect;

public class InsandBuffMobEffect extends MobEffect {
	public InsandBuffMobEffect() {
		super(MobEffectCategory.HARMFUL, -14744315);
        this.addAttributeModifier(PasterdreamModAttributes.TELEPORTATIONCD.get(), uuid, 2, AttributeModifier.Operation.ADDITION);
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED, uuid, -0.3, AttributeModifier.Operation.MULTIPLY_BASE);
        this.addAttributeModifier(Attributes.ATTACK_SPEED, uuid, -0.1, AttributeModifier.Operation.MULTIPLY_BASE);
        this.addAttributeModifier(Attributes.ATTACK_DAMAGE, uuid, -2, AttributeModifier.Operation.ADDITION);
        this.addAttributeModifier(PasterdreamModAttributes.SKILLCD.get(), uuid, 1, AttributeModifier.Operation.ADDITION);
        this.addAttributeModifier(ForgeMod.ENTITY_REACH.get(), uuid, -0.2, AttributeModifier.Operation.ADDITION);
        this.addAttributeModifier(ForgeMod.BLOCK_REACH.get(), uuid, -0.2, AttributeModifier.Operation.ADDITION);
	}
    static String uuid = "a2fe40e7-6ef7-4713-bb35-5717740bc22e";
	@Override
	public String getDescriptionId() {
		return "effect.pasterdream.insand_buff";
	}


	@Override
	public void applyEffectTick(LivingEntity entity, int amplifier) {
		InsandBuffPr0Procedure.execute(entity.level(), entity.getX(), entity.getY(), entity.getZ(), entity);
	}


	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		return true;
	}
}
