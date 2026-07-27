package com.pasterdream.pasterdreammod.client.model.armor;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.item.armor.ForsakensWingItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

/** 遗落之翼 GeckoLib 模型 */
public class ForsakensWingModel extends GeoModel<ForsakensWingItem> {

    @Override
    public ResourceLocation getModelResource(ForsakensWingItem animatable) {
        return ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "geo/forsakens_wing.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ForsakensWingItem animatable) {
        return ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "textures/item/forsakens_wing.png");
    }

    @Override
    public ResourceLocation getAnimationResource(ForsakensWingItem animatable) {
        return ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "animations/forsakens_wing.animation.json");
    }
}
