package net.pasterdream.procedures;

import net.pasterdream.init.PasterdreamModBlocks;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;

public class ClayPot0Pr0Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		if ((world.getFluidState(BlockPos.containing(x, y, z)).createLegacyBlock()) == PasterdreamModBlocks.SHADOW_LIQUID.get().defaultBlockState()) {
			if (world instanceof Level _level && !_level.isClientSide())
				_level.explode(null, x, y, z, 4, Level.ExplosionInteraction.NONE);
		}
	}
}
