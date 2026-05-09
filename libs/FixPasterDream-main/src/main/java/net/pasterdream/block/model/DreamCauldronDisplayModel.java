package net.pasterdream.block.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.block.display.DreamCauldronDisplayItem;

import net.minecraft.resources.ResourceLocation;

public class DreamCauldronDisplayModel extends GeoModel<DreamCauldronDisplayItem> {
	@Override
	public ResourceLocation getAnimationResource(DreamCauldronDisplayItem animatable) {
		return new ResourceLocation("pasterdream", "animations/dream_cauldron.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(DreamCauldronDisplayItem animatable) {
		return new ResourceLocation("pasterdream", "geo/dream_cauldron.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(DreamCauldronDisplayItem entity) {
		return new ResourceLocation("pasterdream", "textures/block/dream_cauldron.png");
	}
}
