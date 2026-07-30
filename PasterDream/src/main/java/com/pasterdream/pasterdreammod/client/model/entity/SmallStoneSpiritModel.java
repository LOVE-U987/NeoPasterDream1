package com.pasterdream.pasterdreammod.client.model.entity;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.entity.mob.SmallStoneSpiritEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

/**
 * 小石灵 GeckoLib 模型
 * <p>
 * 自定义模型代替 {@link software.bernie.geckolib.model.DefaultedEntityGeoModel}，
 * 提供：
 * <ul>
 *   <li>头部追踪：头骨跟随玩家旋转（原版 {@code SmallStoneSpiritModel}）</li>
 *   <li>动态纹理：通过 {@link SmallStoneSpiritEntity#getTexture()} 支持多纹理</li>
 * </ul>
 */
public class SmallStoneSpiritModel extends GeoModel<SmallStoneSpiritEntity> {

    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(
            PasterDreamMod.MOD_ID, "geo/entity/small_stone_spirit.geo.json");
    private static final ResourceLocation ANIMATION = ResourceLocation.fromNamespaceAndPath(
            PasterDreamMod.MOD_ID, "animations/entity/small_stone_spirit.animation.json");

    @Override
    public ResourceLocation getModelResource(SmallStoneSpiritEntity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(SmallStoneSpiritEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(
                PasterDreamMod.MOD_ID, "textures/entity/" + animatable.getTexture() + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(SmallStoneSpiritEntity animatable) {
        return ANIMATION;
    }

    /**
     * 自定义动画逻辑 —— 让头部骨骼跟随玩家视角旋转
     * <p>
     * 从 {@link DataTickets#ENTITY_MODEL_DATA} 中提取实体模型数据，
     * 应用到名为 "head" 的骨骼上，实现小石灵"看向玩家"的效果。
     */
    @Override
    public void setCustomAnimations(SmallStoneSpiritEntity animatable, long instanceId, AnimationState<SmallStoneSpiritEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        GeoBone head = getAnimationProcessor().getBone("head");
        if (head != null) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
            head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
            head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
        }
    }
}
