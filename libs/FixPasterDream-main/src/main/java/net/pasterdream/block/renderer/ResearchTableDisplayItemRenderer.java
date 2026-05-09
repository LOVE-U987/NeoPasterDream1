package net.pasterdream.block.renderer;

import software.bernie.geckolib.renderer.GeoItemRenderer;

import net.pasterdream.block.model.ResearchTableDisplayModel;
import net.pasterdream.block.display.ResearchTableDisplayItem;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

public class ResearchTableDisplayItemRenderer extends GeoItemRenderer<ResearchTableDisplayItem> {
	public ResearchTableDisplayItemRenderer() {
		super(new ResearchTableDisplayModel());
	}

	@Override
	public RenderType getRenderType(ResearchTableDisplayItem animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
