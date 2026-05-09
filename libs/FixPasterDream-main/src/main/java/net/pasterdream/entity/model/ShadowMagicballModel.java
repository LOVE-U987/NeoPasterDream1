package net.pasterdream.entity.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.entity.ShadowMagicballEntity;

import net.minecraft.resources.ResourceLocation;

public class ShadowMagicballModel extends GeoModel<ShadowMagicballEntity> {
	@Override
	public ResourceLocation getAnimationResource(ShadowMagicballEntity entity) {
		return new ResourceLocation("pasterdream", "animations/shadow_magicball.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(ShadowMagicballEntity entity) {
		return new ResourceLocation("pasterdream", "geo/shadow_magicball.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(ShadowMagicballEntity entity) {
		return new ResourceLocation("pasterdream", "textures/entities/" + entity.getTexture() + ".png");
	}

}
