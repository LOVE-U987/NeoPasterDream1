package com.pasterdream.pasterdreammod.client.model.entity;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.entity.mob.FurySpellFieldEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

/**
 * 狂暴法术立场 GeckoLib 模型
 * 资源均为原版素材（geo/动画/纹理）
 */
public class FurySpellFieldModel extends GeoModel<FurySpellFieldEntity> {

    private static final ResourceLocation MODEL_RESOURCE =
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "geo/entity/fury_spell_entity.geo.json");
    private static final ResourceLocation ANIMATION_RESOURCE =
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "animations/entity/fury_spell_entity.animation.json");
    private static final ResourceLocation TEXTURE_RESOURCE =
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "textures/entity/fury_spell_entity.png");

    @Override
    public ResourceLocation getModelResource(FurySpellFieldEntity entity) {
        return MODEL_RESOURCE;
    }

    @Override
    public ResourceLocation getAnimationResource(FurySpellFieldEntity entity) {
        return ANIMATION_RESOURCE;
    }

    @Override
    public ResourceLocation getTextureResource(FurySpellFieldEntity entity) {
        return TEXTURE_RESOURCE;
    }
}
