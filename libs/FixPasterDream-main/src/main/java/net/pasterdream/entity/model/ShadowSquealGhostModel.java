package net.pasterdream.entity.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.entity.ShadowSquealGhostEntity;

import net.minecraft.resources.ResourceLocation;

public class ShadowSquealGhostModel extends GeoModel<ShadowSquealGhostEntity> {
	@Override
	public ResourceLocation getAnimationResource(ShadowSquealGhostEntity entity) {
		return new ResourceLocation("pasterdream", "animations/shadow_ghost.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(ShadowSquealGhostEntity entity) {
		return new ResourceLocation("pasterdream", "geo/shadow_ghost.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(ShadowSquealGhostEntity entity) {
		return new ResourceLocation("pasterdream", "textures/entities/" + entity.getTexture() + ".png");
	}

}
