package net.pasterdream.block.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.block.entity.WindKnightSpawnblock2TileEntity;

import net.minecraft.resources.ResourceLocation;

public class WindKnightSpawnblock2BlockModel extends GeoModel<WindKnightSpawnblock2TileEntity> {
	@Override
	public ResourceLocation getAnimationResource(WindKnightSpawnblock2TileEntity animatable) {
		return new ResourceLocation("pasterdream", "animations/wind_knight_spawnblock_2.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(WindKnightSpawnblock2TileEntity animatable) {
		return new ResourceLocation("pasterdream", "geo/wind_knight_spawnblock_2.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(WindKnightSpawnblock2TileEntity entity) {
		return new ResourceLocation("pasterdream", "textures/block/wind_knight_spawnblock.png");
	}
}
