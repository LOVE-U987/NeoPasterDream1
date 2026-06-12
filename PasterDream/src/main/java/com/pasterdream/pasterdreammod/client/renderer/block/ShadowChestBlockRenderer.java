package com.pasterdream.pasterdreammod.client.renderer.block;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.block.entity.ShadowChestBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

/**
 * 影之箱方块渲染器 (Shadow Chest Block Renderer)
 * 使用 GeckoLib 渲染影之箱的 3D 模型和开盖动画
 */
public class ShadowChestBlockRenderer extends GeoBlockRenderer<ShadowChestBlockEntity> {

    private static final String NAME = "shadow_chest";

    /**
     * 构造影之箱方块渲染器
     *
     * @param context 渲染器提供者上下文
     */
    public ShadowChestBlockRenderer(BlockEntityRendererProvider.Context context) {
        super(new DefaultedBlockGeoModel<>(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, NAME)));
        PasterDreamMod.LOGGER.debug("[ShadowChestBlockRenderer] 初始化完成，资源名: {} | 模型=geo/block/{}.geo.json 纹理=textures/block/{}.png 动画=animations/block/{}.animation.json",
                NAME, NAME, NAME, NAME);
    }
}