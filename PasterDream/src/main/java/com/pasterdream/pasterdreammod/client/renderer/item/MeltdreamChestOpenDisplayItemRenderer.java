package com.pasterdream.pasterdreammod.client.renderer.item;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.item.MeltdreamChestOpenDisplayItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

/**
 * 融梦水晶箱（打开状态）显示物品渲染器
 * 使用自定义 GeoModel 显式指定纹理路径，与关闭态共用同一张纹理贴图
 * <p>
 * 资源引用：
 * - geo/block/meltdream_chest_1.geo.json
 * - textures/block/meltdream_chest_0.png（与关闭态共用）
 * - animations/block/meltdream_chest_1.animation.json
 */
public class MeltdreamChestOpenDisplayItemRenderer extends GeoItemRenderer<MeltdreamChestOpenDisplayItem> {

    private static final String NAME = "meltdream_chest_1";
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "textures/block/meltdream_chest_0.png");

    /**
     * 构造融梦水晶箱（打开状态）显示物品渲染器
     */
    public MeltdreamChestOpenDisplayItemRenderer() {
        super(new DefaultedBlockGeoModel<>(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, NAME)) {
            @Override
            public ResourceLocation getTextureResource(MeltdreamChestOpenDisplayItem animatable) {
                return TEXTURE;
            }
        });
    }
}