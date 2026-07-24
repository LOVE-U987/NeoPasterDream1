package com.pasterdream.pasterdreammod.api.dimension.terrain;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * 大型结构向维度声明的地形需求。
 * <p>
 * 当结构通过 {@code RuinBuilder.withTerrainPlatform()} 标记为大型结构时，
 * 会向目标维度发送此需求，维度尝试在不产生明显断层的前提下调整地形。
 */
public class TerrainRequirements {

    /** 结构底部所需平坦区域半径（区块数） */
    private final int requiredFlatRadius;
    /** 地形与结构之间的混合过渡半径（区块数） */
    private final int terrainBlendRadius;
    /** 平坦区域内允许的最大高度差 */
    private final int maxHeightVariation;
    /** 目标维度 ID，null 表示任意维度 */
    @Nullable
    private final String targetDimension;
    /** 优先生物群系分类，null 表示不限制 */
    @Nullable
    private final String preferredBiomeCategory;
    /** 是否要求结构临近水源 */
    private final boolean requireWaterAccess;
    /** 是否允许结构部分嵌入地形 */
    private final boolean allowPartialEmbedding;
    /** 最大可接受坡度（高度差 / 水平距离） */
    private final double maxSlope;

    /**
     * 通过 Builder 构造 TerrainRequirements。
     *
     * @param builder 构建器实例
     */
    private TerrainRequirements(Builder builder) {
        this.requiredFlatRadius = builder.requiredFlatRadius;
        this.terrainBlendRadius = builder.terrainBlendRadius;
        this.maxHeightVariation = builder.maxHeightVariation;
        this.targetDimension = builder.targetDimension;
        this.preferredBiomeCategory = builder.preferredBiomeCategory;
        this.requireWaterAccess = builder.requireWaterAccess;
        this.allowPartialEmbedding = builder.allowPartialEmbedding;
        this.maxSlope = builder.maxSlope;
    }

    /**
     * @return 结构底部所需平坦区域半径（区块数）
     */
    public int requiredFlatRadius() { return requiredFlatRadius; }

    /**
     * @return 地形与结构之间的混合过渡半径（区块数）
     */
    public int terrainBlendRadius() { return terrainBlendRadius; }

    /**
     * @return 平坦区域内允许的最大高度差
     */
    public int maxHeightVariation() { return maxHeightVariation; }

    /**
     * @return 目标维度 ID，可能为 null（表示任意维度）
     */
    @Nullable public String targetDimension() { return targetDimension; }

    /**
     * @return 优先生物群系分类，可能为 null（表示不限制）
     */
    @Nullable public String preferredBiomeCategory() { return preferredBiomeCategory; }

    /**
     * @return 是否要求结构临近水源
     */
    public boolean requireWaterAccess() { return requireWaterAccess; }

    /**
     * @return 是否允许结构部分嵌入地形
     */
    public boolean allowPartialEmbedding() { return allowPartialEmbedding; }

    /**
     * @return 最大可接受坡度
     */
    public double maxSlope() { return maxSlope; }

    /**
     * 判断此需求是否适用于指定的维度。
     *
     * @param dimensionId 目标维度ID
     * @return 如果 targetDimension 为 null（任意维度）或匹配时返回 true
     */
    public boolean matchesDimension(String dimensionId) {
        return targetDimension == null || targetDimension.equals(dimensionId);
    }

    /**
     * 创建一个新的 Builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * {@link TerrainRequirements} 的构建器。
     * <p>
     * 采用 Builder 模式链式配置地形需求参数，所有字段均有合理默认值。
     */
    public static class Builder {
        /** 结构底部所需平坦区域半径，默认 15 */
        private int requiredFlatRadius = 15;
        /** 地形混合过渡半径，默认 5 */
        private int terrainBlendRadius = 5;
        /** 允许的最大高度差，默认 5 */
        private int maxHeightVariation = 5;
        /** 目标维度 ID，默认 null（任意维度） */
        private String targetDimension;
        /** 优先生物群系分类，默认 null（不限制） */
        private String preferredBiomeCategory;
        /** 是否要求临近水源，默认 false */
        private boolean requireWaterAccess = false;
        /** 是否允许部分嵌入，默认 false */
        private boolean allowPartialEmbedding = false;
        /** 最大可接受坡度，默认 0.3 */
        private double maxSlope = 0.3;

        /**
         * 设置结构底部所需平坦区域半径。
         *
         * @param val 半径值（区块数）
         * @return 当前构建器
         */
        public Builder requiredFlatRadius(int val) { this.requiredFlatRadius = val; return this; }

        /**
         * 设置地形与结构之间的混合过渡半径。
         *
         * @param val 半径值（区块数）
         * @return 当前构建器
         */
        public Builder terrainBlendRadius(int val) { this.terrainBlendRadius = val; return this; }

        /**
         * 设置平坦区域内允许的最大高度差。
         *
         * @param val 高度差值
         * @return 当前构建器
         */
        public Builder maxHeightVariation(int val) { this.maxHeightVariation = val; return this; }

        /**
         * 设置目标维度 ID。
         *
         * @param val 维度 ID，null 表示任意维度
         * @return 当前构建器
         */
        public Builder targetDimension(String val) { this.targetDimension = val; return this; }

        /**
         * 设置优先生物群系分类。
         *
         * @param val 生物群系分类，null 表示不限制
         * @return 当前构建器
         */
        public Builder preferredBiomeCategory(String val) { this.preferredBiomeCategory = val; return this; }

        /**
         * 设置是否要求结构临近水源。
         *
         * @param val true 表示需要水源
         * @return 当前构建器
         */
        public Builder requireWaterAccess(boolean val) { this.requireWaterAccess = val; return this; }

        /**
         * 设置是否允许结构部分嵌入地形。
         *
         * @param val true 表示允许嵌入
         * @return 当前构建器
         */
        public Builder allowPartialEmbedding(boolean val) { this.allowPartialEmbedding = val; return this; }

        /**
         * 设置最大可接受坡度。
         *
         * @param val 坡度值（高度差 / 水平距离）
         * @return 当前构建器
         */
        public Builder maxSlope(double val) { this.maxSlope = val; return this; }

        /**
         * 构建 TerrainRequirements 实例。
         *
         * @throws IllegalStateException 如果必要参数无效
         */
        public TerrainRequirements build() {
            if (requiredFlatRadius <= 0) {
                throw new IllegalStateException("requiredFlatRadius 必须大于 0");
            }
            if (terrainBlendRadius <= 0) {
                throw new IllegalStateException("terrainBlendRadius 必须大于 0");
            }
            return new TerrainRequirements(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TerrainRequirements that)) return false;
        return requiredFlatRadius == that.requiredFlatRadius
                && terrainBlendRadius == that.terrainBlendRadius
                && maxHeightVariation == that.maxHeightVariation
                && requireWaterAccess == that.requireWaterAccess
                && allowPartialEmbedding == that.allowPartialEmbedding
                && Double.compare(maxSlope, that.maxSlope) == 0
                && Objects.equals(targetDimension, that.targetDimension)
                && Objects.equals(preferredBiomeCategory, that.preferredBiomeCategory);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requiredFlatRadius, terrainBlendRadius, maxHeightVariation,
                targetDimension, preferredBiomeCategory, requireWaterAccess,
                allowPartialEmbedding, maxSlope);
    }

    @Override
    public String toString() {
        return "TerrainRequirements{" +
                "flatRadius=" + requiredFlatRadius +
                ", blendRadius=" + terrainBlendRadius +
                ", maxVariation=" + maxHeightVariation +
                (targetDimension != null ? ", target=" + targetDimension : "") +
                '}';
    }
}