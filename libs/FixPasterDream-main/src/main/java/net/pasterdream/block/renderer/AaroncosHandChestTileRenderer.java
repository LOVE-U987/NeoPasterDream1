package net.pasterdream.block.renderer;

import software.bernie.geckolib.renderer.GeoBlockRenderer;

import net.pasterdream.block.model.AaroncosHandChestBlockModel;
import net.pasterdream.block.entity.AaroncosHandChestTileEntity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

public class AaroncosHandChestTileRenderer extends GeoBlockRenderer<AaroncosHandChestTileEntity> {
	public AaroncosHandChestTileRenderer() {
		super(new AaroncosHandChestBlockModel());
	}

	@Override
	public RenderType getRenderType(AaroncosHandChestTileEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
