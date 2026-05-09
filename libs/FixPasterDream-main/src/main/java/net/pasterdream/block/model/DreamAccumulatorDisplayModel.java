package net.pasterdream.block.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.block.display.DreamAccumulatorDisplayItem;

import net.minecraft.resources.ResourceLocation;

public class DreamAccumulatorDisplayModel extends GeoModel<DreamAccumulatorDisplayItem> {
	@Override
	public ResourceLocation getAnimationResource(DreamAccumulatorDisplayItem animatable) {
		return new ResourceLocation("pasterdream", "animations/dream_accumulator.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(DreamAccumulatorDisplayItem animatable) {
		return new ResourceLocation("pasterdream", "geo/dream_accumulator.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(DreamAccumulatorDisplayItem entity) {
		return new ResourceLocation("pasterdream", "textures/block/dream_accumulator.png");
	}
}
