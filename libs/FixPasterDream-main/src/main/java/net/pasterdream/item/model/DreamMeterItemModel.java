package net.pasterdream.item.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.item.DreamMeterItem;

import net.minecraft.resources.ResourceLocation;

public class DreamMeterItemModel extends GeoModel<DreamMeterItem> {
	@Override
	public ResourceLocation getAnimationResource(DreamMeterItem animatable) {
		return new ResourceLocation("pasterdream", "animations/dream_meter.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(DreamMeterItem animatable) {
		return new ResourceLocation("pasterdream", "geo/dream_meter.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(DreamMeterItem animatable) {
		return new ResourceLocation("pasterdream", "textures/item/dream_meter.png");
	}
}
