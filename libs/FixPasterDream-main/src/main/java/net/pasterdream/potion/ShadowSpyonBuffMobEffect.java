
package net.pasterdream.potion;

import net.pasterdream.procedures.ShadowIntrudePr0Procedure;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect;

import net.minecraft.world.item.ItemStack;

import java.util.Collections;
import java.util.List;

public class ShadowSpyonBuffMobEffect extends MobEffect {
	public ShadowSpyonBuffMobEffect() {
		super(MobEffectCategory.HARMFUL, -13421773);
	}

	@Override
	public String getDescriptionId() {
		return "effect.pasterdream.shadow_spyon_buff";
	}

	@Override
	public void applyEffectTick(LivingEntity entity, int amplifier) {
		ShadowIntrudePr0Procedure.execute(entity.level(), entity.getX(), entity.getY(), entity.getZ(), entity);
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
