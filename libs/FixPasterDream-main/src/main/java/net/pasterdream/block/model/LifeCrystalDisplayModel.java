package net.pasterdream.block.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.block.display.LifeCrystalDisplayItem;

import net.minecraft.resources.ResourceLocation;

public class LifeCrystalDisplayModel extends GeoModel<LifeCrystalDisplayItem> {
	@Override
	public ResourceLocation getAnimationResource(LifeCrystalDisplayItem animatable) {
		return new ResourceLocation("pasterdream", "animations/life_crystal.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(LifeCrystalDisplayItem animatable) {
		return new ResourceLocation("pasterdream", "geo/life_crystal.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(LifeCrystalDisplayItem entity) {
		return new ResourceLocation("pasterdream", "textures/block/life_crystal.png");
	}
}
