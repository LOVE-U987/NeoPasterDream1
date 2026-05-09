package net.pasterdream.block.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.block.display.ShadowVortexDisplayItem;

import net.minecraft.resources.ResourceLocation;

public class ShadowVortexDisplayModel extends GeoModel<ShadowVortexDisplayItem> {
	@Override
	public ResourceLocation getAnimationResource(ShadowVortexDisplayItem animatable) {
		return new ResourceLocation("pasterdream", "animations/shadow_vortex.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(ShadowVortexDisplayItem animatable) {
		return new ResourceLocation("pasterdream", "geo/shadow_vortex.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(ShadowVortexDisplayItem entity) {
		return new ResourceLocation("pasterdream", "textures/block/shadow_vortex.png");
	}
}
