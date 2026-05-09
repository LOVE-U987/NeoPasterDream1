package net.pasterdream.block.renderer;

import software.bernie.geckolib.renderer.GeoItemRenderer;

import net.pasterdream.block.model.TwilightLanternDisplayModel;
import net.pasterdream.block.display.TwilightLanternDisplayItem;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

public class TwilightLanternDisplayItemRenderer extends GeoItemRenderer<TwilightLanternDisplayItem> {
	public TwilightLanternDisplayItemRenderer() {
		super(new TwilightLanternDisplayModel());
	}

	@Override
	public RenderType getRenderType(TwilightLanternDisplayItem animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
