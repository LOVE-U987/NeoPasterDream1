package net.pasterdream.block.renderer;

import software.bernie.geckolib.renderer.GeoBlockRenderer;

import net.pasterdream.block.model.ForcedTowerBlockModel;
import net.pasterdream.block.entity.ForcedTowerTileEntity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

public class ForcedTowerTileRenderer extends GeoBlockRenderer<ForcedTowerTileEntity> {
	public ForcedTowerTileRenderer() {
		super(new ForcedTowerBlockModel());
	}

	@Override
	public RenderType getRenderType(ForcedTowerTileEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
