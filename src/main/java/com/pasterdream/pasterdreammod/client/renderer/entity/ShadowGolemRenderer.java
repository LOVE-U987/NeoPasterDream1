package com.pasterdream.pasterdreammod.client.renderer.entity;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.entity.mob.ShadowGolemEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/**
 * 暗影魔像渲染器
 * 使用 GeckoLib 渲染动画实体
 */
public class ShadowGolemRenderer extends GeoEntityRenderer<ShadowGolemEntity> {

    /**
     * 构造暗影魔像渲染器
     *
     * @param context 渲染器上下文
     */
    public ShadowGolemRenderer(EntityRendererProvider.Context context) {
        super(context, new DefaultedEntityGeoModel<>(
                ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "shadow_golem")));
    }
}
