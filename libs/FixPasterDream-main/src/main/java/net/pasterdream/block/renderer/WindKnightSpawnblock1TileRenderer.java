package net.pasterdream.block.renderer;

import software.bernie.geckolib.renderer.GeoBlockRenderer;

import net.pasterdream.block.model.WindKnightSpawnblock1BlockModel;
import net.pasterdream.block.entity.WindKnightSpawnblock1TileEntity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

public class WindKnightSpawnblock1TileRenderer extends GeoBlockRenderer<WindKnightSpawnblock1TileEntity> {
	public WindKnightSpawnblock1TileRenderer() {
		super(new WindKnightSpawnblock1BlockModel());
	}

	@Override
	public RenderType getRenderType(WindKnightSpawnblock1TileEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
