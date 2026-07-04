package com.pasterdream.pasterdreammod.entity.client;

import com.pasterdream.pasterdreammod.entity.mob.AaroncosLefthand0Entity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/**
 * 亚伦柯斯左手渲染器 (Aaroncos Lefthand 0 Renderer)
 * <p>
 * 使用 {@link AaroncosLefthand0Model} 加载 GeckoLib 模型/纹理/动画。
 * 使用半透明渲染以支持透明纹理，且死亡时无翻转旋转。
 */
public class AaroncosLefthand0Renderer extends GeoEntityRenderer<AaroncosLefthand0Entity> {

    /**
     * 构造亚伦柯斯左手渲染器
     *
     * @param renderManager 渲染器上下文
     */
    public AaroncosLefthand0Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new AaroncosLefthand0Model());
        this.shadowRadius = 2.8f;
        this.addRenderLayer(new AaroncosLefthand0Layer(this));
    }

    @Override
    public RenderType getRenderType(AaroncosLefthand0Entity animatable, ResourceLocation texture,
                                    @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }

    @Override
    public void preRender(PoseStack poseStack, AaroncosLefthand0Entity entity, BakedGeoModel model,
                          @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer,
                          boolean isReRender, float partialTick, int packedLight, int packedOverlay,
                          int colour) {
        float scale = 1f;
        this.scaleHeight = scale;
        this.scaleWidth = scale;
        super.preRender(poseStack, entity, model, bufferSource, buffer,
                isReRender, partialTick, packedLight, packedOverlay, colour);
    }

    @Override
    protected float getDeathMaxRotation(AaroncosLefthand0Entity entityLivingBaseIn) {
        return 0.0F;
    }
}
