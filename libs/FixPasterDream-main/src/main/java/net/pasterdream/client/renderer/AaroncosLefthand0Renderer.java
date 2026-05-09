
package net.pasterdream.client.renderer;

import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.cache.object.BakedGeoModel;

import net.pasterdream.entity.model.AaroncosLefthand0Model;
import net.pasterdream.entity.layer.AaroncosLefthand0Layer;
import net.pasterdream.entity.AaroncosLefthand0Entity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;

public class AaroncosLefthand0Renderer extends GeoEntityRenderer<AaroncosLefthand0Entity> {
	public AaroncosLefthand0Renderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new AaroncosLefthand0Model());
		this.shadowRadius = 2.8f;
		this.addRenderLayer(new AaroncosLefthand0Layer(this));
	}

	@Override
	public RenderType getRenderType(AaroncosLefthand0Entity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}

	@Override
	public void preRender(PoseStack poseStack, AaroncosLefthand0Entity entity, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red,
			float green, float blue, float alpha) {
		float scale = 1f;
		this.scaleHeight = scale;
		this.scaleWidth = scale;
		super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	protected float getDeathMaxRotation(AaroncosLefthand0Entity entityLivingBaseIn) {
		return 0.0F;
	}
}
