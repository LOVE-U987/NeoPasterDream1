package net.pasterdream.entity.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.entity.BlackBeetleEntity;

import net.minecraft.resources.ResourceLocation;

public class BlackBeetleModel extends GeoModel<BlackBeetleEntity> {
	@Override
	public ResourceLocation getAnimationResource(BlackBeetleEntity entity) {
		return new ResourceLocation("pasterdream", "animations/black_beetle.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(BlackBeetleEntity entity) {
		return new ResourceLocation("pasterdream", "geo/black_beetle.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(BlackBeetleEntity entity) {
		return new ResourceLocation("pasterdream", "textures/entities/" + entity.getTexture() + ".png");
	}

}
