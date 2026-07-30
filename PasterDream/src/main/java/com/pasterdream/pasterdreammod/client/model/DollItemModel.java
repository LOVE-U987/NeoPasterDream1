package com.pasterdream.pasterdreammod.client.model;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.api.doll.DollAPI;
import com.pasterdream.pasterdreammod.api.doll.DollConfig;
import com.pasterdream.pasterdreammod.item.DollDisplayItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

/**
 * 通用玩偶物品模型
 * <p>
 * 物品形态始终使用基础模型与配置纹理，不切换抱物模型。
 */
public class DollItemModel extends GeoModel<DollDisplayItem> {

    private static final ResourceLocation EMPTY_ANIMATION =
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "animations/block/empty.animation.json");

    @Override
    public ResourceLocation getModelResource(DollDisplayItem animatable) {
        DollConfig config = getConfig(animatable);
        return config != null ? config.model() : missingModel();
    }

    @Override
    public ResourceLocation getTextureResource(DollDisplayItem animatable) {
        DollConfig config = getConfig(animatable);
        return config != null ? config.texture() : missingTexture();
    }

    @Override
    public ResourceLocation getAnimationResource(DollDisplayItem animatable) {
        return EMPTY_ANIMATION;
    }

    private static DollConfig getConfig(DollDisplayItem animatable) {
        return DollAPI.getConfig(animatable.getBlock()).orElse(null);
    }

    private static ResourceLocation missingModel() {
        return ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "geo/block/missing_doll.geo.json");
    }

    private static ResourceLocation missingTexture() {
        return ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "textures/block/missing_doll.png");
    }
}
