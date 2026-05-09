package net.pasterdream.block.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.block.display.TheEndlessBookOfDreamSeekersDisplayItem;

import net.minecraft.resources.ResourceLocation;

public class TheEndlessBookOfDreamSeekersDisplayModel extends GeoModel<TheEndlessBookOfDreamSeekersDisplayItem> {
	@Override
	public ResourceLocation getAnimationResource(TheEndlessBookOfDreamSeekersDisplayItem animatable) {
		return new ResourceLocation("pasterdream", "animations/the_endless_book_of_dream_seekers.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(TheEndlessBookOfDreamSeekersDisplayItem animatable) {
		return new ResourceLocation("pasterdream", "geo/the_endless_book_of_dream_seekers.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(TheEndlessBookOfDreamSeekersDisplayItem entity) {
		return new ResourceLocation("pasterdream", "textures/block/the_endless_book_of_dream_seekers.png");
	}
}
