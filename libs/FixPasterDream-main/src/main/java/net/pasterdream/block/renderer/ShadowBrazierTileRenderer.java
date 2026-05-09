package net.pasterdream.block.renderer;

import software.bernie.geckolib.renderer.GeoBlockRenderer;

import net.pasterdream.block.model.ShadowBrazierBlockModel;
import net.pasterdream.block.entity.ShadowBrazierTileEntity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

public class ShadowBrazierTileRenderer extends GeoBlockRenderer<ShadowBrazierTileEntity> {
	public ShadowBrazierTileRenderer() {
		super(new ShadowBrazierBlockModel());
	}

	@Override
	public RenderType getRenderType(ShadowBrazierTileEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
