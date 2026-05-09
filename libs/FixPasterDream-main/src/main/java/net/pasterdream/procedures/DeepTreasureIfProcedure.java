package net.pasterdream.procedures;

import net.minecraft.world.item.ItemStack;

public class DeepTreasureIfProcedure {
	public static boolean execute(ItemStack itemstack) {
		return itemstack.getOrCreateTag().getBoolean("deep_treasure_super") == true;
	}
}
