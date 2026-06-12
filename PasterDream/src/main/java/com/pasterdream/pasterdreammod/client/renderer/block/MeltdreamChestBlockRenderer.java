package com.pasterdream.pasterdreammod.client.renderer.block;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.block.entity.MeltdreamChestBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

/**
 * 融梦水晶箱方块渲染器 (Meltdream Chest Block Renderer)
 * 使用 GeckoLib 渲染融梦水晶箱的 3D 模型和开启动画
 *
 * 资源引用：DefaultedBlockGeoModel 会自动查找
 * - geo/block/meltdream_chest_0.geo.json
 * - textures/block/meltdream_chest_0.png
 * - animations/block/meltdream_chest_0.animation.json
 */
public class MeltdreamChestBlockRenderer extends GeoBlockRenderer<MeltdreamChestBlockEntity> {

    private static final String NAME = "meltdream_chest_0";

    /**
     * 构造融梦水晶箱方块渲染器
     *
     * @param context 渲染器提供者上下文
     */
    public MeltdreamChestBlockRenderer(BlockEntityRendererProvider.Context context) {
        super(new DefaultedBlockGeoModel<>(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, NAME)));
    }
}
