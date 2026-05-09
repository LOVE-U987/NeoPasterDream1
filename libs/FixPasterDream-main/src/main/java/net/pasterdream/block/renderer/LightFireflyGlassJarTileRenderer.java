package net.pasterdream.block.renderer;

import software.bernie.geckolib.renderer.GeoBlockRenderer;

import net.pasterdream.block.model.LightFireflyGlassJarBlockModel;
import net.pasterdream.block.entity.LightFireflyGlassJarTileEntity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

public class LightFireflyGlassJarTileRenderer extends GeoBlockRenderer<LightFireflyGlassJarTileEntity> {
	public LightFireflyGlassJarTileRenderer() {
		super(new LightFireflyGlassJarBlockModel());
	}

	@Override
	public RenderType getRenderType(LightFireflyGlassJarTileEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
