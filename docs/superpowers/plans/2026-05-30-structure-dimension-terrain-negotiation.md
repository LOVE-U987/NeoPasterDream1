# 结构-维度地形协商协议 实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task.

**Goal:** 为 DimensionAPI 和 RuinAPI 添加联动功能，实现大型结构的地形需求协商、平滑地形调整、放置统计与诊断日志

**Architecture:** 新增 `StructureTerrainNegotiator` 作为核心桥梁，`TerrainAdjuster` 作为地形调整引擎，通过 `TerrainRequirements` / `TerrainAssessment` 数据对象在 RuinAPI 和 DimensionAPI 之间传递信息

**Tech Stack:** Java 21, NeoForge 1.21.1, Minecraft WorldGen API, SLF4J

---

### Task 1: 创建 TerrainRequirements 数据类

**Files:**
- Create: `src/main/java/com/pasterdream/pasterdreammod/api/dimension/terrain/TerrainRequirements.java`

- [ ] **Step 1: 创建 TerrainRequirements Record**

```java
package com.pasterdream.pasterdreammod.api.dimension.terrain;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * 大型结构向维度声明的地形需求。
 * <p>
 * 当结构通过 {@code RuinBuilder.withTerrainPlatform()} 标记为大型结构时，
 * 会向目标维度发送此需求，维度尝试在不产生明显断层的前提下调整地形。
 * <p>
 * 使用示例：
 * <pre>{@code
 * TerrainRequirements reqs = TerrainRequirements.builder()
 *     .requiredFlatRadius(25)
 *     .terrainBlendRadius(8)
 *     .maxHeightVariation(3)
 *     .targetDimension("pasterdream:dyedream_world")
 *     .build();
 * }</pre>
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
     * 判断此需求是否适用于指定的维度
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
     * TerrainRequirements 构建器
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
```

- [ ] **Step 2: 验证编译通过**

Run: `.\gradlew.bat compileJava`
Expected: BUILD SUCCESSFUL

---

### Task 2: 创建 TerrainAssessment 结果类

**Files:**
- Create: `src/main/java/com/pasterdream/pasterdreammod/api/dimension/terrain/TerrainAssessment.java`

- [ ] **Step 1: 创建 TerrainAssessment Record**

```java
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
     * 快速创建一个成功评估
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
     * 快速创建一个失败评估
     */
    public static TerrainAssessment failure(int chunkX, int chunkZ, String reason) {
        return builder()
                .status(Status.FAILURE)
                .assessedChunkX(chunkX).assessedChunkZ(chunkZ)
                .failureReason(reason)
                .diagnosis("评估失败: " + reason)
                .build();
    }

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
```

- [ ] **Step 2: 验证编译通过**

Run: `.\gradlew.bat compileJava`
Expected: BUILD SUCCESSFUL

---

### Task 3: 创建 StructurePlacementRecord 诊断统计类

**Files:**
- Create: `src/main/java/com/pasterdream/pasterdreammod/api/dimension/terrain/StructurePlacementRecord.java`

- [ ] **Step 1: 创建 StructurePlacementRecord 类**

```java
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
     * 构造结构放置记录
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

    public List<String> recentFailureReasons() {
        return Collections.unmodifiableList(recentFailureReasons);
    }

    /**
     * 记录一次成功放置
     */
    public void recordSuccess() {
        totalAttempts++;
        successCount++;
    }

    /**
     * 记录一次失败放置
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
     * 获取当前失败率
     *
     * @return 失败率（0.0 ~ 1.0）
     */
    public double getFailureRate() {
        if (totalAttempts == 0) return 0.0;
        return (double) failureCount / totalAttempts;
    }

    /**
     * 判断是否频繁失败（失败率超过阈值且尝试次数足够多）
     *
     * @return 如果频繁失败返回 true
     */
    public boolean isFailingFrequently() {
        return totalAttempts >= 3 && getFailureRate() > FAILURE_THRESHOLD;
    }

    /**
     * 获取诊断摘要字符串
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
```

- [ ] **Step 2: 验证编译通过**

Run: `.\gradlew.bat compileJava`
Expected: BUILD SUCCESSFUL

---

### Task 4: 创建 StructureTerrainNegotiator 核心桥梁

**Files:**
- Create: `src/main/java/com/pasterdream/pasterdreammod/api/dimension/terrain/StructureTerrainNegotiator.java`

- [ ] **Step 1: 创建 StructureTerrainNegotiator 类**

```java
package com.pasterdream.pasterdreammod.api.dimension.terrain;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 结构-维度地形协商器 —— 连接 RuinAPI 和 DimensionAPI 的中心协调器。
 * <p>
 * 负责管理大型结构的注册、地形需求存储、放置结果追踪和诊断输出。
 * 采用单例模式，全局唯一实例。
 * <p>
 * 职责：
 * <ul>
 *   <li>注册大型结构及其地形需求</li>
 *   <li>启用维度的支持</li>
 *   <li>评估指定位置的地形是否符合需求</li>
 *   <li>记录放置结果并追踪失败统计</li>
 *   <li>输出诊断日志</li>
 * </ul>
 */
public class StructureTerrainNegotiator {

    private static volatile StructureTerrainNegotiator instance;

    /** 大型结构注册表：结构名称 → 地形需求 */
    private final Map<String, TerrainRequirements> largeStructures = new ConcurrentHashMap<>();

    /** 支持的大型结构的维度集合 */
    private final Set<String> enabledDimensions = ConcurrentHashMap.newKeySet();

    /** 结构放置统计：结构名称 → 放置记录 */
    private final Map<String, StructurePlacementRecord> placementRecords = new ConcurrentHashMap<>();

    /** 结构到目标维度的映射：结构名称 → 维度 ID */
    private final Map<String, String> structureDimensions = new ConcurrentHashMap<>();

    private StructureTerrainNegotiator() {
        PasterDreamMod.LOGGER.info("[TerrainNegotiator] 🏗️ 地形协商器初始化完成");
    }

    /**
     * 获取地形协商器单例
     */
    public static StructureTerrainNegotiator getInstance() {
        if (instance == null) {
            synchronized (StructureTerrainNegotiator.class) {
                if (instance == null) {
                    instance = new StructureTerrainNegotiator();
                }
            }
        }
        return instance;
    }

    // ======================== 注册接口 ========================

    /**
     * 注册一个大型结构及其地形需求
     *
     * @param structureName 结构注册名称
     * @param dimensionId   目标维度 ID
     * @param requirements  地形需求
     */
    public void registerLargeStructure(String structureName, String dimensionId, TerrainRequirements requirements) {
        largeStructures.put(structureName, requirements);
        structureDimensions.put(structureName, dimensionId);
        PasterDreamMod.LOGGER.info("[TerrainNegotiator] 📝 注册大型结构: {} (维度: {}) | 需求: {}",
                structureName, dimensionId, requirements);
    }

    /**
     * 启用维度的大型结构支持
     *
     * @param dimensionId 维度 ID
     */
    public void enableDimensionSupport(String dimensionId) {
        enabledDimensions.add(dimensionId);
        PasterDreamMod.LOGGER.info("[TerrainNegotiator] 🌍 启用维度的大型结构支持: {}", dimensionId);
    }

    // ======================== 查询接口 ========================

    /**
     * 获取已注册的地形需求
     *
     * @param structureName 结构名称
     * @return 地形需求，未注册返回 null
     */
    @Nullable
    public TerrainRequirements getRequirements(String structureName) {
        return largeStructures.get(structureName);
    }

    /**
     * 判断指定维度是否启用了大型结构支持
     */
    public boolean isDimensionEnabled(String dimensionId) {
        return enabledDimensions.contains(dimensionId);
    }

    /**
     * 获取所有注册的大型结构名称
     */
    public Set<String> getRegisteredStructures() {
        return Collections.unmodifiableSet(largeStructures.keySet());
    }

    /**
     * 获取指定区块附近是否有大型结构的需求
     *
     * @param dimensionId 维度 ID
     * @param chunkX      区块 X
     * @param chunkZ      区块 Z
     * @return 匹配的结构名称列表
     */
    public List<String> findMatchingStructures(String dimensionId, int chunkX, int chunkZ) {
        List<String> matches = new ArrayList<>();
        for (Map.Entry<String, TerrainRequirements> entry : largeStructures.entrySet()) {
            String name = entry.getKey();
            TerrainRequirements reqs = entry.getValue();
            String targetDim = structureDimensions.get(name);

            if (targetDim != null && targetDim.equals(dimensionId)) {
                matches.add(name);
            }
        }
        return matches;
    }

    // ======================== 地形评估接口 ========================

    /**
     * 评估指定位置的地形是否适合放置结构
     *
     * @param structureName 结构名称
     * @param chunkX        区块 X
     * @param chunkZ        区块 Z
     * @param level         世界实例
     * @return 地形评估结果
     */
    public TerrainAssessment assessTerrain(String structureName, int chunkX, int chunkZ, Level level) {
        TerrainRequirements reqs = largeStructures.get(structureName);
        if (reqs == null) {
            return TerrainAssessment.failure(chunkX, chunkZ,
                    "结构 [" + structureName + "] 未注册为大型结构");
        }

        int centerX = chunkX * 16 + 8;
        int centerZ = chunkZ * 16 + 8;

        double avgHeight = TerrainAdjuster.calculateAverageHeight(level, centerX, centerZ, reqs.requiredFlatRadius());
        double maxVar = TerrainAdjuster.calculateMaxVariation(level, centerX, centerZ, reqs.requiredFlatRadius());
        double slope = TerrainAdjuster.estimateSlope(level, centerX, centerZ, reqs.requiredFlatRadius());

        if (maxVar <= reqs.maxHeightVariation() && slope <= reqs.maxSlope()) {
            return TerrainAssessment.success(chunkX, chunkZ, avgHeight,
                    String.format("地形适宜: 起伏=%.1f, 坡度=%.2f, 平均高度=%.1f", maxVar, slope, avgHeight));
        }

        return TerrainAssessment.builder()
                .status(TerrainAssessment.Status.PARTIAL)
                .assessedChunkX(chunkX).assessedChunkZ(chunkZ)
                .averageHeight(avgHeight)
                .maxHeightVariation(maxVar)
                .estimatedSlope(slope)
                .diagnosis(String.format("地形需调整: 起伏=%.1f(需≤%d), 坡度=%.2f(需≤%.2f)",
                        maxVar, reqs.maxHeightVariation(), slope, reqs.maxSlope()))
                .build();
    }

    // ======================== 放置反馈接口 ========================

    /**
     * 报告结构放置结果
     *
     * @param structureName 结构名称
     * @param success       是否成功
     * @param reason        失败原因（成功时可传空字符串）
     */
    public void reportPlacement(String structureName, boolean success, String reason) {
        StructurePlacementRecord record = placementRecords.computeIfAbsent(structureName,
                name -> new StructurePlacementRecord(name,
                        structureDimensions.getOrDefault(name, "unknown")));

        if (success) {
            record.recordSuccess();
        } else {
            record.recordFailure(reason);
            PasterDreamMod.LOGGER.warn("[TerrainNegotiator] ⚠️ 结构 [{}] 放置失败: {}", structureName, reason);

            if (record.isFailingFrequently()) {
                PasterDreamMod.LOGGER.error("[TerrainNegotiator] ❌ 结构 [{}] 频繁失败！失败率: {:.1%}",
                        structureName, record.getFailureRate());
                printStructureDiagnostics(structureName);
            }
        }
    }

    /**
     * 获取结构的放置统计记录
     *
     * @param structureName 结构名称
     * @return 放置记录，不存在返回 null
     */
    @Nullable
    public StructurePlacementRecord getPlacementRecord(String structureName) {
        return placementRecords.get(structureName);
    }

    /**
     * 获取所有放置记录
     */
    public Map<String, StructurePlacementRecord> getAllPlacementRecords() {
        return Collections.unmodifiableMap(placementRecords);
    }

    // ======================== 诊断接口 ========================

    /**
     * 打印所有频繁失败的结构的诊断信息
     */
    public void printDiagnostics() {
        boolean hasFailing = false;
        StringBuilder sb = new StringBuilder();
        sb.append("\n").append("=".repeat(60));
        sb.append("\n  🏗️ 结构生成诊断报告");
        sb.append("\n").append("=".repeat(60));

        for (StructurePlacementRecord record : placementRecords.values()) {
            sb.append("\n").append(record.toDiagnosticString());
            if (record.isFailingFrequently()) {
                hasFailing = true;
                sb.append(" ⚠️ 频繁失败");
            }
        }

        if (placementRecords.isEmpty()) {
            sb.append("\n  📭 暂无结构放置记录");
        }

        sb.append("\n").append("=".repeat(60));

        if (hasFailing) {
            PasterDreamMod.LOGGER.error("[TerrainNegotiator] {}", sb);
        } else {
            PasterDreamMod.LOGGER.info("[TerrainNegotiator] {}", sb);
        }
    }

    /**
     * 打印指定结构的详细诊断信息
     *
     * @param structureName 结构名称
     */
    public void printStructureDiagnostics(String structureName) {
        StructurePlacementRecord record = placementRecords.get(structureName);
        if (record == null) {
            PasterDreamMod.LOGGER.warn("[TerrainNegotiator] 结构 [{}] 无放置记录", structureName);
            return;
        }

        TerrainRequirements reqs = largeStructures.get(structureName);
        StringBuilder sb = new StringBuilder();
        sb.append("\n").append("─".repeat(50));
        sb.append("\n  📋 结构诊断详情: ").append(structureName);
        sb.append("\n").append("─".repeat(50));
        sb.append("\n  ").append(record.toDiagnosticString());

        if (reqs != null) {
            sb.append("\n\n  📐 地形需求配置:");
            sb.append("\n     平地半径: ").append(reqs.requiredFlatRadius());
            sb.append("\n     过渡宽度: ").append(reqs.terrainBlendRadius());
            sb.append("\n     最大起伏: ").append(reqs.maxHeightVariation());
            sb.append("\n     最大坡度: ").append(String.format("%.2f", reqs.maxSlope()));
            if (reqs.targetDimension() != null) {
                sb.append("\n     目标维度: ").append(reqs.targetDimension());
            }
            sb.append("\n     允许嵌入: ").append(reqs.allowPartialEmbedding());
        }

        sb.append("\n").append("─".repeat(50));
        PasterDreamMod.LOGGER.error("[TerrainNegotiator] {}", sb);
    }

    /**
     * 重置所有统计（用于测试）
     */
    public void reset() {
        largeStructures.clear();
        enabledDimensions.clear();
        placementRecords.clear();
        structureDimensions.clear();
        PasterDreamMod.LOGGER.info("[TerrainNegotiator] 🔄 已重置所有统计数据");
    }
}
```

- [ ] **Step 2: 验证编译通过**

Run: `.\gradlew.bat compileJava`
Expected: BUILD SUCCESSFUL

---

### Task 5: 创建 TerrainAdjuster 地形调整引擎

**Files:**
- Create: `src/main/java/com/pasterdream/pasterdreammod/api/dimension/terrain/TerrainAdjuster.java`

- [ ] **Step 1: 创建 TerrainAdjuster 类**

```java
package com.pasterdream.pasterdreammod.api.dimension.terrain;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;

/**
 * 地形调整引擎 —— 采用隆起平台策略，在不产生明显断层的前提下调整地形。
 * <p>
 * 核心算法通过余弦插值实现平滑过渡：
 * <pre>
 * 原始地形：  ⛰️⛰️⛰️⛰️⛰️⛰️⛰️⛰️⛰️⛰️
 *                          ↓
 * 隆起平台：  ⛰️⛰️╱￣￣￣￣￣￣￣╲⛰️⛰️
 *                   ↑ 平地 ↑
 *                 ← 渐变区 →  ← 渐变区 →
 * </pre>
 */
public class TerrainAdjuster {

    private TerrainAdjuster() {
        throw new UnsupportedOperationException("TerrainAdjuster 是纯静态工具类，不可实例化");
    }

    /**
     * 在指定区块区域创建平滑平台
     * <p>
     * 将区块内指定中心的区域地形抬升/降低为平地，
     * 边缘使用余弦插值与原地形无缝衔接。
     *
     * @param chunk        目标区块
     * @param centerX      平台中心 X（世界坐标）
     * @param centerZ      平台中心 Z（世界坐标）
     * @param flatRadius   平地半径
     * @param blendRadius  渐变过渡半径
     */
    public static void createSmoothPlatform(ChunkAccess chunk, int centerX, int centerZ,
                                            int flatRadius, int blendRadius) {
        int totalRadius = flatRadius + blendRadius;
        int minBX = centerX - totalRadius;
        int maxBX = centerX + totalRadius;
        int minBZ = centerZ - totalRadius;
        int maxBZ = centerZ + totalRadius;

        int chunkMinX = chunk.getPos().getMinBlockX();
        int chunkMaxX = chunk.getPos().getMaxBlockX();
        int chunkMinZ = chunk.getPos().getMinBlockZ();
        int chunkMaxZ = chunk.getPos().getMaxBlockZ();

        int startX = Math.max(minBX, chunkMinX);
        int endX = Math.min(maxBX, chunkMaxX);
        int startZ = Math.max(minBZ, chunkMinZ);
        int endZ = Math.min(maxBZ, chunkMaxZ);

        PasterDreamMod.LOGGER.debug("[TerrainAdjuster] 🏗️ 创建平滑平台: center=({},{}), flat={}, blend={}, total={}",
                centerX, centerZ, flatRadius, blendRadius, totalRadius);

        int count = 0;
        for (int x = startX; x <= endX; x++) {
            for (int z = startZ; z <= endZ; z++) {
                double distance = Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(z - centerZ, 2));

                double blendFactor;
                if (distance <= flatRadius) {
                    blendFactor = 1.0;
                } else if (distance <= totalRadius) {
                    double t = (distance - flatRadius) / blendRadius;
                    blendFactor = cosineBlend(1.0 - t);
                } else {
                    continue;
                }

                int originalHeight = getSurfaceHeight(chunk, x, z);
                int targetHeight = calculateTargetHeight(chunk, centerX, centerZ, flatRadius);
                int adjustedHeight = (int) Math.round(originalHeight + (targetHeight - originalHeight) * blendFactor);

                adjustColumn(chunk, x, z, originalHeight, adjustedHeight);
                count++;
            }
        }

        PasterDreamMod.LOGGER.debug("[TerrainAdjuster] ✅ 平台创建完成: 调整了 {} 个方块列", count);
    }

    /**
     * 计算指定区域的平均地形高度
     *
     * @param level  世界实例
     * @param centerX 中心 X
     * @param centerZ 中心 Z
     * @param radius  采样半径
     * @return 平均高度
     */
    public static double calculateAverageHeight(Level level, int centerX, int centerZ, int radius) {
        long totalHeight = 0;
        int sampleCount = 0;

        for (int x = centerX - radius; x <= centerX + radius; x++) {
            for (int z = centerZ - radius; z <= centerZ + radius; z++) {
                BlockPos pos = new BlockPos(x, 0, z);
                int height = level.getHeight();
                sampleCount++;
                totalHeight += height;
            }
        }

        return sampleCount > 0 ? (double) totalHeight / sampleCount : 64.0;
    }

    /**
     * 计算指定区域的最大地形起伏
     *
     * @param level  世界实例
     * @param centerX 中心 X
     * @param centerZ 中心 Z
     * @param radius  采样半径
     * @return 最大高度差
     */
    public static double calculateMaxVariation(Level level, int centerX, int centerZ, int radius) {
        double min = Double.MAX_VALUE;
        double max = -Double.MAX_VALUE;

        for (int x = centerX - radius; x <= centerX + radius; x++) {
            for (int z = centerZ - radius; z <= centerZ + radius; z++) {
                BlockPos pos = new BlockPos(x, 0, z);
                int height = level.getHeight();
                if (height < min) min = height;
                if (height > max) max = height;
            }
        }

        return (min == Double.MAX_VALUE) ? 0.0 : max - min;
    }

    /**
     * 估算区域坡度
     *
     * @param level  世界实例
     * @param centerX 中心 X
     * @param centerZ 中心 Z
     * @param radius  采样半径
     * @return 估算坡度值（0~1）
     */
    public static double estimateSlope(Level level, int centerX, int centerZ, int radius) {
        double variation = calculateMaxVariation(level, centerX, centerZ, radius);
        double maxPossibleVariation = level.getMaxBuildHeight() - level.getMinBuildHeight();
        return maxPossibleVariation > 0 ? variation / maxPossibleVariation : 0.0;
    }

    /**
     * 获取地表高度
     */
    private static int getSurfaceHeight(ChunkAccess chunk, int x, int z) {
        int maxY = chunk.getMaxBuildHeight();
        int minY = chunk.getMinBuildHeight();

        for (int y = maxY - 1; y >= minY; y--) {
            BlockState state = chunk.getBlockState(new BlockPos(x, y, z));
            if (!state.isAir() && !state.liquid()) {
                return y;
            }
        }
        return minY;
    }

    /**
     * 计算平台目标高度（取中心区域的平均高度）
     */
    private static int calculateTargetHeight(ChunkAccess chunk, int centerX, int centerZ, int flatRadius) {
        long total = 0;
        int count = 0;
        int sampleStep = Math.max(1, flatRadius / 4);

        for (int x = centerX - flatRadius; x <= centerX + flatRadius; x += sampleStep) {
            for (int z = centerZ - flatRadius; z <= centerZ + flatRadius; z += sampleStep) {
                total += getSurfaceHeight(chunk, x, z);
                count++;
            }
        }

        return count > 0 ? (int) Math.round((double) total / count) : 64;
    }

    /**
     * 调整单个方块列的地形
     */
    private static void adjustColumn(ChunkAccess chunk, int x, int z,
                                     int originalHeight, int targetHeight) {
        int heightDiff = targetHeight - originalHeight;

        if (heightDiff > 0) {
            fillColumn(chunk, x, z, originalHeight + 1, targetHeight, Blocks.STONE.defaultBlockState());
        } else if (heightDiff < 0) {
            removeColumn(chunk, x, z, targetHeight + 1, originalHeight);
        }
    }

    /**
     * 填充方块列
     */
    private static void fillColumn(ChunkAccess chunk, int x, int z,
                                   int startY, int endY, BlockState fillState) {
        BlockState topState = Blocks.GRASS_BLOCK.defaultBlockState();
        for (int y = startY; y <= endY; y++) {
            BlockState state = (y == endY) ? topState : fillState;
            chunk.setBlockState(new BlockPos(x, y, z), state, false);
        }
    }

    /**
     * 移除方块列
     */
    private static void removeColumn(ChunkAccess chunk, int x, int z,
                                     int startY, int endY) {
        for (int y = startY; y <= endY; y++) {
            chunk.setBlockState(new BlockPos(x, y, z), Blocks.AIR.defaultBlockState(), false);
        }
    }

    /**
     * 余弦插值函数
     * <p>
     * 使用 {@code (1 - cos(π * t)) / 2} 实现平滑过渡。
     * t=0 时返回 0（完全使用原始值），t=1 时返回 1（完全使用目标值）。
     *
     * @param t 插值参数（0~1）
     * @return 插值后的混合因子
     */
    public static double cosineBlend(double t) {
        t = Math.max(0.0, Math.min(1.0, t));
        return (1.0 - Math.cos(Math.PI * t)) / 2.0;
    }
}
```

- [ ] **Step 2: 验证编译通过**

Run: `.\gradlew.bat compileJava`
Expected: BUILD SUCCESSFUL

---

### Task 6: 修改 RuinResult 添加地形需求字段

**Files:**
- Modify: `src/main/java/com/pasterdream/pasterdreammod/api/ruin/RuinResult.java`

- [ ] **Step 1: 为 RuinResult 添加 terrainRequirements 字段

将 RuinResult 从 record 改为普通类以支持可选字段，或保留 record 但添加新构造方法

```java
package com.pasterdream.pasterdreammod.api.ruin;

import com.pasterdream.pasterdreammod.api.dimension.terrain.TerrainRequirements;
import com.pasterdream.pasterdreammod.api.ruin.builder.RuinBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.StructureType;

import javax.annotation.Nullable;

/**
 * 遗迹/结构注册结果 —— 包含结构相关的所有 ResourceKey 引用
 * <p>
 * 由 {@link RuinBuilder#build()} 返回，
 * 持有结构类型、结构实例和结构集的 ResourceKey，
 * 方便在代码中引用已注册的结构资源。
 * <p>
 * 使用示例：
 * <pre>{@code
 * RuinResult result = RuinAPI.createRuin("dyedream_ruins")
 *     .biomeTag("pasterdream:is_dyedream")
 *     .templatePool("pasterdream:dyedream_ruins_pool")
 *     .structureClass(DyedreamRuinsStructure.class)
 *     .codec(DyedreamRuinsStructure.CODEC)
 *     .build();
 *
 * ResourceKey<Structure> structureKey = result.structureKey();
 * }</pre>
 *
 * @param name                 结构注册名称
 * @param typeKey              结构类型 ResourceKey
 * @param structureKey         结构实例 ResourceKey
 * @param setKey               结构集 ResourceKey，可能为 null
 * @param terrainRequirements  地形需求（大型结构用），可能为 null
 */
public record RuinResult(
        String name,
        ResourceKey<StructureType<?>> typeKey,
        ResourceKey<Structure> structureKey,
        @Nullable ResourceKey<StructureSet> setKey,
        @Nullable TerrainRequirements terrainRequirements
) {

    public RuinResult {
        if (name == null) {
            throw new IllegalArgumentException("RuinResult: name 不能为 null");
        }
        if (typeKey == null) {
            throw new IllegalArgumentException("RuinResult: typeKey 不能为 null");
        }
        if (structureKey == null) {
            throw new IllegalArgumentException("RuinResult: structureKey 不能为 null");
        }
    }

    /**
     * 创建一个仅包含结构和类型 Key 的初始结果（无结构集、无地形需求）
     */
    public static RuinResult of(String modId, String name) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(modId, name);
        return new RuinResult(
                name,
                ResourceKey.create(Registries.STRUCTURE_TYPE, id),
                ResourceKey.create(Registries.STRUCTURE, id),
                null,
                null
        );
    }

    /**
     * 创建一个包含地形需求的结果
     */
    public static RuinResult of(String modId, String name, @Nullable TerrainRequirements terrainRequirements) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(modId, name);
        return new RuinResult(
                name,
                ResourceKey.create(Registries.STRUCTURE_TYPE, id),
                ResourceKey.create(Registries.STRUCTURE, id),
                null,
                terrainRequirements
        );
    }

    public boolean hasSetKey() {
        return setKey != null;
    }

    /**
     * 判断是否为大型结构（附带地形需求）
     */
    public boolean isLargeStructure() {
        return terrainRequirements != null;
    }

    public RuinResult withSetKey(String setName, String modId) {
        ResourceLocation setId = ResourceLocation.fromNamespaceAndPath(modId, setName);
        return new RuinResult(
                name, typeKey, structureKey,
                ResourceKey.create(Registries.STRUCTURE_SET, setId),
                terrainRequirements
        );
    }

    public RuinResult withSetKey(ResourceKey<StructureSet> setKey) {
        return new RuinResult(name, typeKey, structureKey, setKey, terrainRequirements);
    }
}
```

- [ ] **Step 2: 验证编译通过**

Run: `.\gradlew.bat compileJava`
Expected: BUILD SUCCESSFUL

---

### Task 7: 修改 RuinBuilder 添加大型结构支持

**Files:**
- Modify: `src/main/java/com/pasterdream/pasterdreammod/api/ruin/builder/RuinBuilder.java`

- [ ] **Step 1: 在 RuinBuilder 中添加导入和字段

添加以下导入：
```java
import com.pasterdream.pasterdreammod.api.dimension.terrain.TerrainRequirements;
import com.pasterdream.pasterdreammod.api.dimension.terrain.StructureTerrainNegotiator;
```

在 `private String basePath;` 后添加：
```java
/** 大型结构的地形需求（null 表示为普通结构） */
private TerrainRequirements terrainRequirements;
```

- [ ] **Step 2: 添加 largeStructure() 和 withTerrainPlatform() 方法

在 `basePath()` 方法之后、`build()` 方法之前添加：

```java
    // ======================== 大型结构支持 ========================

    /**
     * 将此结构标记为大型结构，并指定完整的地形需求
     * <p>
     * 启用与 DimensionAPI 的地形协商机制：
     * 维度会在区块生成时尝试为结构调整地形，
     * 避免产生明显断层，并返回评估结果。
     *
     * @param reqs 地形需求
     * @return 当前构建器实例
     */
    public RuinBuilder largeStructure(TerrainRequirements reqs) {
        this.terrainRequirements = reqs;
        PasterDreamMod.LOGGER.info("[RuinBuilder] 🏗️ 标记为大型结构: {} | 需求={}", structureName, reqs);
        return this;
    }

    /**
     * 快捷方法：将此结构标记为大型结构并配置平地平台
     * <p>
     * 自动计算渐变半径 = max(5, flatRadius / 3)，并启用地形协商。
     *
     * @param flatRadius 需要的平地半径（格）
     * @return 当前构建器实例
     */
    public RuinBuilder withTerrainPlatform(int flatRadius) {
        TerrainRequirements reqs = TerrainRequirements.builder()
                .requiredFlatRadius(flatRadius)
                .terrainBlendRadius(Math.max(5, flatRadius / 3))
                .targetDimension(modId + ":" + structureName)
                .build();
        return largeStructure(reqs);
    }
```

- [ ] **Step 3: 修改 build() 方法，将 terrainRequirements 传入 RuinResult

找到 `RuinResult result = RuinResult.of(modId, structureName);`
替换为：
```java
        RuinResult result = (terrainRequirements != null)
                ? RuinResult.of(modId, structureName, terrainRequirements)
                : RuinResult.of(modId, structureName);
```

- [ ] **Step 4: 在 build() 方法中，创建 RuinResult 后，如果是大型结构则注册到 Negotiatior

在 `com.pasterdream.pasterdreammod.api.ruin.RuinAPI.cacheRuin(result);` 之前添加：
```java
        // 如果是大型结构，注册到地形协商器
        if (terrainRequirements != null) {
            StructureTerrainNegotiator negotiator = StructureTerrainNegotiator.getInstance();
            negotiator.registerLargeStructure(structureName, modId, terrainRequirements);
            PasterDreamMod.LOGGER.info("[RuinBuilder] 🔗 已注册大型结构到地形协商器: {}", structureName);
        }
```

- [ ] **Step 5: 验证编译通过**

Run: `.\gradlew.bat compileJava`
Expected: BUILD SUCCESSFUL

---

### Task 8: 修改 RuinAPI 添加诊断方法

**Files:**
- Modify: `src/main/java/com/pasterdream/pasterdreammod/api/ruin/RuinAPI.java`

- [ ] **Step 1: 添加导入

```java
import com.pasterdream.pasterdreammod.api.dimension.terrain.StructureTerrainNegotiator;
import com.pasterdream.pasterdreammod.api.dimension.terrain.StructurePlacementRecord;
import com.pasterdream.pasterdreammod.api.dimension.terrain.TerrainAssessment;
import net.minecraft.world.level.Level;
```

- [ ] **Step 2: 添加诊断方法

在 `cacheRuin()` 方法之后、类末尾之前添加：

```java
    // ======================== 大型结构诊断 ========================

    /**
     * 打印所有已注册结构（含大型结构）的生成诊断报告
     * <p>
     * 调用后会在控制台输出详细的放置统计信息，
     * 包括成功/失败次数、失败率、最近失败原因等。
     * 频繁失败的结构会标记警示信息。
     */
    public static void printStructureDiagnostics() {
        StructureTerrainNegotiator negotiator = StructureTerrainNegotiator.getInstance();
        negotiator.printDiagnostics();
    }

    /**
     * 打印指定结构的详细诊断信息
     *
     * @param structureName 结构注册名称
     */
    public static void printStructureDiagnostics(String structureName) {
        StructureTerrainNegotiator negotiator = StructureTerrainNegotiator.getInstance();
        negotiator.printStructureDiagnostics(structureName);
    }

    /**
     * 评估指定位置的地形是否适合放置指定的结构
     *
     * @param structureName 结构注册名称
     * @param chunkX        区块 X 坐标
     * @param chunkZ        区块 Z 坐标
     * @param level         世界实例
     * @return 地形评估结果
     */
    public static TerrainAssessment assessTerrain(String structureName, int chunkX, int chunkZ, Level level) {
        StructureTerrainNegotiator negotiator = StructureTerrainNegotiator.getInstance();
        return negotiator.assessTerrain(structureName, chunkX, chunkZ, level);
    }

    /**
     * 报告结构放置结果
     *
     * @param structureName 结构注册名称
     * @param success       是否成功放置
     * @param reason        失败原因（成功时传空字符串）
     */
    public static void reportPlacement(String structureName, boolean success, String reason) {
        StructureTerrainNegotiator negotiator = StructureTerrainNegotiator.getInstance();
        negotiator.reportPlacement(structureName, success, reason);
    }

    /**
     * 获取结构的放置统计记录
     *
     * @param structureName 结构注册名称
     * @return 放置记录，不存在返回 null
     */
    @Nullable
    public static StructurePlacementRecord getPlacementRecord(String structureName) {
        StructureTerrainNegotiator negotiator = StructureTerrainNegotiator.getInstance();
        return negotiator.getPlacementRecord(structureName);
    }
```

- [ ] **Step 3: 添加缺失的 Nullable 导入

确保文件顶部已有 `import javax.annotation.Nullable;`，如果没有则添加。

- [ ] **Step 4: 验证编译通过**

Run: `.\gradlew.bat compileJava`
Expected: BUILD SUCCESSFUL

---

### Task 9: 修改 DimensionAPI 暴露协商器方法

**Files:**
- Modify: `src/main/java/com/pasterdream/pasterdreammod/api/dimension/DimensionAPI.java`

- [ ] **Step 1: 添加导入

```java
import com.pasterdream.pasterdreammod.api.dimension.terrain.StructureTerrainNegotiator;
```

- [ ] **Step 2: 添加方法

在 `getMusicEvent()` 方法之后添加：

```java
    // ======================== 大型结构地形协商支持 ========================

    /**
     * 获取地形协商器实例
     * <p>
     * 通过协商器可以注册大型结构、评估地形、查看放置统计等。
     *
     * @return {@link StructureTerrainNegotiator} 单例
     */
    public static StructureTerrainNegotiator getTerrainNegotiator() {
        return StructureTerrainNegotiator.getInstance();
    }

    /**
     * 启用维度的大型结构支持
     * <p>
     * 调用后，当该维度生成区块时，
     * 如果附近有已注册的大型结构，会自动尝试调整地形以适应结构。
     *
     * @param result 之前由 {@link #createDimension} 返回的维度结果
     */
    public static void enableLargeStructureSupport(DimensionResult result) {
        StructureTerrainNegotiator negotiator = StructureTerrainNegotiator.getInstance();
        negotiator.enableDimensionSupport(result.dimensionName());
        PasterDreamMod.LOGGER.info("[DimensionAPI] 🏗️ 已启用维度的大型结构支持: {} ({})",
                result.dimensionName(), result.dimensionTypeId());
    }

    /**
     * 启用维度的大型结构支持（按维度名称）
     *
     * @param dimensionId 维度 ID（如 "pasterdream:dyedream_world"）
     */
    public static void enableLargeStructureSupport(String dimensionId) {
        StructureTerrainNegotiator negotiator = StructureTerrainNegotiator.getInstance();
        negotiator.enableDimensionSupport(dimensionId);
        PasterDreamMod.LOGGER.info("[DimensionAPI] 🏗️ 已启用维度的大型结构支持: {}", dimensionId);
    }
```

- [ ] **Step 3: 验证编译通过**

Run: `.\gradlew.bat compileJava`
Expected: BUILD SUCCESSFUL

---

### Task 10: 全面编译验证

**Files:** （无文件修改）

- [ ] **Step 1: 执行完整编译**

Run: `.\gradlew.bat compileJava`
Expected: BUILD SUCCESSFUL

- [ ] **Step 2: 运行 API 测试**

Run: `.\gradlew.bat runAllApiTests`
Expected: BUILD SUCCESSFUL，所有测试通过

- [ ] **Step 3: 运行游戏验证（可选）**

Run: `.\gradlew.bat runClient`
Expected: 游戏启动，无崩溃