package com.pasterdream.pasterdreammod.worldgen.decor;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

/**
 * 装饰物类型枚举 —— 定义多方块装饰结构的形状分类
 * <p>
 * 每种类型对应一种生成算法，由 {@link GenericDecorationFeature} 统一调度。
 * CUSTOM 类型允许通过自定义生成器扩展新的形状。
 */
public enum DecorationType implements StringRepresentable {
    /** 柱形：从地下延伸到地上的锥形柱体，底部粗顶部细 */
    PILLAR("pillar"),
    /** 团块：不规则椭球状团块，使用随机游走算法 */
    BLOB("blob"),
    /** 尖刺：底部粗尖端细的锥形尖刺 */
    SPIKE("spike"),
    /** 门框：双柱+顶部横梁组成的门框形结构 */
    GATE("gate"),
    /** 散布：在地表随机散布的单个方块群 */
    SCATTER("scatter"),
    /** 水下结构：在水体中生成的结构，需要水环境 */
    AQUATIC("aquatic"),
    /** 自定义：由用户提供生成逻辑的扩展类型 */
    CUSTOM("custom");

    private final String name;

    DecorationType(String name) {
        this.name = name;
    }

    public static final Codec<DecorationType> CODEC = StringRepresentable.fromEnum(DecorationType::values);

    @Override
    public @NotNull String getSerializedName() {
        return name;
    }
}