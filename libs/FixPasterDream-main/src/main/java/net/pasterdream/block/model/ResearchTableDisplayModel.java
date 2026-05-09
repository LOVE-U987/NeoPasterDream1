package net.pasterdream.block.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.block.display.ResearchTableDisplayItem;

import net.minecraft.resources.ResourceLocation;

public class ResearchTableDisplayModel extends GeoModel<ResearchTableDisplayItem> {
	@Override
	public ResourceLocation getAnimationResource(ResearchTableDisplayItem animatable) {
		return new ResourceLocation("pasterdream", "animations/research_table.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(ResearchTableDisplayItem animatable) {
		return new ResourceLocation("pasterdream", "geo/research_table.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(ResearchTableDisplayItem entity) {
		return new ResourceLocation("pasterdream", "textures/block/research_table.png");
	}
}
