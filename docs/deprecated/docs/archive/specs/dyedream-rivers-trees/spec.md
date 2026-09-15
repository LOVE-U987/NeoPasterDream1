# 染梦世界河流与树木扩展方案 - 产品需求文档

## Overview
- **Summary**: 为 NeoPasterDream1 模组的染梦世界重写河流系统（参考 ES 模组的 `RiverEntry` + 梯度噪声检测机制）并扩展树木多样性，实现不同群系的树木变种和树叶颜色变化效果（类似原版橡树在不同群系颜色不同）。
- **Purpose**: 解决当前染梦世界河流系统过于简单、树木缺乏多样性、树叶颜色不随群系变化的问题，提升玩家探索体验和视觉享受。
- **Target Users**: Minecraft 1.21.1 NeoForge 玩家，特别是喜爱探索和建造的玩家群体。

## Goals
- [x] 重写河流系统，实现 ES 模组风格的 `RiverEntry` + 梯度噪声检测算法
- [x] 创建染梦河流生物群系，实现粉色/紫色梦幻河流效果
- [x] 为不同群系添加差异化的树木变种和密度
- [x] 实现树叶颜色随群系变化（类似原版橡树在不同群系颜色不同）
- [x] 添加河岸和水生植被，增强生态感

## Non-Goals (Out of Scope)
- 新增方块/物品注册（除非必须）
- 修改维度结构或地形生成器核心逻辑
- 添加新实体或怪物
- 修改现有方块属性或配方

## Background & Context

### 现有架构分析

**河流系统**：当前 `DyedreamBiomeSource` 使用简单的侵蚀度噪声阈值判定（`erosion > 0.7`），河流通过 `DyedreamChunkGenerator` 中的 `carveRivers()` 方法雕刻。维度配置已使用自定义 `DyedreamBiomeSource`。`DyedreamNoises.RIVER_THRESHOLD` 常量已存在（值为 0.7）。

**ES模组河流机制**：使用 `RiverEntry` 记录系统 + FastNoise + 梯度计算判定河流位置。核心算法：
1. 使用 domain warp 噪声采样获取噪声值
2. 计算梯度向量（gradX, gradZ）：`gradX = (nx1 - nx2) / (2 * delta)`，`gradZ = (nz1 - nz2) / (2 * delta)`
3. 使用 `riverValue = |noiseValue| / gradLength` 判定是否在河流范围内
4. 支持河流宽度、过渡区域、偏移量等参数配置
5. 通过 `domainWarp()` 实现自然弯曲的河流走向

**树叶颜色机制**：原版 Minecraft 通过生物群系的 `foliage_color` 属性控制树叶颜色，树叶方块需设置 `color: true` 属性。当前 `DyedreamLeavesBlock` 继承 `LeavesBlock`，但未启用此功能。需要在构造函数中传入 `LeavesBlock.Properties` 并启用 `color: true`。

**当前状态**：
- ✅ FastNoise 工具类已存在（`util/FastNoise.java`），支持 domain warp 功能
- ✅ DyedreamNoises.RIVER_THRESHOLD 常量已存在
- ❌ DyedreamBiomeSource 使用简单的侵蚀度阈值，需要重写为 ES 风格梯度噪声检测
- ❌ DyedreamLeavesBlock 未启用群系颜色变化
- ❌ 缺少河流生物群系配置文件
- ❌ 各生物群系之间树木密度没有差异
- ❌ 河流周边缺少水生植被

### 当前群系配置

| 群系 | foliage_color | grass_color | 特征 |
|------|---------------|-------------|------|
| biome_dyedream_0 (平原) | -145678 (粉紫) | -145678 | 粉色粉尘粒子 |
| biome_dyedream_1 (森林) | -216083 (深紫) | -21522 | 末影粒子 |
| biome_dyedream_2 (冰雪) | -22035 (蓝白) | -21522 | 雪花粒子 |
| biome_dyedream_3 (高原) | -22035 (蓝绿) | -21522 | 绿色粉尘粒子 |
| biome_dyedream_deep_ocean | -22035 | -21522 | 气泡粒子 |
| biome_dyedream_mushroom_plains | -541729 (青绿) | -728243 | 孢子粒子 |

## Functional Requirements

### FR-1: 河流系统重写（ES风格）
- **FR-1.1**: 创建 `RiverEntry` 记录类，支持河流数据、宽度、过渡区域、偏移量等参数，包含 CODEC 支持 JSON 序列化
- **FR-1.2**: 重写 `DyedreamBiomeSource`，实现梯度噪声检测算法判定河流位置：
  - 使用 FastNoise 的 domain warp 噪声生成自然河流走向
  - 计算噪声梯度向量：`gradX = (noise(x+1,z) - noise(x-1,z)) / 2`，`gradZ = (noise(x,z+1) - noise(x,z-1)) / 2`
  - 使用 `riverValue = |noiseValue| / gradLength` 判定河流范围
  - 支持河流宽度 `size` 和过渡区域 `transitionSize` 参数
- **FR-1.3**: 创建 `biome_dyedream_river` 生物群系，设置粉色/紫色水色和雾气效果
- **FR-1.4**: 更新维度配置使用自定义 `DyedreamBiomeSource` 和河流参数，添加河流生物群系到群系列表
- **FR-1.5**: 更新 `DyedreamChunkGenerator` 中的河流逻辑，与新 BiomeSource 保持一致，确保河床地形和水面恢复正常工作

### FR-2: 树木系统扩展
- **FR-2.1**: 创建多种树形 configured_feature 配置：
  - `dyedream_tree_normal.json`（普通树形，5-8格高）
  - `dyedream_tree_large.json`（巨型树形，10-15格高）
  - `dyedream_tree_weeping.json`（垂泪树形，向下垂的树叶）
  - `dyedream_tree_icy.json`（冰晶树形，使用冰系方块）
  - `dyedream_tree_mushroom.json`（蘑菇树变种）
- **FR-2.2**: 创建 `dyedream_tree_selector.json` 树形选择器配置，支持随机选择不同树形
- **FR-2.3**: 创建多种 placed_feature 配置：
  - `dyedream_trees_dense.json`（高密度森林）
  - `dyedream_trees_sparse.json`（稀疏平原/高原）
  - `dyedream_trees_icy.json`（冰雪群系）
  - `dyedream_trees_mushroom.json`（蘑菇平原）
- **FR-2.4**: 添加生物群系修饰器配置：
  - 平原群系（dyedream_0）：稀疏基础树木
  - 森林群系（dyedream_1）：高密度多种树形
  - 冰雪群系（dyedream_2）：冰晶树变种
  - 高原群系（dyedream_3）：稀疏特殊树木
  - 蘑菇平原：蘑菇树变种

### FR-3: 树叶颜色变化
- **FR-3.1**: 修改 `DyedreamLeavesBlock` 构造函数，启用 `color: true` 属性
- **FR-3.2**: 更新 `dyedream_leaves.json` 方块状态定义支持群系颜色变化
- **FR-3.3**: 更新树叶模型使用 `tintindex` 支持染色
- **FR-3.4**: 确保各生物群系的 `foliage_color` 正确影响树叶颜色

### FR-4: 河岸和水生植被
- **FR-4.1**: 创建 `patch_dyedream_reeds` configured_feature（芦苇）
- **FR-4.2**: 创建 `patch_dyedream_water_flowers` configured_feature（水生花卉）
- **FR-4.3**: 创建 `dyedream_river_vegetation` placed_feature
- **FR-4.4**: 创建 `dyedream_river_features.json` biome_modifier 添加河流植被到河流生物群系

## Non-Functional Requirements
- **NFR-1**: 生成性能：河流和树木生成不应显著影响世界加载速度，使用缓存机制优化噪声采样
- **NFR-2**: 视觉一致性：新增元素必须与染梦世界现有的粉色/紫色主题保持一致
- **NFR-3**: 兼容性：修改应兼容现有存档，不破坏已有结构
- **NFR-4**: 河流自然度：河流走向应自然流畅，避免直线或锯齿状，使用 domain warp 噪声实现

## Constraints
- **Technical**: NeoForge 1.21.1，Minecraft 1.21.1 数据目录命名变更（`loot_tables` → `loot_table`，`recipes` → `recipe`）
- **Dependencies**: 基于现有方块和物品，不引入新资源包
- **Design**: 保持染梦世界独特的梦幻粉色/紫色视觉风格
- **API-Split**: Builder/Facade/Config 类放入 PasterDreamAPI 模块，业务逻辑放入 PasterDream 模块

## Assumptions
- 现有方块（dyedream_log, dyedream_leaves, dyedream_sapling）已正确注册
- 各生物群系的 `foliage_color` 属性已设置但可能未生效
- FastNoise 工具类已存在且支持 domain warp 功能
- `DyedreamBiomeSource` 已注册并可正常工作
- `DyedreamNoises.RIVER_THRESHOLD` 常量已存在

## Acceptance Criteria

### AC-1: 河流生成验证
- **Given**: 玩家进入染梦世界并探索
- **When**: 玩家在平原或森林区域移动
- **Then**: 可以看到自然生成的河流贯穿不同地形，河水呈现粉色/紫色调，河流走向自然流畅
- **Verification**: `human-judgment`

### AC-2: 树叶颜色变化验证
- **Given**: 玩家在不同生物群系中观察树木
- **When**: 玩家比较平原、森林、冰雪群系的树叶颜色
- **Then**: 树叶颜色随群系变化，平原偏粉色（-145678），森林偏深紫（-216083），冰雪偏蓝白（-22035）
- **Verification**: `human-judgment`

### AC-3: 树木多样性验证
- **Given**: 玩家在染梦世界不同区域探索
- **When**: 玩家观察森林、平原、高原区域的树木
- **Then**: 可以看到多种树形（普通树、巨型树、垂泪树、冰晶树），密度与群系匹配
- **Verification**: `human-judgment`

### AC-4: 河岸植被验证
- **Given**: 玩家靠近染梦河流
- **When**: 玩家观察河岸两侧和河底
- **Then**: 可以看到芦苇、荷花、水草等装饰性植被
- **Verification**: `human-judgment`

### AC-5: 编译验证
- **Given**: 项目代码已修改完成
- **When**: 运行 `.\gradlew compileJava`
- **Then**: 编译成功，无错误
- **Verification**: `programmatic`

### AC-6: 数据生成验证
- **Given**: 配置文件已创建完成
- **When**: 运行 `.\gradlew runData`
- **Then**: 数据生成成功，无错误
- **Verification**: `programmatic`

## Open Questions
- [ ] 是否需要新增特殊的染梦河流方块（如粉色河水）？
- [ ] 是否需要为特殊树种创建新的叶子/木头变体？
- [ ] 河流的宽度和深度范围应该是多少？