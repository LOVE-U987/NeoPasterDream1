package com.pasterdream.pasterdreammod.client.model.entity;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.entity.mob.TerraswordWaveEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

/**
 * 大地之刃剑气 GeckoLib 模型
 * 资源均为原版素材（geo/动画/纹理），路径沿用本项目 entity 子目录规范
 */
public class TerraswordWaveModel extends GeoModel<TerraswordWaveEntity> {

    private static final ResourceLocation MODEL_RESOURCE =
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "geo/entity/terrasword_wave.geo.json");
    private static final ResourceLocation ANIMATION_RESOURCE =
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "animations/entity/terrasword_wave.animation.json");
    private static final ResourceLocation TEXTURE_RESOURCE =
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "textures/entity/terrasword_wave.png");

    @Override
    public ResourceLocation getModelResource(TerraswordWaveEntity entity) {
        return MODEL_RESOURCE;
    }

    @Override
    public ResourceLocation getAnimationResource(TerraswordWaveEntity entity) {
        return ANIMATION_RESOURCE;
    }

    @Override
    public ResourceLocation getTextureResource(TerraswordWaveEntity entity) {
        return TEXTURE_RESOURCE;
    }
}
