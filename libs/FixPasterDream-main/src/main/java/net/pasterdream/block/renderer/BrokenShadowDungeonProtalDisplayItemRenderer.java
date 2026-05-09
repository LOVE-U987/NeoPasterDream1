package net.pasterdream.block.renderer;

import software.bernie.geckolib.renderer.GeoItemRenderer;

import net.pasterdream.block.model.BrokenShadowDungeonProtalDisplayModel;
import net.pasterdream.block.display.BrokenShadowDungeonProtalDisplayItem;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

public class BrokenShadowDungeonProtalDisplayItemRenderer extends GeoItemRenderer<BrokenShadowDungeonProtalDisplayItem> {
	public BrokenShadowDungeonProtalDisplayItemRenderer() {
		super(new BrokenShadowDungeonProtalDisplayModel());
	}

	@Override
	public RenderType getRenderType(BrokenShadowDungeonProtalDisplayItem animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
