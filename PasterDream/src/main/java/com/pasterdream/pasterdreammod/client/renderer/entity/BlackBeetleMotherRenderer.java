package com.pasterdream.pasterdreammod.client.renderer.entity;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.entity.mob.BlackBeetleMotherEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/**
 * 黑甲虫母体渲染器
 * GeckoLib 动画实体，使用 DefaultedEntityGeoModel 自动加载模型/纹理/动画
 */
public class BlackBeetleMotherRenderer extends GeoEntityRenderer<BlackBeetleMotherEntity> {

    private static final String NAME = "black_beetle_mother";

    /**
     * 构造黑甲虫母体渲染器
     *
     * @param context 渲染器上下文
     */
    public BlackBeetleMotherRenderer(EntityRendererProvider.Context context) {
        super(context, new DefaultedEntityGeoModel<>(
                ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, NAME)));
    }
}