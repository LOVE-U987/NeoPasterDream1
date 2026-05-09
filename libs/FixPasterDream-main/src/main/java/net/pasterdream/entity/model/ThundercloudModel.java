package net.pasterdream.entity.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.entity.ThundercloudEntity;

import net.minecraft.resources.ResourceLocation;

public class ThundercloudModel extends GeoModel<ThundercloudEntity> {
	@Override
	public ResourceLocation getAnimationResource(ThundercloudEntity entity) {
		return new ResourceLocation("pasterdream", "animations/thundercloud.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(ThundercloudEntity entity) {
		return new ResourceLocation("pasterdream", "geo/thundercloud.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(ThundercloudEntity entity) {
		return new ResourceLocation("pasterdream", "textures/entities/" + entity.getTexture() + ".png");
	}

}
