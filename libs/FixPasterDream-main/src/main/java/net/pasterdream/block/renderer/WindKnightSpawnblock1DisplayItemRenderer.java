package net.pasterdream.block.renderer;

import software.bernie.geckolib.renderer.GeoItemRenderer;

import net.pasterdream.block.model.WindKnightSpawnblock1DisplayModel;
import net.pasterdream.block.display.WindKnightSpawnblock1DisplayItem;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

public class WindKnightSpawnblock1DisplayItemRenderer extends GeoItemRenderer<WindKnightSpawnblock1DisplayItem> {
	public WindKnightSpawnblock1DisplayItemRenderer() {
		super(new WindKnightSpawnblock1DisplayModel());
	}

	@Override
	public RenderType getRenderType(WindKnightSpawnblock1DisplayItem animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
