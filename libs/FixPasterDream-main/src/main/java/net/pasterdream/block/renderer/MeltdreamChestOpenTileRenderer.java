package net.pasterdream.block.renderer;

import software.bernie.geckolib.renderer.GeoBlockRenderer;

import net.pasterdream.block.model.MeltdreamChestOpenBlockModel;
import net.pasterdream.block.entity.MeltdreamChestOpenTileEntity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

public class MeltdreamChestOpenTileRenderer extends GeoBlockRenderer<MeltdreamChestOpenTileEntity> {
	public MeltdreamChestOpenTileRenderer() {
		super(new MeltdreamChestOpenBlockModel());
	}

	@Override
	public RenderType getRenderType(MeltdreamChestOpenTileEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
