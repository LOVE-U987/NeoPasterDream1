package net.pasterdream.block.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.block.display.ForcedTowerDisplayItem;

import net.minecraft.resources.ResourceLocation;

public class ForcedTowerDisplayModel extends GeoModel<ForcedTowerDisplayItem> {
	@Override
	public ResourceLocation getAnimationResource(ForcedTowerDisplayItem animatable) {
		return new ResourceLocation("pasterdream", "animations/forced_tower.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(ForcedTowerDisplayItem animatable) {
		return new ResourceLocation("pasterdream", "geo/forced_tower.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(ForcedTowerDisplayItem entity) {
		return new ResourceLocation("pasterdream", "textures/block/forced_tower.png");
	}
}
