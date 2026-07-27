package com.pasterdream.pasterdreammod.client.model;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.item.ShadowHandLanternItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

/**
 * 暗影提灯 GeckoLib 模型资源绑定。
 * <ul>
 *   <li>geo/shadow_hand_lantern.geo.json</li>
 *   <li>textures/item/shadow_hand_lantern.png</li>
 *   <li>animations/shadow_hand_lantern.animation.json</li>
 * </ul>
 */
public class ShadowHandLanternItemModel extends GeoModel<ShadowHandLanternItem> {

    private static final ResourceLocation MODEL_RL =
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "geo/shadow_hand_lantern.geo.json");
    private static final ResourceLocation TEXTURE_RL =
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "textures/item/shadow_hand_lantern.png");
    private static final ResourceLocation ANIM_RL =
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "animations/shadow_hand_lantern.animation.json");

    @Override
    public ResourceLocation getAnimationResource(ShadowHandLanternItem animatable) {
        return ANIM_RL;
    }

    @Override
    public ResourceLocation getModelResource(ShadowHandLanternItem animatable) {
        return MODEL_RL;
    }

    @Override
    public ResourceLocation getTextureResource(ShadowHandLanternItem animatable) {
        return TEXTURE_RL;
    }
}
