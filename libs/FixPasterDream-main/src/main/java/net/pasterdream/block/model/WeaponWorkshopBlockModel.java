package net.pasterdream.block.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.block.entity.WeaponWorkshopTileEntity;

import net.minecraft.resources.ResourceLocation;

public class WeaponWorkshopBlockModel extends GeoModel<WeaponWorkshopTileEntity> {
	@Override
	public ResourceLocation getAnimationResource(WeaponWorkshopTileEntity animatable) {
		return new ResourceLocation("pasterdream", "animations/weapon_workshop.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(WeaponWorkshopTileEntity animatable) {
		return new ResourceLocation("pasterdream", "geo/weapon_workshop.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(WeaponWorkshopTileEntity entity) {
		return new ResourceLocation("pasterdream", "textures/block/weapon_workshop.png");
	}
}
