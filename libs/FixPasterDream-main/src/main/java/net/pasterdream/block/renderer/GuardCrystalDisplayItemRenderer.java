package net.pasterdream.block.renderer;

import software.bernie.geckolib.renderer.GeoItemRenderer;

import net.pasterdream.block.model.GuardCrystalDisplayModel;
import net.pasterdream.block.display.GuardCrystalDisplayItem;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

public class GuardCrystalDisplayItemRenderer extends GeoItemRenderer<GuardCrystalDisplayItem> {
	public GuardCrystalDisplayItemRenderer() {
		super(new GuardCrystalDisplayModel());
	}

	@Override
	public RenderType getRenderType(GuardCrystalDisplayItem animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
