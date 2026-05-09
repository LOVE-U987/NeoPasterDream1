package net.pasterdream.block.renderer;

import software.bernie.geckolib.renderer.GeoBlockRenderer;

import net.pasterdream.block.model.ShadowTrap0BlockModel;
import net.pasterdream.block.entity.ShadowTrap0TileEntity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

public class ShadowTrap0TileRenderer extends GeoBlockRenderer<ShadowTrap0TileEntity> {
	public ShadowTrap0TileRenderer() {
		super(new ShadowTrap0BlockModel());
	}

	@Override
	public RenderType getRenderType(ShadowTrap0TileEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
