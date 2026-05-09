package net.pasterdream.block.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.block.entity.QymDoll0TileEntity;

import net.minecraft.resources.ResourceLocation;

public class QymDoll0BlockModel extends GeoModel<QymDoll0TileEntity> {
	@Override
	public ResourceLocation getAnimationResource(QymDoll0TileEntity animatable) {
		return new ResourceLocation("pasterdream", "animations/qym_doll_0.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(QymDoll0TileEntity animatable) {
		return new ResourceLocation("pasterdream", "geo/qym_doll_0.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(QymDoll0TileEntity entity) {
		return new ResourceLocation("pasterdream", "textures/block/qym_doll_0.png");
	}
}
