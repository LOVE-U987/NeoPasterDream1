package net.pasterdream.entity.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.entity.HealingSpellEntityEntity;

import net.minecraft.resources.ResourceLocation;

public class HealingSpellEntityModel extends GeoModel<HealingSpellEntityEntity> {
	@Override
	public ResourceLocation getAnimationResource(HealingSpellEntityEntity entity) {
		return new ResourceLocation("pasterdream", "animations/healing_spell.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(HealingSpellEntityEntity entity) {
		return new ResourceLocation("pasterdream", "geo/healing_spell.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(HealingSpellEntityEntity entity) {
		return new ResourceLocation("pasterdream", "textures/entities/" + entity.getTexture() + ".png");
	}

}
