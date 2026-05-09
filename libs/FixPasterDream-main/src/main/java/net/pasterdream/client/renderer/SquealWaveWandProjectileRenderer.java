package net.pasterdream.client.renderer;

import net.pasterdream.entity.SquealWaveWandProjectileEntity;
import net.pasterdream.client.model.Modelsqueal_wave;

import net.minecraft.util.Mth;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

import com.mojang.math.Axis;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;

public class SquealWaveWandProjectileRenderer extends EntityRenderer<SquealWaveWandProjectileEntity> {
	private static final ResourceLocation texture = new ResourceLocation("pasterdream:textures/entities/squeal_wave.png");
	private final Modelsqueal_wave model;

	public SquealWaveWandProjectileRenderer(EntityRendererProvider.Context context) {
		super(context);
		model = new Modelsqueal_wave(context.bakeLayer(Modelsqueal_wave.LAYER_LOCATION));
	}

	@Override
	public void render(SquealWaveWandProjectileEntity entityIn, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn) {
		VertexConsumer vb = bufferIn.getBuffer(RenderType.entityCutout(this.getTextureLocation(entityIn)));
		poseStack.pushPose();
		poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entityIn.yRotO, entityIn.getYRot()) - 90));
		poseStack.mulPose(Axis.ZP.rotationDegrees(90 + Mth.lerp(partialTicks, entityIn.xRotO, entityIn.getXRot())));
		model.renderToBuffer(poseStack, vb, packedLightIn, OverlayTexture.NO_OVERLAY, 1, 1, 1, 0.0625f);
		poseStack.popPose();
		super.render(entityIn, entityYaw, partialTicks, poseStack, bufferIn, packedLightIn);
	}

	@Override
	public ResourceLocation getTextureLocation(SquealWaveWandProjectileEntity entity) {
		return texture;
	}
}
