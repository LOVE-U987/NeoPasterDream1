package com.pasterdream.pasterdreammod.client.renderer.entity;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.entity.mob.ShadowSquealGhostEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/**
 * 暗影尖啸幽灵渲染器
 * <p>
 * 注意：与 {@link ShadowGhostEntity} 共享 shadow_ghost.geo.json 模型文件，
 * 因此在渲染器中指向 "shadow_ghost" 资源名。
 */
public class ShadowSquealGhostRenderer extends GeoEntityRenderer<ShadowSquealGhostEntity> {

    private static final String NAME = "shadow_ghost";

    /**
     * 构造暗影尖啸幽灵渲染器
     *
     * @param context 渲染器上下文
     */
    public ShadowSquealGhostRenderer(EntityRendererProvider.Context context) {
        super(context, new DefaultedEntityGeoModel<>(
                ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, NAME)));
    }
}