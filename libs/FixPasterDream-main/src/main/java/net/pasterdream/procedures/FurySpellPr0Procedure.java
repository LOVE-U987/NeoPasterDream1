package net.pasterdream.procedures;

import net.pasterdream.init.PasterdreamModEntities;
import net.pasterdream.PasterdreamMod;

import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Entity;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;

public class FurySpellPr0Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		if (world instanceof Level _level) {
			if (!_level.isClientSide()) {
				_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.splash_potion.break")), SoundSource.NEUTRAL, (float) 1.2, 1);
			} 
		}
		if (world instanceof Level _level) {
			if (!_level.isClientSide()) {
				_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("pasterdream:fury_spell_0")), SoundSource.NEUTRAL, 1, 1);
			} 
		}
		if (world instanceof ServerLevel _level) {
			Entity entityToSpawn = PasterdreamModEntities.FURY_SPELL_ENTITY.get().spawn(_level, BlockPos.containing(x, y + 1, z), MobSpawnType.MOB_SUMMONED);
			if (entityToSpawn != null) {
				entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
			}
		}
		FurySpellPr1Procedure.execute(world, x, y, z);
		PasterdreamMod.queueServerWork(10, () -> {
			FurySpellPr1Procedure.execute(world, x, y, z);
		});
		PasterdreamMod.queueServerWork(20, () -> {
			FurySpellPr1Procedure.execute(world, x, y, z);
		});
		PasterdreamMod.queueServerWork(30, () -> {
			FurySpellPr1Procedure.execute(world, x, y, z);
		});
		PasterdreamMod.queueServerWork(40, () -> {
			FurySpellPr1Procedure.execute(world, x, y, z);
		});
		PasterdreamMod.queueServerWork(50, () -> {
			FurySpellPr1Procedure.execute(world, x, y, z);
		});
		PasterdreamMod.queueServerWork(60, () -> {
			FurySpellPr1Procedure.execute(world, x, y, z);
		});
	}
}
