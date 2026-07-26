package com.pasterdream.pasterdreammod.client.renderer.entity;

import com.pasterdream.pasterdreammod.client.model.entity.TerraswordWaveModel;
import com.pasterdream.pasterdreammod.entity.mob.TerraswordWaveEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/**
 * 大地之刃剑气渲染器
 * 与原版一致：半透明渲染 + 0.1 阴影半径 + 死亡不倾倒（getDeathMaxRotation 0）
 */
public class TerraswordWaveRenderer extends GeoEntityRenderer<TerraswordWaveEntity> {

    /**
     * 构造大地之刃剑气渲染器
     *
     * @param context 渲染器上下文
     */
    public TerraswordWaveRenderer(EntityRendererProvider.Context context) {
        super(context, new TerraswordWaveModel());
        this.shadowRadius = 0.1f;
    }

    @Override
    public RenderType getRenderType(TerraswordWaveEntity animatable, ResourceLocation texture,
                                    MultiBufferSource bufferSource, float partialTick) {
        // 与原版一致：半透明渲染
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }

    @Override
    protected float getDeathMaxRotation(TerraswordWaveEntity entity) {
        // 与原版一致：剑气“死亡”时不做侧倾动画
        return 0.0F;
    }
}
