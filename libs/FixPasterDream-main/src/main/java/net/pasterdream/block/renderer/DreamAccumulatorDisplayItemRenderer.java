package net.pasterdream.block.renderer;

import software.bernie.geckolib.renderer.GeoItemRenderer;

import net.pasterdream.block.model.DreamAccumulatorDisplayModel;
import net.pasterdream.block.display.DreamAccumulatorDisplayItem;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

public class DreamAccumulatorDisplayItemRenderer extends GeoItemRenderer<DreamAccumulatorDisplayItem> {
	public DreamAccumulatorDisplayItemRenderer() {
		super(new DreamAccumulatorDisplayModel());
	}

	@Override
	public RenderType getRenderType(DreamAccumulatorDisplayItem animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
