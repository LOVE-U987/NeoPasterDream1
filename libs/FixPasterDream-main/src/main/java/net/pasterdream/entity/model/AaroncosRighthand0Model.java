package net.pasterdream.entity.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.entity.AaroncosRighthand0Entity;

import net.minecraft.resources.ResourceLocation;

public class AaroncosRighthand0Model extends GeoModel<AaroncosRighthand0Entity> {
	@Override
	public ResourceLocation getAnimationResource(AaroncosRighthand0Entity entity) {
		return new ResourceLocation("pasterdream", "animations/aaroncos_righthand_0.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(AaroncosRighthand0Entity entity) {
		return new ResourceLocation("pasterdream", "geo/aaroncos_righthand_0.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(AaroncosRighthand0Entity entity) {
		return new ResourceLocation("pasterdream", "textures/entities/" + entity.getTexture() + ".png");
	}

}
