package com.pasterdream.pasterdreammod.pasterdreamspells.client.model.entity;

import com.pasterdream.pasterdreammod.pasterdreamspells.PasterDreamSpellsMod;
import com.pasterdream.pasterdreammod.pasterdreamspells.entity.mob.FurySpellFieldEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

/**
 * 狂暴法术立场 GeckoLib 模型
 * <p>
 * 资源位于 pasterdreamspells 命名空间下：
 * <ul>
 *   <li>模型：geo/entity/fury_spell_entity.geo.json</li>
 *   <li>动画：animations/entity/fury_spell_entity.animation.json</li>
 *   <li>纹理：textures/entity/fury_spell_entity.png</li>
 * </ul>
 *
 * @author PasterDream
 */
public class FurySpellFieldModel extends GeoModel<FurySpellFieldEntity> {

    private static final ResourceLocation MODEL_RESOURCE =
            ResourceLocation.fromNamespaceAndPath(PasterDreamSpellsMod.MOD_ID, "geo/entity/fury_spell_entity.geo.json");
    private static final ResourceLocation ANIMATION_RESOURCE =
            ResourceLocation.fromNamespaceAndPath(PasterDreamSpellsMod.MOD_ID, "animations/entity/fury_spell_entity.animation.json");
    private static final ResourceLocation TEXTURE_RESOURCE =
            ResourceLocation.fromNamespaceAndPath(PasterDreamSpellsMod.MOD_ID, "textures/entity/fury_spell_entity.png");

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
