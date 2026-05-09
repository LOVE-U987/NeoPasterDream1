package net.pasterdream.procedures;

import net.pasterdream.init.PasterdreamModItems;
import net.pasterdream.init.PasterdreamModEntities;
import net.pasterdream.init.PasterdreamModBlocks;
import net.pasterdream.PasterdreamMod;

import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.BlockPos;

import java.util.Map;

public class WindKnightSpawnblockPr0Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		if ((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == PasterdreamModBlocks.WIND_KNIGHT_SPAWNBLOCK_0.get()) {
			if ((entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == PasterdreamModItems.WINDRUNNER_CRYSTAL.get()) {
				{
					BlockPos _bp = BlockPos.containing(x, y, z);
					BlockState _bs = PasterdreamModBlocks.WIND_KNIGHT_SPAWNBLOCK_1.get().defaultBlockState();
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
				if (world instanceof Level _level) {
					if (!_level.isClientSide()) {
						_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.amethyst_block.place")), SoundSource.BLOCKS, 1, 1);
					} 
				}
				if (world instanceof ServerLevel _level)
					_level.sendParticles(ParticleTypes.SCRAPE, (x + 0.5), (y + 1), (z + 0.5), 8, 0.5, 0.5, 0.5, 0.1);
				(entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).shrink(1);
			} else {
				if (entity instanceof Player _player && !_player.level().isClientSide())
					_player.displayClientMessage(Component.literal("\u9700\u8981\u5D4C\u5165 [\u98CE\u884C\u8005\u6C34\u6676] "), true);
			}
		}
		if ((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == PasterdreamModBlocks.WIND_KNIGHT_SPAWNBLOCK_1.get()) {
			if ((entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == PasterdreamModItems.WIND_IRON_INGOT.get()) {
				(entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).shrink(1);
				if (world instanceof Level _level) {
					if (!_level.isClientSide()) {
						_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.anvil.use")), SoundSource.BLOCKS, 1, 1);
					} 
				}
				if (world instanceof ServerLevel _level)
					_level.sendParticles(ParticleTypes.SCRAPE, (x + 0.5), (y + 1.5), (z + 0.5), 12, 0.7, 0.7, 0.7, 0.1);
				PasterdreamMod.queueServerWork(1, () -> {
					{
						BlockPos _bp = BlockPos.containing(x, y, z);
						BlockState _bs = PasterdreamModBlocks.WIND_KNIGHT_SPAWNBLOCK_2.get().defaultBlockState();
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
				});
			} else {
				if (entity instanceof Player _player && !_player.level().isClientSide())
					_player.displayClientMessage(Component.literal("\u9700\u8981 [\u51DD\u98CE\u94C1\u952D] \u7EC4\u88C5\u8EAF\u5E72"), true);
			}
		}
		if ((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == PasterdreamModBlocks.WIND_KNIGHT_SPAWNBLOCK_2.get()) {
			if ((entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == PasterdreamModItems.WIND_IRON_INGOT.get()) {
				if (world instanceof Level _level) {
					if (!_level.isClientSide()) {
						_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.anvil.use")), SoundSource.BLOCKS, 1, 1);
					} 
				}
				if (world instanceof ServerLevel _level)
					_level.sendParticles(ParticleTypes.SCRAPE, (x + 0.5), (y + 1.5), (z + 0.5), 20, 0.9, 1, 0.9, 0.1);
				(entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).shrink(1);
				PasterdreamMod.queueServerWork(1, () -> {
					{
						BlockPos _bp = BlockPos.containing(x, y, z);
						BlockState _bs = PasterdreamModBlocks.WIND_KNIGHT_SPAWNBLOCK_3.get().defaultBlockState();
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
				});
			} else {
				if (entity instanceof Player _player && !_player.level().isClientSide())
					_player.displayClientMessage(Component.literal("\u9700\u8981 [\u51DD\u98CE\u94C1\u952D] \u7EC4\u88C5\u624B\u81C2"), true);
			}
		}
		if ((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == PasterdreamModBlocks.WIND_KNIGHT_SPAWNBLOCK_3.get()) {
			if ((entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == PasterdreamModItems.WIND_IRON_INGOT.get()) {
				if (world instanceof Level _level) {
					if (!_level.isClientSide()) {
						_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.anvil.use")), SoundSource.BLOCKS, 1, 1);
					} 
				}
				if (world instanceof ServerLevel _level)
					_level.sendParticles(ParticleTypes.SCRAPE, (x + 0.5), (y + 1.5), (z + 0.5), 24, 0.9, 1, 0.9, 0.1);
				(entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).shrink(1);
				PasterdreamMod.queueServerWork(1, () -> {
					{
						BlockPos _bp = BlockPos.containing(x, y, z);
						BlockState _bs = PasterdreamModBlocks.WIND_KNIGHT_SPAWNBLOCK_4.get().defaultBlockState();
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
				});
			} else {
				if (entity instanceof Player _player && !_player.level().isClientSide())
					_player.displayClientMessage(Component.literal("\u9700\u8981 [\u51DD\u98CE\u94C1\u952D] \u7EC4\u88C5\u5934\u9885"), true);
			}
		}
		if ((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == PasterdreamModBlocks.WIND_KNIGHT_SPAWNBLOCK_4.get()) {
			if ((entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == PasterdreamModItems.LIGHTNING_SPELL.get()) {
				(entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).shrink(1);
				LightningSpellPr0Procedure.execute(world, (x + 0.5), (y + 0.5), (z + 0.5));
				PasterdreamMod.queueServerWork(86, () -> {
					if ((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == PasterdreamModBlocks.WIND_KNIGHT_SPAWNBLOCK_4.get()) {
						{
							BlockPos _bp = BlockPos.containing(x, y, z);
							BlockState _bs = PasterdreamModBlocks.WIND_KNIGHT_SPAWNBLOCK_0.get().defaultBlockState();
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
						if (world instanceof ServerLevel _level) {
							Entity entityToSpawn = PasterdreamModEntities.WIND_KNIGHT.get().spawn(_level, BlockPos.containing(x + 0.5, y + 1, z + 0.5), MobSpawnType.MOB_SUMMONED);
							if (entityToSpawn != null) {
								entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
							}
						}
						if (world instanceof ServerLevel _level) {
							Entity entityToSpawn = PasterdreamModEntities.THUNDERCLOUD.get().spawn(_level, BlockPos.containing(x + 6.5, y + 8, z + 6.5), MobSpawnType.MOB_SUMMONED);
							if (entityToSpawn != null) {
								entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
							}
						}
						if (world instanceof ServerLevel _level) {
							Entity entityToSpawn = PasterdreamModEntities.THUNDERCLOUD.get().spawn(_level, BlockPos.containing(x - 6.5, y + 8, z + 6.5), MobSpawnType.MOB_SUMMONED);
							if (entityToSpawn != null) {
								entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
							}
						}
						if (world instanceof ServerLevel _level) {
							Entity entityToSpawn = PasterdreamModEntities.THUNDERCLOUD.get().spawn(_level, BlockPos.containing(x + 6.5, y + 8, z - 6.5), MobSpawnType.MOB_SUMMONED);
							if (entityToSpawn != null) {
								entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
							}
						}
						if (world instanceof ServerLevel _level) {
							Entity entityToSpawn = PasterdreamModEntities.THUNDERCLOUD.get().spawn(_level, BlockPos.containing(x - 6.5, y + 8, z - 6.5), MobSpawnType.MOB_SUMMONED);
							if (entityToSpawn != null) {
								entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
							}
						}
						if (world instanceof Level _level) {
							if (!_level.isClientSide()) {
								_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("pasterdream:shadow_door")), SoundSource.MASTER, 1, 1);
							} 
						}
					}
				});
			} else {
				if (entity instanceof Player _player && !_player.level().isClientSide())
					_player.displayClientMessage(Component.literal("\u76DB\u6709\u95EA\u7535\u7684\u9B54\u836F\u624D\u80FD\u5524\u9192\u5080\u5121"), true);
			}
		}
	}
}
