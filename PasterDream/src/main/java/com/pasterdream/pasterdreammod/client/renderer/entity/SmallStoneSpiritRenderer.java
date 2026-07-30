package com.pasterdream.pasterdreammod.client.renderer.entity;

import com.pasterdream.pasterdreammod.client.model.entity.SmallStoneSpiritModel;
import com.pasterdream.pasterdreammod.entity.mob.SmallStoneSpiritEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/**
 * 小石灵渲染器
 * <p>
 * 使用自定义 {@link SmallStoneSpiritModel} 加载 GeckoLib 模型/纹理/动画，
 * 支持头部追踪与动态纹理切换。
 */
public class SmallStoneSpiritRenderer extends GeoEntityRenderer<SmallStoneSpiritEntity> {

    /**
     * 构造小石灵渲染器
     *
     * @param context 渲染器上下文
     */
    public SmallStoneSpiritRenderer(EntityRendererProvider.Context context) {
        super(context, new SmallStoneSpiritModel());
    }
}
