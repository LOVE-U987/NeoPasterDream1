package net.pasterdream.block.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.block.entity.LifeCrystalTileEntity;

import net.minecraft.resources.ResourceLocation;

public class LifeCrystalBlockModel extends GeoModel<LifeCrystalTileEntity> {
	@Override
	public ResourceLocation getAnimationResource(LifeCrystalTileEntity animatable) {
		return new ResourceLocation("pasterdream", "animations/life_crystal.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(LifeCrystalTileEntity animatable) {
		return new ResourceLocation("pasterdream", "geo/life_crystal.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(LifeCrystalTileEntity entity) {
		return new ResourceLocation("pasterdream", "textures/block/life_crystal.png");
	}
}
