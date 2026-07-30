package com.pasterdream.pasterdreammod.api.client.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import software.bernie.geckolib.cache.object.GeoBone;

/**
 * 在 GeckoLib 骨骼上挂载/对齐原版 {@link ModelPart}（如物品渲染器上的玩家手臂）。
 */
public final class AnimUtils {

    private AnimUtils() {
    }

    public static void renderPartOverBone(ModelPart model, GeoBone bone, PoseStack stack, VertexConsumer buffer,
                                          int packedLightIn, int packedOverlayIn, float alpha) {
        renderPartOverBone(model, bone, stack, buffer, packedLightIn, packedOverlayIn, alpha, alpha, alpha, alpha);
    }

    public static void renderPartOverBone(ModelPart model, GeoBone bone, PoseStack stack, VertexConsumer buffer,
                                          int packedLightIn, int packedOverlayIn, float r, float g, float b, float a) {
        setupModelFromBone(model, bone);
        model.render(stack, buffer, packedLightIn, packedOverlayIn);
    }

    public static void setupModelFromBone(ModelPart model, GeoBone bone) {
        model.setPos(bone.getPivotX(), bone.getPivotY(), bone.getPivotZ());
        model.xRot = 0.0f;
        model.yRot = 0.0f;
        model.zRot = 0.0f;
    }
}
