package com.pasterdream.pasterdreammod.api.client.renderer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

/**
 * 通用 DefaultedBlockGeoModel 方块渲染器（entityTranslucent）。
 * <p>
 * 资源约定：{@code geo/block/NAME.geo.json}、{@code textures/block/NAME.png}、
 * {@code animations/block/NAME.animation.json}。
 *
 * @param <T> 须同时为 BlockEntity 与 GeoAnimatable（如 {@code GeoFreeDataBlockEntity}）
 */
public class DefaultedGeoBlockRenderer<T extends BlockEntity & GeoAnimatable> extends GeoBlockRenderer<T> {

    public DefaultedGeoBlockRenderer(ResourceLocation modelId) {
        super(new DefaultedBlockGeoModel<>(modelId));
    }

    public DefaultedGeoBlockRenderer(String namespace, String name) {
        this(ResourceLocation.fromNamespaceAndPath(namespace, name));
    }

    @Override
    public RenderType getRenderType(T animatable, ResourceLocation texture,
                                    MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }
}
