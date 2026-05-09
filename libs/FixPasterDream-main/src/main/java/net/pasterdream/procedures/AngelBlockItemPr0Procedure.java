package net.pasterdream.procedures;

import net.pasterdream.init.PasterdreamModBlocks;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;

public class AngelBlockItemPr0Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z, ItemStack itemstack) {
		if ((world.getBlockState(BlockPos.containing(x, y - 1, z))).getBlock() == Blocks.AIR) {
			world.setBlock(BlockPos.containing(x, y - 1, z), PasterdreamModBlocks.ANGEL_BLOCK.get().defaultBlockState(), 3);
			itemstack.shrink(1);
		}
	}
}
