package net.pasterdream.procedures;

import net.pasterdream.init.PasterdreamModBlocks;

import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.sounds.SoundSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;

public class ShadowDungeonDoorPr1Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		if (world instanceof Level _level) {
			if (!_level.isClientSide()) {
				_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("pasterdream:shadow_door")), SoundSource.BLOCKS, 1, 1);
			} else {
				_level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("pasterdream:shadow_door")), SoundSource.BLOCKS, 1, 1, false);
			}
		}
		if ((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == PasterdreamModBlocks.SHADOW_DUNGEON_DOOR_0.get()) {
			world.destroyBlock(BlockPos.containing(x, y, z), false);
			world.destroyBlock(BlockPos.containing(x + 1, y, z + 1), false);
			world.destroyBlock(BlockPos.containing(x - 1, y, z + 1), false);
			world.destroyBlock(BlockPos.containing(x - 1, y, z - 1), false);
			world.destroyBlock(BlockPos.containing(x + 1, y, z - 1), false);
			world.destroyBlock(BlockPos.containing(x + 1, y, z), false);
			world.destroyBlock(BlockPos.containing(x - 1, y, z), false);
			world.destroyBlock(BlockPos.containing(x, y, z + 1), false);
			world.destroyBlock(BlockPos.containing(x, y, z - 1), false);
		} else {
			world.destroyBlock(BlockPos.containing(x, y, z), false);
			world.destroyBlock(BlockPos.containing(x + 1, y + 1, z), false);
			world.destroyBlock(BlockPos.containing(x - 1, y + 1, z), false);
			world.destroyBlock(BlockPos.containing(x - 1, y - 1, z), false);
			world.destroyBlock(BlockPos.containing(x + 1, y - 1, z), false);
			world.destroyBlock(BlockPos.containing(x + 1, y, z), false);
			world.destroyBlock(BlockPos.containing(x - 1, y, z), false);
			world.destroyBlock(BlockPos.containing(x, y + 1, z), false);
			world.destroyBlock(BlockPos.containing(x, y - 1, z), false);
		}
	}
}
