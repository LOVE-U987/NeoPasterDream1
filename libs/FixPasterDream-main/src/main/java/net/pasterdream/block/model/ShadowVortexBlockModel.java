package net.pasterdream.block.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.block.entity.ShadowVortexTileEntity;

import net.minecraft.resources.ResourceLocation;

public class ShadowVortexBlockModel extends GeoModel<ShadowVortexTileEntity> {
	@Override
	public ResourceLocation getAnimationResource(ShadowVortexTileEntity animatable) {
		return new ResourceLocation("pasterdream", "animations/shadow_vortex.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(ShadowVortexTileEntity animatable) {
		return new ResourceLocation("pasterdream", "geo/shadow_vortex.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(ShadowVortexTileEntity entity) {
		return new ResourceLocation("pasterdream", "textures/block/shadow_vortex.png");
	}
}
