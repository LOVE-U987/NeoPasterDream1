package net.pasterdream.block.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.block.entity.AaroncoshandspawnblockTileEntity;

import net.minecraft.resources.ResourceLocation;

public class AaroncoshandspawnblockBlockModel extends GeoModel<AaroncoshandspawnblockTileEntity> {
	@Override
	public ResourceLocation getAnimationResource(AaroncoshandspawnblockTileEntity animatable) {
		return new ResourceLocation("pasterdream", "animations/aaroncos_hand_spawn_block.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(AaroncoshandspawnblockTileEntity animatable) {
		return new ResourceLocation("pasterdream", "geo/aaroncos_hand_spawn_block.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(AaroncoshandspawnblockTileEntity entity) {
		return new ResourceLocation("pasterdream", "textures/block/aaroncos_hand_spawn_block.png");
	}
}
