package com.pasterdream.pasterdreammod.api.dimension.terrain;

import com.pasterdream.pasterdreammod.api.PasterDreamAPI;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 结构-维度地形协商器 —— 连接 RuinAPI 和 DimensionAPI 的中心协调器。
 * <p>
 * 负责管理大型结构的注册、地形需求存储、放置结果追踪和诊断输出。
 * 采用单例模式，全局唯一实例。
 */
public class StructureTerrainNegotiator {

    private static volatile StructureTerrainNegotiator instance;

    /** 大型结构注册表：结构名称 → 地形需求 */
    private final Map<String, TerrainRequirements> largeStructures = new ConcurrentHashMap<>();

    /** 启用大型结构支持的维度集合 */
    private final Set<String> enabledDimensions = ConcurrentHashMap.newKeySet();

    /** 结构放置统计：结构名称 → 放置记录 */
    private final Map<String, StructurePlacementRecord> placementRecords = new ConcurrentHashMap<>();

    /** 结构到目标维度的映射：结构名称 → 维度 ID */
    private final Map<String, String> structureDimensions = new ConcurrentHashMap<>();

    private StructureTerrainNegotiator() {
        PasterDreamAPI.LOGGER.info("[TerrainNegotiator] 🏗️ 地形协商器初始化完成");
    }

    /**
     * 获取地形协商器单例。
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
     * 注册一个大型结构及其地形需求。
     *
     * @param structureName 结构注册名称
     * @param dimensionId   目标维度 ID
     * @param requirements  地形需求
     */
    public void registerLargeStructure(String structureName, String dimensionId, TerrainRequirements requirements) {
        largeStructures.put(structureName, requirements);
        structureDimensions.put(structureName, dimensionId);
        PasterDreamAPI.LOGGER.info("[TerrainNegotiator] 📝 注册大型结构: {} (维度: {}) | 需求: {}",
                structureName, dimensionId, requirements);
    }

    /**
     * 启用维度的大型结构支持。
     *
     * @param dimensionId 维度 ID
     */
    public void enableDimensionSupport(String dimensionId) {
        enabledDimensions.add(dimensionId);
        PasterDreamAPI.LOGGER.info("[TerrainNegotiator] 🌍 启用维度的大型结构支持: {}", dimensionId);
    }

    // ======================== 查询接口 ========================

    /**
     * 获取已注册的地形需求。
     *
     * @param structureName 结构名称
     * @return 地形需求，未注册返回 null
     */
    @Nullable
    public TerrainRequirements getRequirements(String structureName) {
        return largeStructures.get(structureName);
    }

    /**
     * 判断指定维度是否启用了大型结构支持。
     */
    public boolean isDimensionEnabled(String dimensionId) {
        return enabledDimensions.contains(dimensionId);
    }

    /**
     * 获取所有注册的大型结构名称。
     */
    public Set<String> getRegisteredStructures() {
        return Collections.unmodifiableSet(largeStructures.keySet());
    }

    /**
     * 查找指定维度中匹配的结构。
     *
     * @param dimensionId 维度 ID
     * @return 匹配的结构名称列表
     */
    public List<String> findMatchingStructures(String dimensionId) {
        List<String> matches = new ArrayList<>();
        for (Map.Entry<String, TerrainRequirements> entry : largeStructures.entrySet()) {
            String name = entry.getKey();
            String targetDim = structureDimensions.get(name);
            if (targetDim != null && targetDim.equals(dimensionId)) {
                matches.add(name);
            }
        }
        return matches;
    }

    // ======================== 地形评估接口 ========================

    /**
     * 评估指定位置的地形是否适合放置结构。
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
     * 报告结构放置结果。
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
            PasterDreamAPI.LOGGER.warn("[TerrainNegotiator] ⚠️ 结构 [{}] 放置失败: {}", structureName, reason);

            if (record.isFailingFrequently()) {
                String failureRateStr = String.format("%.1f%%", record.getFailureRate() * 100);
                PasterDreamAPI.LOGGER.error("[TerrainNegotiator] ❌ 结构 [{}] 频繁失败！失败率: {}",
                        structureName, failureRateStr);
                printStructureDiagnostics(structureName);
            }
        }
    }

    /**
     * 获取结构的放置统计记录。
     *
     * @param structureName 结构名称
     * @return 放置记录，不存在返回 null
     */
    @Nullable
    public StructurePlacementRecord getPlacementRecord(String structureName) {
        return placementRecords.get(structureName);
    }

    /**
     * 获取所有放置记录。
     */
    public Map<String, StructurePlacementRecord> getAllPlacementRecords() {
        return Collections.unmodifiableMap(placementRecords);
    }

    // ======================== 诊断接口 ========================

    /**
     * 打印所有频繁失败的结构的诊断信息。
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
            PasterDreamAPI.LOGGER.error("[TerrainNegotiator] {}", sb);
        } else {
            PasterDreamAPI.LOGGER.info("[TerrainNegotiator] {}", sb);
        }
    }

    /**
     * 打印指定结构的详细诊断信息。
     *
     * @param structureName 结构名称
     */
    public void printStructureDiagnostics(String structureName) {
        StructurePlacementRecord record = placementRecords.get(structureName);
        if (record == null) {
            PasterDreamAPI.LOGGER.warn("[TerrainNegotiator] 结构 [{}] 无放置记录", structureName);
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
        PasterDreamAPI.LOGGER.error("[TerrainNegotiator] {}", sb);
    }

    /**
     * 重置所有统计（用于测试）。
     */
    public void reset() {
        largeStructures.clear();
        enabledDimensions.clear();
        placementRecords.clear();
        structureDimensions.clear();
        PasterDreamAPI.LOGGER.info("[TerrainNegotiator] 🔄 已重置所有统计数据");
    }
}