package net.pasterdream.procedures;

import net.pasterdream.init.PasterdreamModParticleTypes;
import net.pasterdream.init.PasterdreamModBlocks;
import net.pasterdream.PasterdreamMod;

import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.BlockPos;

import java.util.Map;

public class GardenDecryptionPr0Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		if ((world instanceof Level _lvl ? _lvl.dimension() : Level.OVERWORLD) == (ResourceKey.create(Registries.DIMENSION, new ResourceLocation("pasterdream:dyedream_world")))
				&& (world.getBlockState(BlockPos.containing(x, y - 3, z))).getBlock() == PasterdreamModBlocks.DYEDREAM_DESK.get()) {
			if ((world.getBlockState(BlockPos.containing(x - 2, y, z - 2))).getBlock() == PasterdreamModBlocks.FLOWER_13.get() && (world.getBlockState(BlockPos.containing(x + 2, y, z - 2))).getBlock() == PasterdreamModBlocks.CROP_3A.get()
					&& (world.getBlockState(BlockPos.containing(x + 2, y, z + 2))).getBlock() == PasterdreamModBlocks.FLOWER_8.get() && (world.getBlockState(BlockPos.containing(x - 2, y, z + 2))).getBlock() == PasterdreamModBlocks.CROP_2A.get()
					&& (world.getBlockState(BlockPos.containing(x + 2, y, z + 0))).getBlock() == PasterdreamModBlocks.GRASS_3.get() && (world.getBlockState(BlockPos.containing(x - 2, y, z + 0))).getBlock() == PasterdreamModBlocks.GRASS_3.get()
					&& (world.getBlockState(BlockPos.containing(x - 2, y, z + 0))).getBlock() == PasterdreamModBlocks.GRASS_3.get() && (world.getBlockState(BlockPos.containing(x + 0, y, z + 2))).getBlock() == PasterdreamModBlocks.GRASS_3.get()
					&& (world.getBlockState(BlockPos.containing(x + 0, y, z - 2))).getBlock() == PasterdreamModBlocks.GRASS_3.get()) {
				PasterdreamMod.queueServerWork(1, () -> {
					world.destroyBlock(BlockPos.containing(x + 2, y, z + 2), false);
					world.destroyBlock(BlockPos.containing(x + 2, y, z - 2), false);
					world.destroyBlock(BlockPos.containing(x - 2, y, z - 2), false);
					world.destroyBlock(BlockPos.containing(x - 2, y, z + 2), false);
					world.destroyBlock(BlockPos.containing(x + 2, y, z + 0), false);
					world.destroyBlock(BlockPos.containing(x - 2, y, z + 0), false);
					world.destroyBlock(BlockPos.containing(x + 0, y, z + 2), false);
					world.destroyBlock(BlockPos.containing(x + 0, y, z - 2), false);
				});
				if (world instanceof ServerLevel _level)
					_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.CRACK_0_PARTICLE.get()), (x + 0.5), (y + 1), (z + 0.5), 32, 0.5, 1, 0.5, 0.05);
				if (world instanceof ServerLevel _level)
					_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.DUST_0_PARTICLE.get()), (x + 0.5), (y + 1), (z + 0.5), 128, 2, 2, 2, 0.1);
				if (world instanceof Level _level) {
					if (!_level.isClientSide()) {
						_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("pasterdream:dream0")), SoundSource.BLOCKS, 1, 1);
					} else {
						_level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("pasterdream:dream0")), SoundSource.BLOCKS, 1, 1, false);
					}
				}
				PasterdreamMod.queueServerWork(2, () -> {
					{
						BlockPos _bp = BlockPos.containing(x, y, z);
						BlockState _bs = PasterdreamModBlocks.FLOWER_12.get().defaultBlockState();
						BlockState _bso = world.getBlockState(_bp);
						for (Map.Entry<Property<?>, Comparable<?>> entry : _bso.getValues().entrySet()) {
							Property _property = _bs.getBlock().getStateDefinition().getProperty(entry.getKey().getName());
							if (_property != null && _bs.getValue(_property) != null)
								try {
									_bs = _bs.setValue(_property, (Comparable) entry.getValue());
								} catch (Exception e) {
								}
						}
						world.setBlock(_bp, _bs, 3);
					}
					world.destroyBlock(BlockPos.containing(x, y - 3, z), false);
				});
			}
		}
	}
}
