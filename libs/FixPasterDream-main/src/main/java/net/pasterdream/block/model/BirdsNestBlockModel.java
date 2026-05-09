package net.pasterdream.block.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.block.entity.BirdsNestTileEntity;

import net.minecraft.resources.ResourceLocation;

public class BirdsNestBlockModel extends GeoModel<BirdsNestTileEntity> {
	@Override
	public ResourceLocation getAnimationResource(BirdsNestTileEntity animatable) {
		return new ResourceLocation("pasterdream", "animations/birds_nest.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(BirdsNestTileEntity animatable) {
		return new ResourceLocation("pasterdream", "geo/birds_nest.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(BirdsNestTileEntity entity) {
		return new ResourceLocation("pasterdream", "textures/block/birds_nest.png");
	}
}
