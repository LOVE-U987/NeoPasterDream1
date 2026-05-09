package net.pasterdream.block.renderer;

import software.bernie.geckolib.renderer.GeoItemRenderer;

import net.pasterdream.block.model.FireflyGlassJarDisplayModel;
import net.pasterdream.block.display.FireflyGlassJarDisplayItem;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

public class FireflyGlassJarDisplayItemRenderer extends GeoItemRenderer<FireflyGlassJarDisplayItem> {
	public FireflyGlassJarDisplayItemRenderer() {
		super(new FireflyGlassJarDisplayModel());
	}

	@Override
	public RenderType getRenderType(FireflyGlassJarDisplayItem animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
