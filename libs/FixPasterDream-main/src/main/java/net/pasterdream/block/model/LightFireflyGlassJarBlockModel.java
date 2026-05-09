package net.pasterdream.block.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.block.entity.LightFireflyGlassJarTileEntity;

import net.minecraft.resources.ResourceLocation;

public class LightFireflyGlassJarBlockModel extends GeoModel<LightFireflyGlassJarTileEntity> {
	@Override
	public ResourceLocation getAnimationResource(LightFireflyGlassJarTileEntity animatable) {
		return new ResourceLocation("pasterdream", "animations/ecology_glass_jar.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(LightFireflyGlassJarTileEntity animatable) {
		return new ResourceLocation("pasterdream", "geo/ecology_glass_jar.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(LightFireflyGlassJarTileEntity entity) {
		return new ResourceLocation("pasterdream", "textures/block/ecology_glass_jar.png");
	}
}
