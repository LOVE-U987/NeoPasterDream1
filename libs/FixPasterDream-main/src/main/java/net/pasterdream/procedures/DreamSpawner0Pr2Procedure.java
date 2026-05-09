package net.pasterdream.procedures;

import net.pasterdream.block.entity.DreamSpawner0BlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;

public class DreamSpawner0Pr2Procedure {
	public static void execute(Level world, int x, int y, int z, Player player) {
		ItemStack item = player.getMainHandItem();
		if (world.getBlockEntity(new BlockPos(x, y, z)) instanceof DreamSpawner0BlockEntity entity && item.getItem() instanceof SpawnEggItem) {
			entity.setItem(0, item);
		}
	}
}
