package com.pasterdream.pasterdreammod.client.renderer.entity;

import com.pasterdream.pasterdreammod.client.model.entity.ShadowGhostModel;
import com.pasterdream.pasterdreammod.entity.mob.ShadowGhostEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/**
 * 暗影幽灵渲染器
 * GeckoLib 动画实体，使用自定义模型支持动态纹理切换
 */
public class ShadowGhostRenderer extends GeoEntityRenderer<ShadowGhostEntity> {

    /**
     * 构造暗影幽灵渲染器
     *
     * @param context 渲染器上下文
     */
    public ShadowGhostRenderer(EntityRendererProvider.Context context) {
        super(context, new ShadowGhostModel());
    }
}