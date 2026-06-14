package com.pasterdream.pasterdreammod.client.renderer.entity;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.entity.mob.AshBoneWingEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/**
 * 灰烬骨翼渲染器
 * 使用 GeckoLib 渲染更强的飞行敌对生物
 */
public class AshBoneWingRenderer extends GeoEntityRenderer<AshBoneWingEntity> {

    private static final String NAME = "ash_bone_wing";

    /**
     * 构造灰烬骨翼渲染器
     *
     * @param context 渲染器上下文
     */
    public AshBoneWingRenderer(EntityRendererProvider.Context context) {
        super(context, new DefaultedEntityGeoModel<>(
                ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, NAME)));
    }
}