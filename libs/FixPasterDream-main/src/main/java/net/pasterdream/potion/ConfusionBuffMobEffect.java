
package net.pasterdream.potion;

import net.pasterdream.procedures.ConfusionBuffPr0Procedure;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.ItemStack;

import java.util.Collections;
import java.util.List;

public class ConfusionBuffMobEffect extends MobEffect {
	public ConfusionBuffMobEffect() {
		super(MobEffectCategory.HARMFUL, -12108960);
	}

	@Override
	public String getDescriptionId() {
		return "effect.pasterdream.confusion_buff";
	}

	@Override
	public void applyEffectTick(LivingEntity entity, int amplifier) {
		ConfusionBuffPr0Procedure.execute(entity);
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
