package net.pasterdream.block.renderer;

import software.bernie.geckolib.renderer.GeoItemRenderer;

import net.pasterdream.block.model.WeaponWorkshopDisplayModel;
import net.pasterdream.block.display.WeaponWorkshopDisplayItem;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

public class WeaponWorkshopDisplayItemRenderer extends GeoItemRenderer<WeaponWorkshopDisplayItem> {
	public WeaponWorkshopDisplayItemRenderer() {
		super(new WeaponWorkshopDisplayModel());
	}

	@Override
	public RenderType getRenderType(WeaponWorkshopDisplayItem animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
