package com.pasterdream.pasterdreammod.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.pasterdream.pasterdreammod.block.entity.MemorialDollBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;
import software.bernie.geckolib.util.RenderUtil;

/**
 * 纪念玩偶方块渲染器基类 (Memorial Doll Block Renderer)
 * <p>
 * 渲染 3D 玩偶方块，并在抱物状态下于 {@code bb_main} 占位骨骼中心渲染被抱住的物品。
 *
 * @param <T> 纪念玩偶方块实体类型
 */
public abstract class MemorialDollBlockRenderer<T extends MemorialDollBlockEntity> extends GeoBlockRenderer<T> {

    /**
     * 被抱物品的渲染缩放
     */
    private static final float HELD_ITEM_SCALE = 0.5F;

    /**
     * bb_main 占位立方体中心相对其骨骼原点的偏移（单位：方块）
     * <p>
     * 模型 JSON 中 bb_main 的 origin=[1,6,-6]，size=[2,2,2]，inflate=2。
     * GeckoLib 加载时会将 X 轴取反并按方块比例缩放，实际立方体中心在 MC 坐标系中为：
     * <ul>
     *     <li>X = -((1 + 2/2) / 16) = -0.125</li>
     *     <li>Y = (6 + 2/2) / 16 = 0.4375</li>
     *     <li>Z = (-6 + 2/2) / 16 = -0.3125</li>
     * </ul>
     * 其中 inflate 同时向两侧扩展，不改变中心点位置。
     */
    private static final float BB_MAIN_CENTER_X = -0.125F;
    private static final float BB_MAIN_CENTER_Y = 0.4375F;
    private static final float BB_MAIN_CENTER_Z = -0.3125F;

    /**
     * 构造纪念玩偶方块渲染器
     *
     * @param model 方块模型
     */
    protected MemorialDollBlockRenderer(GeoModel<T> model) {
        super(model);
    }

    @Override
    public RenderType getRenderType(T animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }

    /**
     * 保留 GeoBlockRenderer 默认的 preRender 变换（包括 {@code translate(0.5, 0, 0.5)} 居中、
     * 根据方块朝向旋转等），确保模型与碰撞箱对齐。
     *
     * @param poseStack       姿态矩阵
     * @param animatable      当前方块实体
     * @param model           烘焙后的模型
     * @param bufferSource    缓冲源
     * @param buffer          顶点缓冲
     * @param isReRender      是否重新渲染
     * @param partialTick     部分游戏刻
     * @param packedLight     光照值
     * @param packedOverlay   覆盖值
     * @param color           渲染颜色
     */
    @Override
    public void preRender(PoseStack poseStack, T animatable, BakedGeoModel model, MultiBufferSource bufferSource,
                          VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight,
                          int packedOverlay, int color) {
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight,
                packedOverlay, color);
    }

    /**
     * 递归渲染骨骼。对于 {@code bb_main} 占位骨骼，隐藏其自带立方体，
     * 并在骨骼局部坐标系中心渲染被抱住的物品。
     *
     * @param poseStack       当前姿态矩阵
     * @param animatable      当前方块实体
     * @param bone            当前骨骼
     * @param renderType      渲染类型
     * @param bufferSource    缓冲源
     * @param buffer          顶点缓冲
     * @param isReRender      是否重新渲染
     * @param partialTick     部分游戏刻
     * @param packedLight     光照值
     * @param packedOverlay   覆盖值
     * @param color           渲染颜色
     */
    @Override
    public void renderRecursively(PoseStack poseStack, T animatable, GeoBone bone, RenderType renderType,
                                  MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender,
                                  float partialTick, int packedLight, int packedOverlay, int color) {
        if ("bb_main".equals(bone.getName())) {
            // 隐藏占位立方体，避免 super 渲染它
            bone.setHidden(true);
            // 先正常走完父类逻辑（矩阵追踪、子骨骼等）
            super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender,
                    partialTick, packedLight, packedOverlay, color);
            // 父类调用后 PoseStack 已退回父坐标系，需要重新进入 bb_main 局部坐标系
            poseStack.pushPose();
            RenderUtil.prepMatrixForBone(poseStack, bone);
            renderHeldItem(poseStack, animatable, bufferSource, packedLight, packedOverlay);
            poseStack.popPose();
            return;
        }

        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender,
                partialTick, packedLight, packedOverlay, color);
    }

    /**
     * 在玩偶手部位置渲染被抱住的物品
     *
     * @param stack           当前姿态矩阵（已进入 bb_main 局部坐标系）
     * @param animatable      当前方块实体
     * @param bufferSource    缓冲源
     * @param packedLightIn   光照值
     * @param packedOverlayIn 覆盖值
     */
    private void renderHeldItem(PoseStack stack, T animatable, MultiBufferSource bufferSource,
                                int packedLightIn, int packedOverlayIn) {
        if (animatable == null || !animatable.isHolding()) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            return;
        }

        ItemStack heldItem = animatable.getHeldItem();
        if (heldItem.isEmpty()) {
            return;
        }

        ItemRenderer itemRenderer = minecraft.getItemRenderer();
        stack.pushPose();
        stack.translate(BB_MAIN_CENTER_X, BB_MAIN_CENTER_Y, BB_MAIN_CENTER_Z);
        stack.scale(HELD_ITEM_SCALE, HELD_ITEM_SCALE, HELD_ITEM_SCALE);
        itemRenderer.renderStatic(heldItem, ItemDisplayContext.FIXED, packedLightIn, packedOverlayIn,
                stack, bufferSource, minecraft.level, 0);
        stack.popPose();
    }
}
