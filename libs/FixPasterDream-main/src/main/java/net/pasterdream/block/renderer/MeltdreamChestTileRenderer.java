package net.pasterdream.block.renderer;

import software.bernie.geckolib.renderer.GeoBlockRenderer;

import net.pasterdream.block.model.MeltdreamChestBlockModel;
import net.pasterdream.block.entity.MeltdreamChestTileEntity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

public class MeltdreamChestTileRenderer extends GeoBlockRenderer<MeltdreamChestTileEntity> {
	public MeltdreamChestTileRenderer() {
		super(new MeltdreamChestBlockModel());
	}

	@Override
	public RenderType getRenderType(MeltdreamChestTileEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
