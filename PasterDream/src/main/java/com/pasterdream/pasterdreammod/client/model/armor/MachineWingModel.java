package com.pasterdream.pasterdreammod.client.model.armor;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.item.armor.MachineWingItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

/** 机械之翼 GeckoLib 模型 */
public class MachineWingModel extends GeoModel<MachineWingItem> {

    @Override
    public ResourceLocation getModelResource(MachineWingItem animatable) {
        return ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "geo/machine_wing.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(MachineWingItem animatable) {
        return ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "textures/item/machine_wing.png");
    }

    @Override
    public ResourceLocation getAnimationResource(MachineWingItem animatable) {
        return ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "animations/machine_wing.animation.json");
    }
}
