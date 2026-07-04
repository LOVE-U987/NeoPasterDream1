package com.pasterdream.pasterdreammod.entity.client;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.entity.mob.AaroncosRighthand0Entity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

/**
 * 亚伦柯斯右手模型 (Aaroncos Righthand 0 Model)
 * <p>
 * 自定义 GeoModel，使用 entity.getTexture() 动态获取纹理路径，
 * 支持运行时切换纹理（如受伤变红等效果）。
 */
public class AaroncosRighthand0Model extends GeoModel<AaroncosRighthand0Entity> {

    @Override
    public ResourceLocation getModelResource(AaroncosRighthand0Entity entity) {
        return ResourceLocation.fromNamespaceAndPath(
                PasterDreamMod.MOD_ID, "geo/entity/aaroncos_righthand_0.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(AaroncosRighthand0Entity entity) {
        return ResourceLocation.fromNamespaceAndPath(
                PasterDreamMod.MOD_ID, "textures/entity/" + entity.getTexture() + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(AaroncosRighthand0Entity entity) {
        return ResourceLocation.fromNamespaceAndPath(
                PasterDreamMod.MOD_ID, "animations/entity/aaroncos_righthand_0.animation.json");
    }
}
