package net.pasterdream.block.renderer;

import software.bernie.geckolib.renderer.GeoBlockRenderer;

import net.pasterdream.block.model.WeaponTableBlockModel;
import net.pasterdream.block.entity.WeaponTableTileEntity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

public class WeaponTableTileRenderer extends GeoBlockRenderer<WeaponTableTileEntity> {
	public WeaponTableTileRenderer() {
		super(new WeaponTableBlockModel());
	}

	@Override
	public RenderType getRenderType(WeaponTableTileEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
