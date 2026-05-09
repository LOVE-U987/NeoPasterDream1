package net.pasterdream.block.renderer;

import software.bernie.geckolib.renderer.GeoItemRenderer;

import net.pasterdream.block.model.QymDoll0DisplayModel;
import net.pasterdream.block.display.QymDoll0DisplayItem;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

public class QymDoll0DisplayItemRenderer extends GeoItemRenderer<QymDoll0DisplayItem> {
	public QymDoll0DisplayItemRenderer() {
		super(new QymDoll0DisplayModel());
	}

	@Override
	public RenderType getRenderType(QymDoll0DisplayItem animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
