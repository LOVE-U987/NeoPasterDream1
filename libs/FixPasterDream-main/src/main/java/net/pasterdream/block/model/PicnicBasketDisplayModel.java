package net.pasterdream.block.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.block.display.PicnicBasketDisplayItem;

import net.minecraft.resources.ResourceLocation;

public class PicnicBasketDisplayModel extends GeoModel<PicnicBasketDisplayItem> {
	@Override
	public ResourceLocation getAnimationResource(PicnicBasketDisplayItem animatable) {
		return new ResourceLocation("pasterdream", "animations/picnic_basket.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(PicnicBasketDisplayItem animatable) {
		return new ResourceLocation("pasterdream", "geo/picnic_basket.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(PicnicBasketDisplayItem entity) {
		return new ResourceLocation("pasterdream", "textures/block/picnic_basket.png");
	}
}
