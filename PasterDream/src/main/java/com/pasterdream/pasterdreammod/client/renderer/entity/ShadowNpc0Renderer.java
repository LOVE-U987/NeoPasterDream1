package com.pasterdream.pasterdreammod.client.renderer.entity;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.entity.mob.ShadowNpc0Entity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/**
 * ???（无名NPC）渲染器
 * GeckoLib 动画实体，使用 DefaultedEntityGeoModel 自动加载模型/纹理/动画
 */
public class ShadowNpc0Renderer extends GeoEntityRenderer<ShadowNpc0Entity> {

    private static final String NAME = "shadow_npc_0";

    /**
     * 构造无名 NPC 渲染器
     *
     * @param context 渲染器上下文
     */
    public ShadowNpc0Renderer(EntityRendererProvider.Context context) {
        super(context, new DefaultedEntityGeoModel<>(
                ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, NAME)));
    }
}