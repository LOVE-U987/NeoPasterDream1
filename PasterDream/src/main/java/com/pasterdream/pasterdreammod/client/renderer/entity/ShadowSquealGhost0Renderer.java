package com.pasterdream.pasterdreammod.client.renderer.entity;

import com.pasterdream.pasterdreammod.client.model.entity.ShadowSquealGhost0Model;
import com.pasterdream.pasterdreammod.entity.mob.ShadowSquealGhost0Entity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/**
 * 暗影尖啸幽灵0渲染器
 * GeckoLib 动画实体，使用自定义模型支持动态纹理切换
 */
public class ShadowSquealGhost0Renderer extends GeoEntityRenderer<ShadowSquealGhost0Entity> {

    /**
     * 构造暗影尖啸幽灵0渲染器
     *
     * @param context 渲染器上下文
     */
    public ShadowSquealGhost0Renderer(EntityRendererProvider.Context context) {
        super(context, new ShadowSquealGhost0Model());
    }
}