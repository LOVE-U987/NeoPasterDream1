
package net.pasterdream.client.renderer;

import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.cache.object.BakedGeoModel;

import net.pasterdream.entity.model.WeakenessTerrorbeakModel;
import net.pasterdream.entity.WeakenessTerrorbeakEntity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;

public class WeakenessTerrorbeakRenderer extends GeoEntityRenderer<WeakenessTerrorbeakEntity> {
	public WeakenessTerrorbeakRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new WeakenessTerrorbeakModel());
		this.shadowRadius = 1.5f;
	}

	@Override
	public RenderType getRenderType(WeakenessTerrorbeakEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}

	@Override
	public void preRender(PoseStack poseStack, WeakenessTerrorbeakEntity entity, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red,
			float green, float blue, float alpha) {
		float scale = 1f;
		this.scaleHeight = scale;
		this.scaleWidth = scale;
		super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	protected float getDeathMaxRotation(WeakenessTerrorbeakEntity entityLivingBaseIn) {
		return 0.0F;
	}
}
