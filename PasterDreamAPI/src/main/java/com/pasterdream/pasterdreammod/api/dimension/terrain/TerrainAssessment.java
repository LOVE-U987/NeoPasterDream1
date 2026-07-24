package com.pasterdream.pasterdreammod.api.dimension.terrain;

/**
 * 维度对结构地形需求的评估结果。
 * <p>
 * 当 {@link StructureTerrainNegotiator} 评估地形后返回此结果，
 * 包含地形是否适合放置结构、具体高度数据及诊断信息。
 */
public class TerrainAssessment {

    /**
     * 地形评估结果状态。
     */
    public enum Status {
        /** 地形完全满足结构放置要求 */
        SUCCESS,
        /** 地形部分满足要求，可能需要额外处理或调整 */
        PARTIAL,
        /** 地形无法满足结构放置要求 */
        FAILURE
    }

    /** 评估结果状态 */
    private final Status status;
    /** 被评估区域中心区块 X 坐标 */
    private final int assessedChunkX;
    /** 被评估区域中心区块 Z 坐标 */
    private final int assessedChunkZ;
    /** 评估区域平均高度 */
    private final double averageHeight;
    /** 评估区域内最大高度差 */
    private final double maxHeightVariation;
    /** 估计坡度 */
    private final double estimatedSlope;
    /** 诊断信息（供日志和调试使用） */
    private final String diagnosis;
    /** 失败原因，仅在状态为 FAILURE 时有效 */
    private final String failureReason;

    /**
     * 通过 Builder 构造 TerrainAssessment。
     *
     * @param builder 构建器实例
     */
    private TerrainAssessment(Builder builder) {
        this.status = builder.status;
        this.assessedChunkX = builder.assessedChunkX;
        this.assessedChunkZ = builder.assessedChunkZ;
        this.averageHeight = builder.averageHeight;
        this.maxHeightVariation = builder.maxHeightVariation;
        this.estimatedSlope = builder.estimatedSlope;
        this.diagnosis = builder.diagnosis;
        this.failureReason = builder.failureReason;
    }

    /**
     * @return 评估结果状态
     */
    public Status status() { return status; }

    /**
     * @return 被评估区域中心区块 X 坐标
     */
    public int assessedChunkX() { return assessedChunkX; }

    /**
     * @return 被评估区域中心区块 Z 坐标
     */
    public int assessedChunkZ() { return assessedChunkZ; }

    /**
     * @return 评估区域平均高度
     */
    public double averageHeight() { return averageHeight; }

    /**
     * @return 评估区域内最大高度差
     */
    public double maxHeightVariation() { return maxHeightVariation; }

    /**
     * @return 估计坡度
     */
    public double estimatedSlope() { return estimatedSlope; }

    /**
     * @return 诊断信息
     */
    public String diagnosis() { return diagnosis; }

    /**
     * @return 失败原因，仅在 {@link #isFailure()} 为 true 时有效
     */
    public String failureReason() { return failureReason; }

    /**
     * @return 如果评估状态为 SUCCESS 返回 true
     */
    public boolean isSuccess() { return status == Status.SUCCESS; }

    /**
     * @return 如果评估状态为 FAILURE 返回 true
     */
    public boolean isFailure() { return status == Status.FAILURE; }

    /**
     * 创建一个新的 Builder。
     *
     * @return Builder 实例
     */
    public static Builder builder() { return new Builder(); }

    /**
     * 快速创建一个成功评估。
     *
     * @param chunkX    评估的区块 X
     * @param chunkZ    评估的区块 Z
     * @param avgHeight 区域平均高度
     * @param diagnosis 诊断信息
     * @return 成功评估实例
     */
    public static TerrainAssessment success(int chunkX, int chunkZ, double avgHeight, String diagnosis) {
        return builder()
                .status(Status.SUCCESS)
                .assessedChunkX(chunkX).assessedChunkZ(chunkZ)
                .averageHeight(avgHeight)
                .diagnosis(diagnosis)
                .build();
    }

    /**
     * 快速创建一个失败评估。
     *
     * @param chunkX 评估的区块 X
     * @param chunkZ 评估的区块 Z
     * @param reason 失败原因
     * @return 失败评估实例
     */
    public static TerrainAssessment failure(int chunkX, int chunkZ, String reason) {
        return builder()
                .status(Status.FAILURE)
                .assessedChunkX(chunkX).assessedChunkZ(chunkZ)
                .failureReason(reason)
                .diagnosis("评估失败: " + reason)
                .build();
    }

    /**
     * {@link TerrainAssessment} 的构建器。
     * <p>
     * 采用 Builder 模式链式配置评估结果参数，默认状态为 SUCCESS。
     */
    public static class Builder {
        /** 评估状态，默认 SUCCESS */
        private Status status = Status.SUCCESS;
        /** 被评估区块 X 坐标 */
        private int assessedChunkX;
        /** 被评估区块 Z 坐标 */
        private int assessedChunkZ;
        /** 区域平均高度 */
        private double averageHeight;
        /** 区域最大高度差 */
        private double maxHeightVariation;
        /** 估计坡度 */
        private double estimatedSlope;
        /** 诊断信息，默认空字符串 */
        private String diagnosis = "";
        /** 失败原因，默认空字符串 */
        private String failureReason = "";

        /**
         * 设置评估状态。
         *
         * @param val 状态枚举
         * @return 当前构建器
         */
        public Builder status(Status val) { this.status = val; return this; }

        /**
         * 设置被评估区块 X 坐标。
         *
         * @param val X 坐标
         * @return 当前构建器
         */
        public Builder assessedChunkX(int val) { this.assessedChunkX = val; return this; }

        /**
         * 设置被评估区块 Z 坐标。
         *
         * @param val Z 坐标
         * @return 当前构建器
         */
        public Builder assessedChunkZ(int val) { this.assessedChunkZ = val; return this; }

        /**
         * 设置区域平均高度。
         *
         * @param val 平均高度
         * @return 当前构建器
         */
        public Builder averageHeight(double val) { this.averageHeight = val; return this; }

        /**
         * 设置区域最大高度差。
         *
         * @param val 最大高度差
         * @return 当前构建器
         */
        public Builder maxHeightVariation(double val) { this.maxHeightVariation = val; return this; }

        /**
         * 设置估计坡度。
         *
         * @param val 坡度值
         * @return 当前构建器
         */
        public Builder estimatedSlope(double val) { this.estimatedSlope = val; return this; }

        /**
         * 设置诊断信息。
         *
         * @param val 诊断字符串
         * @return 当前构建器
         */
        public Builder diagnosis(String val) { this.diagnosis = val; return this; }

        /**
         * 设置失败原因。
         *
         * @param val 失败原因字符串
         * @return 当前构建器
         */
        public Builder failureReason(String val) { this.failureReason = val; return this; }

        /**
         * 构建 TerrainAssessment 实例。
         */
        public TerrainAssessment build() {
            return new TerrainAssessment(this);
        }
    }

    @Override
    public String toString() {
        return "TerrainAssessment{" +
                "status=" + status +
                ", chunk=(" + assessedChunkX + "," + assessedChunkZ + ")" +
                ", avgHeight=" + String.format("%.1f", averageHeight) +
                ", variation=" + String.format("%.1f", maxHeightVariation) +
                (isFailure() ? ", reason=" + failureReason : "") +
                '}';
    }
}