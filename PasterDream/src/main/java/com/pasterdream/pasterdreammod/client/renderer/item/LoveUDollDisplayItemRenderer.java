package com.pasterdream.pasterdreammod.client.renderer.item;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.item.LoveUDollDisplayItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

/**
 * 琴雨梦纪念玩偶显示物品渲染器 (Love U Doll Display Item Renderer)
 * <p>
 * 使用 {@link DefaultedBlockGeoModel} 引用方块模型资源。
 */
public class LoveUDollDisplayItemRenderer extends GeoItemRenderer<LoveUDollDisplayItem> {

    private static final String NAME = "love_u_doll";

    /**
     * 构造琴雨梦纪念玩偶显示物品渲染器
     */
    public LoveUDollDisplayItemRenderer() {
        super(new DefaultedBlockGeoModel<>(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, NAME)));
    }
}
