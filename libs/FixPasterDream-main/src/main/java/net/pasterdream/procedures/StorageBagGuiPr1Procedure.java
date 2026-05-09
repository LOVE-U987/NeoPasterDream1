package net.pasterdream.procedures;

import net.pasterdream.init.PasterdreamModItems;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;

public class StorageBagGuiPr1Procedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		if (!((entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == PasterdreamModItems.STORAGE_BAG.get())) {
			if (entity instanceof Player _player)
				_player.closeContainer();
		}
	}
}
