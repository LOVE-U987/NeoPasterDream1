package net.pasterdream.procedures;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.chat.Component;

public class Dreamnotes8Pr1Procedure {
	public static void execute(Entity entity, ItemStack itemstack) {
		if (entity == null)
			return;
		if (itemstack.getOrCreateTag().getBoolean("switch") == true) {
			if (entity instanceof Player _player && !_player.level().isClientSide())
				_player.displayClientMessage(Component.literal(("\u4E3B\u4E16\u754C "
						+ (("X:" + new java.text.DecimalFormat("####").format(itemstack.getOrCreateTag().getDouble("x"))) + "" + (" " + ("Z:" + new java.text.DecimalFormat("####").format(itemstack.getOrCreateTag().getDouble("z"))))))), true);
		}
	}
}
