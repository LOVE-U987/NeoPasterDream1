
package net.pasterdream.client.renderer;

import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.cache.object.BakedGeoModel;

import net.pasterdream.procedures.SmallStoneSpiritPr1Procedure;
import net.pasterdream.entity.model.SmallStoneSpiritModel;
import net.pasterdream.entity.SmallStoneSpiritEntity;

import net.minecraft.world.level.Level;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;

public class SmallStoneSpiritRenderer extends GeoEntityRenderer<SmallStoneSpiritEntity> {
	public SmallStoneSpiritRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new SmallStoneSpiritModel());
		this.shadowRadius = 0.5f;
	}

	@Override
	public RenderType getRenderType(SmallStoneSpiritEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}

	@Override
	public void preRender(PoseStack poseStack, SmallStoneSpiritEntity entity, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red,
			float green, float blue, float alpha) {
		Level world = entity.level();
		double x = entity.getX();
		double y = entity.getY();
		double z = entity.getZ();
		float scale = (float) SmallStoneSpiritPr1Procedure.execute(entity);
		this.scaleHeight = scale;
		this.scaleWidth = scale;
		super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	protected float getDeathMaxRotation(SmallStoneSpiritEntity entityLivingBaseIn) {
		return 0.0F;
	}
}
