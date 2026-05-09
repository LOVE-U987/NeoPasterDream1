package net.pasterdream.block.renderer;

import software.bernie.geckolib.renderer.GeoBlockRenderer;

import net.pasterdream.block.model.DreamAccumulatorBlockModel;
import net.pasterdream.block.entity.DreamAccumulatorTileEntity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

public class DreamAccumulatorTileRenderer extends GeoBlockRenderer<DreamAccumulatorTileEntity> {
	public DreamAccumulatorTileRenderer() {
		super(new DreamAccumulatorBlockModel());
	}

	@Override
	public RenderType getRenderType(DreamAccumulatorTileEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
