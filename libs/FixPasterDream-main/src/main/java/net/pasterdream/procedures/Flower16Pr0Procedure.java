package net.pasterdream.procedures;

import net.pasterdream.init.PasterdreamModBlocks;

import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.Entity;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.BlockPos;

import java.util.Map;
import java.util.Comparator;

public class Flower16Pr0Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		if ((world.getBlockState(BlockPos.containing(x, y - 3, z))).getBlock() == PasterdreamModBlocks.DYEDREAM_DESK.get()) {
			if ((world.getBlockState(BlockPos.containing(x + 0, y, z + 5))).getBlock() == PasterdreamModBlocks.CROP_0A.get() && (world.getBlockState(BlockPos.containing(x + 0, y, z - 5))).getBlock() == PasterdreamModBlocks.DYEDREAM_SAPLING.get()
					&& (world.getBlockState(BlockPos.containing(x + 5, y, z + 0))).getBlock() == PasterdreamModBlocks.FLOWER_14.get() && (world.getBlockState(BlockPos.containing(x - 5, y, z + 0))).getBlock() == PasterdreamModBlocks.FLOWER_9.get()
					&& !world.getEntitiesOfClass(SnowGolem.class, AABB.ofSize(new Vec3(x, y, z), 9, 9, 9), e -> true).isEmpty() && !world.getEntitiesOfClass(Allay.class, AABB.ofSize(new Vec3(x, y, z), 9, 9, 9), e -> true).isEmpty()) {
				if (!((Entity) world.getEntitiesOfClass(Allay.class, AABB.ofSize(new Vec3(x, y, z), 15, 15, 15), e -> true).stream().sorted(new Object() {
					Comparator<Entity> compareDistOf(double _x, double _y, double _z) {
						return Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_x, _y, _z));
					}
				}.compareDistOf(x, y, z)).findFirst().orElse(null)).level().isClientSide())
					((Entity) world.getEntitiesOfClass(Allay.class, AABB.ofSize(new Vec3(x, y, z), 15, 15, 15), e -> true).stream().sorted(new Object() {
						Comparator<Entity> compareDistOf(double _x, double _y, double _z) {
							return Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_x, _y, _z));
						}
					}.compareDistOf(x, y, z)).findFirst().orElse(null)).discard();
				if (!((Entity) world.getEntitiesOfClass(SnowGolem.class, AABB.ofSize(new Vec3(x, y, z), 15, 15, 15), e -> true).stream().sorted(new Object() {
					Comparator<Entity> compareDistOf(double _x, double _y, double _z) {
						return Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_x, _y, _z));
					}
				}.compareDistOf(x, y, z)).findFirst().orElse(null)).level().isClientSide())
					((Entity) world.getEntitiesOfClass(SnowGolem.class, AABB.ofSize(new Vec3(x, y, z), 15, 15, 15), e -> true).stream().sorted(new Object() {
						Comparator<Entity> compareDistOf(double _x, double _y, double _z) {
							return Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_x, _y, _z));
						}
					}.compareDistOf(x, y, z)).findFirst().orElse(null)).discard();
				world.destroyBlock(BlockPos.containing(x, y, z - 5), false);
				world.destroyBlock(BlockPos.containing(x, y, z + 5), false);
				world.destroyBlock(BlockPos.containing(x + 5, y, z), false);
				world.destroyBlock(BlockPos.containing(x - 5, y, z), false);
				{
					BlockPos _bp = BlockPos.containing(x, y, z);
					BlockState _bs = PasterdreamModBlocks.FLOWER_17.get().defaultBlockState();
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
				if (world instanceof ServerLevel _level)
					_level.sendParticles(ParticleTypes.SNOWFLAKE, (x + 0.5), (y + 0.5), (z + 0.5), 16, 0.5, 0.5, 0.5, 0.05);
				if (world instanceof ServerLevel _level)
					_level.sendParticles(ParticleTypes.ITEM_SNOWBALL, (x + 0.5), (y + 0.5), (z + 0.5), 16, 0.5, 0.5, 0.5, 0.05);
				if (world instanceof ServerLevel _level)
					_level.sendParticles(ParticleTypes.SNOWFLAKE, (x + 0.5), (y + 1), (z + 0.5), 64, 2, 1, 2, 0.1);
				if (world instanceof Level _level) {
					if (!_level.isClientSide()) {
						_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("pasterdream:dream0")), SoundSource.NEUTRAL, 1, 1);
					} else {
						_level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("pasterdream:dream0")), SoundSource.NEUTRAL, 1, 1, false);
					}
				}
			}
		}
	}
}
