
package net.pasterdream.client.renderer;

import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.cache.object.BakedGeoModel;

import net.pasterdream.entity.model.ThundercloudModel;
import net.pasterdream.entity.layer.ThundercloudLayer;
import net.pasterdream.entity.ThundercloudEntity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;

public class ThundercloudRenderer extends GeoEntityRenderer<ThundercloudEntity> {
	public ThundercloudRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new ThundercloudModel());
		this.shadowRadius = 0.8f;
		this.addRenderLayer(new ThundercloudLayer(this));
	}

	@Override
	public RenderType getRenderType(ThundercloudEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}

	@Override
	public void preRender(PoseStack poseStack, ThundercloudEntity entity, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green,
			float blue, float alpha) {
		float scale = 2f;
		this.scaleHeight = scale;
		this.scaleWidth = scale;
		super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	protected float getDeathMaxRotation(ThundercloudEntity entityLivingBaseIn) {
		return 0.0F;
	}
}
