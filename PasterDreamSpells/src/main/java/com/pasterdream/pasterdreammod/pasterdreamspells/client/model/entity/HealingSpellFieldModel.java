package com.pasterdream.pasterdreammod.pasterdreamspells.client.model.entity;

import com.pasterdream.pasterdreammod.pasterdreamspells.PasterDreamSpellsMod;
import com.pasterdream.pasterdreammod.pasterdreamspells.entity.mob.HealingSpellFieldEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

/**
 * 治疗法术立场 GeckoLib 模型
 * <p>
 * 资源位于 pasterdreamspells 命名空间下：
 * <ul>
 *   <li>模型：geo/entity/healing_spell.geo.json</li>
 *   <li>动画：animations/entity/healing_spell.animation.json</li>
 *   <li>纹理：textures/entity/healing_spell.png</li>
 * </ul>
 *
 * @author PasterDream
 */
public class HealingSpellFieldModel extends GeoModel<HealingSpellFieldEntity> {

    private static final ResourceLocation MODEL_RESOURCE =
            ResourceLocation.fromNamespaceAndPath(PasterDreamSpellsMod.MOD_ID, "geo/entity/healing_spell.geo.json");
    private static final ResourceLocation ANIMATION_RESOURCE =
            ResourceLocation.fromNamespaceAndPath(PasterDreamSpellsMod.MOD_ID, "animations/entity/healing_spell.animation.json");
    private static final ResourceLocation TEXTURE_RESOURCE =
            ResourceLocation.fromNamespaceAndPath(PasterDreamSpellsMod.MOD_ID, "textures/entity/healing_spell.png");

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
