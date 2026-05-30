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

    private final int requiredFlatRadius;
    private final int terrainBlendRadius;
    private final int maxHeightVariation;
    @Nullable
    private final String targetDimension;
    @Nullable
    private final String preferredBiomeCategory;
    private final boolean requireWaterAccess;
    private final boolean allowPartialEmbedding;
    private final double maxSlope;

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

    public int requiredFlatRadius() { return requiredFlatRadius; }
    public int terrainBlendRadius() { return terrainBlendRadius; }
    public int maxHeightVariation() { return maxHeightVariation; }
    @Nullable public String targetDimension() { return targetDimension; }
    @Nullable public String preferredBiomeCategory() { return preferredBiomeCategory; }
    public boolean requireWaterAccess() { return requireWaterAccess; }
    public boolean allowPartialEmbedding() { return allowPartialEmbedding; }
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
     * TerrainRequirements 构建器。
     */
    public static class Builder {
        private int requiredFlatRadius = 15;
        private int terrainBlendRadius = 5;
        private int maxHeightVariation = 5;
        private String targetDimension;
        private String preferredBiomeCategory;
        private boolean requireWaterAccess = false;
        private boolean allowPartialEmbedding = false;
        private double maxSlope = 0.3;

        public Builder requiredFlatRadius(int val) { this.requiredFlatRadius = val; return this; }
        public Builder terrainBlendRadius(int val) { this.terrainBlendRadius = val; return this; }
        public Builder maxHeightVariation(int val) { this.maxHeightVariation = val; return this; }
        public Builder targetDimension(String val) { this.targetDimension = val; return this; }
        public Builder preferredBiomeCategory(String val) { this.preferredBiomeCategory = val; return this; }
        public Builder requireWaterAccess(boolean val) { this.requireWaterAccess = val; return this; }
        public Builder allowPartialEmbedding(boolean val) { this.allowPartialEmbedding = val; return this; }
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