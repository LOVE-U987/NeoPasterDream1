package net.pasterdream.block.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.block.display.WindKnightSpawnblock1DisplayItem;

import net.minecraft.resources.ResourceLocation;

public class WindKnightSpawnblock1DisplayModel extends GeoModel<WindKnightSpawnblock1DisplayItem> {
	@Override
	public ResourceLocation getAnimationResource(WindKnightSpawnblock1DisplayItem animatable) {
		return new ResourceLocation("pasterdream", "animations/wind_knight_spawnblock_1.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(WindKnightSpawnblock1DisplayItem animatable) {
		return new ResourceLocation("pasterdream", "geo/wind_knight_spawnblock_1.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(WindKnightSpawnblock1DisplayItem entity) {
		return new ResourceLocation("pasterdream", "textures/block/wind_knight_spawnblock.png");
	}
}
