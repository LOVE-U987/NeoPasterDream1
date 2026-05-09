
package net.pasterdream.potion;

import net.pasterdream.procedures.GoldenrodTeaBuffPr0Procedure;

import net.minecraftforge.client.extensions.common.IClientMobEffectExtensions;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect;

public class GoldenrodTeaBuffMobEffect extends MobEffect {
	public GoldenrodTeaBuffMobEffect() {
		super(MobEffectCategory.BENEFICIAL, -395670);
	}

	@Override
	public String getDescriptionId() {
		return "effect.pasterdream.goldenrod_tea_buff";
	}

	@Override
	public void applyEffectTick(LivingEntity entity, int amplifier) {
		GoldenrodTeaBuffPr0Procedure.execute(entity);
	}

	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		return true;
	}

	@Override
	public void initializeClient(java.util.function.Consumer<IClientMobEffectExtensions> consumer) {
		consumer.accept(new IClientMobEffectExtensions() {
			@Override
			public boolean isVisibleInGui(MobEffectInstance effect) {
				return false;
			}
		});
	}
}
