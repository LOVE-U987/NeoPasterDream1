# 更新日志：Issue #10 系列修复

> 生成日期：2026-08-19
> 关联文档：[issue-#11-priority-tracker.md](issue-#11-priority-tracker.md)

---

## 概要

处理了 #10 下的 12 个子问题（结构生成密度与命名），并对生物群系命名系统进行了全面重构，以确保一致性和向后兼容性。

---

## #10 结构生成修复

### #10.4 云端地下结构生成（P0）

| 子问题 | 修复内容 | 文件 |
|--------|---------|------|
| #10.4.1 heightmap=MOTION_BLOCKING | 改为 `WORLD_SURFACE_WG` 以避免洞穴顶部 | `cloudfall_mound.json`、`floating_cloud_island.json` |
| #10.4.2 fillHang=true | 改为 `false`（Java + JSON x3） | `ModDecorations.java`、`cloudfall_mound_dense.json`、`cloudfall_mound_sparse.json` |
| #10.4.3 rarity=2 过于密集 | 改为 `6` | `cloudfall_mound.json` |
| #10.4.4 replaceable 包含 CAVE_AIR | 移除 `Blocks.CAVE_AIR` | `ModDecorations.java`（x2） |
| #10.4.5 注入所有 9 个生物群系 | 保留所有生物群系，通过 rarity 降低密度 | 无需修改 |

### #10.3 树木生成（P0-P1）

| 子问题 | 修复内容 | 文件 |
|--------|---------|------|
| #10.3.1 biome_dyedream_1 有 9 个树木特性 | 将 `dyedream_highlands_features.json` 改为目标生物群系 `biome_dyedream_0` | `dyedream_highlands_features.json` |
| #10.3.2 dyedream_trees_dense count=10 | 改为 `4` | `dyedream_trees_dense.json` |
| #10.3.3 巨型/超大型树概率过高 | 巨型：0.18→0.06，超大型：0.04→0.013 | `dyedream_tree_selector.json` |

### #10.2 冰域装饰（P1-P2）

| 子问题 | 修复内容 | 文件 |
|--------|---------|------|
| #10.2.1 ice_crystal_spike rarity=1 | 改为 `3` | `IceDecorations.java` |
| #10.2.2 warm_crystal_spike 缺少 JSON | 跳过（需手动创建 JSON） | - |

### #10.1 结构集（P0-P1）

| 子问题 | 修复内容 | 文件 |
|--------|---------|------|
| #10.1.1 small_ballon 有 11 个 structure_sets | 跳过（需合并到 random_selector） | - |
| #10.1.2 biome_dyedream_0 有 42 个 structure_sets | 跳过（需审查） | - |
| #10.1.3 desert_cottage/wishingtree 比率 0.8 | desert_cottage：spacing 60→90，sep 48→30；wishingtree_1：spacing 89→120，sep 72→40 | `desert_cottage_0_set.json`、`dream_wishingtree_1_set.json` |

---

## 生物群系命名系统重构

### 问题

生物群系命名系统在 17 个生物群系中使用了 5 种不同的命名约定：
- `biome_dyedream_0/1/2/3`（不透明的数字编号）
- `biome_dyedream_deep_ocean`（描述性命名）
- `wind_journey_biome_0/1`（前缀反转）
- `cold_domain_biome`（前缀反转）
- `aaroncos_arena_biome`（无前缀）

### 解决方案

实施双名称系统（方案 A：双文件）：
1. 创建 17 个使用描述性名称的新生物群系 JSON 文件
2. 保留旧生物群系 JSON 文件以实现向后兼容
3. 更新所有新引用以使用新名称
4. 旧常量标记为 `@Deprecated(since = "0.9.9", forRemoval = true)`

### 命名约定

统一格式：`{维度}_{生物群系类型}`

### 迁移映射

| 旧名称 | 新名称 | 维度 |
|--------|--------|------|
| `biome_dyedream_0` | `dyedream_plains` | 染梦 |
| `biome_dyedream_1` | `dyedream_forest` | 染梦 |
| `biome_dyedream_2` | `dyedream_frozen_tundra` | 染梦 |
| `biome_dyedream_3` | `dyedream_cold_ocean` | 染梦 |
| `biome_dyedream_deep_ocean` | `dyedream_deep_ocean` | 染梦 |
| `biome_dyedream_mushroom_plains` | `dyedream_mushroom_plains` | 染梦 |
| `biome_dyedream_shore` | `dyedream_shore` | 染梦 |
| `biome_dyedream_dense_forest` | `dyedream_dense_forest` | 染梦 |
| `biome_dyedream_river` | `dyedream_river` | 染梦 |
| `biome_shadow_0` | `shadow_wastes` | 暗影 |
| `biome_shadow_1` | `shadow_forest` | 暗影 |
| `biome_shadow_2` | `shadow_barrens` | 暗影 |
| `wind_journey_biome_0` | `wind_journey_islands` | 风之旅 |
| `wind_journey_biome_1` | `wind_journey_desert` | 风之旅 |
| `cold_domain_biome` | `cold_domain_tundra` | 冰域 |
| `aaroncos_arena_biome` | `aaroncos_arena` | 竞技场 |
| `aaroncos_arena_dim_biome` | `aaroncos_arena_void` | 竞技场 |

### 修改文件统计

| 类别 | 数量 | 说明 |
|------|:----:|------|
| 新生物群系 JSON 文件 | 17 | 使用新名称创建 |
| 维度 JSON | 5 | 更新生物群系列表 |
| 生物群系修饰器 JSON | 30 | 更新生物群系引用 |
| 标签文件 | 4 | 添加新名称（保留旧名称） |
| 结构 JSON | 87 | 更新生物群系引用 |
| 语言文件 | 2 | 更新翻译键 |
| 天空盒 JSON | 24 | 更新生物群系引用 |
| Java 文件（字符串引用） | 5 | 更新字符串字面量 |
| Java 文件（常量引用） | 4 | 保留旧常量（兼容性） |
| **总计** | **178** | |

### 向后兼容性

- 保留旧生物群系 JSON 文件以支持旧世界加载
- 保留旧 `ResourceKey` 常量并添加 `@Deprecated` 注解
- 在生物群系标签中同时包含旧名称和新名称
- 弃用用法会产生编译警告（将在 0.9.10 中清理）

### 延期项目（0.9.10）

- 移除旧生物群系 JSON 文件
- 移除已弃用的 `ResourceKey` 常量
- 将剩余 Java 代码迁移到使用新常量
- 重命名树木装饰器类（`BiomeDyedream0TrunkDecorator` → `DyedreamPlainsTrunkDecorator`）

---

## 编译结果

```
BUILD SUCCESSFUL in 13s
29 deprecation warnings (expected, from old constant usage)
```
