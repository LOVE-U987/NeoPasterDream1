package net.pasterdream.block.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.block.display.WindKnightSpawnblock3DisplayItem;

import net.minecraft.resources.ResourceLocation;

public class WindKnightSpawnblock3DisplayModel extends GeoModel<WindKnightSpawnblock3DisplayItem> {
	@Override
	public ResourceLocation getAnimationResource(WindKnightSpawnblock3DisplayItem animatable) {
		return new ResourceLocation("pasterdream", "animations/wind_knight_spawnblock_3.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(WindKnightSpawnblock3DisplayItem animatable) {
		return new ResourceLocation("pasterdream", "geo/wind_knight_spawnblock_3.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(WindKnightSpawnblock3DisplayItem entity) {
		return new ResourceLocation("pasterdream", "textures/block/wind_knight_spawnblock.png");
	}
}
