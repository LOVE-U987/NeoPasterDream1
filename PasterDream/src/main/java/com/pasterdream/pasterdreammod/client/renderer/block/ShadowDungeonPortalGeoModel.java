package com.pasterdream.pasterdreammod.client.renderer.block;

import com.pasterdream.pasterdreammod.block.BrokenShadowDungeonProtalBlock;
import com.pasterdream.pasterdreammod.block.entity.W4GeoDataBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

/**
 * 暗影地牢传送门统一 GeoModel — 根据 animation 属性切换纹理/模型/动画资源。
 * <p>
 * animation=0（破损）或 1（修复中）：使用 {@code broken_shadow_dungeon_protal} 资源；
 * animation=2（已修复）：使用 {@code shadow_dungeon_portal} 资源。
 * 实现"一个方块，不同状态"的效果。
 */
public class ShadowDungeonPortalGeoModel extends GeoModel<W4GeoDataBlockEntity> {

    private static final ResourceLocation BROKEN_MODEL =
            ResourceLocation.fromNamespaceAndPath("pasterdream", "geo/block/broken_shadow_dungeon_protal");
    private static final ResourceLocation BROKEN_TEXTURE =
            ResourceLocation.fromNamespaceAndPath("pasterdream", "textures/block/broken_shadow_dungeon_protal");
    private static final ResourceLocation BROKEN_ANIMATION =
            ResourceLocation.fromNamespaceAndPath("pasterdream", "animations/block/broken_shadow_dungeon_protal");

    private static final ResourceLocation FIXED_MODEL =
            ResourceLocation.fromNamespaceAndPath("pasterdream", "geo/block/shadow_dungeon_portal");
    private static final ResourceLocation FIXED_TEXTURE =
            ResourceLocation.fromNamespaceAndPath("pasterdream", "textures/block/shadow_dungeon_portal");
    private static final ResourceLocation FIXED_ANIMATION =
            ResourceLocation.fromNamespaceAndPath("pasterdream", "animations/block/shadow_dungeon_portal");

    @Override
    public ResourceLocation getModelResource(W4GeoDataBlockEntity animatable) {
        return isFixed(animatable) ? FIXED_MODEL : BROKEN_MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(W4GeoDataBlockEntity animatable) {
        return isFixed(animatable) ? FIXED_TEXTURE : BROKEN_TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(W4GeoDataBlockEntity animatable) {
        return isFixed(animatable) ? FIXED_ANIMATION : BROKEN_ANIMATION;
    }

    /**
     * 判断当前方块实体是否为"已修复"状态（animation=2）
     */
    private static boolean isFixed(W4GeoDataBlockEntity be) {
        if (be.getLevel() == null) {
            return false;
        }
        var state = be.getBlockState();
        if (state.hasProperty(BrokenShadowDungeonProtalBlock.ANIMATION)) {
            return state.getValue(BrokenShadowDungeonProtalBlock.ANIMATION) == 2;
        }
        return false;
    }
}
