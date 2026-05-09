package net.pasterdream.block.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.block.entity.ShadowChestTileEntity;

import net.minecraft.resources.ResourceLocation;

public class ShadowChestBlockModel extends GeoModel<ShadowChestTileEntity> {
	@Override
	public ResourceLocation getAnimationResource(ShadowChestTileEntity animatable) {
		return new ResourceLocation("pasterdream", "animations/shadow_chest.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(ShadowChestTileEntity animatable) {
		return new ResourceLocation("pasterdream", "geo/shadow_chest.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(ShadowChestTileEntity entity) {
		return new ResourceLocation("pasterdream", "textures/block/shadow_chest.png");
	}
}
