package net.pasterdream.block.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.block.display.GuardCrystalDisplayItem;

import net.minecraft.resources.ResourceLocation;

public class GuardCrystalDisplayModel extends GeoModel<GuardCrystalDisplayItem> {
	@Override
	public ResourceLocation getAnimationResource(GuardCrystalDisplayItem animatable) {
		return new ResourceLocation("pasterdream", "animations/guard_crystal.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(GuardCrystalDisplayItem animatable) {
		return new ResourceLocation("pasterdream", "geo/guard_crystal.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(GuardCrystalDisplayItem entity) {
		return new ResourceLocation("pasterdream", "textures/block/guard_crystal.png");
	}
}
