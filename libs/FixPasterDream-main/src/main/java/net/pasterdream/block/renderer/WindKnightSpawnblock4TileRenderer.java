package net.pasterdream.block.renderer;

import software.bernie.geckolib.renderer.GeoBlockRenderer;

import net.pasterdream.block.model.WindKnightSpawnblock4BlockModel;
import net.pasterdream.block.entity.WindKnightSpawnblock4TileEntity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

public class WindKnightSpawnblock4TileRenderer extends GeoBlockRenderer<WindKnightSpawnblock4TileEntity> {
	public WindKnightSpawnblock4TileRenderer() {
		super(new WindKnightSpawnblock4BlockModel());
	}

	@Override
	public RenderType getRenderType(WindKnightSpawnblock4TileEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
