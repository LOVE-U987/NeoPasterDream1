package net.pasterdream.block.renderer;

import software.bernie.geckolib.renderer.GeoBlockRenderer;

import net.pasterdream.block.model.ShadowBlastFurnaceBlockModel;
import net.pasterdream.block.entity.ShadowBlastFurnaceTileEntity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

public class ShadowBlastFurnaceTileRenderer extends GeoBlockRenderer<ShadowBlastFurnaceTileEntity> {
	public ShadowBlastFurnaceTileRenderer() {
		super(new ShadowBlastFurnaceBlockModel());
	}

	@Override
	public RenderType getRenderType(ShadowBlastFurnaceTileEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
