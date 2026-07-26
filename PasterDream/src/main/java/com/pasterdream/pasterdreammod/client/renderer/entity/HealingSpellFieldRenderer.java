package com.pasterdream.pasterdreammod.client.renderer.entity;

import com.pasterdream.pasterdreammod.client.model.entity.HealingSpellFieldModel;
import com.pasterdream.pasterdreammod.entity.mob.HealingSpellFieldEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/**
 * 治疗法术立场渲染器
 * 与原版一致：半透明渲染 + 0.1 阴影半径
 */
public class HealingSpellFieldRenderer extends GeoEntityRenderer<HealingSpellFieldEntity> {

    /**
     * 构造治疗法术立场渲染器
     *
     * @param context 渲染器上下文
     */
    public HealingSpellFieldRenderer(EntityRendererProvider.Context context) {
        super(context, new HealingSpellFieldModel());
        this.shadowRadius = 0.1f;
    }

    @Override
    public RenderType getRenderType(HealingSpellFieldEntity animatable, ResourceLocation texture,
                                    MultiBufferSource bufferSource, float partialTick) {
        // 与原版一致：半透明渲染
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }
}
