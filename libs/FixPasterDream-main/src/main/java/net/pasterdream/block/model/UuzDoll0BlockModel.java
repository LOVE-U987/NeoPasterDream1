package net.pasterdream.block.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.block.entity.UuzDoll0TileEntity;

import net.minecraft.resources.ResourceLocation;

public class UuzDoll0BlockModel extends GeoModel<UuzDoll0TileEntity> {
	@Override
	public ResourceLocation getAnimationResource(UuzDoll0TileEntity animatable) {
		return new ResourceLocation("pasterdream", "animations/uuz_doll_0.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(UuzDoll0TileEntity animatable) {
		return new ResourceLocation("pasterdream", "geo/uuz_doll_0.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(UuzDoll0TileEntity entity) {
		return new ResourceLocation("pasterdream", "textures/block/uuz_doll_0.png");
	}
}
