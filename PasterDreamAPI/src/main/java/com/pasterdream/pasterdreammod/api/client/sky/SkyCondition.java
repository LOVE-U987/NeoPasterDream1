package com.pasterdream.pasterdreammod.api.client.sky;

import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

/**
 * 天空盒显示条件 —— 决定一条 {@link SkyboxEntry} 是否应在当前上下文激活
 * <p>
 * 支持按生物群系、群系标签、维度、时间窗等维度组合判断，并提供
 * {@link #and} / {@link #or} 组合器。数据包 JSON 中的
 * {@code biomes / biome_tags / dimensions / time} 字段最终也会被解析为
 * 本接口的 Lambda 实现。
 *
 * @see SkyboxRegistry
 * @see SkyboxRenderContext
 */
@FunctionalInterface
public interface SkyCondition {

    /**
     * 判断条件是否满足
     *
     * @param context 渲染上下文
     * @return 是否满足
     */
    boolean matches(SkyboxRenderContext context);

    /**
     * 与组合：两个条件同时满足才通过
     *
     * @param other 另一个条件
     * @return 组合条件
     */
    default SkyCondition and(SkyCondition other) {
        return context -> this.matches(context) && other.matches(context);
    }

    /**
     * 或组合：任一条件满足即通过
     *
     * @param other 另一个条件
     * @return 组合条件
     */
    default SkyCondition or(SkyCondition other) {
        return context -> this.matches(context) || other.matches(context);
    }

    /**
     * 恒真条件（无限制）
     *
     * @return 恒真条件
     */
    static SkyCondition always() {
        return context -> true;
    }

    /**
     * 按生物群系精确匹配
     *
     * @param biome 群系 Key
     * @return 群系条件
     */
    static SkyCondition biome(ResourceKey<Biome> biome) {
        return context -> biome.equals(context.biomeKey());
    }

    /**
     * 按生物群系标签匹配
     *
     * @param tag 群系标签
     * @return 标签条件
     */
    static SkyCondition biomeTag(TagKey<Biome> tag) {
        return context -> context.biome().is(tag);
    }

    /**
     * 按维度匹配
     *
     * @param dimension 维度 Key
     * @return 维度条件
     */
    static SkyCondition dimension(ResourceKey<Level> dimension) {
        return context -> dimension.equals(context.level().dimension());
    }
}
