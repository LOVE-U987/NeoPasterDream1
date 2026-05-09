package net.pasterdream.block.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.block.display.WeaponWorkshopDisplayItem;

import net.minecraft.resources.ResourceLocation;

public class WeaponWorkshopDisplayModel extends GeoModel<WeaponWorkshopDisplayItem> {
	@Override
	public ResourceLocation getAnimationResource(WeaponWorkshopDisplayItem animatable) {
		return new ResourceLocation("pasterdream", "animations/weapon_workshop.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(WeaponWorkshopDisplayItem animatable) {
		return new ResourceLocation("pasterdream", "geo/weapon_workshop.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(WeaponWorkshopDisplayItem entity) {
		return new ResourceLocation("pasterdream", "textures/block/weapon_workshop.png");
	}
}
