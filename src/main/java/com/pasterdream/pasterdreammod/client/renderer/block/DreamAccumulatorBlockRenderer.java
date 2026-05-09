package com.pasterdream.pasterdreammod.client.renderer.block;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.block.entity.DreamAccumulatorBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

/**
 * 蓄梦池方块渲染器 (Dream Accumulator Block Renderer)
 * 使用 GeckoLib 渲染动画方块
 *
 * 渲染流程：
 * 1. 从 geo/dream_accumulator.geo.json 加载模型
 * 2. 从 textures/block/dream_accumulator.png 加载纹理
 * 3. 从 animations/dream_accumulator.animation.json 加载动画
 * 4. 根据 BlockEntity 的动画状态进行渲染
 */
public class DreamAccumulatorBlockRenderer extends GeoBlockRenderer<DreamAccumulatorBlockEntity> {

    /**
     * 构造蓄梦池方块渲染器
     * 使用 DefaultedBlockGeoModel 自动加载资源文件
     *
     * 资源文件路径约定：
     * - 模型：assets/pasterdream/geo/dream_accumulator.geo.json
     * - 纹理：assets/pasterdream/textures/block/dream_accumulator.png
     * - 动画：assets/pasterdream/animations/dream_accumulator.animation.json
     */
    public DreamAccumulatorBlockRenderer() {
        super(new DefaultedBlockGeoModel<>(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "dream_accumulator")));
    }
}
