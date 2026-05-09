package net.pasterdream.item.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.item.MachineWingItem;

import net.minecraft.resources.ResourceLocation;

public class MachineWingModel extends GeoModel<MachineWingItem> {
	@Override
	public ResourceLocation getAnimationResource(MachineWingItem object) {
		return new ResourceLocation("pasterdream", "animations/machine_wing.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(MachineWingItem object) {
		return new ResourceLocation("pasterdream", "geo/machine_wing.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(MachineWingItem object) {
		return new ResourceLocation("pasterdream", "textures/item/machine_wing.png");
	}
}
