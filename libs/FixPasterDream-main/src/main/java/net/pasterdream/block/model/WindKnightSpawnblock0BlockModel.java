package net.pasterdream.block.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.block.entity.WindKnightSpawnblock0TileEntity;

import net.minecraft.resources.ResourceLocation;

public class WindKnightSpawnblock0BlockModel extends GeoModel<WindKnightSpawnblock0TileEntity> {
	@Override
	public ResourceLocation getAnimationResource(WindKnightSpawnblock0TileEntity animatable) {
		return new ResourceLocation("pasterdream", "animations/wind_knight_spawnblock_0.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(WindKnightSpawnblock0TileEntity animatable) {
		return new ResourceLocation("pasterdream", "geo/wind_knight_spawnblock_0.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(WindKnightSpawnblock0TileEntity entity) {
		return new ResourceLocation("pasterdream", "textures/block/wind_knight_spawnblock.png");
	}
}
