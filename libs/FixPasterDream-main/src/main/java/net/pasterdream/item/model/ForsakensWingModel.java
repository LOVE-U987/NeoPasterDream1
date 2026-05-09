package net.pasterdream.item.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.item.ForsakensWingItem;

import net.minecraft.resources.ResourceLocation;

public class ForsakensWingModel extends GeoModel<ForsakensWingItem> {
	@Override
	public ResourceLocation getAnimationResource(ForsakensWingItem object) {
		return new ResourceLocation("pasterdream", "animations/forsakens_wing.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(ForsakensWingItem object) {
		return new ResourceLocation("pasterdream", "geo/forsakens_wing.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(ForsakensWingItem object) {
		return new ResourceLocation("pasterdream", "textures/item/forsakens_wing.png");
	}
}
