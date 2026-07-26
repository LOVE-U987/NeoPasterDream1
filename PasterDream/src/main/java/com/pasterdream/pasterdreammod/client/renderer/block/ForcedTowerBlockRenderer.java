package com.pasterdream.pasterdreammod.client.renderer.block;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.block.entity.ForcedTowerBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

/**
 * 强征传送塔方块渲染器 (Forced Tower Block Renderer)
 * 使用 GeckoLib 渲染传送塔的 3D 模型与动画（原版使用半透明渲染层）
 *
 * 资源引用：DefaultedBlockGeoModel 会自动查找
 * - geo/block/forced_tower.geo.json
 * - textures/block/forced_tower.png
 * - animations/block/forced_tower.animation.json
 */
public class ForcedTowerBlockRenderer extends GeoBlockRenderer<ForcedTowerBlockEntity> {

    private static final String NAME = "forced_tower";

    /**
     * 构造强征传送塔方块渲染器
     *
     * @param context 渲染器提供者上下文
     */
    public ForcedTowerBlockRenderer(BlockEntityRendererProvider.Context context) {
        super(new DefaultedBlockGeoModel<>(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, NAME)));
    }

    @Override
    public RenderType getRenderType(ForcedTowerBlockEntity animatable, ResourceLocation texture,
                                    MultiBufferSource bufferSource, float partialTick) {
        // 与原版 ForcedTowerTileRenderer 一致：半透明渲染层
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }
}
