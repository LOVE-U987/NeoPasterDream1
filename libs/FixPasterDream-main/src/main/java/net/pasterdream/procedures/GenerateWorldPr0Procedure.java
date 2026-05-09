package net.pasterdream.procedures;

import net.pasterdream.init.PasterdreamModGameRules;
import net.pasterdream.configuration.PasterdreamConfigCommonConfiguration;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;

import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Mth;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.BlockPos;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class GenerateWorldPr0Procedure {
	@SubscribeEvent
	public static void onWorldLoad(net.minecraftforge.event.level.LevelEvent.Load event) {
		execute(event, event.getLevel());
	}

	public static void execute(LevelAccessor world) {
		execute(null, world);
	}

	private static void execute(@Nullable Event event, LevelAccessor world) {
		if ((world instanceof Level _lvl ? _lvl.dimension() : Level.OVERWORLD) == Level.OVERWORLD) {
			if ((world.getLevelData().getGameRules().getInt(PasterdreamModGameRules.RANDOMCOORDZ)) == 0 && (world.getLevelData().getGameRules().getInt(PasterdreamModGameRules.RANDOMCOORDX)) == 0) {
				world.getLevelData().getGameRules().getRule(PasterdreamModGameRules.RANDOMCOORDX).set(Mth.nextInt(RandomSource.create(), -100, 100), world.getServer());
				world.getLevelData().getGameRules().getRule(PasterdreamModGameRules.RANDOMCOORDZ).set(Mth.nextInt(RandomSource.create(), -100, 100), world.getServer());
			}
			if (world instanceof ServerLevel _serverworld) {
				StructureTemplate template = _serverworld.getStructureManager().getOrCreate(new ResourceLocation("pasterdream", "shadow_world_door"));
				if (template != null) {
					template.placeInWorld(_serverworld,
							BlockPos.containing(((world.getLevelData().getGameRules().getInt(PasterdreamModGameRules.RANDOMCOORDX)) * 20 + (world.getLevelData().getGameRules().getInt(PasterdreamModGameRules.RANDOMCOORDX))) - 22, -60,
									((world.getLevelData().getGameRules().getInt(PasterdreamModGameRules.RANDOMCOORDZ)) * 20 + (world.getLevelData().getGameRules().getInt(PasterdreamModGameRules.RANDOMCOORDZ))) - 21),
							BlockPos.containing(((world.getLevelData().getGameRules().getInt(PasterdreamModGameRules.RANDOMCOORDX)) * 20 + (world.getLevelData().getGameRules().getInt(PasterdreamModGameRules.RANDOMCOORDX))) - 22, -60,
									((world.getLevelData().getGameRules().getInt(PasterdreamModGameRules.RANDOMCOORDZ)) * 20 + (world.getLevelData().getGameRules().getInt(PasterdreamModGameRules.RANDOMCOORDZ))) - 21),
							new StructurePlaceSettings().setRotation(Rotation.NONE).setMirror(Mirror.NONE).setIgnoreEntities(false), _serverworld.random, 3);
				}
			}
			if (PasterdreamConfigCommonConfiguration.THE_ORIGIN_OF_THE_WORLD_INITIALLY_GENERATED_DYEDREAM_CRACK.get()) {
				if (world.canSeeSkyFromBelowWater(new BlockPos(-9, 110, -10)) == true) {
					if (world instanceof ServerLevel _serverworld) {
						StructureTemplate template = _serverworld.getStructureManager().getOrCreate(new ResourceLocation("pasterdream", "dyedreamcrack0"));
						if (template != null) {
							template.placeInWorld(_serverworld, new BlockPos(-9, 110, -10), new BlockPos(-9, 110, -10), new StructurePlaceSettings().setRotation(Rotation.NONE).setMirror(Mirror.NONE).setIgnoreEntities(false), _serverworld.random, 3);
						}
					}
				} else {
					if (world instanceof ServerLevel _serverworld) {
						StructureTemplate template = _serverworld.getStructureManager().getOrCreate(new ResourceLocation("pasterdream", "dyedreamcrack0"));
						if (template != null) {
							template.placeInWorld(_serverworld, new BlockPos(-9, 160, -10), new BlockPos(-9, 160, -10), new StructurePlaceSettings().setRotation(Rotation.NONE).setMirror(Mirror.NONE).setIgnoreEntities(false), _serverworld.random, 3);
						}
					}
				}
			}
		}
		if ((world instanceof Level _lvl ? _lvl.dimension() : Level.OVERWORLD) == (ResourceKey.create(Registries.DIMENSION, new ResourceLocation("pasterdream:dyedream_world")))) {
			if (PasterdreamConfigCommonConfiguration.DYEDREAM_ORIGIN_SPAWNPOINT.get()) {
				if (world.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, -9, -9) <= 100) {
					if (world instanceof ServerLevel _serverworld) {
						StructureTemplate template = _serverworld.getStructureManager().getOrCreate(new ResourceLocation("pasterdream", "dyedreamcrack0"));
						if (template != null) {
							template.placeInWorld(_serverworld, new BlockPos(-9, 110, -10), new BlockPos(-9, 110, -10), new StructurePlaceSettings().setRotation(Rotation.NONE).setMirror(Mirror.NONE).setIgnoreEntities(false), _serverworld.random, 3);
						}
					}
				} else {
					if (world instanceof ServerLevel _serverworld) {
						StructureTemplate template = _serverworld.getStructureManager().getOrCreate(new ResourceLocation("pasterdream", "dyedreamcrack0"));
						if (template != null) {
							template.placeInWorld(_serverworld, new BlockPos(-9, 160, -10), new BlockPos(-9, 160, -10), new StructurePlaceSettings().setRotation(Rotation.NONE).setMirror(Mirror.NONE).setIgnoreEntities(false), _serverworld.random, 3);
						}
					}
				}
			}
		}
		if ((world instanceof Level _lvl ? _lvl.dimension() : Level.OVERWORLD) == (ResourceKey.create(Registries.DIMENSION, new ResourceLocation("pasterdream:lamp_shadow_world")))) {
			if (world.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, -9, -9) <= 100) {
				if (world instanceof ServerLevel _serverworld) {
					StructureTemplate template = _serverworld.getStructureManager().getOrCreate(new ResourceLocation("pasterdream", "shadow_world_spawn"));
					if (template != null) {
						template.placeInWorld(_serverworld, new BlockPos(-11, 100, -9), new BlockPos(-11, 100, -9), new StructurePlaceSettings().setRotation(Rotation.NONE).setMirror(Mirror.NONE).setIgnoreEntities(false), _serverworld.random, 3);
					}
				}
			} else {
				if (world instanceof ServerLevel _serverworld) {
					StructureTemplate template = _serverworld.getStructureManager().getOrCreate(new ResourceLocation("pasterdream", "shadow_world_spawn"));
					if (template != null) {
						template.placeInWorld(_serverworld, new BlockPos(-11, 150, -9), new BlockPos(-11, 150, -9), new StructurePlaceSettings().setRotation(Rotation.NONE).setMirror(Mirror.NONE).setIgnoreEntities(false), _serverworld.random, 3);
					}
				}
			}
		}
	}
}
