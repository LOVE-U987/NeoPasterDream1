package com.pasterdream.pasterdreammod.client.renderer.entity;

import com.pasterdream.pasterdreammod.client.model.entity.ShadowSquealGhostModel;
import com.pasterdream.pasterdreammod.entity.mob.ShadowSquealGhostEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/**
 * 暗影尖啸幽灵渲染器
 * GeckoLib 动画实体，使用自定义模型支持动态纹理切换
 */
public class ShadowSquealGhostRenderer extends GeoEntityRenderer<ShadowSquealGhostEntity> {

    /**
     * 构造暗影尖啸幽灵渲染器
     *
     * @param context 渲染器上下文
     */
    public ShadowSquealGhostRenderer(EntityRendererProvider.Context context) {
        super(context, new ShadowSquealGhostModel());
    }
}