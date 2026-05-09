package net.pasterdream.block.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.block.display.ShadowDungeonPortalDisplayItem;

import net.minecraft.resources.ResourceLocation;

public class ShadowDungeonPortalDisplayModel extends GeoModel<ShadowDungeonPortalDisplayItem> {
	@Override
	public ResourceLocation getAnimationResource(ShadowDungeonPortalDisplayItem animatable) {
		return new ResourceLocation("pasterdream", "animations/shadow_dungeon_portal.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(ShadowDungeonPortalDisplayItem animatable) {
		return new ResourceLocation("pasterdream", "geo/shadow_dungeon_portal.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(ShadowDungeonPortalDisplayItem entity) {
		return new ResourceLocation("pasterdream", "textures/block/shadow_dungeon_portal.png");
	}
}
