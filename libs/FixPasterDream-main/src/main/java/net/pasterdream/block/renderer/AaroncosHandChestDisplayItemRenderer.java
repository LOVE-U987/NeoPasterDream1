package net.pasterdream.block.renderer;

import software.bernie.geckolib.renderer.GeoItemRenderer;

import net.pasterdream.block.model.AaroncosHandChestDisplayModel;
import net.pasterdream.block.display.AaroncosHandChestDisplayItem;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

public class AaroncosHandChestDisplayItemRenderer extends GeoItemRenderer<AaroncosHandChestDisplayItem> {
	public AaroncosHandChestDisplayItemRenderer() {
		super(new AaroncosHandChestDisplayModel());
	}

	@Override
	public RenderType getRenderType(AaroncosHandChestDisplayItem animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
