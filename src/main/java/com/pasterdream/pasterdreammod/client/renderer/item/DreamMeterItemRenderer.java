package com.pasterdream.pasterdreammod.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.pasterdream.pasterdreammod.client.model.DreamMeterItemModel;
import com.pasterdream.pasterdreammod.item.DreamMeterItem;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoItemRenderer;

import java.util.HashSet;
import java.util.Set;

/**
 * 忆梦魔导透镜渲染器 (Dream Meter Item Renderer)
 * 使用 GeckoLib 渲染 3D 手持模型
 * <p>
 * 渲染特性：
 * - 第三人称、第一人称均显示完整的 3D 透镜模型
 * - 隐藏右手/左手骨骼，使透镜模型在手中自然呈现
 * - 使用半透明渲染以支持纹理透明度
 */
public class DreamMeterItemRenderer extends GeoItemRenderer<DreamMeterItem> {

    public ItemDisplayContext transformType;
    private final Set<String> hiddenBones = new HashSet<>();

    public DreamMeterItemRenderer() {
        super(new DreamMeterItemModel());
    }

    @Override
    public RenderType getRenderType(DreamMeterItem animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext transformType, PoseStack matrixStack,
                             MultiBufferSource bufferIn, int combinedLightIn, int p_239207_6_) {
        this.transformType = transformType;
        super.renderByItem(stack, transformType, matrixStack, bufferIn, combinedLightIn, p_239207_6_);
    }

    @Override
    public void renderRecursively(PoseStack stack, DreamMeterItem animatable, GeoBone bone, RenderType type,
                                  MultiBufferSource buffer, VertexConsumer bufferIn, boolean isReRender,
                                  float partialTick, int packedLightIn, int packedOverlayIn, int color) {
        String name = bone.getName();
        if (name.equals("right") || name.equals("left")) {
            bone.setHidden(true);
        } else {
            bone.setHidden(this.hiddenBones.contains(name));
        }
        super.renderRecursively(stack, animatable, bone, type, buffer, bufferIn, isReRender, partialTick,
                packedLightIn, packedOverlayIn, color);
    }
}
