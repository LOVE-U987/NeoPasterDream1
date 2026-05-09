package net.pasterdream.block.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.block.entity.AaroncosHandChestTileEntity;

import net.minecraft.resources.ResourceLocation;

public class AaroncosHandChestBlockModel extends GeoModel<AaroncosHandChestTileEntity> {
	@Override
	public ResourceLocation getAnimationResource(AaroncosHandChestTileEntity animatable) {
		return new ResourceLocation("pasterdream", "animations/aaroncos_hand_chest.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(AaroncosHandChestTileEntity animatable) {
		return new ResourceLocation("pasterdream", "geo/aaroncos_hand_chest.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(AaroncosHandChestTileEntity entity) {
		return new ResourceLocation("pasterdream", "textures/block/aaroncos_hand_chest.png");
	}
}
