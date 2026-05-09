
package net.pasterdream.potion;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.ItemStack;

import java.util.Collections;
import java.util.List;

public class TeleportationBuffMobEffect extends MobEffect {
	public TeleportationBuffMobEffect() {
		super(MobEffectCategory.BENEFICIAL, -1);
	}

	@Override
	public String getDescriptionId() {
		return "effect.pasterdream.teleportation_buff";
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
