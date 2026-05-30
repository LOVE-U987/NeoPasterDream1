# 结构-维度地形协商协议 设计文档

## 概述

为 PasterDream 模组的自定义维度 API（DimensionAPI）和自定义结构 API（RuinAPI）添加联动功能：大型结构在生成前向目标维度发送地形需求，维度尝试在不产生明显断层/地理问题的前提下调整地形，并将评估结果返回给结构，完成结构生成检测机制。

## 核心概念

### 结构-维度地形协商协议

```
🏛️ 大型结构 (Ruin API)             🌍 维度 (Dimension API)
        │                                    │
        │  ① 注册时声明地形需求 ─────────────→ │
        │   "我要一块半径20的平地"             │
        │                                    │
        │  ② 区块生成时 ───────────────────→ │
        │   "这个位置在结构范围内"             │
        │                                    │
        │  ←─── ③ 返回评估结果 ────────────  │
        │   "地形已调整，平均高度Y=64"         │
        │                                    │
        │  ④ 结构尝试放置 ──────────────────→ │
        │  ←─── ⑤ 报告结果 ────────────────  │
        │   "成功/失败"                       │
        │                                    │
        │  ⑥ 失败统计 → 打印诊断日志           │
```

### 隆起平台策略

地形调整采用余弦插值平滑过渡，将起伏地形抬升/降低为平地，边缘渐变衔接：

```
原始地形：  ⛰️⛰️⛰️⛰️⛰️⛰️⛰️⛰️⛰️⛰️
                         ↓
隆起平台：  ⛰️⛰️╱￣￣￣￣￣￣￣╲⛰️⛰️
                  ↑ 平地 ↑
                ← 渐变区 →  ← 渐变区 →
```

## 数据模型

### TerrainRequirements

大型结构向维度声明的地形需求。

| 字段 | 类型 | 说明 |
|------|------|------|
| requiredFlatRadius | int | 需要的平地半径（格） |
| terrainBlendRadius | int | 边缘渐变过渡宽度（格） |
| maxHeightVariation | int | 能接受的最大地形起伏（格） |
| targetDimension | String | 目标维度ID（null=任意） |
| preferredBiomeCategory | String | 偏好生物群系分类（null=不限） |
| requireWaterAccess | boolean | 是否需要靠近水源 |
| allowPartialEmbedding | boolean | 是否允许部分埋入地下 |
| maxSlope | double | 最大允许坡度（0~1，0=完全平坦） |

### TerrainAssessment

维度评估完地形后返回给结构的结果。

| 字段 | 类型 | 说明 |
|------|------|------|
| status | Status | SUCCESS / PARTIAL / FAILURE |
| assessedChunkX | int | 评估区块 X |
| assessedChunkZ | int | 评估区块 Z |
| averageHeight | double | 区域平均地形高度 |
| maxHeightVariation | double | 区域内最大高度差 |
| estimatedSlope | double | 估算坡度值 |
| diagnosis | String | 可读诊断信息 |
| failureReason | String | 失败时的具体原因 |

### StructurePlacementRecord

跟踪结构放置尝试的统计数据，用于诊断。

| 字段 | 类型 | 说明 |
|------|------|------|
| structureName | String | 结构注册名称 |
| totalAttempts | int | 总尝试次数 |
| successCount | int | 成功次数 |
| failureCount | int | 失败次数 |
| recentFailureReasons | List<String> | 最近10次失败原因 |
| targetDimensionId | String | 目标维度ID |

## 组件设计

### 新组件

#### StructureTerrainNegotiator（核心桥梁）

连接 RuinAPI 和 DimensionAPI 的中心协调器。单例模式。

**注册接口：**
- `registerLargeStructure(String name, TerrainRequirements reqs)` — 注册大型结构及地形需求
- `enableDimensionSupport(String dimensionId)` — 启用维度的大型结构支持

**地形协商接口：**
- `assessTerrain(String structureName, int chunkX, int chunkZ, Level level)` — 评估地形
- `getTerrainAdjustmentTargets(String dimensionId, int chunkX, int chunkZ)` — 获取需调整的区块

**结果反馈接口：**
- `reportPlacement(String structureName, boolean success, String reason)` — 报告放置结果
- `getPlacementRecord(String structureName)` — 获取放置统计

**诊断接口：**
- `printDiagnostics()` — 打印频繁失败的结构的诊断信息

#### TerrainAdjuster

执行实际地形调整的引擎。采用隆起平台策略。

**核心方法：**
- `createSmoothPlatform(ChunkAccess chunk, int centerX, int centerZ, int flatRadius, int blendRadius)` — 创建平滑平台
- `analyzeTerrain(Level level, int centerX, int centerZ, int radius)` — 分析地形起伏
- `cosineBlend(double t)` — 余弦插值（t: 0→1）

### 现有API改动

#### RuinBuilder.java

新增方法：
- `largeStructure(TerrainRequirements reqs)` — 完整配置
- `withTerrainPlatform(int flatRadius)` — 快捷方法

#### RuinResult.java

新增可选字段：
- `TerrainRequirements terrainRequirements` — 地形需求（可为null）

#### RuinAPI.java

新增方法：
- `printStructureDiagnostics()` — 打印诊断报告

#### DimensionAPI.java

新增方法：
- `enableLargeStructureSupport(DimensionResult dimension)` — 启用支持
- `getTerrainNegotiator()` — 获取地形协商器

## 数据流

### 注册阶段（Mod初始化）
1. `RuinBuilder.withTerrainPlatform(25)` → 创建 TerrainRequirements
2. `RuinResult` 存储地形需求
3. `StructureTerrainNegotiator.registerLargeStructure(name, reqs)`
4. `DimensionAPI.enableLargeStructureSupport(result)`

### 世界生成阶段（运行时）
1. 区块生成 → `TerrainAdjuster` 检查是否在结构范围内
2. 如果是 → 分析地形起伏度
3. 不适合 → `createSmoothPlatform()` 创建平滑平台
4. 结构放置 → `assessTerrain()` 验证地形
5. `reportPlacement(success/fail)` 更新统计
6. 失败率>50% → `printDiagnostics()` 输出诊断日志

## 文件清单

### 新建文件
- `api/dimension/terrain/TerrainRequirements.java`
- `api/dimension/terrain/TerrainAssessment.java`
- `api/dimension/terrain/StructurePlacementRecord.java`
- `api/dimension/terrain/StructureTerrainNegotiator.java`
- `api/dimension/terrain/TerrainAdjuster.java`

### 修改文件
- `api/ruin/builder/RuinBuilder.java`
- `api/ruin/RuinResult.java`
- `api/ruin/RuinAPI.java`
- `api/dimension/DimensionAPI.java`