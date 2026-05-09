package net.pasterdream.block.renderer;

import software.bernie.geckolib.renderer.GeoItemRenderer;

import net.pasterdream.block.model.LightFireflyGlassJarDisplayModel;
import net.pasterdream.block.display.LightFireflyGlassJarDisplayItem;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

public class LightFireflyGlassJarDisplayItemRenderer extends GeoItemRenderer<LightFireflyGlassJarDisplayItem> {
	public LightFireflyGlassJarDisplayItemRenderer() {
		super(new LightFireflyGlassJarDisplayModel());
	}

	@Override
	public RenderType getRenderType(LightFireflyGlassJarDisplayItem animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
