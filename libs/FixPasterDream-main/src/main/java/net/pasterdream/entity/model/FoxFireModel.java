package net.pasterdream.entity.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.entity.FoxFireEntity;

import net.minecraft.resources.ResourceLocation;

public class FoxFireModel extends GeoModel<FoxFireEntity> {
	@Override
	public ResourceLocation getAnimationResource(FoxFireEntity entity) {
		return new ResourceLocation("pasterdream", "animations/fox_fire.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(FoxFireEntity entity) {
		return new ResourceLocation("pasterdream", "geo/fox_fire.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(FoxFireEntity entity) {
		return new ResourceLocation("pasterdream", "textures/entities/" + entity.getTexture() + ".png");
	}

}
