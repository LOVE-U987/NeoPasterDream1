package net.pasterdream.block.renderer;

import software.bernie.geckolib.renderer.GeoItemRenderer;

import net.pasterdream.block.model.PicnicBasketDisplayModel;
import net.pasterdream.block.display.PicnicBasketDisplayItem;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

public class PicnicBasketDisplayItemRenderer extends GeoItemRenderer<PicnicBasketDisplayItem> {
	public PicnicBasketDisplayItemRenderer() {
		super(new PicnicBasketDisplayModel());
	}

	@Override
	public RenderType getRenderType(PicnicBasketDisplayItem animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
