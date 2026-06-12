package com.pasterdream.pasterdreammod.client.renderer.item;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.item.DreamCauldronDisplayItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

/**
 * 梦境炼药锅显示物品渲染器 (Dream Cauldron Display Item Renderer)
 * 使用 DefaultedBlockGeoModel 引用方块模型资源
 *
 * 资源引用：
 * - geo/block/dream_cauldron.geo.json
 * - textures/block/dream_cauldron.png
 * - animations/block/dream_cauldron.animation.json
 */
public class DreamCauldronDisplayItemRenderer extends GeoItemRenderer<DreamCauldronDisplayItem> {

    private static final String NAME = "dream_cauldron";

    /**
     * 构造梦境炼药锅显示物品渲染器
     */
    public DreamCauldronDisplayItemRenderer() {
        super(new DefaultedBlockGeoModel<>(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, NAME)));
    }
}
