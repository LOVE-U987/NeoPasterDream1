package net.pasterdream.block.renderer;

import software.bernie.geckolib.renderer.GeoBlockRenderer;

import net.pasterdream.block.model.EcologyGlassJarBlockModel;
import net.pasterdream.block.entity.EcologyGlassJarTileEntity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

public class EcologyGlassJarTileRenderer extends GeoBlockRenderer<EcologyGlassJarTileEntity> {
	public EcologyGlassJarTileRenderer() {
		super(new EcologyGlassJarBlockModel());
	}

	@Override
	public RenderType getRenderType(EcologyGlassJarTileEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
