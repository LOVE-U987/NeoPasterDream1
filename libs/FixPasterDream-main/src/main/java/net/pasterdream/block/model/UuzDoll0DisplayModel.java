package net.pasterdream.block.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.block.display.UuzDoll0DisplayItem;

import net.minecraft.resources.ResourceLocation;

public class UuzDoll0DisplayModel extends GeoModel<UuzDoll0DisplayItem> {
	@Override
	public ResourceLocation getAnimationResource(UuzDoll0DisplayItem animatable) {
		return new ResourceLocation("pasterdream", "animations/uuz_doll_0.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(UuzDoll0DisplayItem animatable) {
		return new ResourceLocation("pasterdream", "geo/uuz_doll_0.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(UuzDoll0DisplayItem entity) {
		return new ResourceLocation("pasterdream", "textures/block/uuz_doll_0.png");
	}
}
