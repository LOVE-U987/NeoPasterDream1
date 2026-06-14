package com.pasterdream.pasterdreammod.client.renderer.entity;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.entity.mob.FoxFireEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/**
 * 狐火渲染器
 * <p>
 * GeckoLib 动画实体，使用 DefaultedEntityGeoModel 自动加载模型/纹理/动画。
 * 无阴影，缩放 2.4 倍以呈现火焰的视觉效果。
 */
public class FoxFireRenderer extends GeoEntityRenderer<FoxFireEntity> {

    private static final String NAME = "fox_fire";

    /**
     * 构造狐火渲染器
     *
     * @param context 渲染器上下文
     */
    public FoxFireRenderer(EntityRendererProvider.Context context) {
        super(context, new DefaultedEntityGeoModel<>(
                ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, NAME)));
        this.shadowRadius = 0f;
        // 缩放 2.4 倍以匹配原模组视觉效果
        withScale(2.4f);
    }
}