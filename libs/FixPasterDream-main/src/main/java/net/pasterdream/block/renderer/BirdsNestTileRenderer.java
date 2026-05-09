package net.pasterdream.block.renderer;

import software.bernie.geckolib.renderer.GeoBlockRenderer;

import net.pasterdream.block.model.BirdsNestBlockModel;
import net.pasterdream.block.entity.BirdsNestTileEntity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

public class BirdsNestTileRenderer extends GeoBlockRenderer<BirdsNestTileEntity> {
	public BirdsNestTileRenderer() {
		super(new BirdsNestBlockModel());
	}

	@Override
	public RenderType getRenderType(BirdsNestTileEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
