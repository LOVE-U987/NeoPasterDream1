package net.pasterdream.block.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.block.display.BrokenShadowDungeonProtalDisplayItem;

import net.minecraft.resources.ResourceLocation;

public class BrokenShadowDungeonProtalDisplayModel extends GeoModel<BrokenShadowDungeonProtalDisplayItem> {
	@Override
	public ResourceLocation getAnimationResource(BrokenShadowDungeonProtalDisplayItem animatable) {
		return new ResourceLocation("pasterdream", "animations/broken_shadow_dungeon_portal.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(BrokenShadowDungeonProtalDisplayItem animatable) {
		return new ResourceLocation("pasterdream", "geo/broken_shadow_dungeon_portal.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(BrokenShadowDungeonProtalDisplayItem entity) {
		return new ResourceLocation("pasterdream", "textures/block/shadow_dungeon_portal.png");
	}
}
