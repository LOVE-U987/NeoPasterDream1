package net.pasterdream.block.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.block.display.ShadowBlastFurnaceDisplayItem;

import net.minecraft.resources.ResourceLocation;

public class ShadowBlastFurnaceDisplayModel extends GeoModel<ShadowBlastFurnaceDisplayItem> {
	@Override
	public ResourceLocation getAnimationResource(ShadowBlastFurnaceDisplayItem animatable) {
		return new ResourceLocation("pasterdream", "animations/shadow_blast_furnace.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(ShadowBlastFurnaceDisplayItem animatable) {
		return new ResourceLocation("pasterdream", "geo/shadow_blast_furnace.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(ShadowBlastFurnaceDisplayItem entity) {
		return new ResourceLocation("pasterdream", "textures/block/shadow_blast_furnace.png");
	}
}
