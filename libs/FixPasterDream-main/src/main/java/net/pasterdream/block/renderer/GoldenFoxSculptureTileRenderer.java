package net.pasterdream.block.renderer;

import software.bernie.geckolib.renderer.GeoBlockRenderer;

import net.pasterdream.block.model.GoldenFoxSculptureBlockModel;
import net.pasterdream.block.entity.GoldenFoxSculptureTileEntity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

public class GoldenFoxSculptureTileRenderer extends GeoBlockRenderer<GoldenFoxSculptureTileEntity> {
	public GoldenFoxSculptureTileRenderer() {
		super(new GoldenFoxSculptureBlockModel());
	}

	@Override
	public RenderType getRenderType(GoldenFoxSculptureTileEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
