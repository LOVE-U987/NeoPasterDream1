package com.pasterdream.pasterdreammod.client.renderer.entity;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.entity.mob.ShadowTuneTotemEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

/**
 * 暗影图腾自定义 Geo 模型
 * <p>
 * 因为注册名是 shadow_tune_totem，但 geo 模型文件名是 shadow_rune_totem.geo.json（不一致），
 * 需要自定义模型路径指向正确的文件。
 */
public class ShadowTuneTotemModel extends GeoModel<ShadowTuneTotemEntity> {

    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(
            PasterDreamMod.MOD_ID, "geo/entity/shadow_rune_totem.geo.json");
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            PasterDreamMod.MOD_ID, "textures/entity/shadow_rune_totem.png");
    private static final ResourceLocation ANIMATION = ResourceLocation.fromNamespaceAndPath(
            PasterDreamMod.MOD_ID, "animations/entity/shadow_rune_totem.animation.json");

    @Override
    public ResourceLocation getModelResource(ShadowTuneTotemEntity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(ShadowTuneTotemEntity animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(ShadowTuneTotemEntity animatable) {
        return ANIMATION;
    }
}