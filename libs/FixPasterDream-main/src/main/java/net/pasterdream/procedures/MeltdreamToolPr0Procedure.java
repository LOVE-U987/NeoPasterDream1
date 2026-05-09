package net.pasterdream.procedures;

import net.pasterdream.capability.MeltDreamEnergyCapability;
import net.pasterdream.init.PasterdreamModAttributes;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.util.RandomSource;

public class MeltdreamToolPr0Procedure {
	public static void execute(Entity entity, ItemStack itemstack) {
		if (entity == null)
			return;
		if (entity instanceof Player pl && pl.tickCount % 10 == 0) {
			if (itemstack.getDamageValue() >= 1 && MeltDreamEnergyCapability.consumePlayerMeltDreamEnergy(pl,0.01)) {
                if (itemstack.hurt(-1, RandomSource.create(), null)) {
                    itemstack.shrink(1);
                    itemstack.setDamageValue(0);
                }
			}
		}
	}
}
