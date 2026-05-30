package com.pasterdream.pasterdreammod.client.renderer.block;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.block.entity.LifeCrystalBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

/**
 * 生命水晶方块渲染器 (Life Crystal Block Renderer)
 * 使用 GeckoLib 渲染生命水晶的 3D 模型和动画
 */
public class LifeCrystalBlockRenderer extends GeoBlockRenderer<LifeCrystalBlockEntity> {

    private static final String NAME = "life_crystal";

    /**
     * 构造生命水晶方块渲染器
     *
     * @param context 渲染器提供者上下文
     */
    public LifeCrystalBlockRenderer(BlockEntityRendererProvider.Context context) {
        super(new DefaultedBlockGeoModel<>(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, NAME)));
        PasterDreamMod.LOGGER.debug("[LifeCrystalBlockRenderer] 初始化完成，资源名: {} | 模型=geo/block/{}.geo.json 纹理=textures/block/{}.png 动画=animations/block/{}.animation.json",
                NAME, NAME, NAME, NAME);
    }
}