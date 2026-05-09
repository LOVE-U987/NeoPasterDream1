package net.pasterdream.entity.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.entity.ShadowTuneTotemEntity;

import net.minecraft.resources.ResourceLocation;

public class ShadowTuneTotemModel extends GeoModel<ShadowTuneTotemEntity> {
	@Override
	public ResourceLocation getAnimationResource(ShadowTuneTotemEntity entity) {
		return new ResourceLocation("pasterdream", "animations/shadow_rune_totem.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(ShadowTuneTotemEntity entity) {
		return new ResourceLocation("pasterdream", "geo/shadow_rune_totem.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(ShadowTuneTotemEntity entity) {
		return new ResourceLocation("pasterdream", "textures/entities/" + entity.getTexture() + ".png");
	}

}
