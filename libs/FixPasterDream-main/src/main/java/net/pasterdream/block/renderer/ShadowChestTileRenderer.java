package net.pasterdream.block.renderer;

import software.bernie.geckolib.renderer.GeoBlockRenderer;

import net.pasterdream.block.model.ShadowChestBlockModel;
import net.pasterdream.block.entity.ShadowChestTileEntity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

public class ShadowChestTileRenderer extends GeoBlockRenderer<ShadowChestTileEntity> {
	public ShadowChestTileRenderer() {
		super(new ShadowChestBlockModel());
	}

	@Override
	public RenderType getRenderType(ShadowChestTileEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
