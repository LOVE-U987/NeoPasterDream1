package net.pasterdream.procedures;

import net.pasterdream.init.PasterdreamModParticleTypes;
import net.pasterdream.PasterdreamMod;

import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.BlockPos;

public class IceSpellPr0Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		if (world instanceof Level _level) {
			if (!_level.isClientSide()) {
				_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.splash_potion.break")), SoundSource.NEUTRAL, (float) 1.2, 1);
			} 
		}
		if (world instanceof Level _level) {
			if (!_level.isClientSide()) {
				_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("pasterdream:ice_spell")), SoundSource.NEUTRAL, 1, 1);
			} 
		}
		if (world instanceof ServerLevel _level)
			_level.sendParticles(ParticleTypes.SNOWFLAKE, (x + 0.5), (y + 1.5), (z + 0.5), 240, 2.5, 1.5, 2.5, 0.05);
		if (world instanceof ServerLevel _level)
			_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.SNOWFLAKE_0_PARTICLE.get()), (x + 0.5), (y + 2), (z + 0.5), 128, 2.5, 1.5, 2.5, 0.1);
		if (world instanceof ServerLevel _level)
			_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.SNOWFLAKE_1_PARTICLE.get()), (x + 0.5), (y + 2), (z + 0.5), 128, 2.5, 1.5, 2.5, 0.1);
		IceSpellPr1Procedure.execute(world, x, y, z);
		PasterdreamMod.queueServerWork(5, () -> {
			IceSpellPr1Procedure.execute(world, x, y, z);
		});
		PasterdreamMod.queueServerWork(10, () -> {
			IceSpellPr1Procedure.execute(world, x, y, z);
		});
		PasterdreamMod.queueServerWork(15, () -> {
			IceSpellPr1Procedure.execute(world, x, y, z);
		});
		PasterdreamMod.queueServerWork(20, () -> {
			IceSpellPr1Procedure.execute(world, x, y, z);
		});
	}
}
