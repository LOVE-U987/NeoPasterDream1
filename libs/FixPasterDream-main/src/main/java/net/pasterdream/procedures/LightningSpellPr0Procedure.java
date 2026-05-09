package net.pasterdream.procedures;

import net.pasterdream.PasterdreamMod;

import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.EntityType;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Mth;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.BlockPos;

public class LightningSpellPr0Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		if (world instanceof Level _level) {
			if (!_level.isClientSide()) {
				_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.splash_potion.break")), SoundSource.NEUTRAL, 1, 1);
			} 
		}
		if (world instanceof ServerLevel _level)
			_level.sendParticles(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, x, (y + 1), z, 64, 2, 1, 2, 0.01);
		if (world instanceof ServerLevel _level)
			_level.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, x, (y + 1), z, 48, 2, 1, 2, 0.01);
		if (world instanceof ServerLevel _level)
			_level.sendParticles(ParticleTypes.ELECTRIC_SPARK, x, (y + 1), z, 32, 2, 1, 2, 0.02);
		PasterdreamMod.queueServerWork(2, () -> {
			if (world instanceof Level _level) {
				if (!_level.isClientSide()) {
					_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("pasterdream:lightning_spell")), SoundSource.NEUTRAL, 1, 1);
				} 
			}
			if (world instanceof ServerLevel _level)
				_level.sendParticles(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, x, (y + 1), z, 32, 2, 1, 2, 0.02);
			if (world instanceof ServerLevel _level)
				_level.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, x, (y + 1), z, 32, 2, 1, 2, 0.02);
			if (world instanceof ServerLevel _level)
				_level.sendParticles(ParticleTypes.ELECTRIC_SPARK, x, (y + 1), z, 32, 2, 1, 2, 0.02);
		});
		PasterdreamMod.queueServerWork(7, () -> {
			if (world instanceof ServerLevel _level)
				_level.sendParticles(ParticleTypes.ELECTRIC_SPARK, x, (y + 1), z, 32, 2, 1, 2, 0.02);
		});
		PasterdreamMod.queueServerWork(12, () -> {
			if (world instanceof ServerLevel _level)
				_level.sendParticles(ParticleTypes.ELECTRIC_SPARK, x, (y + 1), z, 32, 2, 1, 2, 0.02);
		});
		PasterdreamMod.queueServerWork(17, () -> {
			if (world instanceof ServerLevel _level)
				_level.sendParticles(ParticleTypes.ELECTRIC_SPARK, x, (y + 1), z, 32, 2, 1, 2, 0.02);
		});
		PasterdreamMod.queueServerWork(22, () -> {
			if (world instanceof ServerLevel _level)
				_level.sendParticles(ParticleTypes.ELECTRIC_SPARK, x, (y + 1), z, 32, 2, 1, 2, 0.02);
		});
		PasterdreamMod.queueServerWork(27, () -> {
			if (world instanceof ServerLevel _level)
				_level.sendParticles(ParticleTypes.ELECTRIC_SPARK, x, (y + 1), z, 32, 2, 1, 2, 0.02);
		});
		PasterdreamMod.queueServerWork(32, () -> {
			if (world instanceof ServerLevel _level)
				_level.sendParticles(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, x, y, z, 32, 2, 1, 2, 0.02);
			if (world instanceof ServerLevel _level)
				_level.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, x, y, z, 32, 2, 1, 2, 0.02);
			if (world instanceof ServerLevel _level)
				_level.sendParticles(ParticleTypes.ELECTRIC_SPARK, x, (y + 1), z, 32, 2, 1, 2, 0.02);
		});
		PasterdreamMod.queueServerWork(37, () -> {
			if (world instanceof ServerLevel _level)
				_level.sendParticles(ParticleTypes.ELECTRIC_SPARK, x, (y + 1), z, 32, 2, 1, 2, 0.02);
		});
		PasterdreamMod.queueServerWork(42, () -> {
			if (world instanceof ServerLevel _level)
				_level.sendParticles(ParticleTypes.ELECTRIC_SPARK, x, (y + 1), z, 32, 2, 1, 2, 0.02);
		});
		PasterdreamMod.queueServerWork(47, () -> {
			if (world instanceof ServerLevel _level)
				_level.sendParticles(ParticleTypes.ELECTRIC_SPARK, x, (y + 1), z, 32, 2, 1, 2, 0.02);
		});
		PasterdreamMod.queueServerWork(55, () -> {
			if (world instanceof ServerLevel _level)
				_level.sendParticles(ParticleTypes.ELECTRIC_SPARK, x, (y + 1), z, 128, 2, 1, 2, 0.5);
			if (world instanceof ServerLevel _level) {
				LightningBolt entityToSpawn = EntityType.LIGHTNING_BOLT.create(_level);
				entityToSpawn.moveTo(Vec3.atBottomCenterOf(BlockPos.containing(x + Mth.nextDouble(RandomSource.create(), -2, 2), y, z + Mth.nextDouble(RandomSource.create(), -2, 2))));;
				_level.addFreshEntity(entityToSpawn);
			}
		});
		PasterdreamMod.queueServerWork(65, () -> {
			if (world instanceof ServerLevel _level)
				_level.sendParticles(ParticleTypes.ELECTRIC_SPARK, x, (y + 1), z, 128, 2, 1, 2, 0.5);
			if (world instanceof ServerLevel _level) {
				LightningBolt entityToSpawn = EntityType.LIGHTNING_BOLT.create(_level);
				entityToSpawn.moveTo(Vec3.atBottomCenterOf(BlockPos.containing(x + Mth.nextDouble(RandomSource.create(), -2, 2), y, z + Mth.nextDouble(RandomSource.create(), -2, 2))));;
				_level.addFreshEntity(entityToSpawn);
			}
		});
		PasterdreamMod.queueServerWork(75, () -> {
			if (world instanceof ServerLevel _level)
				_level.sendParticles(ParticleTypes.ELECTRIC_SPARK, x, (y + 1), z, 128, 2, 1, 2, 0.5);
			if (world instanceof ServerLevel _level) {
				LightningBolt entityToSpawn = EntityType.LIGHTNING_BOLT.create(_level);
				entityToSpawn.moveTo(Vec3.atBottomCenterOf(BlockPos.containing(x + Mth.nextDouble(RandomSource.create(), -2, 2), y, z + Mth.nextDouble(RandomSource.create(), -2, 2))));;
				_level.addFreshEntity(entityToSpawn);
			}
		});
		PasterdreamMod.queueServerWork(85, () -> {
			if (world instanceof ServerLevel _level)
				_level.sendParticles(ParticleTypes.ELECTRIC_SPARK, x, (y + 1), z, 128, 2, 1, 2, 0.5);
			if (world instanceof ServerLevel _level) {
				LightningBolt entityToSpawn = EntityType.LIGHTNING_BOLT.create(_level);
				entityToSpawn.moveTo(Vec3.atBottomCenterOf(BlockPos.containing(x + Mth.nextDouble(RandomSource.create(), -2, 2), y, z + Mth.nextDouble(RandomSource.create(), -2, 2))));;
				_level.addFreshEntity(entityToSpawn);
			}
		});
	}
}
