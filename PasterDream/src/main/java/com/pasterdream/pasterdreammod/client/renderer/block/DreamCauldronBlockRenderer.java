package com.pasterdream.pasterdreammod.client.renderer.block;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.block.entity.DreamCauldronBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

/**
 * 梦境炼药锅方块渲染器 (Dream Cauldron Block Renderer)
 * 使用 GeckoLib 渲染梦境炼药锅的 3D 模型和动画
 *
 * 资源引用：DefaultedBlockGeoModel 会自动查找
 * - geo/block/dream_cauldron.geo.json
 * - textures/block/dream_cauldron.png
 * - animations/block/dream_cauldron.animation.json
 */
public class DreamCauldronBlockRenderer extends GeoBlockRenderer<DreamCauldronBlockEntity> {

    private static final String NAME = "dream_cauldron";

    /**
     * 构造梦境炼药锅方块渲染器
     *
     * @param context 渲染器提供者上下文
     */
    public DreamCauldronBlockRenderer(BlockEntityRendererProvider.Context context) {
        super(new DefaultedBlockGeoModel<>(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, NAME)));
    }
}
