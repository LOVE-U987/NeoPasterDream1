---
name: "world-decoration-api"
description: "为维度提供多方块结构装饰物快速注册的API。包含柱形、团块、尖刺、门框、散布、水下6种类型及自定义扩展，内置悬空检测/填充、占位检测、区域重叠、碎片散落、表面嵌入等逻辑。在需要为维度添加新的装饰性地物时调用。"
---

# WorldDecorationAPI 使用指南

## 概述

`WorldDecorationAPI` 是 PasterDream 模组提供的**多方块装饰物快速注册系统**。通过流式 Builder API，你可以用寥寥几行 Java 代码定义一个复杂的装饰物结构，并自动生成对应的 `configured_feature` 和 `placed_feature` JSON 数据文件。

### 核心类

| 类 | 路径 | 作用 |
|:--|:-----|:----|
| `DecorationBuilder` | `worldgen/decor/DecorationBuilder.java` | 流式 Builder，链式配置装饰物参数 |
| `DecorationType` | `worldgen/decor/DecorationType.java` | 装饰物类型枚举 |
| `DecorationConfig` | `worldgen/decor/DecorationConfig.java` | 统一配置记录（含 MapCodec 序列化） |
| `GenericDecorationFeature` | `worldgen/decor/GenericDecorationFeature.java` | 统一 Feature 实现，按类型调度生成算法 |
| `DecorationRegistry` | `worldgen/decor/DecorationRegistry.java` | 注册管理中心 + JSON 自动生成 |
| `WorldGenUtils` | `worldgen/WorldGenUtils.java` | 共享工具方法（findGroundY、isSolidSurface 等） |

## 快速开始

### 1. 注册 FEATURES

确保 `PasterDreamMod.java` 中已注册 `DecorationRegistry.FEATURES`：

```java
// 注册通用装饰物特征（WorldDecorationAPI）
DecorationRegistry.FEATURES.register(modEventBus);
```

### 2. 定义装饰物

```java
import com.pasterdream.pasterdreammod.worldgen.decor.DecorationBuilder;
import com.pasterdream.pasterdreammod.worldgen.decor.DecorationType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.GenerationStep;

// 定义一个方解石柱
DecorationBuilder.create()
    .type(DecorationType.PILLAR)
    .body(Blocks.CALCITE)
    .height(15, 20)
    .width(2, 1)
    .crystal(0.3f, BlockStateProvider.simple(Blocks.AMETHYST_BLOCK))
    .debris(Blocks.CALCITE, 6, 3)
    .checkHang(true)
    .fillHang(false)
    .biome("pasterdream:biome_dyedream_1")
    .rarity(3)
    .step(GenerationStep.Decoration.TOP_LAYER_MODIFICATION)
    .register("calcite_pillar");
```

### 3. 生成 JSON 数据文件

在开发环境的初始化阶段（如 `FMLCommonSetupEvent`）或测试主方法中调用：

```java
// 生成所有已注册装饰物的 JSON 文件到 src/main/resources/data/pasterdream/worldgen/
DecorationRegistry.generateAllJson();
```

此操作会生成：
- `worldgen/configured_feature/<name>.json` — 配置特征定义
- `worldgen/placed_feature/<name>.json` — 放置特征定义（含稀有度过滤、in_square、高度图、群系过滤）

### 4. 引用到 Biome Modifier

JSON 文件生成后，需要在 `neoforge/biome_modifier/` 下创建对应的注入文件：

```json
{
  "type": "neoforge:add_features",
  "biomes": "pasterdream:biome_dyedream_1",
  "features": ["pasterdream:calcite_pillar"],
  "step": "top_layer_modification"
}
```

或者通过 `DecorationRegistry.register()` 返回的 `ResourceKey<PlacedFeature>`，在 Java BiomeModifier 中动态注入。

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

**生成逻辑**:
- `findGroundY` + `undergroundBias`（地下部分更宽）
- 锥形递減：`halfWidth = baseWidth + (topWidth - baseWidth) * progress`
- 表面方块以 `crystalChance` 概率替换为晶体
- **晶体高度打断**：`crystalOnlyOnTop=true` 时，检查 y+1 层的截面是否还包含 (dx,dz) 位置。上层还有结构 = 嵌入状态 → 不生成晶体；上层无结构 = 暴露在外 → 可生成晶体
- 主体填充后散落碎片

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
| `crystal(chance, provider)` | 嵌入矿石概率 | -- |

**生成逻辑**:
- 区域占用预检（`isAreaOccupied`）
- 圆形截面，半径线性递减
- 地下部分宽度偏大（undergroundBias）
- **晶体高度打断**：`crystalOnlyOnTop=true` 时，检查 y+1 层的圆形截面是否还包含 (dx,dz) 位置（判断方法：`nextDistSq ≤ (nextR + 0.5)²`）

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

**生成逻辑**:
- `findGroundY` 找地面层
- 随机游走：基于 `clusterSize` 次循环扩散
- 椭球边界检测：`dist²/noise + (relativeY²/yRadius²) ≤ 1`
- 如果 `fillHang=true`：按 Y 排序后稳定化（下移到有支撑 + 填充路径）

### `DecorationType.GATE` — 门框

**参考**: 冰之门（IceGateFeature）

双柱+顶部横梁组成的门框形结构。

| 关键参数 | 说明 | 默认值 |
|:---------|:-----|:------:|
| `body()` | 主体方块 | 必填 |
| `gateWidth(min, max)` | 门框间距 | 4~8 |
| `pillarRadius(radius)` | 柱半径 | 2 |
| `beamThickness(thickness)` | 横梁厚度 | 2 |
| `height(min, max)` | 高度范围 | 5~10 |

**生成逻辑**:
- 左右两柱独立 `findGroundY`
- **柱子部分**：悬空检测，无支撑跳过
- **横梁跨段（两柱之间）**：直接放置（桥梁概念），不检测支撑
- **横梁延伸段（超出柱子）**：需要下方支撑
- 额外装饰散落

### `DecorationType.SCATTER` — 散布

地表随机散布的单个方块群。

| 关键参数 | 说明 | 默认值 |
|:---------|:-----|:------:|
| `body()` | 散布的方块 | 必填 |
| `checkHang(bool)` | 悬空检测 | true |

**生成逻辑**:
- 20 次尝试，每次 50% 概率放置
- 检查下方是否为固体地面
- `checkHang=true` 时悬空跳过

### `DecorationType.AQUATIC` — 水下结构

在水体中生成的结构，需要水环境。

| 关键参数 | 说明 | 默认值 |
|:---------|:-----|:------:|
| `body()` | 主体方块 | 必填 |
| `height(min, max)` | 高度范围 | 3~8 |
| `waterRequired(bool)` | 是否需要水 | true |

**生成逻辑**:
- 类似 PILLAR 但检查水体环境
- 可替换水方块

### `DecorationType.CUSTOM` — 自定义

预留扩展点。需自行实现生成接口并注入。

```java
// 暂未开放，当前 register 会返回 false
DecorationBuilder.create()
    .type(DecorationType.CUSTOM)
    .body(Blocks.STONE)
    .register("my_custom");
```

## 全部配置参数一览

| 方法 | 类型 | 默认值 | 说明 |
|:-----|:----|:------:|:-----|
| `type(DecorationType)` | enum | PILLAR | 结构类型 |
| `body(Block/BlockStateProvider)` | **必填** | -- | 主体方块 |
| `top(Block/BlockStateProvider)` | 可选 | body | 顶部方块 |
| `crystal(float, BlockStateProvider)` | 可选 | -- | 表面嵌入晶体概率+方块 |
| `debris(Block/BlockStateProvider, int, int)` | 可选 | -- | 碎片方块+数量+半径 |
| `height(int, int)` | int | 3~8 | 高度范围 |
| `width(int, int)` | int | 2, 1 | 柱形截面宽度 |
| `radius(int, int)` | int | 2, 0 | 圆形截面半径 |
| `clusterSize(int)` | int | 50 | 团块方块总数 |
| `yRadius(int)` | int | 4 | 团块垂直半径 |
| `irregularity(float)` | float | 0.3 | 团块不规则度 |
| `gateWidth(int, int)` | int | 4~8 | 门框间距 |
| `pillarRadius(int)` | int | 2 | 门框柱半径 |
| `beamThickness(int)` | int | 2 | 横梁厚度 |
| `decorationChance(float)` | float | 0.0 | 额外装饰概率（门框用） |
| `crystalOnlyOnTop(boolean)` | bool | true | 晶体仅放置于最顶层（顶部高度打断） |
| `checkHang(boolean)` | bool | true | 悬空检测 |
| `fillHang(boolean)` | bool | false | 悬空填充 |
| `occupiedCheck(boolean)` | bool | true | 占用检测 |
| `regionCheck(boolean, float)` | bool | false, 0.3 | 区域重叠检测 |
| `waterRequired(boolean)` | bool | false | 水环境要求 |
| `replaceable(BlockPredicate)` | predicate | null (仅空气) | 可替换方块条件（null=仅空气可替换） |
| `biome(String)` | string | "" | 目标群系 ID |
| `rarity(int)` | int | 1 | 稀有度（1/N） |
| `step(GenerationStep.Decoration)` | enum | TOP_LAYER_MODIFICATION | 生成阶段 |

## 生成阶段选择

根据特征类型选择合适的生成阶段：

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
| `TOP_LAYER_MODIFICATION` | 地表地形修改 | ✅ 柱子/尖刺/团块 |

## JSON 自动生成

`DecorationRegistry.generateAllJson()` 会为每个已注册的装饰物生成：

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

**举个例子**：
```
biome_dyedream_3 同时属于：
  ├── #is_dyedream        → dyedream_vegetation.json 加了 lily_pad 到 step 8
  └── #is_dyedream_ocean  → water_vegetation_sparse.json 又加了 lily_pad 到 step 8
                           ↓
              同一特征在同一阶段出现两次 → Feature order cycle 💥
```

**预防**：
- 设计 biome_modifier 时检查 biome tag 是否有**重叠**
- 如果某个特征是群系专用的（如海洋珊瑚），用精确的群系 tag 而非全量 tag
- 确保每个 placed_feature **只在同一个 step 中出现一次**

### 陷阱4：placed_feature 引用了不存在的 configured_feature

**症状**：`Unbound values in registry worldgen/configured_feature` 或 `worldgen/placed_feature`

**原因**：在一个 placed_feature/configured_feature 的 JSON 里引用了另一个 feature（如 `"feature": "pasterdream:xxx"`），但目标 JSON 文件不存在或注册名对不上。

**预防**：
- 每次引用前确认目标已注册且 JSON 文件存在
- 用 Builder API 注册会自动处理所有引用关系 ✅

### 陷阱5：结构叠罗汉（堆叠生成）

**症状**：同一个区块内多个同类结构叠在一起，一个上面顶着另一个，显得很不自然。

**原因**：结构使用 `rarity_filter` + `in_square` + `heightmap` 放置，但没有**区域重叠检查**。同一个 chunk 的不同位置被选中后各自生成结构，如果彼此间距不够，就会叠在一起。

**举个例子**：
```
方解石柱 A 跟方解石柱 B 间距只有 2 格 →
    柱B
    柱A    ← 叠罗汉 💥
```

**预防**：
- 高大结构（柱子、尖刺、高冰丘）一定要加 `regionCheck(true, 0.3)`，生成前会检测区域是否被占用
- 矮小结构（珊瑚礁、散布类）一般不需要，间距天然够
- 用 Builder API 时建议对 Pillar/Spike 类型默认调用 `.regionCheck(true, 0.3f)`

### 陷阱5.5：⚠️ regionCheck 必须有自定义 replaceable 才可靠！

**症状**：JSON 里写了 `region_check: true`，结构还是叠叠乐，拦不住。

**血的教训 🩸**：`isAreaOccupied` 内部有两种检测模式，**天差地别**：

| 有没有自定义 `replaceable` | 检测方式 | 效果 |
|:---------------------------|:---------|:----:|
| ✅ **有**（如方解石柱子把石头/泥土列为可替换） | **精确布尔检测**：groundY ±2 层逐点排查，**任何一个**非可替换方块就阻止 | 100% 可靠，窄到 2×2 的结构也能发现 💯 |
| ❌ **无**（仅认空气） | 退化为**采样+阈值法**：网格扫描后再算比率 vs 0.3 | 宽结构安全，窄结构（宽度≤3）可能漏检 ⚠️ |

**根因**：如果没有自定义 replaceable，`isReplaceable` 只认空气，所以自然地形（石头、冰块、草地）都会被算作「不可替换」。在采样+阈值模式下，自然地形会大幅稀释结构方块所占的比例，导致窄结构无法超过 0.3 阈值。

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

**错误理解**：`crystalOnlyOnTop(true)` = 只检查 `y < topY` → 只有结构最顶层能放晶体，中间层哪怕缩窄了暴露出来的表面也没有晶体。

**正确实现**：`crystalOnlyOnTop(true)` = 对每个表面方块，**计算 y+1 层的截面是否还包含这个 (dx,dz) 位置**。
- 上层还有结构 → 嵌入状态 → ❌ 不生成晶体
- 上层无结构（暴露在外） → ✅ 可以生成晶体

举个例子——锥形柱子从宽 5 缩到宽 3 再缩到宽 1：
```
    [*]          ← y=topY：中心1块，暴露在外→长晶体✅
  [ ][ ][*]      ← y=mid：宽3，边缘暴露→长晶体✅（老逻辑只有topY有，完全暴殄天物！）
[ ][ ][ ][ ][ ]  ← y=bottom：宽5，边缘上面还有结构→嵌入状态❌
```

**PILLAR/AQUATIC 用方截面检测**：`hasBlockAbove = dx ∈ [-nextHalfSize, nextWidth - nextHalfSize) && dz ∈ [...]`

**SPIKE 用圆截面检测**：`hasBlockAbove = nextDistSq ≤ (nextR + 0.5)²`

### 陷阱7：crystalOnlyOnTop 默认值差异（Builder vs JSON）

**症状**：通过 Builder API 创建的结构晶体很少（只在最顶层），但手写 JSON 的结构晶体看起来很多。

**原因**：Builder API 的 `crystalOnlyOnTop` 默认是 `true`（新装饰物默认晶体帽），但 JSON CODEC 解码时默认是 `false`（兼容旧 JSON）。这是有意设计的差异：

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
3. **⚠️ 给高大结构配 `replaceable` + `regionCheck`**：这是防叠罗汉的黄金组合！有自定义 `replaceable` 才能走精确布尔检测，100% 挡住叠叠乐。Builder API：`.replaceable(predicate).regionCheck(true, 0.3f)`
4. **⚠️ 晶体逻辑是几何截面检测**：`crystalOnlyOnTop(true)` 不是简单的 `y < topY`，而是检查 y+1 层截面是否包含 (dx,dz)。锥形柱变窄时暴露的表面也会长晶体
5. **⚠️ 检查 biome tag 重叠**：在设计 biome_modifier 时，确保同一个 placed_feature 不会通过不同 tag 被同一个群系添加两次
6. **生成阶段选择**：地面结构用 `TOP_LAYER_MODIFICATION`，植被用 `VEGETAL_DECORATION`
7. **稀有度调整**：大型结构用 `rarity(5~10)`，小型结构用 `rarity(2~3)`
8. **悬空填充适用场景**：团块（Blob）建议启用 `fillHang(true)`，柱形/尖刺建议仅 `checkHang(true)`
9. **JSON 生成时机**：建议在定义完装饰物后在 `FMLCommonSetupEvent` 中调用 `generateAllJson()`，然后去 `worldgen/` 目录复制生成的 JSON 文件
10. **Biome Modifier**：生成的 JSON 需要配合 `neoforge/biome_modifier/` 下的注入文件使用，或者通过代码注入