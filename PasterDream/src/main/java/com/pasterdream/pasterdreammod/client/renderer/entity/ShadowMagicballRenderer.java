package com.pasterdream.pasterdreammod.client.renderer.entity;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.entity.projectile.ShadowMagicballEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/**
 * 暗影魔法弹渲染器
 * <p>
 * GeckoLib 动画实体，使用 DefaultedEntityGeoModel 自动加载模型/纹理/动画。
 * 模型路径: geo/entity/shadow_magicball.geo.json
 * 动画路径: animations/entity/shadow_magicball.animation.json
 * 纹理路径: textures/entity/shadow_magicball.png
 */
public class ShadowMagicballRenderer extends GeoEntityRenderer<ShadowMagicballEntity> {

    private static final String NAME = "shadow_magicball";

    /**
     * 构造暗影魔法弹渲染器
     *
     * @param context 渲染器上下文
     */
    public ShadowMagicballRenderer(EntityRendererProvider.Context context) {
        super(context, new DefaultedEntityGeoModel<>(
                ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, NAME)));
    }
}