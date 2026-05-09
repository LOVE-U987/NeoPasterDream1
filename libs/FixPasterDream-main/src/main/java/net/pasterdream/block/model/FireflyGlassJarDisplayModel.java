package net.pasterdream.block.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.block.display.FireflyGlassJarDisplayItem;

import net.minecraft.resources.ResourceLocation;

public class FireflyGlassJarDisplayModel extends GeoModel<FireflyGlassJarDisplayItem> {
	@Override
	public ResourceLocation getAnimationResource(FireflyGlassJarDisplayItem animatable) {
		return new ResourceLocation("pasterdream", "animations/ecology_glass_jar.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(FireflyGlassJarDisplayItem animatable) {
		return new ResourceLocation("pasterdream", "geo/ecology_glass_jar.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(FireflyGlassJarDisplayItem entity) {
		return new ResourceLocation("pasterdream", "textures/block/ecology_glass_jar.png");
	}
}
