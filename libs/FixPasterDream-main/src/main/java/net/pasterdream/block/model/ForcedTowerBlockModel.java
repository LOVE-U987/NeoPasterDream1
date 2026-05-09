package net.pasterdream.block.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.block.entity.ForcedTowerTileEntity;

import net.minecraft.resources.ResourceLocation;

public class ForcedTowerBlockModel extends GeoModel<ForcedTowerTileEntity> {
	@Override
	public ResourceLocation getAnimationResource(ForcedTowerTileEntity animatable) {
		return new ResourceLocation("pasterdream", "animations/forced_tower.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(ForcedTowerTileEntity animatable) {
		return new ResourceLocation("pasterdream", "geo/forced_tower.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(ForcedTowerTileEntity entity) {
		return new ResourceLocation("pasterdream", "textures/block/forced_tower.png");
	}
}
