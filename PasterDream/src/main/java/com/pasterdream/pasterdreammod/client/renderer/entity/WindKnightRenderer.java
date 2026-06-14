package com.pasterdream.pasterdreammod.client.renderer.entity;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.entity.mob.WindKnightEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/**
 * 风之骑士渲染器
 * GeckoLib 动画实体，使用 DefaultedEntityGeoModel 自动加载模型/纹理/动画
 */
public class WindKnightRenderer extends GeoEntityRenderer<WindKnightEntity> {

    private static final String NAME = "wind_knight";

    /**
     * 构造风之骑士渲染器
     *
     * @param context 渲染器上下文
     */
    public WindKnightRenderer(EntityRendererProvider.Context context) {
        super(context, new DefaultedEntityGeoModel<>(
                ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, NAME)));
    }
}