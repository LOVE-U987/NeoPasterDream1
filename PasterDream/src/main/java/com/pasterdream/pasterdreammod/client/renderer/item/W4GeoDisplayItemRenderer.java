package com.pasterdream.pasterdreammod.client.renderer.item;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.item.W4GeoDisplayItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

/**
 * W4 波次通用 GeckoLib 显示物品渲染器
 * <p>
 * 与方块渲染共用 DefaultedBlockGeoModel 资源（geo/block/NAME.geo.json 等），
 * 按注册名装配。
 */
public class W4GeoDisplayItemRenderer extends GeoItemRenderer<W4GeoDisplayItem> {

    /**
     * 构造通用显示物品渲染器
     *
     * @param name 资源注册名（snake_case）
     */
    public W4GeoDisplayItemRenderer(String name) {
        super(new DefaultedBlockGeoModel<>(
                ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, name)));
    }
}
