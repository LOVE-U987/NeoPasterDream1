package com.pasterdream.pasterdreammod.client.model.entity;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.entity.mob.ShadowGhostEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ShadowGhostModel extends GeoModel<ShadowGhostEntity> {

    private static final ResourceLocation MODEL_RESOURCE = ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "geo/entity/shadow_ghost.geo.json");
    private static final ResourceLocation ANIMATION_RESOURCE = ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "animations/entity/shadow_ghost.animation.json");

    @Override
    public ResourceLocation getAnimationResource(ShadowGhostEntity entity) {
        return ANIMATION_RESOURCE;
    }

    @Override
    public ResourceLocation getModelResource(ShadowGhostEntity entity) {
        return MODEL_RESOURCE;
    }

    @Override
    public ResourceLocation getTextureResource(ShadowGhostEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "textures/entity/" + entity.getTexture() + ".png");
    }
}
