---
name: "world-decoration-api"
description: "为维度提供多方块结构装饰物快速注册的API。包含柱形、团块、尖刺、门框、散布、水下6种内置类型及自定义扩展，内置悬空检测/填充、占位检测、区域重叠、碎片散落、表面嵌入等逻辑。在需要为维度添加新的装饰性地物时调用。"
---

# WorldDecorationAPI 使用指南

## 概述

`WorldDecorationAPI` 是 PasterDream 模组提供的**多方块装饰物快速注册系统**。通过流式 Builder API，你可以用寥寥几行 Java 代码定义一个复杂的装饰物结构。API 位于 **PasterDreamAPI 模块**的 `api/worldgen/decor/` 包。

### 核心类

| 类 | 路径 | 作用 |
|:--|:-----|:----|
| `DecorationBuilder` | `worldgen/decor/DecorationBuilder.java` | 流式 Builder，链式配置装饰物参数，`register()` 返回 `ResourceKey<PlacedFeature>` |
| `DecorationType` | `worldgen/decor/DecorationType.java` | 装饰物类型枚举（7 种，含 CUSTOM） |
| `DecorationConfig` | `worldgen/decor/DecorationConfig.java` | 统一配置记录（含 MapCodec 序列化） |
| `GenericDecorationFeature` | `worldgen/decor/GenericDecorationFeature.java` | 统一 Feature 实现，按类型调度生成算法 |
| `DecorationRegistry` | `worldgen/decor/DecorationRegistry.java` | 注册管理中心（含自定义生成器注册） |
| `TreePlacerAPI` | `worldgen/decor/TreePlacerAPI.java` | 树木 Placer 注册门面 |
| `TreeRegistry` | `worldgen/decor/TreeRegistry.java` | 染梦树变体常量 |
| `WorldGenUtils` | `worldgen/WorldGenUtils.java` | 共享工具方法（findGroundY、isSolidSurface 等） |

## 快速开始

### 1. 注册 FEATURES

`DecorationRegistry.FEATURES` 已由 `PasterDreamAPI.registerAll(modEventBus)` 统一注册，**无需手动注册**：

```java
PasterDreamAPI.registerAll(modEventBus);
```

### 2. 定义装饰物

```java
import com.pasterdream.pasterdreammod.api.worldgen.decor.DecorationBuilder;
import com.pasterdream.pasterdreammod.api.worldgen.decor.DecorationType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.GenerationStep;

// 定义一个方解石柱 —— register() 返回 ResourceKey<PlacedFeature>
ResourceKey<PlacedFeature> key = DecorationBuilder.create()
    .type(DecorationType.PILLAR)
    .body(Blocks.CALCITE)
    .height(15, 20)
    .width(2, 1)
    .crystal(0.3f, BlockStateProvider.simple(Blocks.AMETHYST_BLOCK))
    .debris(Blocks.CALCITE, 6, 3)
    .checkHang(true)
    .fillHang(false)
    .biome("#pasterdream:is_dyedream")      // 群系标签（带 # 前缀）
    .rarity(3)
    .step(GenerationStep.Decoration.TOP_LAYER_MODIFICATION)
    .register("calcite_pillar");
```

### 3. 引用到 Biome Modifier

`register()` 返回的 `ResourceKey<PlacedFeature>` 可直接用于代码中的 BiomeModifier 注入；JSON 数据文件则**手动维护**在：

```
data/pasterdream/worldgen/configured_feature/calcite_pillar.json
data/pasterdream/worldgen/placed_feature/calcite_pillar.json
```

或配合 `neoforge/biome_modifier/` 下的注入文件：

```json
{
  "type": "neoforge:add_features",
  "biomes": "#pasterdream:is_dyedream",
  "features": ["pasterdream:calcite_pillar"],
  "step": "top_layer_modification"
}
```

> ⚠️ **JSON 生成方式已变更**：`DecorationRegistry.generateAllJson()` 已**不存在**（旧版自动生成 JSON 已移除）。装饰物的 `configured_feature` / `placed_feature` JSON 需手动编写，参考下文 JSON 格式章节。

## 装饰物类型详解

### `DecorationType.PILLAR` — 柱形

**参考**: 方解石柱（CalcitePillarFeature）

锥形柱体，从地下延伸到地上，底部粗顶部细。

| 关键参数 | 说明 | 默认值 |
|:---------|:-----|:------:|
| `body()` | 主体方块 | 必填 |
| `height(min, max)` | 高度范围 | 3~8 |
| `width(base, top)` | 底部宽度、顶部宽度（方块数） | 2, 1 |
| `checkHang(bool)` | 悬空检测 | true |
| `crystal(chance, provider)` | 表面嵌入晶体 | -- |

### `DecorationType.SPIKE` — 尖刺

**参考**: 冰刺（IceSpikeFeature）

底部粗尖端细的锥形尖刺，使用圆形截面而非方形截面。

| 关键参数 | 说明 | 默认值 |
|:---------|:-----|:------:|
| `body()` | 主体方块 | 必填 |
| `top()` | 顶部方块 | 同 body |
| `height(min, max)` | 高度范围 | 8~16 |
| `radius(base, top)` | 底部半径、顶部半径（0=尖顶） | 2, 0 |
| `regionCheck(bool, threshold)` | 区域重叠检测 | false |
| `tilt(float)` | 倾斜程度（0=垂直） | 0.0 |
| `crystal(chance, provider)` | 嵌入矿石概率 | -- |

### `DecorationType.BLOB` — 团块

**参考**: 云坠堆（CloudBlobFeature）

不规则椭球状团块，使用随机游走算法。

| 关键参数 | 说明 | 默认值 |
|:---------|:-----|:------:|
| `body()` | 主体方块 | 必填 |
| `clusterSize(size)` | 总方块数 | 50 |
| `yRadius(radius)` | 垂直半径 | 4 |
| `irregularity(0~1)` | 不规则度 | 0.3 |
| `fillHang(bool)` | 悬空填充（下坠+路径填充） | false |

### `DecorationType.GATE` — 门框

**参考**: 冰之门（IceGateFeature）

双柱+顶部横梁组成的门框形结构。

| 关键参数 | 说明 | 默认值 |
|:---------|:-----|:------:|
| `body()` | 主体方块 | 必填 |
| `gateWidth(min, max)` | 门框间距 | 2~6 |
| `pillarRadius(radius)` | 柱半径 | 2 |
| `beamThickness(thickness)` | 横梁厚度 | 2 |
| `height(min, max)` | 高度范围 | 5~10 |
| `decorationChance(float)` | 额外装饰概率（0~1） | 0.0 |

### `DecorationType.SCATTER` — 散布

地表随机散布的单个方块群。

| 关键参数 | 说明 | 默认值 |
|:---------|:-----|:------:|
| `body()` | 散布的方块 | 必填 |
| `checkHang(bool)` | 悬空检测 | true |

### `DecorationType.AQUATIC` — 水下结构

在水体中生成的结构，需要水环境。

| 关键参数 | 说明 | 默认值 |
|:---------|:-----|:------:|
| `body()` | 主体方块 | 必填 |
| `height(min, max)` | 高度范围 | 3~8 |
| `waterRequired(bool)` | 是否需要水 | true |

### `DecorationType.CUSTOM` — 自定义 ✅（已开放）

通过自定义生成器扩展任意生成逻辑：

```java
// 1. 实现 ICustomDecorationGenerator 并注册
DecorationRegistry.registerCustomGenerator("ice_gate", new IceGateGenerator());

// 2. Builder 中指定 customGenerator 键
ResourceKey<PlacedFeature> key = DecorationBuilder.create()
    .type(DecorationType.CUSTOM)
    .body(Blocks.ICE)
    .customGenerator("ice_gate")           // 指定自定义生成器键
    .biome("#pasterdream:is_dyedream_ocean")
    .register("my_custom_decor");

// 3. 查询自定义生成器
ICustomDecorationGenerator gen = DecorationRegistry.getCustomGenerator("ice_gate");
```

> 参考实现：主模块 `registry/IceDecorations.java`、`registry/OceanDecorations.java` 中大量使用 `registerCustomGenerator`。

## DecorationBuilder 全部配置参数一览

| 方法 | 类型 | 默认值 | 说明 |
|:-----|:----|:------:|:-----|
| `type(DecorationType)` | enum | PILLAR | 结构类型 |
| `body(Block / BlockStateProvider)` | **必填** | -- | 主体方块（支持加权/噪声 Provider） |
| `top(Block / BlockStateProvider)` | 可选 | body | 顶部方块 |
| `crystal(float, BlockStateProvider)` | 可选 | -- | 表面嵌入晶体概率+方块 |
| `debris(Block/Provider, int, int)` | 可选 | -- | 碎片方块+数量+半径 |
| `height(int, int)` | int | 3~8 | 高度范围 |
| `width(int, int)` | int | 2, 1 | 柱形截面宽度 |
| `radius(int, int)` | int | 2, 0 | 圆形截面半径 |
| `clusterSize(int)` | int | 50 | 团块方块总数 |
| `yRadius(int)` | int | 4 | 团块垂直半径 |
| `irregularity(float)` | float | 0.3 | 团块不规则度 |
| `gateWidth(int, int)` | int | 2~6 | 门框间距 |
| `pillarRadius(int)` | int | 2 | 门框柱半径 |
| `beamThickness(int)` | int | 2 | 横梁厚度 |
| `decorationChance(float)` | float | 0.0 | 额外装饰概率（门框用） |
| `crystalOnlyOnTop(boolean)` | bool | true | 晶体仅放置于最顶层（顶部高度打断） |
| `checkHang(boolean)` | bool | true | 悬空检测 |
| `fillHang(boolean)` | bool | false | 悬空填充 |
| `occupiedCheck(boolean)` | bool | true | 占用检测 |
| `regionCheck(boolean, float)` | bool | false, 0.3 | 区域重叠检测 |
| `waterRequired(boolean)` | bool | false | 水环境要求 |
| `avoidRuins(boolean)` | bool | true | 遗迹避让 |
| `tilt(float)` | float | 0.0 | 尖刺倾斜程度（0=垂直） |
| `replaceable(BlockPredicate)` | predicate | null（全可替换） | 可替换方块条件（null=全可替换） |
| `customGenerator(String)` | string | "" | 自定义生成器键（CUSTOM 类型专用） |
| `biome(String)` | string | "" | 目标群系 ID（可带 `#` 前缀引用 tag） |
| `rarity(int)` | int | 1 | 稀有度（1/N） |
| `step(GenerationStep.Decoration)` | enum | TOP_LAYER_MODIFICATION | 生成阶段 |
| **`register(String name)`** | String | -- | **终结点**，返回 `ResourceKey<PlacedFeature>` |

> 校验规则：`body` 为空抛 `NullPointerException`；`biome` 为空或 `minHeight > maxHeight` 抛 `IllegalStateException`。

## DecorationRegistry API

| 方法 | 说明 |
|:-----|:-----|
| `FEATURES` | `DeferredRegister<Feature<?>>`（已注册 `generic_decor`，由 registerAll 统一挂总线） |
| `register(name, config, biome, step, rarity)` | 直接注册（绕过 Builder），返回 `ResourceKey<PlacedFeature>` |
| `registerCustomGenerator(key, ICustomDecorationGenerator)` | 注册自定义生成器 |
| `getCustomGenerator(key)` | 查询自定义生成器（未找到返回 null） |
| `getAllDecorations()` | 所有已注册条目（不可变 List<DecorationEntry>） |
| `clear()` | 清空条目（测试/重载场景） |

## TreePlacerAPI / TreeRegistry（树木系统）

### TreePlacerAPI — Placer 类型注册门面

```java
// 创建三种 DeferredRegister
DeferredRegister<TrunkPlacerType<?>> trunks = TreePlacerAPI.trunkPlacers("pasterdream");
DeferredRegister<FoliagePlacerType<?>> foliage = TreePlacerAPI.foliagePlacers("pasterdream");
DeferredRegister<TreeDecoratorType<?>> decorators = TreePlacerAPI.treeDecorators("pasterdream");

// 一次性挂总线（参数可 null 自动跳过）
TreePlacerAPI.registerAll(modEventBus, trunks, foliage, decorators);
```

### TreeRegistry — 染梦树变体常量

```java
// 变体（含权重，供 random_selector 使用）
TreeRegistry.FANCY    // dyedream_tree_fancy    (0.25)
TreeRegistry.BUSHY    // dyedream_tree_bushy    (0.25)
TreeRegistry.GIANT    // dyedream_tree_giant    (0.25)
TreeRegistry.WEEPING  // dyedream_tree_weeping  (0.25)
TreeRegistry.GLOWING  // dyedream_tree_glowing  (0.25)
TreeRegistry.VARIANTS // List.of(上述 5 种)

// ResourceKey 常量
TreeRegistry.DEFAULT_TREE     // pasterdream:dyedream_tree（兜底变体）
TreeRegistry.TREE_SELECTOR    // pasterdream:dyedream_tree_selector
TreeRegistry.DYEDREAM_TREES   // pasterdream:dyedream_trees（主入口 placed_feature）

// 每个变体可获取 configured_feature key
TreeRegistry.FANCY.configuredKey();  // ResourceKey<ConfiguredFeature<?, ?>>
```

## JSON 自动生成（已移除 ⚠️）

> 旧版 `DecorationRegistry.generateAllJson()` **已移除**。装饰物 JSON 需手动编写并放在 `data/pasterdream/worldgen/` 下。

### configured_feature JSON 示例

```json
{
  "type": "pasterdream:generic_decor",
  "config": {
    "type": "pillar",
    "body_block": { "type": "minecraft:simple_state_provider", "state": { "Name": "minecraft:calcite" } },
    "min_height": 15,
    "max_height": 20,
    "base_width": 2,
    "top_width": 1,
    "crystal_chance": 0.3,
    "check_hang": true
  }
}
```

### placed_feature JSON 示例

```json
{
  "feature": "pasterdream:calcite_pillar",
  "placement": [
    { "type": "minecraft:rarity_filter", "chance": 3 },
    { "type": "minecraft:in_square" },
    { "type": "minecraft:heightmap", "heightmap": "MOTION_BLOCKING" },
    { "type": "minecraft:biome" }
  ]
}
```

## 生成阶段选择

| 阶段 | 适用场景 | 对应原版枚举 |
|:-----|:---------|:------------|
| `RAW_GENERATION` | 基岩层特殊结构 | -- |
| `LAKES` | 湖泊类 | -- |
| `LOCAL_MODIFICATIONS` | 局部地形的修改 | -- |
| `UNDERGROUND_STRUCTURES` | 地下结构 | -- |
| `SURFACE_STRUCTURES` | 地表结构 | ✅ 一般结构默认 |
| `TOP_LAYER_MODIFICATION` | 地表地形修改 | ✅ 柱子/尖刺/团块 |
| `UNDERGROUND_ORES` | 矿石 | -- |
| `UNDERGROUND_DECORATION` | 地下装饰 | -- |
| `FLUID_SPRINGS` | 流体泉 | -- |
| `VEGETAL_DECORATION` | 植被 | ✅ 水上植物等 |

## ⚠️ 常见陷阱（血的教训 🩸）

### 陷阱1：`replaceable` 写成了 `minecraft:always_true`

**症状**：世界加载时报错 `Unknown registry key: minecraft:always_true`，游戏闪退。

**原因**：Minecraft 1.21.1 **没有注册** `minecraft:always_true` 这个 `block_predicate_type`。Builder 默认值是 `null` 不编码，手写 JSON 时写进去就炸。

**修复**：
- 用 Builder API -> 不设 `replaceable()`，自动不编码 ✅
- 手写 JSON -> **直接删掉** `replaceable` 字段 ✅

### 陷阱2：`config.type` 写了大写

**症状**：游戏崩溃 `Unknown element name:SPIKE`（或 `GATE`、`PILLAR`、`AQUATIC` 等）

**原因**：`DecorationType` 枚举实现 `StringRepresentable`，序列化用的是构造参数的**小写**（`"spike"`、`"gate"`、`"pillar"`、`"aquatic"`）。JSON 里写 `"SPIKE"` 解析器不认识。

**修复**：
- 用 Builder API -> 自动小写 ✅
- 手写 JSON -> 确保 `"type": "spike"`、`"type": "pillar"` ✅

### 陷阱3：Biome tag 重叠导致的 Feature order cycle

**症状**：`Feature order cycle found, involved sources: [pasterdream:biome_dyedream_3]`，世界无法生成区块。

**原因**：某个生物群系同时属于多个 tag（如 `#is_dyedream` 和 `#is_dyedream_ocean`），不同的 biome_modifier 通过不同 tag **往同一个生成阶段添加了同一个 placed_feature**，造成循环依赖。

**预防**：
- 设计 biome_modifier 时检查 biome tag 是否有**重叠**
- 如果某个特征是群系专用的（如海洋珊瑚），用精确的群系 tag 而非全量 tag
- 确保每个 placed_feature **只在同一个 step 中出现一次**

### 陷阱4：placed_feature 引用了不存在的 configured_feature

**症状**：`Unbound values in registry worldgen/configured_feature` 或 `worldgen/placed_feature`

**原因**：在一个 placed_feature/configured_feature 的 JSON 里引用了另一个 feature（如 `"feature": "pasterdream:xxx"`），但目标 JSON 文件不存在或注册名对不上。

**预防**：每次引用前确认目标已注册且 JSON 文件存在。

### 陷阱5：结构叠罗汉（堆叠生成）

**症状**：同一个区块内多个同类结构叠在一起，一个上面顶着另一个，显得很不自然。

**原因**：结构使用 `rarity_filter` + `in_square` + `heightmap` 放置，但没有**区域重叠检查**。

**预防**：
- 高大结构（柱子、尖刺、高冰丘）一定要加 `regionCheck(true, 0.3)`，生成前会检测区域是否被占用
- 矮小结构（珊瑚礁、散布类）一般不需要，间距天然够

### 陷阱5.5：⚠️ regionCheck 必须有自定义 replaceable 才可靠！

**症状**：JSON 里写了 `region_check: true`，结构还是叠叠乐，拦不住。

**血的教训 🩸**：`isAreaOccupied` 内部有两种检测模式，**天差地别**：

| 有没有自定义 `replaceable` | 检测方式 | 效果 |
|:---------------------------|:---------|:----:|
| ✅ **有**（如方解石柱子把石头/泥土列为可替换） | **精确布尔检测**：groundY ±2 层逐点排查，**任何一个**非可替换方块就阻止 | 100% 可靠 💯 |
| ❌ **无**（仅认空气） | 退化为**采样+阈值法**：网格扫描后再算比率 vs 0.3 | 宽结构安全，窄结构（宽度≤3）可能漏检 ⚠️ |

**所以一定要给加了 `regionCheck` 的结构配 `replaceable` 谓词！**

**正确的 JSON 示例（给方解石柱加 replaceable）：**
```json
{
  "type": "pasterdream:generic_decor",
  "config": {
    "type": "pillar",
    "body_block": ...,
    "region_check": true,
    "region_threshold": 0.3,
    "replaceable": {
      "type": "minecraft:any_of",
      "predicates": [
        {
          "type": "minecraft:matching_blocks",
          "blocks": ["minecraft:stone", "minecraft:dirt", "minecraft:grass_block"]
        },
        { "type": "minecraft:replaceable" }
      ]
    }
  }
}
```

**用 Builder API 自动规避**：`.replaceable(BlockPredicate)` → 自动走精确布尔检测 ✅

### 陷阱6：crystalOnlyOnTop = true 不是 y < topY，是几何体截面检测

**正确实现**：`crystalOnlyOnTop(true)` = 对每个表面方块，**计算 y+1 层的截面是否还包含这个 (dx,dz) 位置**。
- 上层还有结构 → 嵌入状态 → ❌ 不生成晶体
- 上层无结构（暴露在外） → ✅ 可以生成晶体

**PILLAR/AQUATIC 用方截面检测**：`hasBlockAbove = dx ∈ [-nextHalfSize, nextWidth - nextHalfSize) && dz ∈ [...]`

**SPIKE 用圆截面检测**：`hasBlockAbove = nextDistSq ≤ (nextR + 0.5)²`

### 陷阱7：crystalOnlyOnTop 默认值差异（Builder vs JSON）

| 方式 | 默认值 | 说明 |
|:-----|:------:|:-----|
| Builder API 创建新结构 | `true` | 晶体=高度帽，仅暴露在外的表面出晶体 |
| 手写 JSON 旧结构 | `false` | 晶体=表面装饰，所有暴露面都可能出 |

**修复**：
- 想让新结构晶体多？加 `.crystalOnlyOnTop(false)` ✅
- 想让旧 JSON 结构启用高度打断？加 `"crystal_only_on_top": true` ✅

## 最佳实践

1. **装饰物命名规范**：用下划线分隔，如 `my_cool_pillar`
2. **⚠️ 完全使用 Builder API 注册，避免手写 JSON！** 上面所有陷阱通过 Builder API 都能自动规避 ✅
3. **⚠️ 给高大结构配 `replaceable` + `regionCheck`**：这是防叠罗汉的黄金组合！Builder API：`.replaceable(predicate).regionCheck(true, 0.3f)`
4. **⚠️ 晶体逻辑是几何截面检测**：`crystalOnlyOnTop(true)` 不是简单的 `y < topY`
5. **⚠️ 检查 biome tag 重叠**：确保同一个 placed_feature 不会通过不同 tag 被同一个群系添加两次
6. **生成阶段选择**：地面结构用 `TOP_LAYER_MODIFICATION`，植被用 `VEGETAL_DECORATION`
7. **稀有度调整**：大型结构用 `rarity(5~10)`，小型结构用 `rarity(2~3)`
8. **悬空填充适用场景**：团块（Blob）建议启用 `fillHang(true)`，柱形/尖刺建议仅 `checkHang(true)`
9. **JSON 手动维护**：`configured_feature` / `placed_feature` JSON 写在 `data/pasterdream/worldgen/` 下，并与 `register()` 名称保持一致
10. **Biome Modifier**：代码注入用 `register()` 返回的 `ResourceKey<PlacedFeature>`，或 `neoforge/biome_modifier/` JSON 注入

## 引用文件

- [DecorationBuilder.java](file:///C:/Users/97128/Documents/GitHub/NeoPasterDream1/PasterDreamAPI/src/main/java/com/pasterdream/pasterdreammod/api/worldgen/decor/DecorationBuilder.java) — 流式构建器
- [DecorationType.java](file:///C:/Users/97128/Documents/GitHub/NeoPasterDream1/PasterDreamAPI/src/main/java/com/pasterdream/pasterdreammod/api/worldgen/decor/DecorationType.java) — 类型枚举
- [DecorationConfig.java](file:///C:/Users/97128/Documents/GitHub/NeoPasterDream1/PasterDreamAPI/src/main/java/com/pasterdream/pasterdreammod/api/worldgen/decor/DecorationConfig.java) — 配置记录
- [DecorationRegistry.java](file:///C:/Users/97128/Documents/GitHub/NeoPasterDream1/PasterDreamAPI/src/main/java/com/pasterdream/pasterdreammod/api/worldgen/decor/DecorationRegistry.java) — 注册中心
- [GenericDecorationFeature.java](file:///C:/Users/97128/Documents/GitHub/NeoPasterDream1/PasterDreamAPI/src/main/java/com/pasterdream/pasterdreammod/api/worldgen/decor/GenericDecorationFeature.java) — 统一 Feature 实现
- [ICustomDecorationGenerator.java](file:///C:/Users/97128/Documents/GitHub/NeoPasterDream1/PasterDreamAPI/src/main/java/com/pasterdream/pasterdreammod/api/worldgen/decor/ICustomDecorationGenerator.java) — 自定义生成器接口
- [TreePlacerAPI.java](file:///C:/Users/97128/Documents/GitHub/NeoPasterDream1/PasterDreamAPI/src/main/java/com/pasterdream/pasterdreammod/api/worldgen/decor/TreePlacerAPI.java) — 树木 Placer 注册门面
- [TreeRegistry.java](file:///C:/Users/97128/Documents/GitHub/NeoPasterDream1/PasterDreamAPI/src/main/java/com/pasterdream/pasterdreammod/api/worldgen/decor/TreeRegistry.java) — 染梦树变体常量
- [WorldGenUtils.java](file:///C:/Users/97128/Documents/GitHub/NeoPasterDream1/PasterDreamAPI/src/main/java/com/pasterdream/pasterdreammod/api/worldgen/WorldGenUtils.java) — 世界生成工具类
- [ModDecorations.java](file:///C:/Users/97128/Documents/GitHub/NeoPasterDream1/PasterDream/src/main/java/com/pasterdream/pasterdreammod/registry/ModDecorations.java) — 主模块使用示例
- [IceDecorations.java](file:///C:/Users/97128/Documents/GitHub/NeoPasterDream1/PasterDream/src/main/java/com/pasterdream/pasterdreammod/registry/IceDecorations.java) — 自定义生成器示例
