
package net.pasterdream.potion;

import net.pasterdream.procedures.RestrainmoveBlockBuffPr0Procedure;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect;

import net.minecraft.world.item.ItemStack;

import java.util.Collections;
import java.util.List;

public class RestrainmoveBlockBuffMobEffect extends MobEffect {
	public RestrainmoveBlockBuffMobEffect() {
		super(MobEffectCategory.BENEFICIAL, -7469562);
	}

	@Override
	public String getDescriptionId() {
		return "effect.pasterdream.restrainmove_block_buff";
	}

	@Override
	public void applyEffectTick(LivingEntity entity, int amplifier) {
		RestrainmoveBlockBuffPr0Procedure.execute(entity);
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
