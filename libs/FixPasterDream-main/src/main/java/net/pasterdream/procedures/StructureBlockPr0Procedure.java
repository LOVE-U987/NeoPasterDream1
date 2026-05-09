package net.pasterdream.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.BlockPos;

public class StructureBlockPr0Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		world.scheduleTick(BlockPos.containing(x, y, z), world.getBlockState(BlockPos.containing(x, y, z)).getBlock(), 10);
	}
}
