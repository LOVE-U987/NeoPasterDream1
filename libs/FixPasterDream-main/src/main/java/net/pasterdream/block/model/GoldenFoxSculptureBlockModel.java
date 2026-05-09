package net.pasterdream.block.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.block.entity.GoldenFoxSculptureTileEntity;

import net.minecraft.resources.ResourceLocation;

public class GoldenFoxSculptureBlockModel extends GeoModel<GoldenFoxSculptureTileEntity> {
	@Override
	public ResourceLocation getAnimationResource(GoldenFoxSculptureTileEntity animatable) {
		return new ResourceLocation("pasterdream", "animations/golden_fox_sculpture.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(GoldenFoxSculptureTileEntity animatable) {
		return new ResourceLocation("pasterdream", "geo/golden_fox_sculpture.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(GoldenFoxSculptureTileEntity entity) {
		return new ResourceLocation("pasterdream", "textures/block/golden_fox_sculpture.png");
	}
}
