package com.pasterdream.pasterdreammod.client.renderer.item;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.item.ShadowChestDisplayItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

/**
 * 暗影箱显示物品渲染器
 * 使用 DefaultedBlockGeoModel 引用方块模型资源
 */
public class ShadowChestDisplayItemRenderer extends GeoItemRenderer<ShadowChestDisplayItem> {

    private static final String NAME = "shadow_chest";

    /**
     * 构造暗影箱显示物品渲染器
     */
    public ShadowChestDisplayItemRenderer() {
        super(new DefaultedBlockGeoModel<>(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, NAME)));
        PasterDreamMod.LOGGER.debug("[ShadowChestDisplayItemRenderer] 初始化完成，资源名: {} | 模型=geo/block/{}.geo.json 纹理=textures/block/{}.png 动画=animations/block/{}.animation.json",
                NAME, NAME, NAME, NAME);
    }
}