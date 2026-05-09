package net.pasterdream.entity.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.entity.FireflyEntity;

import net.minecraft.resources.ResourceLocation;

public class FireflyModel extends GeoModel<FireflyEntity> {
	@Override
	public ResourceLocation getAnimationResource(FireflyEntity entity) {
		return new ResourceLocation("pasterdream", "animations/firefly.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(FireflyEntity entity) {
		return new ResourceLocation("pasterdream", "geo/firefly.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(FireflyEntity entity) {
		return new ResourceLocation("pasterdream", "textures/entities/" + entity.getTexture() + ".png");
	}

}
