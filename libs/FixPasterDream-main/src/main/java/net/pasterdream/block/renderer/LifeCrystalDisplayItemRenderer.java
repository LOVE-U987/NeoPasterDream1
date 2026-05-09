package net.pasterdream.block.renderer;

import software.bernie.geckolib.renderer.GeoItemRenderer;

import net.pasterdream.block.model.LifeCrystalDisplayModel;
import net.pasterdream.block.display.LifeCrystalDisplayItem;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

public class LifeCrystalDisplayItemRenderer extends GeoItemRenderer<LifeCrystalDisplayItem> {
	public LifeCrystalDisplayItemRenderer() {
		super(new LifeCrystalDisplayModel());
	}

	@Override
	public RenderType getRenderType(LifeCrystalDisplayItem animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
