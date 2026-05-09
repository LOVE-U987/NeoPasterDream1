package net.pasterdream.block.renderer;

import software.bernie.geckolib.renderer.GeoItemRenderer;

import net.pasterdream.block.model.TheEndlessBookOfDreamSeekersDisplayModel;
import net.pasterdream.block.display.TheEndlessBookOfDreamSeekersDisplayItem;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

public class TheEndlessBookOfDreamSeekersDisplayItemRenderer extends GeoItemRenderer<TheEndlessBookOfDreamSeekersDisplayItem> {
	public TheEndlessBookOfDreamSeekersDisplayItemRenderer() {
		super(new TheEndlessBookOfDreamSeekersDisplayModel());
	}

	@Override
	public RenderType getRenderType(TheEndlessBookOfDreamSeekersDisplayItem animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
