package net.pasterdream.block.renderer;

import software.bernie.geckolib.renderer.GeoBlockRenderer;

import net.pasterdream.block.model.FireflyGlassJarBlockModel;
import net.pasterdream.block.entity.FireflyGlassJarTileEntity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

public class FireflyGlassJarTileRenderer extends GeoBlockRenderer<FireflyGlassJarTileEntity> {
	public FireflyGlassJarTileRenderer() {
		super(new FireflyGlassJarBlockModel());
	}

	@Override
	public RenderType getRenderType(FireflyGlassJarTileEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
