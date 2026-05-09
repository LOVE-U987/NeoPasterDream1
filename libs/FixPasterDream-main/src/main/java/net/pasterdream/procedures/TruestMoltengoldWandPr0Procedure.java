package net.pasterdream.procedures;

import net.pasterdream.init.PasterdreamModBlocks;

import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.sounds.SoundSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;

public class TruestMoltengoldWandPr0Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		if (world instanceof Level _level) {
			if (!_level.isClientSide()) {
				_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.dragon_fireball.explode")), SoundSource.NEUTRAL, (float) 0.5, 1);
			} else {
				_level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.dragon_fireball.explode")), SoundSource.NEUTRAL, (float) 0.5, 1, false);
			}
		}
		if ((world.getBlockState(BlockPos.containing(x, y + 1, z))).getBlock() == Blocks.AIR && !((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == Blocks.AIR)) {
			world.setBlock(BlockPos.containing(x, y + 1, z), Blocks.FIRE.defaultBlockState(), 3);
		}
		if ((world.getBlockState(BlockPos.containing(x, y + 2, z))).getBlock() == Blocks.AIR) {
			world.setBlock(BlockPos.containing(x, y + 2, z), PasterdreamModBlocks.STARCALL_BLOCK.get().defaultBlockState(), 3);
		}
		if (world.canSeeSkyFromBelowWater(BlockPos.containing(x, y + 2, z)) && Math.random() <= 0.5) {
			world.setBlock(BlockPos.containing(x, y + 11, z), PasterdreamModBlocks.STARCALL_CRACK.get().defaultBlockState(), 3);
			if (world instanceof Level _level) {
				if (!_level.isClientSide()) {
					_level.playSound(null, BlockPos.containing(x, y + 1, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("pasterdream:starcall_crack")), SoundSource.NEUTRAL, (float) 1.5, 1);
				} else {
					_level.playLocalSound(x, (y + 1), z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("pasterdream:starcall_crack")), SoundSource.NEUTRAL, (float) 1.5, 1, false);
				}
			}
		}
	}
}
