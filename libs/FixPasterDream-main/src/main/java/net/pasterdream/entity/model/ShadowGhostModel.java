package net.pasterdream.entity.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.entity.ShadowGhostEntity;

import net.minecraft.resources.ResourceLocation;

public class ShadowGhostModel extends GeoModel<ShadowGhostEntity> {
	@Override
	public ResourceLocation getAnimationResource(ShadowGhostEntity entity) {
		return new ResourceLocation("pasterdream", "animations/shadow_ghost.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(ShadowGhostEntity entity) {
		return new ResourceLocation("pasterdream", "geo/shadow_ghost.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(ShadowGhostEntity entity) {
		return new ResourceLocation("pasterdream", "textures/entities/" + entity.getTexture() + ".png");
	}

}
