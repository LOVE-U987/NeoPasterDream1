package com.pasterdream.pasterdreammod.client.model.entity;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.entity.mob.ShadowSquealGhost0Entity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ShadowSquealGhost0Model extends GeoModel<ShadowSquealGhost0Entity> {

    private static final ResourceLocation MODEL_RESOURCE = ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "geo/entity/shadow_ghost.geo.json");
    private static final ResourceLocation ANIMATION_RESOURCE = ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "animations/entity/shadow_ghost.animation.json");

    @Override
    public ResourceLocation getAnimationResource(ShadowSquealGhost0Entity entity) {
        return ANIMATION_RESOURCE;
    }

    @Override
    public ResourceLocation getModelResource(ShadowSquealGhost0Entity entity) {
        return MODEL_RESOURCE;
    }

    @Override
    public ResourceLocation getTextureResource(ShadowSquealGhost0Entity entity) {
        return ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "textures/entity/" + entity.getTexture() + ".png");
    }
}
