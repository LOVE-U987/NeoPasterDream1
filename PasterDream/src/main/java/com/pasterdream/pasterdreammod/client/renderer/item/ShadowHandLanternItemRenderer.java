package com.pasterdream.pasterdreammod.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.pasterdream.pasterdreammod.client.model.ShadowHandLanternItemModel;
import com.pasterdream.pasterdreammod.item.ShadowHandLanternItem;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoItemRenderer;

/**
 * 暗影提灯物品渲染器：GeckoLib 3D + 半透明。
 * 隐藏原版骨骼位 right/left，避免空骨骼遮挡。
 */
public class ShadowHandLanternItemRenderer extends GeoItemRenderer<ShadowHandLanternItem> {

    public ItemDisplayContext transformType;

    public ShadowHandLanternItemRenderer() {
        super(new ShadowHandLanternItemModel());
    }

    @Override
    public RenderType getRenderType(ShadowHandLanternItem animatable, ResourceLocation texture,
                                    MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext transformType, PoseStack matrixStack,
                             MultiBufferSource bufferIn, int combinedLightIn, int packedOverlay) {
        this.transformType = transformType;
        if (this.animatable != null) {
            this.animatable.getTransformType(transformType);
        }
        super.renderByItem(stack, transformType, matrixStack, bufferIn, combinedLightIn, packedOverlay);
    }

    @Override
    public void renderRecursively(PoseStack stack, ShadowHandLanternItem animatable, GeoBone bone, RenderType type,
                                  MultiBufferSource buffer, VertexConsumer bufferIn, boolean isReRender,
                                  float partialTick, int packedLightIn, int packedOverlayIn, int color) {
        String name = bone.getName();
        if (name.equals("right") || name.equals("left") || name.isEmpty()) {
            bone.setHidden(true);
        }
        super.renderRecursively(stack, animatable, bone, type, buffer, bufferIn, isReRender, partialTick,
                packedLightIn, packedOverlayIn, color);
    }
}
