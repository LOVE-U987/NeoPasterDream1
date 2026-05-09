package net.pasterdream.block.renderer;

import software.bernie.geckolib.renderer.GeoBlockRenderer;

import net.pasterdream.block.model.WeaponWorkshopBlockModel;
import net.pasterdream.block.entity.WeaponWorkshopTileEntity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

public class WeaponWorkshopTileRenderer extends GeoBlockRenderer<WeaponWorkshopTileEntity> {
	public WeaponWorkshopTileRenderer() {
		super(new WeaponWorkshopBlockModel());
	}

	@Override
	public RenderType getRenderType(WeaponWorkshopTileEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
