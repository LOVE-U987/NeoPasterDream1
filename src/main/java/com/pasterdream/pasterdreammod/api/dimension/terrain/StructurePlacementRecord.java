package com.pasterdream.pasterdreammod.api.dimension.terrain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 结构放置记录 —— 追踪结构放置尝试的统计数据，用于诊断。
 * <p>
 * 记录每个结构的总尝试次数、成功/失败次数及最近失败原因，
 * 当失败率超过阈值时触发诊断日志。
 */
public class StructurePlacementRecord {

    private static final int MAX_RECENT_FAILURES = 10;
    private static final double FAILURE_THRESHOLD = 0.5;

    private final String structureName;
    private final String targetDimensionId;
    private int totalAttempts;
    private int successCount;
    private int failureCount;
    private final List<String> recentFailureReasons;

    /**
     * 构造结构放置记录。
     *
     * @param structureName     结构注册名称
     * @param targetDimensionId 目标维度 ID
     */
    public StructurePlacementRecord(String structureName, String targetDimensionId) {
        this.structureName = structureName;
        this.targetDimensionId = targetDimensionId;
        this.totalAttempts = 0;
        this.successCount = 0;
        this.failureCount = 0;
        this.recentFailureReasons = new ArrayList<>();
    }

    public String structureName() { return structureName; }
    public String targetDimensionId() { return targetDimensionId; }
    public int totalAttempts() { return totalAttempts; }
    public int successCount() { return successCount; }
    public int failureCount() { return failureCount; }

    /**
     * 获取最近失败原因的不可修改列表。
     */
    public List<String> recentFailureReasons() {
        return Collections.unmodifiableList(recentFailureReasons);
    }

    /**
     * 记录一次成功放置。
     */
    public void recordSuccess() {
        totalAttempts++;
        successCount++;
    }

    /**
     * 记录一次失败放置。
     *
     * @param reason 失败原因
     */
    public void recordFailure(String reason) {
        totalAttempts++;
        failureCount++;
        recentFailureReasons.add(reason);
        if (recentFailureReasons.size() > MAX_RECENT_FAILURES) {
            recentFailureReasons.remove(0);
        }
    }

    /**
     * 获取当前失败率。
     *
     * @return 失败率（0.0 ~ 1.0）
     */
    public double getFailureRate() {
        if (totalAttempts == 0) return 0.0;
        return (double) failureCount / totalAttempts;
    }

    /**
     * 判断是否频繁失败（失败率超过阈值且尝试次数足够多）。
     *
     * @return 如果频繁失败返回 true
     */
    public boolean isFailingFrequently() {
        return totalAttempts >= 3 && getFailureRate() > FAILURE_THRESHOLD;
    }

    /**
     * 获取格式化后的诊断摘要字符串。
     *
     * @return 格式化的诊断摘要
     */
    public String toDiagnosticString() {
        StringBuilder sb = new StringBuilder();
        sb.append("  🔍 ").append(structureName)
                .append(" (维度: ").append(targetDimensionId).append(")")
                .append("\n     总尝试: ").append(totalAttempts)
                .append(" | ✅ 成功: ").append(successCount)
                .append(" | ❌ 失败: ").append(failureCount)
                .append(" | 📊 失败率: ").append(String.format("%.1f%%", getFailureRate() * 100));

        if (!recentFailureReasons.isEmpty()) {
            sb.append("\n     最近失败原因:");
            for (int i = 0; i < recentFailureReasons.size(); i++) {
                sb.append("\n       ").append(i + 1).append(". ").append(recentFailureReasons.get(i));
            }
        }
        return sb.toString();
    }
}