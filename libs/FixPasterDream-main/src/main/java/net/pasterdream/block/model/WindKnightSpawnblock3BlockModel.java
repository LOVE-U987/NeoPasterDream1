package net.pasterdream.block.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.block.entity.WindKnightSpawnblock3TileEntity;

import net.minecraft.resources.ResourceLocation;

public class WindKnightSpawnblock3BlockModel extends GeoModel<WindKnightSpawnblock3TileEntity> {
	@Override
	public ResourceLocation getAnimationResource(WindKnightSpawnblock3TileEntity animatable) {
		return new ResourceLocation("pasterdream", "animations/wind_knight_spawnblock_3.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(WindKnightSpawnblock3TileEntity animatable) {
		return new ResourceLocation("pasterdream", "geo/wind_knight_spawnblock_3.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(WindKnightSpawnblock3TileEntity entity) {
		return new ResourceLocation("pasterdream", "textures/block/wind_knight_spawnblock.png");
	}
}
