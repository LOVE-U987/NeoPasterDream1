package net.pasterdream.block.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.block.entity.ShadowBrazierTileEntity;

import net.minecraft.resources.ResourceLocation;

public class ShadowBrazierBlockModel extends GeoModel<ShadowBrazierTileEntity> {
	@Override
	public ResourceLocation getAnimationResource(ShadowBrazierTileEntity animatable) {
		return new ResourceLocation("pasterdream", "animations/shadow_brazier.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(ShadowBrazierTileEntity animatable) {
		return new ResourceLocation("pasterdream", "geo/shadow_brazier.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(ShadowBrazierTileEntity entity) {
		return new ResourceLocation("pasterdream", "textures/block/shadow_brazier.png");
	}
}
