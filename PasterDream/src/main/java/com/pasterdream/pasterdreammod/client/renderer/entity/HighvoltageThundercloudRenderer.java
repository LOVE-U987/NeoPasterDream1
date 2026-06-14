package com.pasterdream.pasterdreammod.client.renderer.entity;

import com.pasterdream.pasterdreammod.entity.mob.HighvoltageThundercloudEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/**
 * 高压雷云渲染器
 * 使用自定义 {@link HighvoltageModel} 指向正确的 geo 模型文件 highvoltage_thundercloud.geo.json
 */
public class HighvoltageThundercloudRenderer extends GeoEntityRenderer<HighvoltageThundercloudEntity> {

    /**
     * 构造高压雷云渲染器
     *
     * @param context 渲染器上下文
     */
    public HighvoltageThundercloudRenderer(EntityRendererProvider.Context context) {
        super(context, new HighvoltageModel());
    }
}