package net.pasterdream.block.renderer;

import software.bernie.geckolib.renderer.GeoItemRenderer;

import net.pasterdream.block.model.ShadowDungeonPortalDisplayModel;
import net.pasterdream.block.display.ShadowDungeonPortalDisplayItem;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

public class ShadowDungeonPortalDisplayItemRenderer extends GeoItemRenderer<ShadowDungeonPortalDisplayItem> {
	public ShadowDungeonPortalDisplayItemRenderer() {
		super(new ShadowDungeonPortalDisplayModel());
	}

	@Override
	public RenderType getRenderType(ShadowDungeonPortalDisplayItem animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
