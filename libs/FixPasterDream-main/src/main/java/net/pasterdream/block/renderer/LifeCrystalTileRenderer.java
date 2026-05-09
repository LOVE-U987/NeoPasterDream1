package net.pasterdream.block.renderer;

import software.bernie.geckolib.renderer.GeoBlockRenderer;

import net.pasterdream.block.model.LifeCrystalBlockModel;
import net.pasterdream.block.entity.LifeCrystalTileEntity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

public class LifeCrystalTileRenderer extends GeoBlockRenderer<LifeCrystalTileEntity> {
	public LifeCrystalTileRenderer() {
		super(new LifeCrystalBlockModel());
	}

	@Override
	public RenderType getRenderType(LifeCrystalTileEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
