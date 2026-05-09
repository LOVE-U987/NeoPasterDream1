package net.pasterdream.block.renderer;

import software.bernie.geckolib.renderer.GeoItemRenderer;

import net.pasterdream.block.model.DreamCauldronDisplayModel;
import net.pasterdream.block.display.DreamCauldronDisplayItem;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

public class DreamCauldronDisplayItemRenderer extends GeoItemRenderer<DreamCauldronDisplayItem> {
	public DreamCauldronDisplayItemRenderer() {
		super(new DreamCauldronDisplayModel());
	}

	@Override
	public RenderType getRenderType(DreamCauldronDisplayItem animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
