package net.pasterdream.block.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.block.entity.ShadowBlastFurnaceTileEntity;

import net.minecraft.resources.ResourceLocation;

public class ShadowBlastFurnaceBlockModel extends GeoModel<ShadowBlastFurnaceTileEntity> {
	@Override
	public ResourceLocation getAnimationResource(ShadowBlastFurnaceTileEntity animatable) {
		return new ResourceLocation("pasterdream", "animations/shadow_blast_furnace.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(ShadowBlastFurnaceTileEntity animatable) {
		return new ResourceLocation("pasterdream", "geo/shadow_blast_furnace.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(ShadowBlastFurnaceTileEntity entity) {
		return new ResourceLocation("pasterdream", "textures/block/shadow_blast_furnace.png");
	}
}
