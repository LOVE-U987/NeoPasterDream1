package net.pasterdream.block.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.block.display.ShadowTrap0DisplayItem;

import net.minecraft.resources.ResourceLocation;

public class ShadowTrap0DisplayModel extends GeoModel<ShadowTrap0DisplayItem> {
	@Override
	public ResourceLocation getAnimationResource(ShadowTrap0DisplayItem animatable) {
		return new ResourceLocation("pasterdream", "animations/shadow_trap_0.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(ShadowTrap0DisplayItem animatable) {
		return new ResourceLocation("pasterdream", "geo/shadow_trap_0.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(ShadowTrap0DisplayItem entity) {
		return new ResourceLocation("pasterdream", "textures/block/shadow_trap_0.png");
	}
}
