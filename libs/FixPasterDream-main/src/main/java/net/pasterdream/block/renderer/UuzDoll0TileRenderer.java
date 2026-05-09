package net.pasterdream.block.renderer;

import software.bernie.geckolib.renderer.GeoBlockRenderer;

import net.pasterdream.block.model.UuzDoll0BlockModel;
import net.pasterdream.block.entity.UuzDoll0TileEntity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

public class UuzDoll0TileRenderer extends GeoBlockRenderer<UuzDoll0TileEntity> {
	public UuzDoll0TileRenderer() {
		super(new UuzDoll0BlockModel());
	}

	@Override
	public RenderType getRenderType(UuzDoll0TileEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
