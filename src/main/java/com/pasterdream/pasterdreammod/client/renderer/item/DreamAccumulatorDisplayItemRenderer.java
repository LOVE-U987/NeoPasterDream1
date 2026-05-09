package com.pasterdream.pasterdreammod.client.renderer.item;

import com.pasterdream.pasterdreammod.item.DreamAccumulatorDisplayItem;
import com.pasterdream.pasterdreammod.client.model.DreamAccumulatorDisplayModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import software.bernie.geckolib.renderer.GeoItemRenderer;

/**
 * 蓄梦池显示物品渲染器 (Dream Accumulator Display Item Renderer)
 * 用于手持时渲染 GeckoLib 动画模型
 *
 * 渲染流程：
 * 1. 从 geo/block/dream_accumulator.geo.json 加载模型
 * 2. 从 textures/block/dream_accumulator.png 加载纹理
 * 3. 从 animations/block/dream_accumulator.animation.json 加载动画
 */
public class DreamAccumulatorDisplayItemRenderer extends GeoItemRenderer<DreamAccumulatorDisplayItem> {

    /**
     * 构造物品渲染器
     * 使用 DreamAccumulatorDisplayModel 加载资源
     */
    public DreamAccumulatorDisplayItemRenderer() {
        super(new DreamAccumulatorDisplayModel());
    }

    /**
     * 获取渲染类型
     * 使用半透明渲染以支持透明纹理
     *
     * @param animatable 动画对象
     * @param texture 纹理位置
     * @param bufferSource 缓冲源
     * @param partialTick 部分刻
     * @return RenderType 渲染类型
     */
    @Override
    public RenderType getRenderType(DreamAccumulatorDisplayItem animatable, ResourceLocation texture, 
                                    MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }
}
