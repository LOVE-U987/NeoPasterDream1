package net.pasterdream.block.renderer;

import software.bernie.geckolib.renderer.GeoBlockRenderer;

import net.pasterdream.block.model.TwilightLanternBlockModel;
import net.pasterdream.block.entity.TwilightLanternTileEntity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

public class TwilightLanternTileRenderer extends GeoBlockRenderer<TwilightLanternTileEntity> {
	public TwilightLanternTileRenderer() {
		super(new TwilightLanternBlockModel());
	}

	@Override
	public RenderType getRenderType(TwilightLanternTileEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
