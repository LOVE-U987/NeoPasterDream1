package com.pasterdream.pasterdreammod.entity.client;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.entity.mob.AaroncosLefthand0Entity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

/**
 * 亚伦柯斯左手模型 (Aaroncos Lefthand 0 Model)
 * <p>
 * 自定义 GeoModel，使用 entity.getTexture() 动态获取纹理路径，
 * 支持运行时切换纹理（如受伤变红等效果）。
 */
public class AaroncosLefthand0Model extends GeoModel<AaroncosLefthand0Entity> {

    @Override
    public ResourceLocation getModelResource(AaroncosLefthand0Entity entity) {
        return ResourceLocation.fromNamespaceAndPath(
                PasterDreamMod.MOD_ID, "geo/entity/aaroncos_lefthand_0.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(AaroncosLefthand0Entity entity) {
        return ResourceLocation.fromNamespaceAndPath(
                PasterDreamMod.MOD_ID, "textures/entity/" + entity.getTexture() + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(AaroncosLefthand0Entity entity) {
        return ResourceLocation.fromNamespaceAndPath(
                PasterDreamMod.MOD_ID, "animations/entity/aaroncos_lefthand_0.animation.json");
    }
}
