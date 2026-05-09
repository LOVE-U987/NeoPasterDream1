package net.pasterdream.block.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.block.display.BirdsNestDisplayItem;

import net.minecraft.resources.ResourceLocation;

public class BirdsNestDisplayModel extends GeoModel<BirdsNestDisplayItem> {
	@Override
	public ResourceLocation getAnimationResource(BirdsNestDisplayItem animatable) {
		return new ResourceLocation("pasterdream", "animations/birds_nest.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(BirdsNestDisplayItem animatable) {
		return new ResourceLocation("pasterdream", "geo/birds_nest.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(BirdsNestDisplayItem entity) {
		return new ResourceLocation("pasterdream", "textures/block/birds_nest.png");
	}
}
