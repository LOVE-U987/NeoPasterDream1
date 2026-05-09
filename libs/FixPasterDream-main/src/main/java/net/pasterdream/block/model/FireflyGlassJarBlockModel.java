package net.pasterdream.block.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.block.entity.FireflyGlassJarTileEntity;

import net.minecraft.resources.ResourceLocation;

public class FireflyGlassJarBlockModel extends GeoModel<FireflyGlassJarTileEntity> {
	@Override
	public ResourceLocation getAnimationResource(FireflyGlassJarTileEntity animatable) {
		return new ResourceLocation("pasterdream", "animations/ecology_glass_jar.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(FireflyGlassJarTileEntity animatable) {
		return new ResourceLocation("pasterdream", "geo/ecology_glass_jar.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(FireflyGlassJarTileEntity entity) {
		return new ResourceLocation("pasterdream", "textures/block/ecology_glass_jar.png");
	}
}
