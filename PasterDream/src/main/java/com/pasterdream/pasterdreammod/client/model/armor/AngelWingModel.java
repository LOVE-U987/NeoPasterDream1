package com.pasterdream.pasterdreammod.client.model.armor;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.item.armor.AngelWingItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

/** 天使之翼 GeckoLib 模型 */
public class AngelWingModel extends GeoModel<AngelWingItem> {

    @Override
    public ResourceLocation getModelResource(AngelWingItem animatable) {
        return ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "geo/angel_wing.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(AngelWingItem animatable) {
        return ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "textures/item/angel_wing.png");
    }

    @Override
    public ResourceLocation getAnimationResource(AngelWingItem animatable) {
        return ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "animations/angel_wing.animation.json");
    }
}
