package net.pasterdream.entity.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.entity.JellyfishEntity;

import net.minecraft.resources.ResourceLocation;

public class JellyfishModel extends GeoModel<JellyfishEntity> {
	@Override
	public ResourceLocation getAnimationResource(JellyfishEntity entity) {
		return new ResourceLocation("pasterdream", "animations/jellyfish.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(JellyfishEntity entity) {
		return new ResourceLocation("pasterdream", "geo/jellyfish.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(JellyfishEntity entity) {
		return new ResourceLocation("pasterdream", "textures/entities/" + entity.getTexture() + ".png");
	}

}
