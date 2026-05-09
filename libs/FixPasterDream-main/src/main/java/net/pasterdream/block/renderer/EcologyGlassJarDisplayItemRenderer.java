package net.pasterdream.block.renderer;

import software.bernie.geckolib.renderer.GeoItemRenderer;

import net.pasterdream.block.model.EcologyGlassJarDisplayModel;
import net.pasterdream.block.display.EcologyGlassJarDisplayItem;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

public class EcologyGlassJarDisplayItemRenderer extends GeoItemRenderer<EcologyGlassJarDisplayItem> {
	public EcologyGlassJarDisplayItemRenderer() {
		super(new EcologyGlassJarDisplayModel());
	}

	@Override
	public RenderType getRenderType(EcologyGlassJarDisplayItem animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
