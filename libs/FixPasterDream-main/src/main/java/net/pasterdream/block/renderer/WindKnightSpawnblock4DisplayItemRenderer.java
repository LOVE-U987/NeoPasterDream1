package net.pasterdream.block.renderer;

import software.bernie.geckolib.renderer.GeoItemRenderer;

import net.pasterdream.block.model.WindKnightSpawnblock4DisplayModel;
import net.pasterdream.block.display.WindKnightSpawnblock4DisplayItem;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

public class WindKnightSpawnblock4DisplayItemRenderer extends GeoItemRenderer<WindKnightSpawnblock4DisplayItem> {
	public WindKnightSpawnblock4DisplayItemRenderer() {
		super(new WindKnightSpawnblock4DisplayModel());
	}

	@Override
	public RenderType getRenderType(WindKnightSpawnblock4DisplayItem animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
