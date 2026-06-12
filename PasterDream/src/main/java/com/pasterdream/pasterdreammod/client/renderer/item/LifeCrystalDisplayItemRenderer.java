package com.pasterdream.pasterdreammod.client.renderer.item;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.item.LifeCrystalDisplayItem;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

/**
 * 生命水晶显示物品渲染器
 * 使用 DefaultedBlockGeoModel 引用方块模型资源
 */
public class LifeCrystalDisplayItemRenderer extends GeoItemRenderer<LifeCrystalDisplayItem> {

    private static final String NAME = "life_crystal";

    /**
     * 构造生命水晶显示物品渲染器
     */
    public LifeCrystalDisplayItemRenderer() {
        super(new DefaultedBlockGeoModel<>(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, NAME)));
        PasterDreamMod.LOGGER.debug("[LifeCrystalDisplayItemRenderer] 初始化完成，资源名: {} | 模型=geo/block/{}.geo.json 纹理=textures/block/{}.png 动画=animations/block/{}.animation.json",
                NAME, NAME, NAME, NAME);
    }
}