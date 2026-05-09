package net.pasterdream.block.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.block.entity.WeaponTableTileEntity;

import net.minecraft.resources.ResourceLocation;

public class WeaponTableBlockModel extends GeoModel<WeaponTableTileEntity> {
	@Override
	public ResourceLocation getAnimationResource(WeaponTableTileEntity animatable) {
		return new ResourceLocation("pasterdream", "animations/weapon_table.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(WeaponTableTileEntity animatable) {
		return new ResourceLocation("pasterdream", "geo/weapon_table.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(WeaponTableTileEntity entity) {
		return new ResourceLocation("pasterdream", "textures/block/weapon_table.png");
	}
}
