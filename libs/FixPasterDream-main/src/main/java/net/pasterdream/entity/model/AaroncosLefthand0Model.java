package net.pasterdream.entity.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.entity.AaroncosLefthand0Entity;

import net.minecraft.resources.ResourceLocation;

public class AaroncosLefthand0Model extends GeoModel<AaroncosLefthand0Entity> {
	@Override
	public ResourceLocation getAnimationResource(AaroncosLefthand0Entity entity) {
		return new ResourceLocation("pasterdream", "animations/aaroncos_lefthand_0.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(AaroncosLefthand0Entity entity) {
		return new ResourceLocation("pasterdream", "geo/aaroncos_lefthand_0.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(AaroncosLefthand0Entity entity) {
		return new ResourceLocation("pasterdream", "textures/entities/" + entity.getTexture() + ".png");
	}

}
