package com.pasterdream.pasterdreammod.client.renderer.entity;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.entity.mob.WeakenessTerrorbeakEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/**
 * 虚弱恐怖尖喙渲染器
 * <p>
 * 使用 DefaultedEntityGeoModel 自动加载 geo/entity/weakeness_terrorbeak.geo.json
 * 及其对应的纹理和动画文件。
 */
public class WeakenessTerrorbeakRenderer extends GeoEntityRenderer<WeakenessTerrorbeakEntity> {

    private static final String NAME = "weakeness_terrorbeak";

    /**
     * 构造虚弱恐怖尖喙渲染器
     *
     * @param context 渲染器上下文
     */
    public WeakenessTerrorbeakRenderer(EntityRendererProvider.Context context) {
        super(context, new DefaultedEntityGeoModel<>(
                ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, NAME)));
    }
}