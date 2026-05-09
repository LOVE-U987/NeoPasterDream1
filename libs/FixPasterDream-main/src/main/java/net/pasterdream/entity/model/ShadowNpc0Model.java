package net.pasterdream.entity.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.entity.ShadowNpc0Entity;

import net.minecraft.resources.ResourceLocation;

public class ShadowNpc0Model extends GeoModel<ShadowNpc0Entity> {
	@Override
	public ResourceLocation getAnimationResource(ShadowNpc0Entity entity) {
		return new ResourceLocation("pasterdream", "animations/shadow_npc_0.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(ShadowNpc0Entity entity) {
		return new ResourceLocation("pasterdream", "geo/shadow_npc_0.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(ShadowNpc0Entity entity) {
		return new ResourceLocation("pasterdream", "textures/entities/" + entity.getTexture() + ".png");
	}

}
