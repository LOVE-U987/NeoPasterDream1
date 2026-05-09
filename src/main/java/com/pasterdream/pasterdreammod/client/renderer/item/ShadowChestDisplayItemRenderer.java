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

    /**
     * 构造暗影箱显示物品渲染器
     */
    public ShadowChestDisplayItemRenderer() {
        super(new DefaultedBlockGeoModel<>(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "shadow_chest")));
    }
}