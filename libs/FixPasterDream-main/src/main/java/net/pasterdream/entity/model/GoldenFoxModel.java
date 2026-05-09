package net.pasterdream.entity.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.entity.GoldenFoxEntity;

import net.minecraft.resources.ResourceLocation;

public class GoldenFoxModel extends GeoModel<GoldenFoxEntity> {
	@Override
	public ResourceLocation getAnimationResource(GoldenFoxEntity entity) {
		return new ResourceLocation("pasterdream", "animations/golden_fox.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(GoldenFoxEntity entity) {
		return new ResourceLocation("pasterdream", "geo/golden_fox.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(GoldenFoxEntity entity) {
		return new ResourceLocation("pasterdream", "textures/entities/" + entity.getTexture() + ".png");
	}

}
