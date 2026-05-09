package net.pasterdream.block.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.block.entity.BrokenShadowDungeonProtalTileEntity;

import net.minecraft.resources.ResourceLocation;

public class BrokenShadowDungeonProtalBlockModel extends GeoModel<BrokenShadowDungeonProtalTileEntity> {
	@Override
	public ResourceLocation getAnimationResource(BrokenShadowDungeonProtalTileEntity animatable) {
		return new ResourceLocation("pasterdream", "animations/broken_shadow_dungeon_portal.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(BrokenShadowDungeonProtalTileEntity animatable) {
		return new ResourceLocation("pasterdream", "geo/broken_shadow_dungeon_portal.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(BrokenShadowDungeonProtalTileEntity entity) {
		return new ResourceLocation("pasterdream", "textures/block/shadow_dungeon_portal.png");
	}
}
