package com.pasterdream.pasterdreammod.api.dimension.terrain;

/**
 * 维度对结构地形需求的评估结果。
 * <p>
 * 当 {@link StructureTerrainNegotiator} 评估地形后返回此结果，
 * 包含地形是否适合放置结构、具体高度数据及诊断信息。
 */
public class TerrainAssessment {

    /**
     * 评估状态
     */
    public enum Status {
        /** 地形完全满足要求 */
        SUCCESS,
        /** 地形部分满足要求，可能需要额外处理 */
        PARTIAL,
        /** 地形无法满足要求 */
        FAILURE
    }

    private final Status status;
    private final int assessedChunkX;
    private final int assessedChunkZ;
    private final double averageHeight;
    private final double maxHeightVariation;
    private final double estimatedSlope;
    private final String diagnosis;
    private final String failureReason;

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

    public Status status() { return status; }
    public int assessedChunkX() { return assessedChunkX; }
    public int assessedChunkZ() { return assessedChunkZ; }
    public double averageHeight() { return averageHeight; }
    public double maxHeightVariation() { return maxHeightVariation; }
    public double estimatedSlope() { return estimatedSlope; }
    public String diagnosis() { return diagnosis; }
    public String failureReason() { return failureReason; }

    public boolean isSuccess() { return status == Status.SUCCESS; }
    public boolean isFailure() { return status == Status.FAILURE; }

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
     * TerrainAssessment 构建器。
     */
    public static class Builder {
        private Status status = Status.SUCCESS;
        private int assessedChunkX;
        private int assessedChunkZ;
        private double averageHeight;
        private double maxHeightVariation;
        private double estimatedSlope;
        private String diagnosis = "";
        private String failureReason = "";

        public Builder status(Status val) { this.status = val; return this; }
        public Builder assessedChunkX(int val) { this.assessedChunkX = val; return this; }
        public Builder assessedChunkZ(int val) { this.assessedChunkZ = val; return this; }
        public Builder averageHeight(double val) { this.averageHeight = val; return this; }
        public Builder maxHeightVariation(double val) { this.maxHeightVariation = val; return this; }
        public Builder estimatedSlope(double val) { this.estimatedSlope = val; return this; }
        public Builder diagnosis(String val) { this.diagnosis = val; return this; }
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