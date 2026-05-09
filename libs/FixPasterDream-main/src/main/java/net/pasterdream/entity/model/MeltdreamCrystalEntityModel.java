package net.pasterdream.entity.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.entity.MeltdreamCrystalEntityEntity;

import net.minecraft.resources.ResourceLocation;

public class MeltdreamCrystalEntityModel extends GeoModel<MeltdreamCrystalEntityEntity> {
	@Override
	public ResourceLocation getAnimationResource(MeltdreamCrystalEntityEntity entity) {
		return new ResourceLocation("pasterdream", "animations/meltdream_crystal_entity.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(MeltdreamCrystalEntityEntity entity) {
		return new ResourceLocation("pasterdream", "geo/meltdream_crystal_entity.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(MeltdreamCrystalEntityEntity entity) {
		return new ResourceLocation("pasterdream", "textures/entities/" + entity.getTexture() + ".png");
	}

}
