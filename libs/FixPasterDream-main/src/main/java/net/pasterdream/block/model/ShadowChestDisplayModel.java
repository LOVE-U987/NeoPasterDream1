package net.pasterdream.block.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.block.display.ShadowChestDisplayItem;

import net.minecraft.resources.ResourceLocation;

public class ShadowChestDisplayModel extends GeoModel<ShadowChestDisplayItem> {
	@Override
	public ResourceLocation getAnimationResource(ShadowChestDisplayItem animatable) {
		return new ResourceLocation("pasterdream", "animations/shadow_chest.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(ShadowChestDisplayItem animatable) {
		return new ResourceLocation("pasterdream", "geo/shadow_chest.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(ShadowChestDisplayItem entity) {
		return new ResourceLocation("pasterdream", "textures/block/shadow_chest.png");
	}
}
