package net.pasterdream.block.renderer;

import software.bernie.geckolib.renderer.GeoItemRenderer;

import net.pasterdream.block.model.ShadowBrazierDisplayModel;
import net.pasterdream.block.display.ShadowBrazierDisplayItem;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

public class ShadowBrazierDisplayItemRenderer extends GeoItemRenderer<ShadowBrazierDisplayItem> {
	public ShadowBrazierDisplayItemRenderer() {
		super(new ShadowBrazierDisplayModel());
	}

	@Override
	public RenderType getRenderType(ShadowBrazierDisplayItem animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
