package net.pasterdream.entity.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.entity.WindKnightEntity;

import net.minecraft.resources.ResourceLocation;

public class WindKnightModel extends GeoModel<WindKnightEntity> {
	@Override
	public ResourceLocation getAnimationResource(WindKnightEntity entity) {
		return new ResourceLocation("pasterdream", "animations/wind_knight.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(WindKnightEntity entity) {
		return new ResourceLocation("pasterdream", "geo/wind_knight.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(WindKnightEntity entity) {
		return new ResourceLocation("pasterdream", "textures/entities/" + entity.getTexture() + ".png");
	}

}
