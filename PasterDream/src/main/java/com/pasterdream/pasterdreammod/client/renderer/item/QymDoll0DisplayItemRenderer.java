package com.pasterdream.pasterdreammod.client.renderer.item;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.item.QymDoll0DisplayItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

/**
 * 娇小琴雨梦玩偶显示物品渲染器
 * 使用 DefaultedBlockGeoModel 引用方块模型资源
 */
public class QymDoll0DisplayItemRenderer extends GeoItemRenderer<QymDoll0DisplayItem> {

    private static final String NAME = "qin_doll_0";

    /**
     * 构造娇小琴雨梦玩偶显示物品渲染器
     */
    public QymDoll0DisplayItemRenderer() {
        super(new DefaultedBlockGeoModel<>(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, NAME)));
        PasterDreamMod.LOGGER.debug("[QymDoll0DisplayItemRenderer] 初始化完成，资源名: {} | 模型=geo/block/{}.geo.json 纹理=textures/block/{}.png",
                NAME, NAME, NAME);
    }
}
