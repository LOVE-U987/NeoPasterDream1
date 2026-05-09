package net.pasterdream.block.renderer;

import software.bernie.geckolib.renderer.GeoItemRenderer;

import net.pasterdream.block.model.MeltdreamChestOpenDisplayModel;
import net.pasterdream.block.display.MeltdreamChestOpenDisplayItem;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

public class MeltdreamChestOpenDisplayItemRenderer extends GeoItemRenderer<MeltdreamChestOpenDisplayItem> {
	public MeltdreamChestOpenDisplayItemRenderer() {
		super(new MeltdreamChestOpenDisplayModel());
	}

	@Override
	public RenderType getRenderType(MeltdreamChestOpenDisplayItem animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
