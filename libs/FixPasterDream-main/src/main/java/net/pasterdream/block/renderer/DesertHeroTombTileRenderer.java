package net.pasterdream.block.renderer;

import software.bernie.geckolib.renderer.GeoBlockRenderer;

import net.pasterdream.block.model.DesertHeroTombBlockModel;
import net.pasterdream.block.entity.DesertHeroTombTileEntity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

public class DesertHeroTombTileRenderer extends GeoBlockRenderer<DesertHeroTombTileEntity> {
	public DesertHeroTombTileRenderer() {
		super(new DesertHeroTombBlockModel());
	}

	@Override
	public RenderType getRenderType(DesertHeroTombTileEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
