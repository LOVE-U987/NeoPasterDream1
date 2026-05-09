package net.pasterdream.block.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.block.entity.DesertHeroTombTileEntity;

import net.minecraft.resources.ResourceLocation;

public class DesertHeroTombBlockModel extends GeoModel<DesertHeroTombTileEntity> {
	@Override
	public ResourceLocation getAnimationResource(DesertHeroTombTileEntity animatable) {
		return new ResourceLocation("pasterdream", "animations/desert_hero_tomb.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(DesertHeroTombTileEntity animatable) {
		return new ResourceLocation("pasterdream", "geo/desert_hero_tomb.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(DesertHeroTombTileEntity entity) {
		return new ResourceLocation("pasterdream", "textures/block/desert_hero_tomb.png");
	}
}
