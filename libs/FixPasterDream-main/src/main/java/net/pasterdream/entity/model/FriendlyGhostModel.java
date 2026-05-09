package net.pasterdream.entity.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.entity.FriendlyGhostEntity;

import net.minecraft.resources.ResourceLocation;

public class FriendlyGhostModel extends GeoModel<FriendlyGhostEntity> {
	@Override
	public ResourceLocation getAnimationResource(FriendlyGhostEntity entity) {
		return new ResourceLocation("pasterdream", "animations/shadow_ghost.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(FriendlyGhostEntity entity) {
		return new ResourceLocation("pasterdream", "geo/shadow_ghost.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(FriendlyGhostEntity entity) {
		return new ResourceLocation("pasterdream", "textures/entities/" + entity.getTexture() + ".png");
	}

}
