package com.pasterdream.pasterdreammod.client.model;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.item.DreamAccumulatorDisplayItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

/**
 * 蓄梦池显示物品模型 (Dream Accumulator Display Model)
 * 用于手持时加载 GeckoLib 资源
 *
 * 资源文件路径：
 * - 模型：assets/pasterdream/geo/block/dream_accumulator.geo.json
 * - 纹理：assets/pasterdream/textures/block/dream_accumulator.png
 * - 动画：assets/pasterdream/animations/block/dream_accumulator.animation.json
 */
public class DreamAccumulatorDisplayModel extends GeoModel<DreamAccumulatorDisplayItem> {

    /**
     * 获取动画资源位置
     *
     * @param animatable 动画对象
     * @return 动画资源位置
     */
    @Override
    public ResourceLocation getAnimationResource(DreamAccumulatorDisplayItem animatable) {
        return ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "animations/block/dream_accumulator.animation.json");
    }

    /**
     * 获取模型资源位置
     *
     * @param animatable 动画对象
     * @return 模型资源位置
     */
    @Override
    public ResourceLocation getModelResource(DreamAccumulatorDisplayItem animatable) {
        return ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "geo/block/dream_accumulator.geo.json");
    }

    /**
     * 获取纹理资源位置
     *
     * @param animatable 动画对象
     * @return 纹理资源位置
     */
    @Override
    public ResourceLocation getTextureResource(DreamAccumulatorDisplayItem animatable) {
        return ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "textures/block/dream_accumulator.png");
    }
}
