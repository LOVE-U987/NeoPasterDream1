package net.pasterdream.block.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.block.display.WeaponTableDisplayItem;

import net.minecraft.resources.ResourceLocation;

public class WeaponTableDisplayModel extends GeoModel<WeaponTableDisplayItem> {
	@Override
	public ResourceLocation getAnimationResource(WeaponTableDisplayItem animatable) {
		return new ResourceLocation("pasterdream", "animations/weapon_table.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(WeaponTableDisplayItem animatable) {
		return new ResourceLocation("pasterdream", "geo/weapon_table.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(WeaponTableDisplayItem entity) {
		return new ResourceLocation("pasterdream", "textures/block/weapon_table.png");
	}
}
