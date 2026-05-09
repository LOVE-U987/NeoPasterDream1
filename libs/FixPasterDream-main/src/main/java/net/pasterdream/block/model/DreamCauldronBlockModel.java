package net.pasterdream.block.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.block.entity.DreamCauldronTileEntity;

import net.minecraft.resources.ResourceLocation;

public class DreamCauldronBlockModel extends GeoModel<DreamCauldronTileEntity> {
	@Override
	public ResourceLocation getAnimationResource(DreamCauldronTileEntity animatable) {
		return new ResourceLocation("pasterdream", "animations/dream_cauldron.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(DreamCauldronTileEntity animatable) {
		return new ResourceLocation("pasterdream", "geo/dream_cauldron.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(DreamCauldronTileEntity entity) {
		return new ResourceLocation("pasterdream", "textures/block/dream_cauldron.png");
	}
}
