package net.pasterdream.block.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.block.display.MeltdreamChestOpenDisplayItem;

import net.minecraft.resources.ResourceLocation;

public class MeltdreamChestOpenDisplayModel extends GeoModel<MeltdreamChestOpenDisplayItem> {
	@Override
	public ResourceLocation getAnimationResource(MeltdreamChestOpenDisplayItem animatable) {
		return new ResourceLocation("pasterdream", "animations/meltdream_chest_1.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(MeltdreamChestOpenDisplayItem animatable) {
		return new ResourceLocation("pasterdream", "geo/meltdream_chest_1.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(MeltdreamChestOpenDisplayItem entity) {
		return new ResourceLocation("pasterdream", "textures/block/meltdream_chest_0.png");
	}
}
