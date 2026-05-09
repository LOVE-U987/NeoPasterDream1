package net.pasterdream.block.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.block.display.WindKnightSpawnblock4DisplayItem;

import net.minecraft.resources.ResourceLocation;

public class WindKnightSpawnblock4DisplayModel extends GeoModel<WindKnightSpawnblock4DisplayItem> {
	@Override
	public ResourceLocation getAnimationResource(WindKnightSpawnblock4DisplayItem animatable) {
		return new ResourceLocation("pasterdream", "animations/wind_knight_spawnblock_4.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(WindKnightSpawnblock4DisplayItem animatable) {
		return new ResourceLocation("pasterdream", "geo/wind_knight_spawnblock_4.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(WindKnightSpawnblock4DisplayItem entity) {
		return new ResourceLocation("pasterdream", "textures/block/wind_knight_spawnblock.png");
	}
}
