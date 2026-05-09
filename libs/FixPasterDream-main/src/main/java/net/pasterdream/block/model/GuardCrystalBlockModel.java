package net.pasterdream.block.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.block.entity.GuardCrystalTileEntity;

import net.minecraft.resources.ResourceLocation;

public class GuardCrystalBlockModel extends GeoModel<GuardCrystalTileEntity> {
	@Override
	public ResourceLocation getAnimationResource(GuardCrystalTileEntity animatable) {
		return new ResourceLocation("pasterdream", "animations/guard_crystal.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(GuardCrystalTileEntity animatable) {
		return new ResourceLocation("pasterdream", "geo/guard_crystal.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(GuardCrystalTileEntity entity) {
		return new ResourceLocation("pasterdream", "textures/block/guard_crystal.png");
	}
}
