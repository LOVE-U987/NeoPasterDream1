package com.pasterdream.pasterdreammod.client.renderer.entity;

import com.pasterdream.pasterdreammod.client.model.entity.FurySpellFieldModel;
import com.pasterdream.pasterdreammod.entity.mob.FurySpellFieldEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/**
 * 狂暴法术立场渲染器
 * 与原版一致：半透明渲染 + 2 倍缩放 + 0.1 阴影半径
 */
public class FurySpellFieldRenderer extends GeoEntityRenderer<FurySpellFieldEntity> {

    /**
     * 构造狂暴法术立场渲染器
     *
     * @param context 渲染器上下文
     */
    public FurySpellFieldRenderer(EntityRendererProvider.Context context) {
        super(context, new FurySpellFieldModel());
        this.shadowRadius = 0.1f;
        // 与原版一致：2 倍缩放
        withScale(2f);
    }

    @Override
    public RenderType getRenderType(FurySpellFieldEntity animatable, ResourceLocation texture,
                                    MultiBufferSource bufferSource, float partialTick) {
        // 与原版一致：半透明渲染
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }
}
