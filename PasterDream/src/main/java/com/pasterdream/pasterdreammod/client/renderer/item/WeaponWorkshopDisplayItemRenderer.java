package com.pasterdream.pasterdreammod.client.renderer.item;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.item.WeaponWorkshopDisplayItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

/**
 * 精铸工坊显示物品渲染器 (Weapon Workshop Display Item Renderer)
 * 使用 DefaultedBlockGeoModel 引用方块模型资源
 *
 * 资源引用：
 * - geo/block/weapon_workshop.geo.json
 * - textures/block/weapon_workshop.png
 * - animations/block/weapon_workshop.animation.json
 */
public class WeaponWorkshopDisplayItemRenderer extends GeoItemRenderer<WeaponWorkshopDisplayItem> {

    private static final String NAME = "weapon_workshop";

    /**
     * 构造精铸工坊显示物品渲染器
     */
    public WeaponWorkshopDisplayItemRenderer() {
        super(new DefaultedBlockGeoModel<>(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, NAME)));
    }
}
