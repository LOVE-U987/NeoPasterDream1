package net.pasterdream.block.renderer;

import software.bernie.geckolib.renderer.GeoItemRenderer;

import net.pasterdream.block.model.ShadowChestDisplayModel;
import net.pasterdream.block.display.ShadowChestDisplayItem;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

public class ShadowChestDisplayItemRenderer extends GeoItemRenderer<ShadowChestDisplayItem> {
	public ShadowChestDisplayItemRenderer() {
		super(new ShadowChestDisplayModel());
	}

	@Override
	public RenderType getRenderType(ShadowChestDisplayItem animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
