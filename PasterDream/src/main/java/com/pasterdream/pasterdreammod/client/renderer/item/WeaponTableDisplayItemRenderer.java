package com.pasterdream.pasterdreammod.client.renderer.item;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.item.WeaponTableDisplayItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

/**
 * 精铸工作台显示物品渲染器 (Weapon Table Display Item Renderer)
 * 使用 DefaultedBlockGeoModel 引用方块模型资源
 *
 * 资源引用：
 * - geo/block/weapon_table.geo.json
 * - textures/block/weapon_table.png
 * - animations/block/weapon_table.animation.json
 */
public class WeaponTableDisplayItemRenderer extends GeoItemRenderer<WeaponTableDisplayItem> {

    private static final String NAME = "weapon_table";

    /**
     * 构造精铸工作台显示物品渲染器
     */
    public WeaponTableDisplayItemRenderer() {
        super(new DefaultedBlockGeoModel<>(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, NAME)));
    }
}
