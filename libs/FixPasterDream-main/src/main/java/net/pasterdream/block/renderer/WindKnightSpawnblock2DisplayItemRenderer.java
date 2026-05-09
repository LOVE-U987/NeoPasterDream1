package net.pasterdream.block.renderer;

import software.bernie.geckolib.renderer.GeoItemRenderer;

import net.pasterdream.block.model.WindKnightSpawnblock2DisplayModel;
import net.pasterdream.block.display.WindKnightSpawnblock2DisplayItem;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

public class WindKnightSpawnblock2DisplayItemRenderer extends GeoItemRenderer<WindKnightSpawnblock2DisplayItem> {
	public WindKnightSpawnblock2DisplayItemRenderer() {
		super(new WindKnightSpawnblock2DisplayModel());
	}

	@Override
	public RenderType getRenderType(WindKnightSpawnblock2DisplayItem animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
