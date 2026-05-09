package net.pasterdream.block.renderer;

import software.bernie.geckolib.renderer.GeoItemRenderer;

import net.pasterdream.block.model.WindKnightSpawnblock0DisplayModel;
import net.pasterdream.block.display.WindKnightSpawnblock0DisplayItem;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

public class WindKnightSpawnblock0DisplayItemRenderer extends GeoItemRenderer<WindKnightSpawnblock0DisplayItem> {
	public WindKnightSpawnblock0DisplayItemRenderer() {
		super(new WindKnightSpawnblock0DisplayModel());
	}

	@Override
	public RenderType getRenderType(WindKnightSpawnblock0DisplayItem animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
