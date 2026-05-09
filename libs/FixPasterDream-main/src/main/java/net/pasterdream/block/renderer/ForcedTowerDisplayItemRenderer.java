package net.pasterdream.block.renderer;

import software.bernie.geckolib.renderer.GeoItemRenderer;

import net.pasterdream.block.model.ForcedTowerDisplayModel;
import net.pasterdream.block.display.ForcedTowerDisplayItem;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

public class ForcedTowerDisplayItemRenderer extends GeoItemRenderer<ForcedTowerDisplayItem> {
	public ForcedTowerDisplayItemRenderer() {
		super(new ForcedTowerDisplayModel());
	}

	@Override
	public RenderType getRenderType(ForcedTowerDisplayItem animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
