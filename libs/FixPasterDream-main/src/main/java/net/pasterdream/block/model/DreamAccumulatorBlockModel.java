package net.pasterdream.block.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.block.entity.DreamAccumulatorTileEntity;

import net.minecraft.resources.ResourceLocation;

public class DreamAccumulatorBlockModel extends GeoModel<DreamAccumulatorTileEntity> {
	@Override
	public ResourceLocation getAnimationResource(DreamAccumulatorTileEntity animatable) {
		return new ResourceLocation("pasterdream", "animations/dream_accumulator.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(DreamAccumulatorTileEntity animatable) {
		return new ResourceLocation("pasterdream", "geo/dream_accumulator.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(DreamAccumulatorTileEntity entity) {
		return new ResourceLocation("pasterdream", "textures/block/dream_accumulator.png");
	}
}
