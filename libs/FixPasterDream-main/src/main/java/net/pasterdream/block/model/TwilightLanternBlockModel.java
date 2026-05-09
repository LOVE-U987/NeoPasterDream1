package net.pasterdream.block.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.block.entity.TwilightLanternTileEntity;

import net.minecraft.resources.ResourceLocation;

public class TwilightLanternBlockModel extends GeoModel<TwilightLanternTileEntity> {
	@Override
	public ResourceLocation getAnimationResource(TwilightLanternTileEntity animatable) {
		return new ResourceLocation("pasterdream", "animations/twilight_lantern.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(TwilightLanternTileEntity animatable) {
		return new ResourceLocation("pasterdream", "geo/twilight_lantern.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(TwilightLanternTileEntity entity) {
		return new ResourceLocation("pasterdream", "textures/block/twilight_lantern.png");
	}
}
