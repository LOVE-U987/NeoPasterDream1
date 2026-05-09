package net.pasterdream.block.renderer;

import software.bernie.geckolib.renderer.GeoItemRenderer;

import net.pasterdream.block.model.WeaponTableDisplayModel;
import net.pasterdream.block.display.WeaponTableDisplayItem;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

public class WeaponTableDisplayItemRenderer extends GeoItemRenderer<WeaponTableDisplayItem> {
	public WeaponTableDisplayItemRenderer() {
		super(new WeaponTableDisplayModel());
	}

	@Override
	public RenderType getRenderType(WeaponTableDisplayItem animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
