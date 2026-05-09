package net.pasterdream.block.renderer;

import software.bernie.geckolib.renderer.GeoBlockRenderer;

import net.pasterdream.block.model.ShadowVortexBlockModel;
import net.pasterdream.block.entity.ShadowVortexTileEntity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

public class ShadowVortexTileRenderer extends GeoBlockRenderer<ShadowVortexTileEntity> {
	public ShadowVortexTileRenderer() {
		super(new ShadowVortexBlockModel());
	}

	@Override
	public RenderType getRenderType(ShadowVortexTileEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
