package net.pasterdream.entity.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.entity.BlackBeetleMotherEntity;

import net.minecraft.resources.ResourceLocation;

public class BlackBeetleMotherModel extends GeoModel<BlackBeetleMotherEntity> {
	@Override
	public ResourceLocation getAnimationResource(BlackBeetleMotherEntity entity) {
		return new ResourceLocation("pasterdream", "animations/black_beetle_mother.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(BlackBeetleMotherEntity entity) {
		return new ResourceLocation("pasterdream", "geo/black_beetle_mother.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(BlackBeetleMotherEntity entity) {
		return new ResourceLocation("pasterdream", "textures/entities/" + entity.getTexture() + ".png");
	}

}
