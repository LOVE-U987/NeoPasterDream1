package net.pasterdream.block.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.block.display.EcologyGlassJarDisplayItem;

import net.minecraft.resources.ResourceLocation;

public class EcologyGlassJarDisplayModel extends GeoModel<EcologyGlassJarDisplayItem> {
	@Override
	public ResourceLocation getAnimationResource(EcologyGlassJarDisplayItem animatable) {
		return new ResourceLocation("pasterdream", "animations/ecology_glass_jar.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(EcologyGlassJarDisplayItem animatable) {
		return new ResourceLocation("pasterdream", "geo/ecology_glass_jar.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(EcologyGlassJarDisplayItem entity) {
		return new ResourceLocation("pasterdream", "textures/block/ecology_glass_jar.png");
	}
}
