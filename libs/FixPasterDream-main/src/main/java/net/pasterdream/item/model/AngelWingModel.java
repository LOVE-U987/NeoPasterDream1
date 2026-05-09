package net.pasterdream.item.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.item.AngelWingItem;

import net.minecraft.resources.ResourceLocation;

public class AngelWingModel extends GeoModel<AngelWingItem> {
	@Override
	public ResourceLocation getAnimationResource(AngelWingItem object) {
		return new ResourceLocation("pasterdream", "animations/angel_wing.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(AngelWingItem object) {
		return new ResourceLocation("pasterdream", "geo/angel_wing.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(AngelWingItem object) {
		return new ResourceLocation("pasterdream", "textures/item/angel_wing.png");
	}
}
