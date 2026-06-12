package com.pasterdream.pasterdreammod.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/**
 * 融梦涌泉桶渲染器（BEWLR）
 * <p>
 * 用于解决 {@code neoforge:fluid_container} 模型加载器在开启光影时流体覆盖层丢失的兼容性问题。
 * </p>
 * <p>
 * 实现思路：直接从 {@link net.minecraft.client.resources.model.ModelManager} 获取 fluid_container
 * 烘焙后的 {@link BakedModel}，再通过标准的物品渲染管线（{@link ItemRenderer#renderModelLists}）
 * 进行渲染，绕过光影对 fluid_container 加载路径的拦截，确保流体层走标准半透明渲染管线。
 * </p>
 * <p>
 * 引用资源：
 * <ul>
 *   <li>模型：assets/pasterdream/models/item/meltdream_liquid_bucket.json (neoforge:fluid_container)</li>
 *   <li>流体纹理：assets/pasterdream/textures/block/meltdream_liquid_still.png</li>
 * </ul>
 * </p>
 */
public class MeltdreamLiquidBucketRenderer extends BlockEntityWithoutLevelRenderer {

    /** 流体桶模型的 ModelResourceLocation（inventory variant） */
    private static final ModelResourceLocation BUCKET_MODEL_RL =
            ModelResourceLocation.standalone(
                    net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(
                            "pasterdream", "meltdream_liquid_bucket"));

    /** 缓存的流体桶烘焙模型 */
    private BakedModel cachedModel = null;

    /**
     * 构造融梦涌泉桶渲染器
     * <p>通过 {@link Minecraft#getInstance()} 获取 {@link net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher}
     * 和 {@link net.minecraft.client.model.EntityModelSet} 构造基类。
     * 该构造器仅在客户端 {@code RegisterClientExtensionsEvent} 事件中被调用，
     * 此时 {@link Minecraft} 实例已完全初始化，调用 {@link Minecraft#getInstance()} 是安全的。</p>
     */
    public MeltdreamLiquidBucketRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),
              Minecraft.getInstance().getEntityModels());
    }

    /**
     * 渲染融梦涌泉桶物品
     * <p>
     * 从 {@link net.minecraft.client.resources.model.ModelManager} 直接获取 fluid_container 的
     * {@link BakedModel}，使用 {@link ItemRenderer#renderModelLists} 通过标准渲染管线渲染。
     * 这样流体层数据由标准 VertexConsumer 处理，不受光影着色器对 fluid_container 加载路径的干扰。
     * </p>
     *
     * @param stack            待渲染的物品堆
     * @param displayContext   物品显示上下文（GUI/第一人称/第三人称等）
     * @param poseStack        矩阵栈
     * @param buffer           渲染缓冲区
     * @param combinedLight    组合光照值
     * @param combinedOverlay  组合覆盖值
     */
    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext,
                             PoseStack poseStack, MultiBufferSource buffer,
                             int combinedLight, int combinedOverlay) {
        Minecraft mc = Minecraft.getInstance();
        ItemRenderer itemRenderer = mc.getItemRenderer();

        // 延迟加载烘焙模型（首次渲染时获取）
        if (cachedModel == null) {
            cachedModel = mc.getModelManager().getModel(BUCKET_MODEL_RL);
        }

        poseStack.pushPose();

        // 使用物品的半透明渲染Sheet，确保流体覆盖层能正确显示
        RenderType renderType = RenderType.translucent();
        VertexConsumer consumer = ItemRenderer.getFoilBufferDirect(
                buffer, renderType, false, stack.hasFoil());

        // 通过标准渲染管线渲染fluid_container的烘焙模型
        itemRenderer.renderModelLists(cachedModel, stack, combinedLight, combinedOverlay, poseStack, consumer);

        poseStack.popPose();
    }
}