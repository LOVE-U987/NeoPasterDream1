package net.pasterdream.block.renderer;

import software.bernie.geckolib.renderer.GeoBlockRenderer;

import net.pasterdream.block.model.WindKnightSpawnblock2BlockModel;
import net.pasterdream.block.entity.WindKnightSpawnblock2TileEntity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

public class WindKnightSpawnblock2TileRenderer extends GeoBlockRenderer<WindKnightSpawnblock2TileEntity> {
	public WindKnightSpawnblock2TileRenderer() {
		super(new WindKnightSpawnblock2BlockModel());
	}

	@Override
	public RenderType getRenderType(WindKnightSpawnblock2TileEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
