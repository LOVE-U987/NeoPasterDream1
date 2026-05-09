package net.pasterdream.block.renderer;

import software.bernie.geckolib.renderer.GeoBlockRenderer;

import net.pasterdream.block.model.QymDoll0BlockModel;
import net.pasterdream.block.entity.QymDoll0TileEntity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

public class QymDoll0TileRenderer extends GeoBlockRenderer<QymDoll0TileEntity> {
	public QymDoll0TileRenderer() {
		super(new QymDoll0BlockModel());
	}

	@Override
	public RenderType getRenderType(QymDoll0TileEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
