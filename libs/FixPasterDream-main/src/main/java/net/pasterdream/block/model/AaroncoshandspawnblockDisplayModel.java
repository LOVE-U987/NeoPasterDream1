package net.pasterdream.block.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.block.display.AaroncoshandspawnblockDisplayItem;

import net.minecraft.resources.ResourceLocation;

public class AaroncoshandspawnblockDisplayModel extends GeoModel<AaroncoshandspawnblockDisplayItem> {
	@Override
	public ResourceLocation getAnimationResource(AaroncoshandspawnblockDisplayItem animatable) {
		return new ResourceLocation("pasterdream", "animations/aaroncos_hand_spawn_block.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(AaroncoshandspawnblockDisplayItem animatable) {
		return new ResourceLocation("pasterdream", "geo/aaroncos_hand_spawn_block.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(AaroncoshandspawnblockDisplayItem entity) {
		return new ResourceLocation("pasterdream", "textures/block/aaroncos_hand_spawn_block.png");
	}
}
