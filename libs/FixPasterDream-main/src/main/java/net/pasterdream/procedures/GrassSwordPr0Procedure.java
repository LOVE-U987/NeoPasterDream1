package net.pasterdream.procedures;

import net.pasterdream.PasterdreamMod;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;

public class GrassSwordPr0Procedure {
	public static void execute(LevelAccessor world, Entity entity, ItemStack itemstack) {
		if (entity == null)
			return;
		if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
			_entity.addEffect(new MobEffectInstance(MobEffects.POISON, 60, 0));
		PasterdreamMod.queueServerWork(1, () -> {
			if (!entity.isAlive()) {
				itemstack.getOrCreateTag().putDouble("killed", (itemstack.getOrCreateTag().getDouble("killed") + 1));
			}
		});
	}
}
