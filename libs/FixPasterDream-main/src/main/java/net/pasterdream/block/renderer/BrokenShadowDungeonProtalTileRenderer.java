package net.pasterdream.block.renderer;

import software.bernie.geckolib.renderer.GeoBlockRenderer;

import net.pasterdream.block.model.BrokenShadowDungeonProtalBlockModel;
import net.pasterdream.block.entity.BrokenShadowDungeonProtalTileEntity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

public class BrokenShadowDungeonProtalTileRenderer extends GeoBlockRenderer<BrokenShadowDungeonProtalTileEntity> {
	public BrokenShadowDungeonProtalTileRenderer() {
		super(new BrokenShadowDungeonProtalBlockModel());
	}

	@Override
	public RenderType getRenderType(BrokenShadowDungeonProtalTileEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
