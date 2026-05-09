package net.pasterdream.block.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.block.entity.ShadowDungeonPortalTileEntity;

import net.minecraft.resources.ResourceLocation;

public class ShadowDungeonPortalBlockModel extends GeoModel<ShadowDungeonPortalTileEntity> {
	@Override
	public ResourceLocation getAnimationResource(ShadowDungeonPortalTileEntity animatable) {
		return new ResourceLocation("pasterdream", "animations/shadow_dungeon_portal.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(ShadowDungeonPortalTileEntity animatable) {
		return new ResourceLocation("pasterdream", "geo/shadow_dungeon_portal.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(ShadowDungeonPortalTileEntity entity) {
		return new ResourceLocation("pasterdream", "textures/block/shadow_dungeon_portal.png");
	}
}
