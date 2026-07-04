package com.pasterdream.pasterdreammod.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.entity.mob.AaroncosLefthand0Entity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

/**
 * 亚伦柯斯左手发光层 (Aaroncos Lefthand 0 Glow Layer)
 * <p>
 * 使用 {@link RenderType#eyes} 渲染发光纹理层，
 * 在实体模型之上叠加发光效果，纹理为 aaroncos_lefthand_0_light.png。
 */
public class AaroncosLefthand0Layer extends GeoRenderLayer<AaroncosLefthand0Entity> {

    private static final ResourceLocation LAYER = ResourceLocation.fromNamespaceAndPath(
            PasterDreamMod.MOD_ID, "textures/entity/aaroncos_lefthand_0_light.png");

    /**
     * 构造亚伦柯斯左手发光层
     *
     * @param entityRenderer 所属的实体渲染器
     */
    public AaroncosLefthand0Layer(GeoRenderer<AaroncosLefthand0Entity> entityRenderer) {
        super(entityRenderer);
    }

    @Override
    public void render(PoseStack poseStack, AaroncosLefthand0Entity animatable, BakedGeoModel bakedModel,
                       @Nullable RenderType renderType, MultiBufferSource bufferSource,
                       @Nullable VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        RenderType glowRenderType = RenderType.eyes(LAYER);
        getRenderer().reRender(getDefaultBakedModel(animatable), poseStack, bufferSource, animatable,
                glowRenderType, bufferSource.getBuffer(glowRenderType),
                partialTick, packedLight, OverlayTexture.NO_OVERLAY,
                0xFFFFFFFF);
    }
}
