package net.pasterdream.block.renderer;

import software.bernie.geckolib.renderer.GeoItemRenderer;

import net.pasterdream.block.model.DesertHeroTombDisplayModel;
import net.pasterdream.block.display.DesertHeroTombDisplayItem;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

public class DesertHeroTombDisplayItemRenderer extends GeoItemRenderer<DesertHeroTombDisplayItem> {
	public DesertHeroTombDisplayItemRenderer() {
		super(new DesertHeroTombDisplayModel());
	}

	@Override
	public RenderType getRenderType(DesertHeroTombDisplayItem animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
