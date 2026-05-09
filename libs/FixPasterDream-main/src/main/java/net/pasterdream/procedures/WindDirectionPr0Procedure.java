package net.pasterdream.procedures;

import net.pasterdream.init.PasterdreamModParticleTypes;
import net.pasterdream.init.PasterdreamModGameRules;
import net.pasterdream.PasterdreamMod;

import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Mth;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.network.chat.Component;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.BlockPos;

import java.util.ArrayList;

public class WindDirectionPr0Procedure {
	public static void execute(LevelAccessor world) {
		if ((world instanceof Level _lvl ? _lvl.dimension() : Level.OVERWORLD) == (ResourceKey.create(Registries.DIMENSION, new ResourceLocation("pasterdream:wind_journey_world")))) {
			if (world.dayTime() % 24000 == 0L) {
				world.getLevelData().getGameRules().getRule(PasterdreamModGameRules.PASTERDREAM_WIND_DIRECTION).set(Mth.nextInt(RandomSource.create(), 0, 7), world.getServer());
				PasterdreamMod.queueServerWork(2, () -> {
					if ((world.getLevelData().getGameRules().getInt(PasterdreamModGameRules.PASTERDREAM_WIND_DIRECTION)) == 0) {
						for (Entity entityiterator : new ArrayList<>(world.players())) {
							if (entityiterator instanceof Player _player && !_player.level().isClientSide())
								_player.displayClientMessage(Component.literal("\u00A77\u00A7o\u671D\u9633\u5347\u8D77... \u547C\u5578\u7684\u98CE\u6B63\u5439\u5F80 \u00A7a\u5317\u65B9"), false);
						}
					}
					if ((world.getLevelData().getGameRules().getInt(PasterdreamModGameRules.PASTERDREAM_WIND_DIRECTION)) == 1) {
						for (Entity entityiterator : new ArrayList<>(world.players())) {
							if (entityiterator instanceof Player _player && !_player.level().isClientSide())
								_player.displayClientMessage(Component.literal("\u00A77\u00A7o\u671D\u9633\u5347\u8D77... \u547C\u5578\u7684\u98CE\u6B63\u5439\u5F80 \u00A7a\u4E1C\u5317\u65B9"), false);
						}
					}
					if ((world.getLevelData().getGameRules().getInt(PasterdreamModGameRules.PASTERDREAM_WIND_DIRECTION)) == 2) {
						for (Entity entityiterator : new ArrayList<>(world.players())) {
							if (entityiterator instanceof Player _player && !_player.level().isClientSide())
								_player.displayClientMessage(Component.literal("\u00A77\u00A7o\u671D\u9633\u5347\u8D77... \u547C\u5578\u7684\u98CE\u6B63\u5439\u5F80 \u00A7a\u4E1C\u65B9"), false);
						}
					}
					if ((world.getLevelData().getGameRules().getInt(PasterdreamModGameRules.PASTERDREAM_WIND_DIRECTION)) == 3) {
						for (Entity entityiterator : new ArrayList<>(world.players())) {
							if (entityiterator instanceof Player _player && !_player.level().isClientSide())
								_player.displayClientMessage(Component.literal("\u00A77\u00A7o\u671D\u9633\u5347\u8D77... \u547C\u5578\u7684\u98CE\u6B63\u5439\u5F80 \u00A7a\u4E1C\u5357\u65B9"), false);
						}
					}
					if ((world.getLevelData().getGameRules().getInt(PasterdreamModGameRules.PASTERDREAM_WIND_DIRECTION)) == 4) {
						for (Entity entityiterator : new ArrayList<>(world.players())) {
							if (entityiterator instanceof Player _player && !_player.level().isClientSide())
								_player.displayClientMessage(Component.literal("\u00A77\u00A7o\u671D\u9633\u5347\u8D77... \u547C\u5578\u7684\u98CE\u6B63\u5439\u5F80 \u00A7a\u5357\u65B9"), false);
						}
					}
					if ((world.getLevelData().getGameRules().getInt(PasterdreamModGameRules.PASTERDREAM_WIND_DIRECTION)) == 5) {
						for (Entity entityiterator : new ArrayList<>(world.players())) {
							if (entityiterator instanceof Player _player && !_player.level().isClientSide())
								_player.displayClientMessage(Component.literal("\u00A77\u00A7o\u671D\u9633\u5347\u8D77... \u547C\u5578\u7684\u98CE\u6B63\u5439\u5F80 \u00A7a\u897F\u5357\u65B9"), false);
						}
					}
					if ((world.getLevelData().getGameRules().getInt(PasterdreamModGameRules.PASTERDREAM_WIND_DIRECTION)) == 6) {
						for (Entity entityiterator : new ArrayList<>(world.players())) {
							if (entityiterator instanceof Player _player && !_player.level().isClientSide())
								_player.displayClientMessage(Component.literal("\u00A77\u00A7o\u671D\u9633\u5347\u8D77... \u547C\u5578\u7684\u98CE\u6B63\u5439\u5F80 \u00A7a\u897F\u65B9"), false);
						}
					}
					if ((world.getLevelData().getGameRules().getInt(PasterdreamModGameRules.PASTERDREAM_WIND_DIRECTION)) == 7) {
						for (Entity entityiterator : new ArrayList<>(world.players())) {
							if (entityiterator instanceof Player _player && !_player.level().isClientSide())
								_player.displayClientMessage(Component.literal("\u00A77\u00A7o\u671D\u9633\u5347\u8D77... \u547C\u5578\u7684\u98CE\u6B63\u5439\u5F80 \u00A7a\u897F\u5317\u65B9"), false);
						}
					}
				});
				for (Entity entityiterator : new ArrayList<>(world.players())) {
					if (world instanceof Level _level) {
						if (!_level.isClientSide()) {
							_level.playSound(null, BlockPos.containing(entityiterator.getX(), entityiterator.getY(), entityiterator.getZ()), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("pasterdream:wind_chime")), SoundSource.WEATHER,
									1, 1);
						} else {
							_level.playLocalSound((entityiterator.getX()), (entityiterator.getY()), (entityiterator.getZ()), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("pasterdream:wind_chime")), SoundSource.WEATHER, 1, 1, false);
						}
					}
					if (world instanceof ServerLevel _level)
						_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.FEATHER_WHITE_PARTICLE.get()), (entityiterator.getX()), (entityiterator.getY() + 2), (entityiterator.getZ()), 48, 3, 3, 3, 0.05);
				}
				PasterdreamMod.queueServerWork(79, () -> {
					for (Entity entityiterator : new ArrayList<>(world.players())) {
						if (world instanceof Level _level) {
							if (!_level.isClientSide()) {
								_level.playSound(null, BlockPos.containing(entityiterator.getX(), entityiterator.getY(), entityiterator.getZ()), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("pasterdream:breeze_wind")),
										SoundSource.WEATHER, 1, 1);
							} else {
								_level.playLocalSound((entityiterator.getX()), (entityiterator.getY()), (entityiterator.getZ()), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("pasterdream:breeze_wind")), SoundSource.WEATHER, 1, 1,
										false);
							}
						}
					}
				});
			}
			if (world.dayTime() == 1) {
				for (Entity entityiterator : new ArrayList<>(world.players())) {
					if (world instanceof ServerLevel _level)
						_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.FEATHER_WHITE_PARTICLE.get()), (entityiterator.getX()), (entityiterator.getY() + 2), (entityiterator.getZ()), 48, 3, 3, 3, 0.05);
				}
			}
			if (world.dayTime() == 5) {
				for (Entity entityiterator : new ArrayList<>(world.players())) {
					if (world instanceof ServerLevel _level)
						_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.FEATHER_WHITE_PARTICLE.get()), (entityiterator.getX()), (entityiterator.getY() + 2), (entityiterator.getZ()), 48, 3, 3, 3, 0.05);
				}
			}
		}
	}
}
