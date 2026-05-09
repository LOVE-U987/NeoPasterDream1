package net.pasterdream.block.renderer;

import software.bernie.geckolib.renderer.GeoBlockRenderer;

import net.pasterdream.block.model.PicnicBasketBlockModel;
import net.pasterdream.block.entity.PicnicBasketTileEntity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

public class PicnicBasketTileRenderer extends GeoBlockRenderer<PicnicBasketTileEntity> {
	public PicnicBasketTileRenderer() {
		super(new PicnicBasketBlockModel());
	}

	@Override
	public RenderType getRenderType(PicnicBasketTileEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
