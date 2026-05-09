package net.pasterdream.procedures;

import net.minecraft.world.item.ItemStack;

public class StarWishRodPr0Procedure {
	public static void execute(ItemStack itemstack) {
		itemstack.getOrCreateTag().putBoolean("cast", !itemstack.getOrCreateTag().getBoolean("cast"));
	}
}
