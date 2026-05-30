package com.pasterdream.pasterdreammod.client.model;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.item.DreamMeterItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

/**
 * 忆梦魔导透镜模型 (Dream Meter Model)
 * 用于加载 GeckoLib 3D 模型资源
 *
 * 资源文件路径：
 * - 模型：assets/pasterdream/geo/dream_meter.geo.json
 * - 纹理：assets/pasterdream/textures/item/dream_meter.png
 * - 动画：assets/pasterdream/animations/dream_meter.animation.json
 */
public class DreamMeterItemModel extends GeoModel<DreamMeterItem> {

    private static final String MODEL_PATH = "geo/dream_meter.geo.json";
    private static final String TEXTURE_PATH = "textures/item/dream_meter.png";
    private static final String ANIM_PATH = "animations/dream_meter.animation.json";

    private static final ResourceLocation MODEL_RL = ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, MODEL_PATH);
    private static final ResourceLocation TEXTURE_RL = ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, TEXTURE_PATH);
    private static final ResourceLocation ANIM_RL = ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, ANIM_PATH);

    @Override
    public ResourceLocation getAnimationResource(DreamMeterItem animatable) {
        return ANIM_RL;
    }

    @Override
    public ResourceLocation getModelResource(DreamMeterItem animatable) {
        return MODEL_RL;
    }

    @Override
    public ResourceLocation getTextureResource(DreamMeterItem animatable) {
        return TEXTURE_RL;
    }
}