package net.pasterdream.entity.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.entity.ShadowSquealGhost0Entity;

import net.minecraft.resources.ResourceLocation;

public class ShadowSquealGhost0Model extends GeoModel<ShadowSquealGhost0Entity> {
	@Override
	public ResourceLocation getAnimationResource(ShadowSquealGhost0Entity entity) {
		return new ResourceLocation("pasterdream", "animations/shadow_squeal_wave.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(ShadowSquealGhost0Entity entity) {
		return new ResourceLocation("pasterdream", "geo/shadow_squeal_wave.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(ShadowSquealGhost0Entity entity) {
		return new ResourceLocation("pasterdream", "textures/entities/" + entity.getTexture() + ".png");
	}

}
