package com.pasterdream.pasterdreammod.client.renderer.item;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.item.ForcedTowerDisplayItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

/**
 * 强征传送塔显示物品渲染器 (Forced Tower Display Item Renderer)
 * 使用 DefaultedBlockGeoModel 引用方块模型资源
 *
 * 资源引用：
 * - geo/block/forced_tower.geo.json
 * - textures/block/forced_tower.png
 * - animations/block/forced_tower.animation.json
 */
public class ForcedTowerDisplayItemRenderer extends GeoItemRenderer<ForcedTowerDisplayItem> {

    private static final String NAME = "forced_tower";

    /**
     * 构造强征传送塔显示物品渲染器
     */
    public ForcedTowerDisplayItemRenderer() {
        super(new DefaultedBlockGeoModel<>(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, NAME)));
    }
}
