package com.pasterdream.pasterdreammod.worldgen.decor;

import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

/**
 * 自定义装饰物生成器接口 —— 为 WorldDecorationAPI 提供 CUSTOM 类型扩展点
 * <p>
 * 实现此接口并注册到 {@link DecorationRegistry#registerCustomGenerator(String, ICustomDecorationGenerator)}，
 * 即可通过 DecorationBuilder 的 CUSTOM 类型使用自定义生成逻辑，
 * 享受 API 的 Builder 链式配置和 JSON 自动生成能力。
 */
@FunctionalInterface
public interface ICustomDecorationGenerator {

    /**
     * 执行自定义装饰物生成逻辑
     *
     * @param context 特征放置上下文（含 level, random, config, origin）
     * @return 是否放置了至少一个方块
     */
    boolean generate(FeaturePlaceContext<DecorationConfig> context);
}
