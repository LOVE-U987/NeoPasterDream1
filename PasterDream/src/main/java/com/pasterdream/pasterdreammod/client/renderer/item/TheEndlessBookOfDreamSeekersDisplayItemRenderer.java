package com.pasterdream.pasterdreammod.client.renderer.item;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.item.TheEndlessBookOfDreamSeekersDisplayItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

/**
 * 寻梦者的永恒书卷显示物品渲染器
 * 使用 DefaultedBlockGeoModel 引用方块模型资源
 */
public class TheEndlessBookOfDreamSeekersDisplayItemRenderer extends GeoItemRenderer<TheEndlessBookOfDreamSeekersDisplayItem> {

    private static final String NAME = "the_endless_book_of_dream_seekers";

    /**
     * 构造寻梦者的永恒书卷显示物品渲染器
     */
    public TheEndlessBookOfDreamSeekersDisplayItemRenderer() {
        super(new DefaultedBlockGeoModel<>(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, NAME)));
    }
}
