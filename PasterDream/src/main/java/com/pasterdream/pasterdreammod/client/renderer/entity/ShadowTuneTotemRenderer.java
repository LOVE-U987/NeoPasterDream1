package com.pasterdream.pasterdreammod.client.renderer.entity;

import com.pasterdream.pasterdreammod.entity.mob.ShadowTuneTotemEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/**
 * 暗影图腾渲染器
 * 使用自定义 {@link ShadowTuneTotemModel} 指向正确的 geo 模型文件 shadow_rune_totem.geo.json
 */
public class ShadowTuneTotemRenderer extends GeoEntityRenderer<ShadowTuneTotemEntity> {

    /**
     * 构造暗影图腾渲染器
     *
     * @param context 渲染器上下文
     */
    public ShadowTuneTotemRenderer(EntityRendererProvider.Context context) {
        super(context, new ShadowTuneTotemModel());
    }
}