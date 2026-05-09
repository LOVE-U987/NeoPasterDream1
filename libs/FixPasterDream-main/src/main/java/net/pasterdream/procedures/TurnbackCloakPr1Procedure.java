package net.pasterdream.procedures;

import net.minecraft.world.item.ItemStack;

public class TurnbackCloakPr1Procedure {
	public static boolean execute(ItemStack itemstack) {
		return itemstack.getOrCreateTag().getDouble("cloak_cd") <= 0;
	}
}
