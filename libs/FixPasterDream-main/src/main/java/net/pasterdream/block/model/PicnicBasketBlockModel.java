package net.pasterdream.block.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.block.entity.PicnicBasketTileEntity;

import net.minecraft.resources.ResourceLocation;

public class PicnicBasketBlockModel extends GeoModel<PicnicBasketTileEntity> {
	@Override
	public ResourceLocation getAnimationResource(PicnicBasketTileEntity animatable) {
		return new ResourceLocation("pasterdream", "animations/picnic_basket.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(PicnicBasketTileEntity animatable) {
		return new ResourceLocation("pasterdream", "geo/picnic_basket.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(PicnicBasketTileEntity entity) {
		return new ResourceLocation("pasterdream", "textures/block/picnic_basket.png");
	}
}
