package net.pasterdream.block.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.block.display.DesertHeroTombDisplayItem;

import net.minecraft.resources.ResourceLocation;

public class DesertHeroTombDisplayModel extends GeoModel<DesertHeroTombDisplayItem> {
	@Override
	public ResourceLocation getAnimationResource(DesertHeroTombDisplayItem animatable) {
		return new ResourceLocation("pasterdream", "animations/desert_hero_tomb.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(DesertHeroTombDisplayItem animatable) {
		return new ResourceLocation("pasterdream", "geo/desert_hero_tomb.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(DesertHeroTombDisplayItem entity) {
		return new ResourceLocation("pasterdream", "textures/block/desert_hero_tomb.png");
	}
}
