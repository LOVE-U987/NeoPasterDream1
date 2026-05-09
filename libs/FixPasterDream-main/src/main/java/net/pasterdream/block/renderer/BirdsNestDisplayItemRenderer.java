package net.pasterdream.block.renderer;

import software.bernie.geckolib.renderer.GeoItemRenderer;

import net.pasterdream.block.model.BirdsNestDisplayModel;
import net.pasterdream.block.display.BirdsNestDisplayItem;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

public class BirdsNestDisplayItemRenderer extends GeoItemRenderer<BirdsNestDisplayItem> {
	public BirdsNestDisplayItemRenderer() {
		super(new BirdsNestDisplayModel());
	}

	@Override
	public RenderType getRenderType(BirdsNestDisplayItem animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
