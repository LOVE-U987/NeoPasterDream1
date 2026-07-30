package com.pasterdream.pasterdreammod.client.renderer.item;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.item.EoulDollDisplayItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

/**
 * EOUL小幽灵玩偶显示物品渲染器 (Eoul Doll Display Item Renderer)
 * <p>
 * 使用 {@link DefaultedBlockGeoModel} 引用方块模型资源。
 */
public class EoulDollDisplayItemRenderer extends GeoItemRenderer<EoulDollDisplayItem> {

    private static final String NAME = "eoul_doll";

    /**
     * 构造EOUL小幽灵玩偶显示物品渲染器
     */
    public EoulDollDisplayItemRenderer() {
        super(new DefaultedBlockGeoModel<>(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, NAME)));
    }
}
