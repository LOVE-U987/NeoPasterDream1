package net.pasterdream.procedures;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;

public class StarWishRodPr1Procedure {
	public static void execute(Entity entity, ItemStack itemstack) {
		if (entity instanceof Player player &&
				(player.getMainHandItem() != itemstack && player.getOffhandItem() != itemstack))
			itemstack.getOrCreateTag().putBoolean("cast", false);
	}
}
