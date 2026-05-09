package net.pasterdream.block.renderer;

import software.bernie.geckolib.renderer.GeoItemRenderer;

import net.pasterdream.block.model.ShadowBlastFurnaceDisplayModel;
import net.pasterdream.block.display.ShadowBlastFurnaceDisplayItem;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

public class ShadowBlastFurnaceDisplayItemRenderer extends GeoItemRenderer<ShadowBlastFurnaceDisplayItem> {
	public ShadowBlastFurnaceDisplayItemRenderer() {
		super(new ShadowBlastFurnaceDisplayModel());
	}

	@Override
	public RenderType getRenderType(ShadowBlastFurnaceDisplayItem animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
