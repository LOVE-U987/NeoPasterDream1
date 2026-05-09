package net.pasterdream.block.renderer;

import software.bernie.geckolib.renderer.GeoItemRenderer;

import net.pasterdream.block.model.UuzDoll0DisplayModel;
import net.pasterdream.block.display.UuzDoll0DisplayItem;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

public class UuzDoll0DisplayItemRenderer extends GeoItemRenderer<UuzDoll0DisplayItem> {
	public UuzDoll0DisplayItemRenderer() {
		super(new UuzDoll0DisplayModel());
	}

	@Override
	public RenderType getRenderType(UuzDoll0DisplayItem animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
