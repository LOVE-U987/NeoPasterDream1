package net.pasterdream.block.model;

import software.bernie.geckolib.model.GeoModel;

import net.pasterdream.block.entity.WindKnightSpawnblock1TileEntity;

import net.minecraft.resources.ResourceLocation;

public class WindKnightSpawnblock1BlockModel extends GeoModel<WindKnightSpawnblock1TileEntity> {
	@Override
	public ResourceLocation getAnimationResource(WindKnightSpawnblock1TileEntity animatable) {
		return new ResourceLocation("pasterdream", "animations/wind_knight_spawnblock_1.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(WindKnightSpawnblock1TileEntity animatable) {
		return new ResourceLocation("pasterdream", "geo/wind_knight_spawnblock_1.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(WindKnightSpawnblock1TileEntity entity) {
		return new ResourceLocation("pasterdream", "textures/block/wind_knight_spawnblock.png");
	}
}
