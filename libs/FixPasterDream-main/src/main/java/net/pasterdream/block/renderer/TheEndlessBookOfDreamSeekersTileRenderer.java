package net.pasterdream.block.renderer;

import software.bernie.geckolib.renderer.GeoBlockRenderer;

import net.pasterdream.block.model.TheEndlessBookOfDreamSeekersBlockModel;
import net.pasterdream.block.entity.TheEndlessBookOfDreamSeekersTileEntity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

public class TheEndlessBookOfDreamSeekersTileRenderer extends GeoBlockRenderer<TheEndlessBookOfDreamSeekersTileEntity> {
	public TheEndlessBookOfDreamSeekersTileRenderer() {
		super(new TheEndlessBookOfDreamSeekersBlockModel());
	}

	@Override
	public RenderType getRenderType(TheEndlessBookOfDreamSeekersTileEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
