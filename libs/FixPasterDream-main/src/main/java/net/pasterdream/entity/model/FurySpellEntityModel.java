package net.pasterdream.entity.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.entity.FurySpellEntityEntity;

import net.minecraft.resources.ResourceLocation;

public class FurySpellEntityModel extends GeoModel<FurySpellEntityEntity> {
	@Override
	public ResourceLocation getAnimationResource(FurySpellEntityEntity entity) {
		return new ResourceLocation("pasterdream", "animations/fury_spell_entity.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(FurySpellEntityEntity entity) {
		return new ResourceLocation("pasterdream", "geo/fury_spell_entity.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(FurySpellEntityEntity entity) {
		return new ResourceLocation("pasterdream", "textures/entities/" + entity.getTexture() + ".png");
	}

}
