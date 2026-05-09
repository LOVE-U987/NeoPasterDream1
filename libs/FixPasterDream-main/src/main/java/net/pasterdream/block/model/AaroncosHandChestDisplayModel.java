package net.pasterdream.block.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.block.display.AaroncosHandChestDisplayItem;

import net.minecraft.resources.ResourceLocation;

public class AaroncosHandChestDisplayModel extends GeoModel<AaroncosHandChestDisplayItem> {
	@Override
	public ResourceLocation getAnimationResource(AaroncosHandChestDisplayItem animatable) {
		return new ResourceLocation("pasterdream", "animations/aaroncos_hand_chest.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(AaroncosHandChestDisplayItem animatable) {
		return new ResourceLocation("pasterdream", "geo/aaroncos_hand_chest.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(AaroncosHandChestDisplayItem entity) {
		return new ResourceLocation("pasterdream", "textures/block/aaroncos_hand_chest.png");
	}
}
