package com.pasterdream.pasterdreammod.client.renderer.item;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.item.UuzDoll0DisplayItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

/**
 * 娇小幼幼紫玩偶显示物品渲染器
 * 使用 DefaultedBlockGeoModel 引用方块模型资源
 */
public class UuzDoll0DisplayItemRenderer extends GeoItemRenderer<UuzDoll0DisplayItem> {

    private static final String NAME = "little_purple_doll_0";

    /**
     * 构造娇小幼幼紫玩偶显示物品渲染器
     */
    public UuzDoll0DisplayItemRenderer() {
        super(new DefaultedBlockGeoModel<>(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, NAME)));
        PasterDreamMod.LOGGER.debug("[UuzDoll0DisplayItemRenderer] 初始化完成，资源名: {} | 模型=geo/block/{}.geo.json 纹理=textures/block/{}.png",
                NAME, NAME, NAME);
    }
}
