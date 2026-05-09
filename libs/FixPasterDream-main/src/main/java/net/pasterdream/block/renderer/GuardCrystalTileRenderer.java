package net.pasterdream.block.renderer;

import software.bernie.geckolib.renderer.GeoBlockRenderer;

import net.pasterdream.block.model.GuardCrystalBlockModel;
import net.pasterdream.block.entity.GuardCrystalTileEntity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

public class GuardCrystalTileRenderer extends GeoBlockRenderer<GuardCrystalTileEntity> {
	public GuardCrystalTileRenderer() {
		super(new GuardCrystalBlockModel());
	}

	@Override
	public RenderType getRenderType(GuardCrystalTileEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
