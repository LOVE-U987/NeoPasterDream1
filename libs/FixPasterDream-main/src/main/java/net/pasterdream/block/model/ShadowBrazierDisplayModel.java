package net.pasterdream.block.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.block.display.ShadowBrazierDisplayItem;

import net.minecraft.resources.ResourceLocation;

public class ShadowBrazierDisplayModel extends GeoModel<ShadowBrazierDisplayItem> {
	@Override
	public ResourceLocation getAnimationResource(ShadowBrazierDisplayItem animatable) {
		return new ResourceLocation("pasterdream", "animations/shadow_brazier.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(ShadowBrazierDisplayItem animatable) {
		return new ResourceLocation("pasterdream", "geo/shadow_brazier.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(ShadowBrazierDisplayItem entity) {
		return new ResourceLocation("pasterdream", "textures/block/shadow_brazier.png");
	}
}
