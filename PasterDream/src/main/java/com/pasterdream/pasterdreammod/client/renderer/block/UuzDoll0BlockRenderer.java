package com.pasterdream.pasterdreammod.client.renderer.block;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.block.entity.UuzDoll0BlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

/**
 * 娇小幼幼紫玩偶方块渲染器 (Uuz Doll 0 Block Renderer)
 * 使用 GeckoLib 渲染玩偶的 3D 模型
 */
public class UuzDoll0BlockRenderer extends GeoBlockRenderer<UuzDoll0BlockEntity> {

    private static final String NAME = "little_purple_doll_0";

    /**
     * 构造娇小幼幼紫玩偶方块渲染器
     *
     * @param context 渲染器提供者上下文
     */
    public UuzDoll0BlockRenderer(BlockEntityRendererProvider.Context context) {
        super(new DefaultedBlockGeoModel<>(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, NAME)));
        PasterDreamMod.LOGGER.debug("[UuzDoll0BlockRenderer] 初始化完成，资源名: {} | 模型=geo/block/{}.geo.json 纹理=textures/block/{}.png",
                NAME, NAME, NAME);
    }
}
