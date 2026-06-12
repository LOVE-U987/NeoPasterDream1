package com.pasterdream.pasterdreammod.worldgen.decor;

import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

/**
 * 装饰物放置策略接口 —— 每种 {@link DecorationType} 对应一个独立实现
 * <p>
 * 策略模式的核心接口，将 {@link GenericDecorationFeature} 中的 switch 分发逻辑
 * 解耦为独立的放置器实现。新增装饰物类型时只需：
 * <ol>
 *   <li>在 {@link DecorationType} 中添加新枚举值</li>
 *   <li>创建对应的 {@link DecorationPlacer} 实现</li>
 *   <li>在 {@link GenericDecorationFeature} 的构造函数中注册映射</li>
 * </ol>
 * 无需修改 {@code place()} 方法，符合开放-封闭原则。
 *
 * @see GenericDecorationFeature 使用此接口进行类型分发
 * @see DecorationType 装饰物类型枚举
 */
@FunctionalInterface
public interface DecorationPlacer {

    /**
     * 执行装饰物放置逻辑
     *
     * @param context 特征放置上下文，包含配置、世界、原点、随机源等信息
     * @return true 表示至少放置了一个方块
     */
    boolean place(FeaturePlaceContext<DecorationConfig> context);
}