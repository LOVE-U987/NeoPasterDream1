package net.pasterdream.block.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.block.display.GoldenFoxSculptureDisplayItem;

import net.minecraft.resources.ResourceLocation;

public class GoldenFoxSculptureDisplayModel extends GeoModel<GoldenFoxSculptureDisplayItem> {
	@Override
	public ResourceLocation getAnimationResource(GoldenFoxSculptureDisplayItem animatable) {
		return new ResourceLocation("pasterdream", "animations/golden_fox_sculpture.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(GoldenFoxSculptureDisplayItem animatable) {
		return new ResourceLocation("pasterdream", "geo/golden_fox_sculpture.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(GoldenFoxSculptureDisplayItem entity) {
		return new ResourceLocation("pasterdream", "textures/block/golden_fox_sculpture.png");
	}
}
