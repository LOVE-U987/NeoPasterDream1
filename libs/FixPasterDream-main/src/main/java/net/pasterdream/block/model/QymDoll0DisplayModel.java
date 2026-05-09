package net.pasterdream.block.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.block.display.QymDoll0DisplayItem;

import net.minecraft.resources.ResourceLocation;

public class QymDoll0DisplayModel extends GeoModel<QymDoll0DisplayItem> {
	@Override
	public ResourceLocation getAnimationResource(QymDoll0DisplayItem animatable) {
		return new ResourceLocation("pasterdream", "animations/qym_doll_0.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(QymDoll0DisplayItem animatable) {
		return new ResourceLocation("pasterdream", "geo/qym_doll_0.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(QymDoll0DisplayItem entity) {
		return new ResourceLocation("pasterdream", "textures/block/qym_doll_0.png");
	}
}
