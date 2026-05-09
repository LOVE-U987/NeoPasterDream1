package net.pasterdream.block.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.block.entity.TheEndlessBookOfDreamSeekersTileEntity;

import net.minecraft.resources.ResourceLocation;

public class TheEndlessBookOfDreamSeekersBlockModel extends GeoModel<TheEndlessBookOfDreamSeekersTileEntity> {
	@Override
	public ResourceLocation getAnimationResource(TheEndlessBookOfDreamSeekersTileEntity animatable) {
		return new ResourceLocation("pasterdream", "animations/the_endless_book_of_dream_seekers.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(TheEndlessBookOfDreamSeekersTileEntity animatable) {
		return new ResourceLocation("pasterdream", "geo/the_endless_book_of_dream_seekers.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(TheEndlessBookOfDreamSeekersTileEntity entity) {
		return new ResourceLocation("pasterdream", "textures/block/the_endless_book_of_dream_seekers.png");
	}
}
