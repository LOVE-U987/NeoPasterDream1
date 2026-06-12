package com.pasterdream.pasterdreammod.client.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import software.bernie.geckolib.cache.object.GeoBone;

/**
 * 动画工具类
 * 提供在 GeckoLib 骨骼上渲染 Minecraft 原版 ModelPart 的工具方法
 * 用于在自定义物品渲染器中渲染玩家手臂
 */
public class AnimUtils {

    /**
     * 在指定骨骼上渲染 ModelPart
     *
     * @param model        ModelPart 实例
     * @param bone         目标骨骼
     * @param stack        PoseStack
     * @param buffer       VertexConsumer
     * @param packedLightIn  光照
     * @param packedOverlayIn 叠加
     * @param alpha        透明度
     */
    public static void renderPartOverBone(ModelPart model, GeoBone bone, PoseStack stack, VertexConsumer buffer,
                                          int packedLightIn, int packedOverlayIn, float alpha) {
        renderPartOverBone(model, bone, stack, buffer, packedLightIn, packedOverlayIn, alpha, alpha, alpha, alpha);
    }

    /**
     * 在指定骨骼上渲染 ModelPart
     *
     * @param model        ModelPart 实例
     * @param bone         目标骨骼
     * @param stack        PoseStack
     * @param buffer       VertexConsumer
     * @param packedLightIn  光照
     * @param packedOverlayIn 叠加
     * @param r            红色通道
     * @param g            绿色通道
     * @param b            蓝色通道
     * @param a            透明度通道
     */
    public static void renderPartOverBone(ModelPart model, GeoBone bone, PoseStack stack, VertexConsumer buffer,
                                          int packedLightIn, int packedOverlayIn, float r, float g, float b, float a) {
        setupModelFromBone(model, bone);
        model.render(stack, buffer, packedLightIn, packedOverlayIn);
    }

    /**
     * 根据骨骼位置设置 ModelPart 的位置
     *
     * @param model ModelPart 实例
     * @param bone  目标骨骼
     */
    public static void setupModelFromBone(ModelPart model, GeoBone bone) {
        model.setPos(bone.getPivotX(), bone.getPivotY(), bone.getPivotZ());
        model.xRot = 0.0f;
        model.yRot = 0.0f;
        model.zRot = 0.0f;
    }
}