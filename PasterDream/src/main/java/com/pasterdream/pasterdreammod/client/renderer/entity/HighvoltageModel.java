package com.pasterdream.pasterdreammod.client.renderer.entity;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.entity.mob.HighvoltageThundercloudEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

/**
 * 高压雷云自定义 Geo 模型
 * <p>
 * 因为 geo 模型文件名是 highvoltage_thundercloud.geo.json（与注册名 highvoltage 不一致），
 * 需要自定义模型路径指向正确的文件。
 */
public class HighvoltageModel extends GeoModel<HighvoltageThundercloudEntity> {

    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(
            PasterDreamMod.MOD_ID, "geo/entity/highvoltage_thundercloud.geo.json");
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            PasterDreamMod.MOD_ID, "textures/entity/highvoltage_thundercloud.png");
    private static final ResourceLocation ANIMATION = ResourceLocation.fromNamespaceAndPath(
            PasterDreamMod.MOD_ID, "animations/entity/highvoltage_thundercloud.animation.json");

    @Override
    public ResourceLocation getModelResource(HighvoltageThundercloudEntity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(HighvoltageThundercloudEntity animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(HighvoltageThundercloudEntity animatable) {
        return ANIMATION;
    }
}