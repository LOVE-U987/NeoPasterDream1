package net.pasterdream.entity.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.entity.BasaltSnailEntity;

import net.minecraft.resources.ResourceLocation;

public class BasaltSnailModel extends GeoModel<BasaltSnailEntity> {
	@Override
	public ResourceLocation getAnimationResource(BasaltSnailEntity entity) {
		return new ResourceLocation("pasterdream", "animations/basalt_snail.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(BasaltSnailEntity entity) {
		return new ResourceLocation("pasterdream", "geo/basalt_snail.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(BasaltSnailEntity entity) {
		return new ResourceLocation("pasterdream", "textures/entities/" + entity.getTexture() + ".png");
	}

}
