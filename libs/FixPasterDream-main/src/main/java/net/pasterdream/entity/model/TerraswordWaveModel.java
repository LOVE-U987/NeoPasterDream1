package net.pasterdream.entity.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.entity.TerraswordWaveEntity;

import net.minecraft.resources.ResourceLocation;

public class TerraswordWaveModel extends GeoModel<TerraswordWaveEntity> {
	@Override
	public ResourceLocation getAnimationResource(TerraswordWaveEntity entity) {
		return new ResourceLocation("pasterdream", "animations/terrasword_wave.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(TerraswordWaveEntity entity) {
		return new ResourceLocation("pasterdream", "geo/terrasword_wave.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(TerraswordWaveEntity entity) {
		return new ResourceLocation("pasterdream", "textures/entities/" + entity.getTexture() + ".png");
	}

}
