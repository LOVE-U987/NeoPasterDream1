package com.pasterdream.pasterdreammod.api.client.util;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * 染色顶点消费者 —— 包装 {@link VertexConsumer}，把 setColor 强制替换为固定颜色
 * <p>
 * 借鉴开源模组 FDLib（作者 FINDERFEED）的 {@code ColoredVertexConsumer} 设计思路
 * （独立实现，非复制）。用于残影/虚影特效：重渲染实体时把原始模型所有顶点的
 * 颜色替换为固定半透明白，产生"透明虚影"效果。
 * <p>
 * 本类为客户端专用，仅由 {@code api/client/**} 路径持有。
 */
@OnlyIn(Dist.CLIENT)
public class ColoredVertexConsumer implements VertexConsumer {

    private final int r;
    private final int g;
    private final int b;
    private final int a;
    private final VertexConsumer original;

    /**
     * 构造染色顶点消费者
     *
     * @param original 原始顶点消费者
     * @param r        固定红（0-255）
     * @param g        固定绿（0-255）
     * @param b        固定蓝（0-255）
     * @param a        固定透明度（0-255，残影常用 ~50）
     */
    public ColoredVertexConsumer(VertexConsumer original, int r, int g, int b, int a) {
        this.original = original;
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    @Override
    public VertexConsumer addVertex(float px, float py, float pz) {
        return original.addVertex(px, py, pz);
    }

    @Override
    public VertexConsumer setColor(int r, int g, int b, int a) {
        // 强制替换为固定颜色（忽略原始模型颜色）
        return original.setColor(this.r, this.g, this.b, this.a);
    }

    @Override
    public VertexConsumer setUv(float u, float v) {
        return original.setUv(u, v);
    }

    @Override
    public VertexConsumer setUv1(int u, int v) {
        return original.setUv1(u, v);
    }

    @Override
    public VertexConsumer setUv2(int u, int v) {
        return original.setUv2(u, v);
    }

    @Override
    public VertexConsumer setNormal(float px, float py, float pz) {
        return original.setNormal(px, py, pz);
    }

    /**
     * 包装 {@link MultiBufferSource}，使所有缓冲都经固定颜色过滤
     *
     * @param source 原始缓冲源
     * @param r      固定红（0-255）
     * @param g      固定绿（0-255）
     * @param b      固定蓝（0-255）
     * @param a      固定透明度（0-255）
     * @return 过滤后的缓冲源
     */
    public static MultiBufferSource wrapBufferSource(MultiBufferSource source, int r, int g, int b, int a) {
        return renderType -> new ColoredVertexConsumer(source.getBuffer(renderType), r, g, b, a);
    }
}
