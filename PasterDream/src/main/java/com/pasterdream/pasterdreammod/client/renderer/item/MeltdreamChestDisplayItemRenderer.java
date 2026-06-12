package com.pasterdream.pasterdreammod.client.renderer.item;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.item.MeltdreamChestDisplayItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

/**
 * 融梦水晶箱显示物品渲染器
 * 使用 DefaultedBlockGeoModel 引用方块模型资源
 *
 * 资源引用：
 * - geo/block/meltdream_chest_0.geo.json
 * - textures/block/meltdream_chest_0.png
 * - animations/block/meltdream_chest_0.animation.json
 */
public class MeltdreamChestDisplayItemRenderer extends GeoItemRenderer<MeltdreamChestDisplayItem> {

    private static final String NAME = "meltdream_chest_0";

    /**
     * 构造融梦水晶箱显示物品渲染器
     */
    public MeltdreamChestDisplayItemRenderer() {
        super(new DefaultedBlockGeoModel<>(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, NAME)));
    }
}
