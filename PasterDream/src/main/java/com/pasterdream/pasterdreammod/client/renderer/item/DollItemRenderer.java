package com.pasterdream.pasterdreammod.client.renderer.item;

import com.pasterdream.pasterdreammod.client.model.DollItemModel;
import com.pasterdream.pasterdreammod.item.DollDisplayItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

/**
 * 通用玩偶物品渲染器
 */
public class DollItemRenderer extends GeoItemRenderer<DollDisplayItem> {

    /**
     * 构造通用玩偶物品渲染器
     */
    public DollItemRenderer() {
        super(new DollItemModel());
    }
}
