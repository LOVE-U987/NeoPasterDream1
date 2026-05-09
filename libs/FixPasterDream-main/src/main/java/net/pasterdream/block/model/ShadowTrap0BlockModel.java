package net.pasterdream.block.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.block.entity.ShadowTrap0TileEntity;

import net.minecraft.resources.ResourceLocation;

public class ShadowTrap0BlockModel extends GeoModel<ShadowTrap0TileEntity> {
	@Override
	public ResourceLocation getAnimationResource(ShadowTrap0TileEntity animatable) {
		return new ResourceLocation("pasterdream", "animations/shadow_trap_0.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(ShadowTrap0TileEntity animatable) {
		return new ResourceLocation("pasterdream", "geo/shadow_trap_0.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(ShadowTrap0TileEntity entity) {
		return new ResourceLocation("pasterdream", "textures/block/shadow_trap_0.png");
	}
}
