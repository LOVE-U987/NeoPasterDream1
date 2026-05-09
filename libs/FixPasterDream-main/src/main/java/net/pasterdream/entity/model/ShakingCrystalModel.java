package net.pasterdream.entity.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.entity.ShakingCrystalEntity;

import net.minecraft.resources.ResourceLocation;

public class ShakingCrystalModel extends GeoModel<ShakingCrystalEntity> {
	@Override
	public ResourceLocation getAnimationResource(ShakingCrystalEntity entity) {
		return new ResourceLocation("pasterdream", "animations/shaking_crystal.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(ShakingCrystalEntity entity) {
		return new ResourceLocation("pasterdream", "geo/shaking_crystal.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(ShakingCrystalEntity entity) {
		return new ResourceLocation("pasterdream", "textures/entities/" + entity.getTexture() + ".png");
	}

}
