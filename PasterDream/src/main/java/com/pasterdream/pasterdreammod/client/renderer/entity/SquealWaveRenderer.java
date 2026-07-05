package com.pasterdream.pasterdreammod.client.renderer.entity;

import com.pasterdream.pasterdreammod.entity.projectile.SquealWaveProjectileEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class SquealWaveRenderer extends EntityRenderer<SquealWaveProjectileEntity> {

    public SquealWaveRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(SquealWaveProjectileEntity entity) {
        return TextureAtlas.LOCATION_PARTICLES;
    }

    @Override
    protected int getBlockLightLevel(SquealWaveProjectileEntity entity, BlockPos pos) {
        return 15;
    }

    @Override
    public void render(SquealWaveProjectileEntity entity, float entityYaw, float partialTicks,
                       PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.scale(0.5F, 0.5F, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entity.yRotO, entity.getYRot()) - 90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTicks, entity.xRotO, entity.getXRot())));

        float ageInTicks = entity.tickCount + partialTicks;
        float scale = 1.0F + Mth.sin(ageInTicks * 0.1F) * 0.1F;
        poseStack.scale(scale, scale, scale);

        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.lightning());

        int j = 8;
        for (int k = 0; k < j; ++k) {
            poseStack.pushPose();
            poseStack.mulPose(Axis.XP.rotationDegrees(360.0F / j));
            float f1 = ageInTicks * 0.05F + k * 0.1F;
            float f2 = 0.2F + Mth.sin(f1) * 0.1F;
            poseStack.scale(f2, f2, f2);

            float radius = 0.3F;
            int slices = 8;
            int stacks = 8;

            for (int i = 0; i <= stacks; ++i) {
                float angle1 = (float) (i * Math.PI) / stacks;
                float sin1 = Mth.sin(angle1);
                float cos1 = Mth.cos(angle1);

                for (int m = 0; m <= slices; ++m) {
                    float angle2 = (float) (m * Math.PI * 2) / slices;
                    float sin2 = Mth.sin(angle2);
                    float cos2 = Mth.cos(angle2);

                    float x = cos2 * sin1 * radius;
                    float y = cos1 * radius;
                    float z = sin2 * sin1 * radius;

                    vertexConsumer.addVertex(poseStack.last().pose(), x, y, z).setColor(128, 128, 255, 64);
                }
            }
            poseStack.popPose();
        }

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }
}
