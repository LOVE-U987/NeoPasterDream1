package com.pasterdream.pasterdreammod.client.renderer.item;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.item.ShadowBlastFurnaceDisplayItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

/**
 * 暗影高炉显示物品渲染器 (Shadow Blast Furnace Display Item Renderer)
 * 使用 DefaultedBlockGeoModel 引用方块模型资源
 *
 * 资源引用：
 * - geo/block/shadow_blast_furnace.geo.json
 * - textures/block/shadow_blast_furnace.png
 * - animations/block/shadow_blast_furnace.animation.json
 */
public class ShadowBlastFurnaceDisplayItemRenderer extends GeoItemRenderer<ShadowBlastFurnaceDisplayItem> {

    private static final String NAME = "shadow_blast_furnace";

    /**
     * 构造暗影高炉显示物品渲染器
     */
    public ShadowBlastFurnaceDisplayItemRenderer() {
        super(new DefaultedBlockGeoModel<>(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, NAME)));
    }
}
