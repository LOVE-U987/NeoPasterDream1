
package net.pasterdream.client.renderer;

import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.cache.object.BakedGeoModel;

import net.pasterdream.entity.model.AaroncosRighthand0Model;
import net.pasterdream.entity.layer.AaroncosRighthand0Layer;
import net.pasterdream.entity.AaroncosRighthand0Entity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;

public class AaroncosRighthand0Renderer extends GeoEntityRenderer<AaroncosRighthand0Entity> {
	public AaroncosRighthand0Renderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new AaroncosRighthand0Model());
		this.shadowRadius = 2.8f;
		this.addRenderLayer(new AaroncosRighthand0Layer(this));
	}

	@Override
	public RenderType getRenderType(AaroncosRighthand0Entity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}

	@Override
	public void preRender(PoseStack poseStack, AaroncosRighthand0Entity entity, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red,
			float green, float blue, float alpha) {
		float scale = 1f;
		this.scaleHeight = scale;
		this.scaleWidth = scale;
		super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	protected float getDeathMaxRotation(AaroncosRighthand0Entity entityLivingBaseIn) {
		return 0.0F;
	}
}
