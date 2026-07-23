package com.pasterdream.pasterdreammod.client.renderer.item;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.item.GoldenFoxSculptureDisplayItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

/**
 * 狐狸雕像显示物品渲染器
 * 使用 DefaultedBlockGeoModel 引用方块模型资源
 */
public class GoldenFoxSculptureDisplayItemRenderer extends GeoItemRenderer<GoldenFoxSculptureDisplayItem> {

    private static final String NAME = "golden_fox_sculpture";

    /**
     * 构造狐狸雕像显示物品渲染器
     */
    public GoldenFoxSculptureDisplayItemRenderer() {
        super(new DefaultedBlockGeoModel<>(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, NAME)));
        PasterDreamMod.LOGGER.debug("[GoldenFoxSculptureDisplayItemRenderer] 初始化完成，资源名: {} | 模型=geo/block/{}.geo.json 纹理=textures/block/{}.png",
                NAME, NAME, NAME);
    }
}
