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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.NeoForgeRenderTypes;

/**
 * 融梦涌泉桶渲染器（BEWLR）
 * <p>
 * 用于解决 {@code neoforge:fluid_container} 模型加载器在开启光影时流体覆盖层丢失的兼容性问题。
 * </p>
 * <p>
 * <b>问题根因：</b>
 * <ol>
 *   <li>{@code fluid_container} 加载器烘焙的 {@link BakedModel} 内含多层 quads（桶底 + 流体 + 覆盖层），
 *       标准物品渲染通过 {@link net.minecraft.client.resources.model.BakedModel#getRenderPasses} 多 pass 进行</li>
 *   <li>原 BEWLR 使用 {@link RenderType#translucent()}（方块半透明管线）创建 VertexConsumer，
 *       该管线被光影（OptiFine/Iris）拦截并替换着色器，导致流体覆盖层在光影下丢失</li>
 *   <li>而 {@code fluid_container} 在物品渲染时应使用
 *       {@link net.neoforged.neoforge.client.NeoForgeRenderTypes#ITEM_UNSORTED_TRANSLUCENT}
 *       （物品专用半透明管线，使用方块图集纹理，光影不拦截）</li>
 * </ol>
 * </p>
 * <p>
 * <b>解决思路：</b>从 {@link net.minecraft.client.resources.model.ModelManager} 获取
 * fluid_container 的烘焙模型 → 使用正确的物品渲染 RenderType（
 * {@link NeoForgeRenderTypes#ITEM_UNSORTED_TRANSLUCENT}）→ 通过
 * {@link ItemRenderer#renderModelLists} 将全部 quads 走标准物品渲染管线输出。
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
                    ResourceLocation.fromNamespaceAndPath("pasterdream", "meltdream_liquid_bucket"));

    /** 缓存的流体桶烘焙模型 */
    private BakedModel cachedModel = null;

    /**
     * 构造融梦涌泉桶渲染器
     * <p>
     * 通过 {@link Minecraft#getInstance()} 获取 {@link net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher}
     * 和 {@link net.minecraft.client.model.EntityModelSet} 构造基类。
     * 该构造器仅在客户端 {@code RegisterClientExtensionsEvent} 事件中被调用，
     * 此时 {@link Minecraft} 实例已完全初始化，调用 {@link Minecraft#getInstance()} 是安全的。
     * </p>
     */
    public MeltdreamLiquidBucketRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),
              Minecraft.getInstance().getEntityModels());
    }

    /**
     * 渲染融梦涌泉桶物品
     * <p>
     * 从 {@link net.minecraft.client.resources.model.ModelManager} 获取 fluid_container 的烘焙模型，
     * 使用 NeoForge 物品专用半透明 RenderType（{@link NeoForgeRenderTypes#ITEM_UNSORTED_TRANSLUCENT}）
     * 创建 VertexConsumer 并调用 {@link ItemRenderer#renderModelLists} 渲染全部 quads。
     * 该 RenderType 使用方块图集纹理，光影着色器不会拦截此通道，从而解决兼容性问题。
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

        /*
         * 使用 NeoForge 提供的物品专用半透明 RenderType。
         *
         * 该 RenderType 的核心特征：
         * - 纹理来源：TextureAtlas.LOCATION_BLOCKS（方块图集）— 与 fluid_container 烘焙 quads 的 UV 一致
         * - 着色器类型：物品渲染着色器（entity_translucent 系列），而非方块 translucent 着色器
         * - 深度排序：已禁用（Unsorted），避免半透明物品之间的排序冲突
         *
         * 光影（OptiFine/Iris）通常只拦截方块渲染管线（RenderType.translucent() 等），
         * 不会拦截物品/实体渲染管线，因此流体覆盖层在该 RenderType 下可见。
         */
        RenderType renderType = NeoForgeRenderTypes.ITEM_UNSORTED_TRANSLUCENT.get();
        VertexConsumer consumer = ItemRenderer.getFoilBufferDirect(
                buffer, renderType, true, stack.hasFoil());

        // 通过标准物品渲染管线输出所有 quads（桶底 + 流体 + 覆盖层一体渲染）
        itemRenderer.renderModelLists(cachedModel, stack, combinedLight,
                combinedOverlay, poseStack, consumer);

        poseStack.popPose();
    }
}