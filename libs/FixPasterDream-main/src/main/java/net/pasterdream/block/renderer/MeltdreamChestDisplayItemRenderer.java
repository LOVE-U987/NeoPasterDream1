package net.pasterdream.block.renderer;

import software.bernie.geckolib.renderer.GeoItemRenderer;

import net.pasterdream.block.model.MeltdreamChestDisplayModel;
import net.pasterdream.block.display.MeltdreamChestDisplayItem;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

public class MeltdreamChestDisplayItemRenderer extends GeoItemRenderer<MeltdreamChestDisplayItem> {
	public MeltdreamChestDisplayItemRenderer() {
		super(new MeltdreamChestDisplayModel());
	}

	@Override
	public RenderType getRenderType(MeltdreamChestDisplayItem animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
