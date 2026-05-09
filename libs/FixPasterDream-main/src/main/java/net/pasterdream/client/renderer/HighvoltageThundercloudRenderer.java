
package net.pasterdream.client.renderer;

import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.cache.object.BakedGeoModel;

import net.pasterdream.entity.model.HighvoltageThundercloudModel;
import net.pasterdream.entity.layer.HighvoltageThundercloudLayer;
import net.pasterdream.entity.HighvoltageThundercloudEntity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;

public class HighvoltageThundercloudRenderer extends GeoEntityRenderer<HighvoltageThundercloudEntity> {
	public HighvoltageThundercloudRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new HighvoltageThundercloudModel());
		this.shadowRadius = 1f;
		this.addRenderLayer(new HighvoltageThundercloudLayer(this));
	}

	@Override
	public RenderType getRenderType(HighvoltageThundercloudEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}

	@Override
	public void preRender(PoseStack poseStack, HighvoltageThundercloudEntity entity, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red,
			float green, float blue, float alpha) {
		float scale = 2.7f;
		this.scaleHeight = scale;
		this.scaleWidth = scale;
		super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	protected float getDeathMaxRotation(HighvoltageThundercloudEntity entityLivingBaseIn) {
		return 0.0F;
	}
}
