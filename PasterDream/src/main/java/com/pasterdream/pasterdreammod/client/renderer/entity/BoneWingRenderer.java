package com.pasterdream.pasterdreammod.client.renderer.entity;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.entity.mob.BoneWingEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/**
 * 骨翼渲染器
 * 使用 GeckoLib 渲染飞行敌对生物
 */
public class BoneWingRenderer extends GeoEntityRenderer<BoneWingEntity> {

    private static final String NAME = "bone_wing";

    /**
     * 构造骨翼渲染器
     *
     * @param context 渲染器上下文
     */
    public BoneWingRenderer(EntityRendererProvider.Context context) {
        super(context, new DefaultedEntityGeoModel<>(
                ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, NAME)));
    }
}