package net.pasterdream.block.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.block.display.TwilightLanternDisplayItem;

import net.minecraft.resources.ResourceLocation;

public class TwilightLanternDisplayModel extends GeoModel<TwilightLanternDisplayItem> {
	@Override
	public ResourceLocation getAnimationResource(TwilightLanternDisplayItem animatable) {
		return new ResourceLocation("pasterdream", "animations/twilight_lantern.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(TwilightLanternDisplayItem animatable) {
		return new ResourceLocation("pasterdream", "geo/twilight_lantern.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(TwilightLanternDisplayItem entity) {
		return new ResourceLocation("pasterdream", "textures/block/twilight_lantern.png");
	}
}
