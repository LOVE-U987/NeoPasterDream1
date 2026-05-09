package net.pasterdream.entity.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.entity.TerrorbeakEntity;

import net.minecraft.resources.ResourceLocation;

public class TerrorbeakModel extends GeoModel<TerrorbeakEntity> {
	@Override
	public ResourceLocation getAnimationResource(TerrorbeakEntity entity) {
		return new ResourceLocation("pasterdream", "animations/terrorbeak.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(TerrorbeakEntity entity) {
		return new ResourceLocation("pasterdream", "geo/terrorbeak.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(TerrorbeakEntity entity) {
		return new ResourceLocation("pasterdream", "textures/entities/" + entity.getTexture() + ".png");
	}

}
