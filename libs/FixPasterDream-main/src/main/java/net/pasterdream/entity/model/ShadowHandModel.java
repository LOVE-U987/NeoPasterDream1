package net.pasterdream.entity.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.entity.ShadowHandEntity;

import net.minecraft.resources.ResourceLocation;

public class ShadowHandModel extends GeoModel<ShadowHandEntity> {
	@Override
	public ResourceLocation getAnimationResource(ShadowHandEntity entity) {
		return new ResourceLocation("pasterdream", "animations/shadow_hand.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(ShadowHandEntity entity) {
		return new ResourceLocation("pasterdream", "geo/shadow_hand.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(ShadowHandEntity entity) {
		return new ResourceLocation("pasterdream", "textures/entities/" + entity.getTexture() + ".png");
	}

}
