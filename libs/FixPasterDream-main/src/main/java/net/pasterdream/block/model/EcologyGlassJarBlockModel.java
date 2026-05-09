package net.pasterdream.block.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.block.entity.EcologyGlassJarTileEntity;

import net.minecraft.resources.ResourceLocation;

public class EcologyGlassJarBlockModel extends GeoModel<EcologyGlassJarTileEntity> {
	@Override
	public ResourceLocation getAnimationResource(EcologyGlassJarTileEntity animatable) {
		return new ResourceLocation("pasterdream", "animations/ecology_glass_jar.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(EcologyGlassJarTileEntity animatable) {
		return new ResourceLocation("pasterdream", "geo/ecology_glass_jar.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(EcologyGlassJarTileEntity entity) {
		return new ResourceLocation("pasterdream", "textures/block/ecology_glass_jar.png");
	}
}
