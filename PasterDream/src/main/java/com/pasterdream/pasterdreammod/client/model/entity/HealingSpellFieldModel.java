package com.pasterdream.pasterdreammod.client.model.entity;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.entity.mob.HealingSpellFieldEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

/**
 * 治疗法术立场 GeckoLib 模型
 * 资源均为原版素材（geo/动画/纹理）
 */
public class HealingSpellFieldModel extends GeoModel<HealingSpellFieldEntity> {

    private static final ResourceLocation MODEL_RESOURCE =
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "geo/entity/healing_spell.geo.json");
    private static final ResourceLocation ANIMATION_RESOURCE =
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "animations/entity/healing_spell.animation.json");
    private static final ResourceLocation TEXTURE_RESOURCE =
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "textures/entity/healing_spell.png");

    @Override
    public ResourceLocation getModelResource(HealingSpellFieldEntity entity) {
        return MODEL_RESOURCE;
    }

    @Override
    public ResourceLocation getAnimationResource(HealingSpellFieldEntity entity) {
        return ANIMATION_RESOURCE;
    }

    @Override
    public ResourceLocation getTextureResource(HealingSpellFieldEntity entity) {
        return TEXTURE_RESOURCE;
    }
}
