package net.pasterdream.entity.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.entity.WeakenessTerrorbeakEntity;

import net.minecraft.resources.ResourceLocation;

public class WeakenessTerrorbeakModel extends GeoModel<WeakenessTerrorbeakEntity> {
	@Override
	public ResourceLocation getAnimationResource(WeakenessTerrorbeakEntity entity) {
		return new ResourceLocation("pasterdream", "animations/weakness_terrorbeak.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(WeakenessTerrorbeakEntity entity) {
		return new ResourceLocation("pasterdream", "geo/weakness_terrorbeak.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(WeakenessTerrorbeakEntity entity) {
		return new ResourceLocation("pasterdream", "textures/entities/" + entity.getTexture() + ".png");
	}

}
