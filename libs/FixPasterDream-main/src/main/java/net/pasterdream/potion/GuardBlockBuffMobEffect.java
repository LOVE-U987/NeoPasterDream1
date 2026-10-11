
package net.pasterdream.potion;

import net.pasterdream.procedures.GuardBlockBuffPr1Procedure;
import net.pasterdream.procedures.GuardBlockBuffPr0Procedure;

import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.ItemStack;

import java.util.Collections;
import java.util.List;

public class GuardBlockBuffMobEffect extends MobEffect {
	public GuardBlockBuffMobEffect() {
		super(MobEffectCategory.BENEFICIAL, -11534228);
	}

	@Override
	public String getDescriptionId() {
		return "effect.pasterdream.guard_block_buff";
	}

	@Override
	public void addAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
		GuardBlockBuffPr0Procedure.execute(entity);
	}

	@Override
	public void removeAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
		super.removeAttributeModifiers(entity, attributeMap, amplifier);
		GuardBlockBuffPr1Procedure.execute(entity);
	}

	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		return true;
	}

	@Override
    public List<ItemStack> getCurativeItems() {
    	return Collections.emptyList();
    }
}
