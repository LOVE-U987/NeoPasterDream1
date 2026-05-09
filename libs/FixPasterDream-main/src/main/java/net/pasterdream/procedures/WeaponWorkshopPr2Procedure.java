package net.pasterdream.procedures;

import com.google.common.collect.Lists;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.pasterdream.init.PasterdreamModBlocks;
import net.minecraft.util.Mth;

import java.util.List;

public class WeaponWorkshopPr2Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		var list = Lists.newArrayList(
				PasterdreamModBlocks.WORKSHOP_CAULDEON.get(),
				PasterdreamModBlocks.WORKSHOP_BLAST.get(),
				PasterdreamModBlocks.WORKSHOP_GRIND.get(),
				PasterdreamModBlocks.WORKSHOP_ANVIL.get()
		);
		for (int ox = -3; ox <= 3; ox ++) for (int oz = -3; oz <= 3; oz ++) {
			if (list.isEmpty()) break;
			var pos = new BlockPos(Mth.floor(x + ox), Mth.floor(y), Mth.floor(z + oz));
			var block = world.getBlockState(pos).getBlock();
			if (list.contains(block)) {
				world.destroyBlock(pos, false);
				list.remove(block);
				if (list.isEmpty()) break;
			}
		}
	}
}
