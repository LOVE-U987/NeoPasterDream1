package net.pasterdream.procedures;

import net.pasterdream.init.PasterdreamModBlocks;

import net.minecraftforge.items.ItemHandlerHelper;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;

public class FireflyPr0Procedure {
	public static void execute(Entity entity, Entity sourceentity, ItemStack itemstack) {
		if (entity == null || sourceentity == null)
			return;
		if (itemstack.getItem() == PasterdreamModBlocks.ECOLOGY_GLASS_JAR.get().asItem()) {
			if (sourceentity instanceof Player _player) {
				ItemStack _stktoremove = new ItemStack(PasterdreamModBlocks.ECOLOGY_GLASS_JAR.get());
				_player.getInventory().clearOrCountMatchingItems(p -> _stktoremove.getItem() == p.getItem(), 1, _player.inventoryMenu.getCraftSlots());
			}
			if (sourceentity instanceof Player _player) {
				ItemStack _setstack = new ItemStack(PasterdreamModBlocks.LIGHT_FIREFLY_GLASS_JAR.get());
				_setstack.setCount(1);
				ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
			}
			if (!entity.level().isClientSide())
				entity.discard();
		}
	}
}
