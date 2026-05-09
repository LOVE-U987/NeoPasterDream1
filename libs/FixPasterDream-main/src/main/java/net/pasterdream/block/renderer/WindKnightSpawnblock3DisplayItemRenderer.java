package net.pasterdream.block.renderer;

import software.bernie.geckolib.renderer.GeoItemRenderer;

import net.pasterdream.block.model.WindKnightSpawnblock3DisplayModel;
import net.pasterdream.block.display.WindKnightSpawnblock3DisplayItem;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

public class WindKnightSpawnblock3DisplayItemRenderer extends GeoItemRenderer<WindKnightSpawnblock3DisplayItem> {
	public WindKnightSpawnblock3DisplayItemRenderer() {
		super(new WindKnightSpawnblock3DisplayModel());
	}

	@Override
	public RenderType getRenderType(WindKnightSpawnblock3DisplayItem animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
