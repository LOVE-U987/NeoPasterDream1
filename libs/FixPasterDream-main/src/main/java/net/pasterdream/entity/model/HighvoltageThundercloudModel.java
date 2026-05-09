package net.pasterdream.entity.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.entity.HighvoltageThundercloudEntity;

import net.minecraft.resources.ResourceLocation;

public class HighvoltageThundercloudModel extends GeoModel<HighvoltageThundercloudEntity> {
	@Override
	public ResourceLocation getAnimationResource(HighvoltageThundercloudEntity entity) {
		return new ResourceLocation("pasterdream", "animations/highvoltage_thundercloud.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(HighvoltageThundercloudEntity entity) {
		return new ResourceLocation("pasterdream", "geo/highvoltage_thundercloud.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(HighvoltageThundercloudEntity entity) {
		return new ResourceLocation("pasterdream", "textures/entities/" + entity.getTexture() + ".png");
	}

}
