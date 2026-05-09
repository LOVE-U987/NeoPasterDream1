package net.pasterdream.block.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.block.display.WindKnightSpawnblock0DisplayItem;

import net.minecraft.resources.ResourceLocation;

public class WindKnightSpawnblock0DisplayModel extends GeoModel<WindKnightSpawnblock0DisplayItem> {
	@Override
	public ResourceLocation getAnimationResource(WindKnightSpawnblock0DisplayItem animatable) {
		return new ResourceLocation("pasterdream", "animations/wind_knight_spawnblock_0.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(WindKnightSpawnblock0DisplayItem animatable) {
		return new ResourceLocation("pasterdream", "geo/wind_knight_spawnblock_0.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(WindKnightSpawnblock0DisplayItem entity) {
		return new ResourceLocation("pasterdream", "textures/block/wind_knight_spawnblock.png");
	}
}
