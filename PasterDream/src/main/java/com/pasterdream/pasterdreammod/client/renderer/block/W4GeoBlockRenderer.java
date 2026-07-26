package com.pasterdream.pasterdreammod.client.renderer.block;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.block.entity.W4GeoDataBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

/**
 * W4 波次通用 GeckoLib 方块渲染器
 * <p>
 * 按注册名装配 DefaultedBlockGeoModel（geo/block/NAME.geo.json、
 * textures/block/NAME.png、animations/block/NAME.animation.json），
 * 渲染层与原版 TileRenderer 一致使用 entityTranslucent。
 */
public class W4GeoBlockRenderer extends GeoBlockRenderer<W4GeoDataBlockEntity> {

    /**
     * 构造通用 GeckoLib 方块渲染器
     *
     * @param name 资源注册名（snake_case）
     */
    public W4GeoBlockRenderer(String name) {
        super(new DefaultedBlockGeoModel<>(
                ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, name)));
    }

    @Override
    public RenderType getRenderType(W4GeoDataBlockEntity animatable, ResourceLocation texture,
                                    MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }
}
