package com.pasterdream.pasterdreammod.client.renderer.item;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.item.ResearchTableDisplayItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

/**
 * 研究台显示物品渲染器 (Research Table Display Item Renderer)
 * 使用 DefaultedBlockGeoModel 引用方块模型资源
 *
 * 资源引用：
 * - geo/block/research_table.geo.json
 * - textures/block/research_table.png
 * - animations/block/research_table.animation.json
 */
public class ResearchTableDisplayItemRenderer extends GeoItemRenderer<ResearchTableDisplayItem> {

    private static final String NAME = "research_table";

    /**
     * 构造研究台显示物品渲染器
     */
    public ResearchTableDisplayItemRenderer() {
        super(new DefaultedBlockGeoModel<>(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, NAME)));
    }
}
