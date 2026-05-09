package net.pasterdream.block.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.block.entity.MeltdreamChestOpenTileEntity;

import net.minecraft.resources.ResourceLocation;

public class MeltdreamChestOpenBlockModel extends GeoModel<MeltdreamChestOpenTileEntity> {
	@Override
	public ResourceLocation getAnimationResource(MeltdreamChestOpenTileEntity animatable) {
		return new ResourceLocation("pasterdream", "animations/meltdream_chest_1.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(MeltdreamChestOpenTileEntity animatable) {
		return new ResourceLocation("pasterdream", "geo/meltdream_chest_1.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(MeltdreamChestOpenTileEntity entity) {
		return new ResourceLocation("pasterdream", "textures/block/meltdream_chest_0.png");
	}
}
