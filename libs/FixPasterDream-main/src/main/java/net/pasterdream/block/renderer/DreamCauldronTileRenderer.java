package net.pasterdream.block.renderer;

import software.bernie.geckolib.renderer.GeoBlockRenderer;

import net.pasterdream.block.model.DreamCauldronBlockModel;
import net.pasterdream.block.entity.DreamCauldronTileEntity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

public class DreamCauldronTileRenderer extends GeoBlockRenderer<DreamCauldronTileEntity> {
	public DreamCauldronTileRenderer() {
		super(new DreamCauldronBlockModel());
	}

	@Override
	public RenderType getRenderType(DreamCauldronTileEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
