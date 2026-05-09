package net.pasterdream.block.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.block.entity.ResearchTableTileEntity;

import net.minecraft.resources.ResourceLocation;

public class ResearchTableBlockModel extends GeoModel<ResearchTableTileEntity> {
	@Override
	public ResourceLocation getAnimationResource(ResearchTableTileEntity animatable) {
		return new ResourceLocation("pasterdream", "animations/research_table.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(ResearchTableTileEntity animatable) {
		return new ResourceLocation("pasterdream", "geo/research_table.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(ResearchTableTileEntity entity) {
		return new ResourceLocation("pasterdream", "textures/block/research_table.png");
	}
}
