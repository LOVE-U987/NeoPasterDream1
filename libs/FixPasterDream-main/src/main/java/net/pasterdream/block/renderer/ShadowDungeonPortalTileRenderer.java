package net.pasterdream.block.renderer;

import software.bernie.geckolib.renderer.GeoBlockRenderer;

import net.pasterdream.block.model.ShadowDungeonPortalBlockModel;
import net.pasterdream.block.entity.ShadowDungeonPortalTileEntity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

public class ShadowDungeonPortalTileRenderer extends GeoBlockRenderer<ShadowDungeonPortalTileEntity> {
	public ShadowDungeonPortalTileRenderer() {
		super(new ShadowDungeonPortalBlockModel());
	}

	@Override
	public RenderType getRenderType(ShadowDungeonPortalTileEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
