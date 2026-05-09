package net.pasterdream.item.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.item.ShadowHandLanternItem;

import net.minecraft.resources.ResourceLocation;

public class ShadowHandLanternItemModel extends GeoModel<ShadowHandLanternItem> {
	@Override
	public ResourceLocation getAnimationResource(ShadowHandLanternItem animatable) {
		return new ResourceLocation("pasterdream", "animations/shadow_hand_lantern.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(ShadowHandLanternItem animatable) {
		return new ResourceLocation("pasterdream", "geo/shadow_hand_lantern.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(ShadowHandLanternItem animatable) {
		return new ResourceLocation("pasterdream", "textures/item/shadow_hand_lantern.png");
	}
}
