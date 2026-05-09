package net.pasterdream.block.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.block.display.LightFireflyGlassJarDisplayItem;

import net.minecraft.resources.ResourceLocation;

public class LightFireflyGlassJarDisplayModel extends GeoModel<LightFireflyGlassJarDisplayItem> {
	@Override
	public ResourceLocation getAnimationResource(LightFireflyGlassJarDisplayItem animatable) {
		return new ResourceLocation("pasterdream", "animations/ecology_glass_jar.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(LightFireflyGlassJarDisplayItem animatable) {
		return new ResourceLocation("pasterdream", "geo/ecology_glass_jar.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(LightFireflyGlassJarDisplayItem entity) {
		return new ResourceLocation("pasterdream", "textures/block/ecology_glass_jar.png");
	}
}
