package net.pasterdream.block.renderer;

import software.bernie.geckolib.renderer.GeoItemRenderer;

import net.pasterdream.block.model.ShadowVortexDisplayModel;
import net.pasterdream.block.display.ShadowVortexDisplayItem;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

public class ShadowVortexDisplayItemRenderer extends GeoItemRenderer<ShadowVortexDisplayItem> {
	public ShadowVortexDisplayItemRenderer() {
		super(new ShadowVortexDisplayModel());
	}

	@Override
	public RenderType getRenderType(ShadowVortexDisplayItem animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
