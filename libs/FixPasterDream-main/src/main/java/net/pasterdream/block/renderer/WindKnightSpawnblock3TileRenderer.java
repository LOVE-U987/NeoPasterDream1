package net.pasterdream.block.renderer;

import software.bernie.geckolib.renderer.GeoBlockRenderer;

import net.pasterdream.block.model.WindKnightSpawnblock3BlockModel;
import net.pasterdream.block.entity.WindKnightSpawnblock3TileEntity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

public class WindKnightSpawnblock3TileRenderer extends GeoBlockRenderer<WindKnightSpawnblock3TileEntity> {
	public WindKnightSpawnblock3TileRenderer() {
		super(new WindKnightSpawnblock3BlockModel());
	}

	@Override
	public RenderType getRenderType(WindKnightSpawnblock3TileEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
