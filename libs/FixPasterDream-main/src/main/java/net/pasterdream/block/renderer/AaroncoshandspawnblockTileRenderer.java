package net.pasterdream.block.renderer;

import software.bernie.geckolib.renderer.GeoBlockRenderer;

import net.pasterdream.block.model.AaroncoshandspawnblockBlockModel;
import net.pasterdream.block.entity.AaroncoshandspawnblockTileEntity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

public class AaroncoshandspawnblockTileRenderer extends GeoBlockRenderer<AaroncoshandspawnblockTileEntity> {
	public AaroncoshandspawnblockTileRenderer() {
		super(new AaroncoshandspawnblockBlockModel());
	}

	@Override
	public RenderType getRenderType(AaroncoshandspawnblockTileEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
