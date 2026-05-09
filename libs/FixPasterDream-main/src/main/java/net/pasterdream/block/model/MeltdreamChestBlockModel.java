package net.pasterdream.block.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.block.entity.MeltdreamChestTileEntity;

import net.minecraft.resources.ResourceLocation;

public class MeltdreamChestBlockModel extends GeoModel<MeltdreamChestTileEntity> {
	@Override
	public ResourceLocation getAnimationResource(MeltdreamChestTileEntity animatable) {
		return new ResourceLocation("pasterdream", "animations/meltdream_chest_0.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(MeltdreamChestTileEntity animatable) {
		return new ResourceLocation("pasterdream", "geo/meltdream_chest_0.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(MeltdreamChestTileEntity entity) {
		return new ResourceLocation("pasterdream", "textures/block/meltdream_chest_0.png");
	}
}
