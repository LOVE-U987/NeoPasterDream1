package net.pasterdream.block.renderer;

import software.bernie.geckolib.renderer.GeoItemRenderer;

import net.pasterdream.block.model.GoldenFoxSculptureDisplayModel;
import net.pasterdream.block.display.GoldenFoxSculptureDisplayItem;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

public class GoldenFoxSculptureDisplayItemRenderer extends GeoItemRenderer<GoldenFoxSculptureDisplayItem> {
	public GoldenFoxSculptureDisplayItemRenderer() {
		super(new GoldenFoxSculptureDisplayModel());
	}

	@Override
	public RenderType getRenderType(GoldenFoxSculptureDisplayItem animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
