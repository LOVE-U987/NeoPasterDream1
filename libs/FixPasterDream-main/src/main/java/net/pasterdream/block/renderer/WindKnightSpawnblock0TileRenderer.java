package net.pasterdream.block.renderer;

import software.bernie.geckolib.renderer.GeoBlockRenderer;

import net.pasterdream.block.model.WindKnightSpawnblock0BlockModel;
import net.pasterdream.block.entity.WindKnightSpawnblock0TileEntity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

public class WindKnightSpawnblock0TileRenderer extends GeoBlockRenderer<WindKnightSpawnblock0TileEntity> {
	public WindKnightSpawnblock0TileRenderer() {
		super(new WindKnightSpawnblock0BlockModel());
	}

	@Override
	public RenderType getRenderType(WindKnightSpawnblock0TileEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
