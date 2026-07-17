package com.pasterdream.pasterdreammod.client.renderer.block;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.block.entity.QymDoll0BlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

/**
 * 娇小琴雨梦玩偶方块渲染器 (Qym Doll 0 Block Renderer)
 * 使用 GeckoLib 渲染玩偶的 3D 模型
 */
public class QymDoll0BlockRenderer extends GeoBlockRenderer<QymDoll0BlockEntity> {

    private static final String NAME = "qin_doll_0";

    /**
     * 构造娇小琴雨梦玩偶方块渲染器
     *
     * @param context 渲染器提供者上下文
     */
    public QymDoll0BlockRenderer(BlockEntityRendererProvider.Context context) {
        super(new DefaultedBlockGeoModel<>(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, NAME)));
        PasterDreamMod.LOGGER.debug("[QymDoll0BlockRenderer] 初始化完成，资源名: {} | 模型=geo/block/{}.geo.json 纹理=textures/block/{}.png",
                NAME, NAME, NAME);
    }
}
