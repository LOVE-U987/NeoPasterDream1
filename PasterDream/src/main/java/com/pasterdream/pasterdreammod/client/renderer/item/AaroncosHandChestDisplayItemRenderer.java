package com.pasterdream.pasterdreammod.client.renderer.item;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.item.AaroncosHandChestDisplayItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

/**
 * 亚伦柯斯之触战利品箱显示物品渲染器
 * <p>
 * 使用 DefaultedBlockGeoModel 引用方块模型资源：
 * <ul>
 *   <li>模型: {@code geo/block/aaroncos_hand_chest.geo.json}</li>
 *   <li>纹理: {@code textures/block/aaroncos_hand_chest.png}</li>
 *   <li>动画: {@code animations/block/aaroncos_hand_chest.animation.json}</li>
 * </ul>
 */
public class AaroncosHandChestDisplayItemRenderer extends GeoItemRenderer<AaroncosHandChestDisplayItem> {

    private static final String NAME = "aaroncos_hand_chest";

    /**
     * 构造亚伦柯斯之触战利品箱显示物品渲染器
     */
    public AaroncosHandChestDisplayItemRenderer() {
        super(new DefaultedBlockGeoModel<>(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, NAME)));
        PasterDreamMod.LOGGER.debug("[AaroncosHandChestDisplayItemRenderer] 初始化完成，资源名: {} | 模型=geo/block/{}.geo.json 纹理=textures/block/{}.png 动画=animations/block/{}.animation.json",
                NAME, NAME, NAME, NAME);
    }
}
