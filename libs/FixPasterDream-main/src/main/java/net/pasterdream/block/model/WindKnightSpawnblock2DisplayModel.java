package net.pasterdream.block.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.block.display.WindKnightSpawnblock2DisplayItem;

import net.minecraft.resources.ResourceLocation;

public class WindKnightSpawnblock2DisplayModel extends GeoModel<WindKnightSpawnblock2DisplayItem> {
	@Override
	public ResourceLocation getAnimationResource(WindKnightSpawnblock2DisplayItem animatable) {
		return new ResourceLocation("pasterdream", "animations/wind_knight_spawnblock_2.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(WindKnightSpawnblock2DisplayItem animatable) {
		return new ResourceLocation("pasterdream", "geo/wind_knight_spawnblock_2.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(WindKnightSpawnblock2DisplayItem entity) {
		return new ResourceLocation("pasterdream", "textures/block/wind_knight_spawnblock.png");
	}
}
