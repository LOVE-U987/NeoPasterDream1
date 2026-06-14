package com.pasterdream.pasterdreammod.client.renderer.entity;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.entity.mob.CrazyTerrorbeakEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/**
 * 疯狂恐怖尖喙渲染器
 * 使用 GeckoLib 渲染动画实体
 */
public class CrazyTerrorbeakRenderer extends GeoEntityRenderer<CrazyTerrorbeakEntity> {

    private static final String NAME = "crazy_terrorbeak";

    /**
     * 构造疯狂恐怖尖喙渲染器
     *
     * @param context 渲染器上下文
     */
    public CrazyTerrorbeakRenderer(EntityRendererProvider.Context context) {
        super(context, new DefaultedEntityGeoModel<>(
                ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, NAME)));
    }
}