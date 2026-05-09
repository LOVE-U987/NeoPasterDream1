package net.pasterdream.block.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.block.entity.WindKnightSpawnblock4TileEntity;

import net.minecraft.resources.ResourceLocation;

public class WindKnightSpawnblock4BlockModel extends GeoModel<WindKnightSpawnblock4TileEntity> {
	@Override
	public ResourceLocation getAnimationResource(WindKnightSpawnblock4TileEntity animatable) {
		return new ResourceLocation("pasterdream", "animations/wind_knight_spawnblock_4.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(WindKnightSpawnblock4TileEntity animatable) {
		return new ResourceLocation("pasterdream", "geo/wind_knight_spawnblock_4.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(WindKnightSpawnblock4TileEntity entity) {
		return new ResourceLocation("pasterdream", "textures/block/wind_knight_spawnblock.png");
	}
}
