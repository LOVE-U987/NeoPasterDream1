package net.pasterdream.procedures;

import net.pasterdream.init.PasterdreamModBlocks;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.BlockPos;

public class ShadowDungeonDoorPr0Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		if ((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == PasterdreamModBlocks.SHADOW_DUNGEON_DOOR_0.get()) {
			world.setBlock(BlockPos.containing(x + 1, y, z + 1), PasterdreamModBlocks.SHADOW_DUNGEON_DOOR_1.get().defaultBlockState(), 3);
			world.setBlock(BlockPos.containing(x + 1, y, z - 1), PasterdreamModBlocks.SHADOW_DUNGEON_DOOR_1.get().defaultBlockState(), 3);
			world.setBlock(BlockPos.containing(x - 1, y, z - 1), PasterdreamModBlocks.SHADOW_DUNGEON_DOOR_1.get().defaultBlockState(), 3);
			world.setBlock(BlockPos.containing(x - 1, y, z + 1), PasterdreamModBlocks.SHADOW_DUNGEON_DOOR_1.get().defaultBlockState(), 3);
			world.setBlock(BlockPos.containing(x + 1, y, z), PasterdreamModBlocks.SHADOW_DUNGEON_DOOR_1.get().defaultBlockState(), 3);
			world.setBlock(BlockPos.containing(x - 1, y, z), PasterdreamModBlocks.SHADOW_DUNGEON_DOOR_1.get().defaultBlockState(), 3);
			world.setBlock(BlockPos.containing(x, y, z + 1), PasterdreamModBlocks.SHADOW_DUNGEON_DOOR_1.get().defaultBlockState(), 3);
			world.setBlock(BlockPos.containing(x, y, z - 1), PasterdreamModBlocks.SHADOW_DUNGEON_DOOR_1.get().defaultBlockState(), 3);
		} else {
			world.setBlock(BlockPos.containing(x + 1, y + 1, z), PasterdreamModBlocks.SHADOWDUNGEONDOOR_3.get().defaultBlockState(), 3);
			world.setBlock(BlockPos.containing(x + 1, y - 1, z), PasterdreamModBlocks.SHADOWDUNGEONDOOR_3.get().defaultBlockState(), 3);
			world.setBlock(BlockPos.containing(x - 1, y - 1, z), PasterdreamModBlocks.SHADOWDUNGEONDOOR_3.get().defaultBlockState(), 3);
			world.setBlock(BlockPos.containing(x - 1, y + 1, z), PasterdreamModBlocks.SHADOWDUNGEONDOOR_3.get().defaultBlockState(), 3);
			world.setBlock(BlockPos.containing(x + 1, y, z), PasterdreamModBlocks.SHADOWDUNGEONDOOR_3.get().defaultBlockState(), 3);
			world.setBlock(BlockPos.containing(x - 1, y, z), PasterdreamModBlocks.SHADOWDUNGEONDOOR_3.get().defaultBlockState(), 3);
			world.setBlock(BlockPos.containing(x, y + 1, z), PasterdreamModBlocks.SHADOWDUNGEONDOOR_3.get().defaultBlockState(), 3);
			world.setBlock(BlockPos.containing(x, y - 1, z), PasterdreamModBlocks.SHADOWDUNGEONDOOR_3.get().defaultBlockState(), 3);
		}
	}
}
