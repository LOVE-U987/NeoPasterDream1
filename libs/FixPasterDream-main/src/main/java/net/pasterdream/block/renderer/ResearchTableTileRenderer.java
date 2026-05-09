package net.pasterdream.block.renderer;

import software.bernie.geckolib.renderer.GeoBlockRenderer;

import net.pasterdream.block.model.ResearchTableBlockModel;
import net.pasterdream.block.entity.ResearchTableTileEntity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

public class ResearchTableTileRenderer extends GeoBlockRenderer<ResearchTableTileEntity> {
	public ResearchTableTileRenderer() {
		super(new ResearchTableBlockModel());
	}

	@Override
	public RenderType getRenderType(ResearchTableTileEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
