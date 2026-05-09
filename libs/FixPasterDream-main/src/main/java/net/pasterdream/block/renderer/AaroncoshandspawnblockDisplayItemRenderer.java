package net.pasterdream.block.renderer;

import software.bernie.geckolib.renderer.GeoItemRenderer;

import net.pasterdream.block.model.AaroncoshandspawnblockDisplayModel;
import net.pasterdream.block.display.AaroncoshandspawnblockDisplayItem;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

public class AaroncoshandspawnblockDisplayItemRenderer extends GeoItemRenderer<AaroncoshandspawnblockDisplayItem> {
	public AaroncoshandspawnblockDisplayItemRenderer() {
		super(new AaroncoshandspawnblockDisplayModel());
	}

	@Override
	public RenderType getRenderType(AaroncoshandspawnblockDisplayItem animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
