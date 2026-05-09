package net.pasterdream.block.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.block.display.MeltdreamChestDisplayItem;

import net.minecraft.resources.ResourceLocation;

public class MeltdreamChestDisplayModel extends GeoModel<MeltdreamChestDisplayItem> {
	@Override
	public ResourceLocation getAnimationResource(MeltdreamChestDisplayItem animatable) {
		return new ResourceLocation("pasterdream", "animations/meltdream_chest_0.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(MeltdreamChestDisplayItem animatable) {
		return new ResourceLocation("pasterdream", "geo/meltdream_chest_0.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(MeltdreamChestDisplayItem entity) {
		return new ResourceLocation("pasterdream", "textures/block/meltdream_chest_0.png");
	}
}
