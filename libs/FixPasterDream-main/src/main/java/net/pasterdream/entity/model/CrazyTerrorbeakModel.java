package net.pasterdream.entity.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.entity.CrazyTerrorbeakEntity;

import net.minecraft.resources.ResourceLocation;

public class CrazyTerrorbeakModel extends GeoModel<CrazyTerrorbeakEntity> {
	@Override
	public ResourceLocation getAnimationResource(CrazyTerrorbeakEntity entity) {
		return new ResourceLocation("pasterdream", "animations/crazy_terrorbeak.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(CrazyTerrorbeakEntity entity) {
		return new ResourceLocation("pasterdream", "geo/crazy_terrorbeak.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(CrazyTerrorbeakEntity entity) {
		return new ResourceLocation("pasterdream", "textures/entities/" + entity.getTexture() + ".png");
	}

}
