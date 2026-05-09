package net.pasterdream.entity.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.entity.ShadowGolemEntity;

import net.minecraft.resources.ResourceLocation;

public class ShadowGolemModel extends GeoModel<ShadowGolemEntity> {
	@Override
	public ResourceLocation getAnimationResource(ShadowGolemEntity entity) {
		return new ResourceLocation("pasterdream", "animations/shadow_golem.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(ShadowGolemEntity entity) {
		return new ResourceLocation("pasterdream", "geo/shadow_golem.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(ShadowGolemEntity entity) {
		return new ResourceLocation("pasterdream", "textures/entities/" + entity.getTexture() + ".png");
	}

}
