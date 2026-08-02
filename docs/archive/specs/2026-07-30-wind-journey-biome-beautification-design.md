# 风之旅途维度全面美化 — 设计文档

> **日期**: 2026-07-30
> **项目**: NeoPasterDream1 (NeoForge 1.21.1)
> **状态**: 设计稿 v1

---

## 1. 概述

### 1.1 目标

对"风之旅途"（wind_journey_world）维度进行全方位视觉增强，包括：

- **群系体系**：从现有 2 个群系扩展至 6 个特色群系
- **粒子系统**：混合体系 — 贯穿全维度的基础粒子 + 各群系专属粒子
- **地形生成**：重写噪声设置，创造分层浮岛地形
- **树木系统**：自定义 WindbentFoliagePlacer，使用风泊木（windmoor）
- **地表装饰**：通过 DecorationBuilder API 注册各群系专属装饰物
- **植被分布**：各群系独特的草/花/地被植物

### 1.2 核心设定

> **世界观**: 云中仙境 — 永恒的风吹拂着浮空群岛，云海之下是浓密的厚云层，岛屿自中心向边缘逐渐破碎散落

**自然逻辑**:
- 海拔梯度：低层浓密温暖 → 中层植被繁茂 → 高层清冷开阔
- 风蚀梯度：迎风面裸露风蚀 → 背风面植被繁茂
- 水分梯度：云雾汇聚处湿润 → 风影区干燥
- 岛屿侵蚀：大型岛屿周边散落逐级破碎的小岛

---

## 2. 群系体系

### 2.1 群系一览

| ID | 名称 | 海拔 | 温度 | 降雨 | 特征 | 地面方块 |
|----|------|------|------|------|------|---------|
| `wind_journey_biome_0` → **风起草原** | Windrise Plains | 低层 32~56 | 1.0 | 0.5 | 开阔草原，风拂长草，零星岩柱 | cyan_moss_stone / cyan_stone |
| `wind_journey_biome_1` → **碧空群岛** | Azure Archipelago | 高层 80~112 | 0.4 | 0.3 | 高耸石质群岛，白沙草甸，稀少植被 | white_sand / cyan_stone |
| **新增** `wind_journey_biome_2` → **云渊湿地** | Cloudfen Wetlands | 低层 32~48 | 0.8 | 0.9 | 迷雾弥漫，浅水洼与云交织，芦苇 | cyan_moss_stone / mud |
| **新增** `wind_journey_biome_3` → **风蚀崖地** | Windscour Cliffs | 中层 48~80 | 0.6 | 0.3 | 陡峭岩柱/拱门，稀疏坚韧植被 | exposed cyan_stone |
| **新增** `wind_journey_biome_4` → **云栖林** | Cloudrest Grove | 中层 56~80 | 0.7 | 0.6 | 风塑矮林，斑驳光影，藤蔓垂挂 | cyan_moss_stone / cyan_stone |
| **新增** `wind_journey_biome_5` → **亘风原** | Eternal Wind Field | 全层 32~96 | 0.5 | 0.1 | 万年风蚀的裸露岩层，几乎无植被 | weathered cyan_stone |

### 2.2 群系参数映射

新群系的 `temperature`/`humidity`/`continentalness` 参数映射（用于 `multi_noise` 生物群系源）：

| 群系 | temperature | humidity | continentalness | erosion | depth |
|------|-------------|----------|-----------------|---------|-------|
| 风起草原 | 0.7~1.0 | 0.3~0.7 | -0.3~0.5 | 0.2~0.5 | 0.1~0.4 |
| 云渊湿地 | 0.6~0.9 | 0.7~1.0 | -0.5~0.2 | 0.5~0.8 | 0.0~0.3 |
| 风蚀崖地 | 0.4~0.8 | 0.1~0.4 | 0.3~0.8 | 0.0~0.3 | 0.3~0.6 |
| 碧空群岛 | -0.3~0.4 | 0.1~0.4 | -0.8~-0.3 | 0.3~0.6 | 0.6~1.0 |
| 云栖林 | 0.5~0.8 | 0.4~0.8 | 0.0~0.5 | 0.3~0.6 | 0.2~0.5 |
| 亘风原 | 0.3~0.7 | 0.0~0.2 | -0.5~0.5 | 0.0~0.2 | 0.0~0.5 |

### 2.3 生物群系标签

```json
// data/pasterdream/tags/worldgen/biome/is_wind_journey.json
{
  "values": [
    "pasterdream:wind_journey_biome_0",
    "pasterdream:wind_journey_biome_1",
    "pasterdream:wind_journey_biome_2",
    "pasterdream:wind_journey_biome_3",
    "pasterdream:wind_journey_biome_4",
    "pasterdream:wind_journey_biome_5"
  ]
}
```

### 2.4 语言翻译

```json
"biome.pasterdream.wind_journey_biome_0": "风起草原",
"biome.pasterdream.wind_journey_biome_1": "碧空群岛",
"biome.pasterdream.wind_journey_biome_2": "云渊湿地",
"biome.pasterdream.wind_journey_biome_3": "风蚀崖地",
"biome.pasterdream.wind_journey_biome_4": "云栖林",
"biome.pasterdream.wind_journey_biome_5": "亘风原"
```

---

## 3. 地形生成

### 3.1 架构

从当前内联噪声设置迁移为独立的 `noise_settings` JSON 文件，维度 JSON 引用该文件。

**文件结构**:
```
data/pasterdream/
├── worldgen/noise_settings/wind_journey_world.json  ← 新建
├── dimension/wind_journey_world.json                 ← 修改：引用 noise_settings
└── dimension_type/wind_journey_world.json             ← 基本不变
```

### 3.2 垂直分层

| 层 | Y范围 | 描述 |
|----|-------|------|
| 云海层 | 0~32 | 厚云(默认方块)，淹没低层群系 |
| 低层群岛 | 32~56 | 风起草原、云渊湿地 |
| 中层群岛 | 56~80 | 风蚀崖地、云栖林 |
| 高层群岛 | 80~112 | 碧空群岛 |
| 亘风原 | 32~96 | 穿插各层的裸露岩区 |

### 3.3 密度函数策略

```
final_density = 基础岛屿噪声 × 高度衰减(per biome Y-level) × 侵蚀噪声(破碎边缘)
```

- **基础噪声**：复用 `minecraft:noise` + `minecraft:add` 产生间断的补丁状地形
- **高度衰减**：根据 Y 高度对不同群系区域施加不同的密度阈值
- **侵蚀噪声**：额外的 erosion 层使岛屿边缘碎裂，大岛周围出现渐小的碎岛
- **surface_rule**：基于群系 + Y 高度的多条件方块替换

### 3.4 表面方块规则

| 条件 | 表面 | 表下层(1~4格) | 深层 |
|------|------|--------------|------|
| biome_0 (风起草原) + 非水下 | cyan_moss_stone | cyan_stone | thick_cloud |
| biome_0 + 水下 | water | cyan_moss_stone | cyan_stone |
| biome_1 (碧空群岛) + 非水下 | white_sand | white_sand | cyan_stone |
| biome_1 + 水下 | water | white_sand | white_sand |
| biome_2 (云渊湿地) + 非水下 | cyan_moss_stone | mud | thick_cloud |
| biome_3 (风蚀崖地) | cyan_stone | cyan_stone | thick_cloud |
| biome_4 (云栖林) | cyan_moss_stone | cyan_stone | thick_cloud |
| biome_5 (亘风原) | cyan_stone | cyan_stone | cyan_stone |

---

## 4. 粒子系统

### 4.1 设计原则

**混合体系**：贯穿全维度的基础风粒子 + 各群系专属粒子

### 4.2 基础风粒子（全维度）

| 粒子ID | 注册名 | 行为 | 纹理 |
|--------|--------|------|------|
| 风丝 | `wind_thread` | 半透明流线，沿风向(WindDirection游戏规则)漂移，持续生成 | 4帧细长拖尾精灵图 |
| 飘羽 | `feather_drift` | 白色/淡灰羽毛缓慢盘旋下落，密度×2 | 复用现有 `feather_white_particle` (12帧) |

### 4.3 群系专属粒子

| 群系 | 粒子ID | 注册名 | 颜色/行为 | 密度 |
|------|--------|--------|----------|------|
| 风起草原 | 金尘 | `golden_dust` | 暖金色光点，随风飘散，日落密度↑ | 0.005 |
| 云渊湿地 | 雾滴 | `mist_droplet` | 淡蓝白雾粒，缓慢垂直浮动，低处聚集 | 0.008 |
| 风蚀崖地 | 岩尘 | `stone_dust` | 赭褐色微粒，遇风加速，崖壁附近密集 | 0.004 |
| 碧空群岛 | 晶辉 | `crystal_glint` | 银白闪光点，高海拔稀疏闪烁 | 0.002 |
| 云栖林 | 林孢 | `wood_spore` | 翠绿金色孢子，缓慢螺旋上升 | 0.006 |
| 亘风原 | 古尘 | `ancient_dust` | 淡紫尘埃，极慢速，微弱拖尾 | 0.003 |

### 4.4 粒子注册

所有粒子通过 `ParticleAPI`（`PDParticles.java`）注册使用自定义纹理（非原版复用）。

群系绑定：在 biome JSON 的 `effects.particle` 字段中配置（沿用现有方式）。

风向系统：复用 `WindJourneyEvents` 的日更风向，粒子生成方向动态适配。

---

## 5. 树木系统

### 5.1 使用方块

全部复用现有风泊木（windmoor）系列，无需新增方块注册：

| 用途 | 方块 | 注册名 |
|------|------|--------|
| 树干 | 风泊原木 | `windmoor_log` |
| 树冠 | 风泊树叶 #0/#1/#2 | `windmoor_leaves_0/1/2` |

### 5.2 树种设计

| 树种 | 群系 | 高度 | 树冠形态 | 树干 | 实现方式 |
|------|------|------|----------|------|---------|
| **云栖树** (Cloudrest) | 云栖林 | 4~6格 | 扁平伞状，冠径3~4，高度1~2 | 风泊原木 1~2格直立 | `WindbentFoliagePlacer` (FLAT模式) |
| **风偃木** (Windbent) | 风起草原 | 3~5格 | 偏背风面不对称 | 风泊原木倾斜1~2格 | `WindbentFoliagePlacer` (BENT模式) + `WindbentTrunkPlacer` |
| **擎风松** (Windpiercer) | 碧空群岛 | 6~10格 | 锥形，背风面更密 | 风泊原木细高直立 | `WindbentFoliagePlacer` (CONE模式) |

### 5.3 代码实现

```java
// WindbentFoliagePlacer.java — 自定义树冠放置器
// 三种模式: FLAT(扁平伞状), BENT(偏向), CONE(锥形)
// 继承 FoliagePlacer，重写 createFoliage() 和 foliageHeight()

// WindbentTrunkPlacer.java — 可选：自定义树干放置器
// 支持倾斜主干（用于风偃木）

// 注册方式:
// configured_feature — minecraft:tree + custom foliage/trunk placer
```

### 5.4 树木分布

| 群系 | 树木种类 | 密度 | 放置阶段 |
|------|---------|------|---------|
| 风起草原 | 风偃木 | 稀疏（每区块~0.3） | `vegetal_decoration` |
| 云栖林 | 云栖树 | 密集（每区块~3） | `vegetal_decoration` |
| 碧空群岛 | 擎风松 | 极稀疏（每区块~0.1） | `vegetal_decoration` |
| 云渊湿地 | 无 | — | — |
| 风蚀崖地 | 无 | — | — |
| 亘风原 | 无 | — | — |

---

## 6. 地表装饰物

### 6.1 技术方案

- **API**：`DecorationBuilder` 链式 API（已存在，在 PasterDreamAPI 模块）
- **类型**：SPIKE / PILLAR / BLOB / SCATTER / CUSTOM
- **注册**：`DecorationBuilder.create()` → `.register("name")`
- **群系绑定**：JSON 式 `neoforge:add_features` biome modifier
- **生成阶段**：统一 `TOP_LAYER_MODIFICATION`
- **专属性**：全部通过 biome modifier 限定 `#is_wind_journey` 标签或具体群系，不与染梦世界共用

### 6.2 各群系装饰物

#### 风起草原 (Windrise Plains)

| 装饰物 | API类型 | 方块 | 稀有度 | 说明 |
|--------|--------|------|--------|------|
| 风蚀孤石 `wind_solo_rock` | SPIKE | cyan_stone | 4 | 短粗风化岩柱，tilt=0.15，高2~4 |
| 矮岩柱群 `short_pillar_cluster` | PILLAR | cyan_moss_stone | 3 | 2~4格柱体，偶有 moss 镶嵌 |
| 保留：鹅卵石 | — | pebble_0 | — | 现有 feature 保留 |
| 保留：萤火虫巢 | — | firefly_nest | — | 现有 feature 保留 |

#### 云渊湿地 (Cloudfen Wetlands)

| 装饰物 | API类型 | 方块 | 稀有度 | 说明 |
|--------|--------|------|--------|------|
| 雾苔石堆 `mist_stone_mound` | BLOB | cyan_moss_stone | 2 | 不规则团块，clusterSize=30 |
| 气泡泉口 `bubble_spring` | SCATTER | cyan_stone | 5 | 浅水区小石堆（通过 waterRequired 限定） |
| 保留：安全湖泊 | — | water/cyan_stone | — | 现有 feature 迁移至此 |

#### 风蚀崖地 (Windscour Cliffs)

| 装饰物 | API类型 | 方块 | 稀有度 | 说明 |
|--------|--------|------|--------|------|
| 风蚀拱门 `windscour_arch` | CUSTOM | cyan_stone | 8 | 双柱+横梁，倒塌变体(类似 IceGateGenerator) |
| 岩柱群 `cliff_pillar_cluster` | PILLAR | cyan_stone | 3 | 高6~12格，细柱形，topWidth=1 |
| 碎石坡 `scree_slope` | SCATTER | pebble_0 | 1 | 大量散布，count高 |

#### 碧空群岛 (Azure Archipelago)

| 装饰物 | API类型 | 方块 | 稀有度 | 说明 |
|--------|--------|------|--------|------|
| 风磨石 `wind_milled_stone` | BLOB | windrunner_crystal_block | 5 | 光滑圆形白石堆，clusterSize=20 |
| 晶辉晶洞 `crystal_geode` | SCATTER | windrunner_crystal_block | 4 | 地表半暴露水晶簇 |

#### 云栖林 (Cloudrest Grove)

| 装饰物 | API类型 | 方块 | 稀有度 | 说明 |
|--------|--------|------|--------|------|
| 倒木 `fallen_log` | SCATTER | windmoor_log | 4 | 水平放置的倒塌树干(水平方向随机) |
| 树根堆 `root_mound` | BLOB | mossy_cyan_stone_bricks | 3 | 基部膨大根系团块 |
| 蘑菇圈 `mushroom_ring` | SCATTER | red_mushroom/brown_mushroom | 5 | 环形散布小蘑菇 |

#### 亘风原 (Eternal Wind Field)

| 装饰物 | API类型 | 方块 | 稀有度 | 说明 |
|--------|--------|------|--------|------|
| 古老石阵 `ancient_stone_circle` | CUSTOM | cyan_stone | 10 | 3~5根竖石排列成圈 |
| 风蚀沟 `wind_gully` | CUSTOM | cyan_stone | 7 | 地表细长凹陷沟槽 |

### 6.3 现有风之旅途特征迁移

现有 7 个 `ground_feature_wind_journey_0~6` 按以下规则迁移：

| 现有特征 | 迁移目标 | 操作 |
|---------|---------|------|
| feature_0 (高空云团) | 保留，范围扩大至 Y 64~220 | biome modifier 改为 `#is_wind_journey` |
| feature_1 (小石灵方块) | 保留在风起草原 | biome modifier 指向 biome_0 |
| feature_2 (低空云团) | 迁移至云渊湿地 + 碧空群岛 | biome modifier 指向 biome_1 + biome_2 |
| feature_3 (鹅卵石) | 保留在风起草原 | biome modifier 指向 biome_0 |
| feature_4 (萤火虫巢) | 保留在风起草原 | biome modifier 指向 biome_0 |
| feature_5 (青苔石替换淤泥) | 迁移至云渊湿地 | biome modifier 指向 biome_2 |
| feature_6 (安全湖泊) | 迁移至云渊湿地 | biome modifier 指向 biome_2 |

---

## 7. 植被分布

### 7.1 花卉与草丛

| 群系 | 植被 | 来源 | 放置阶段 |
|------|------|------|---------|
| 风起草原 | `grass_13/14/15` + 新增 `flower_19`(风铃花) + 新增 `tall_grass_5`(羽穗草) | 现有+新注册 | `vegetal_decoration` |
| 云渊湿地 | `reeds` + `flower_20`(雾灯花) | 现有+新注册 | `vegetal_decoration` |
| 风蚀崖地 | `flower_21`(岩缝花) — 极稀疏 | 新注册 | `vegetal_decoration` |
| 碧空群岛 | `grass_16`(晶草) + `flower_22`(霜晶花) | 新注册 | `vegetal_decoration` |
| 云栖林 | `grass_7/8` + 现有丛林草被 | 现有 | `vegetal_decoration` |
| 亘风原 | 无 | — | — |

### 7.2 新注册植被列表

| 注册名 | 中文名 | 类型 | 群系 |
|--------|--------|------|------|
| `flower_19` | 风铃花 | DyedreamFlowerBlock (淡黄) | 风起草原 |
| `flower_20` | 雾灯花 | DyedreamFlowerBlock (淡蓝+发光) | 云渊湿地 |
| `flower_21` | 岩缝花 | DyedreamFlowerBlock (紫红) | 风蚀崖地 |
| `flower_22` | 霜晶花 | DyedreamFlowerBlock (白半透明) | 碧空群岛 |
| `grass_16` | 晶草 | DyedreamFlowerBlock (浅蓝) | 碧空群岛 |
| `tall_grass_5` | 羽穗草丛 | DyedreamDoublePlantBlock | 风起草原 |

以上通过 `PDBlocksVegetation.java` 的 API 批量注册流程注册。

---

## 8. 维度配置变更

### 8.1 维度 JSON 变更

从当前内联 noise 配置改为引用独立的 `noise_settings` 文件：

```json
// 修改前: dimension/wind_journey_world.json — 包含嵌入式 noise
// 修改后:
{
  "type": "pasterdream:wind_journey_world",
  "generator": {
    "type": "minecraft:noise",
    "settings": "pasterdream:wind_journey_world",
    "biome_source": {
      "type": "minecraft:multi_noise",
      "preset": "minecraft:overworld",
      "biomes": [
        // 6个群系各自的参数范围
      ]
    }
  }
}
```

### 8.2 Noise Settings

新建 `data/pasterdream/worldgen/noise_settings/wind_journey_world.json`：

| 参数 | 值 |
|------|-----|
| sea_level | 0 |
| min_y | 0 |
| height | 128 |
| size_horizontal | 2 |
| size_vertical | 1 |
| island_noise_override | true |
| aquifers_enabled | false |
| ore_veins_enabled | false |
| default_block | thick_cloud |
| default_fluid | water |

### 8.3 PDBiomes 常量

添加风之旅途群系的 `ResourceKey` 常量：

```java
// PDBiomes.java 新增
public static final ResourceKey<Biome> BIOME_WIND_JOURNEY_0 = ...;  // 风起草原
public static final ResourceKey<Biome> BIOME_WIND_JOURNEY_1 = ...;  // 碧空群岛
public static final ResourceKey<Biome> BIOME_WIND_JOURNEY_2 = ...;  // 云渊湿地
public static final ResourceKey<Biome> BIOME_WIND_JOURNEY_3 = ...;  // 风蚀崖地
public static final ResourceKey<Biome> BIOME_WIND_JOURNEY_4 = ...;  // 云栖林
public static final ResourceKey<Biome> BIOME_WIND_JOURNEY_5 = ...;  // 亘风原
```

### 8.4 WindJourneyEvents 变更

- 移除进维提示"本主题梦境尚未完工"
- 在 `onLevelTick` 中对新增群系的风向广播粒子适当增加视觉反馈

---

## 9. 文件清单（新增/修改）

### 9.1 新增 Java 文件

| 文件 | 路径 | 说明 |
|------|------|------|
| `WindbentFoliagePlacer.java` | `worldgen/foliage/` | 自定义树冠放置器 |
| `WindbentTrunkPlacer.java` | `worldgen/trunk/` | 自定义树干放置器 |
| `WindJourneyDecorations.java` | `registry/` | 风之旅途装饰物注册 |
| `WindscourArchGenerator.java` | `worldgen/feature/` | 风蚀拱门自定义生成器 |
| `AncientStoneCircleGenerator.java` | `worldgen/feature/` | 古老石阵自定义生成器 |
| `WindGullyGenerator.java` | `worldgen/feature/` | 风蚀沟自定义生成器 |

### 9.2 新增资源文件

| 文件 | 数量 |
|------|------|
| `worldgen/biome/wind_journey_biome_2~5.json` | 4 |
| `worldgen/noise_settings/wind_journey_world.json` | 1 |
| `worldgen/configured_feature/`（树木+装饰） | ~15 |
| `worldgen/placed_feature/`（同上） | ~15 |
| `neoforge/biome_modifier/`（群系绑定） | ~18 |
| `tags/worldgen/biome/is_wind_journey.json` | 1 |
| 粒子纹理（`textures/particle/`） | ~8 |
| 新花/草纹理（`textures/block/`） | ~7 |

### 9.3 修改文件

| 文件 | 说明 |
|------|------|
| `dimension/wind_journey_world.json` | 引用 noise_settings |
| `PDBiomes.java` | 添加6个群系 ResourceKey |
| `PDParticles.java` | 注册6个新粒子 |
| `ModDecorations.java` | 添加 WindJourneyDecorations.register() 调用 |
| `WindJourneyEvents.java` | 移除"未完工"提示 |
| `PDBiomeModifiers.java` | 更新已废弃的 codec 引用 |
| `zh_cn.json` | 添加所有新群系/植被/装饰物翻译 |

---

## 10. 实施顺序

### Phase 1: 基础设施
1. 创建 `is_wind_journey.json` 标签
2. 在 `PDBiomes.java` 添加6个群系 `ResourceKey`
3. 创建4个新 biome JSON（2~5）
4. 修改 biome_0 和 biome_1 JSON（更新名称/粒子/颜色等）

### Phase 2: 地形
5. 创建 `noise_settings/wind_journey_world.json`
6. 修改 `dimension/wind_journey_world.json` 引用外部 noise_settings
7. 更新 surface_rule 支持6个群系

### Phase 3: 粒子
8. 注册6个新粒子（PDParticles.java）
9. 创建粒子纹理精灵图
10. 在 biome JSON 中绑定粒子

### Phase 4: 树木
11. 编写 `WindbentFoliagePlacer.java`
12. 编写 `WindbentTrunkPlacer.java`
13. 创建 configured_feature + placed_feature JSON
14. 创建 biome modifier JSON

### Phase 5: 装饰物
15. 编写 `WindJourneyDecorations.java`
16. 编写自定义生成器（拱门/石阵/风蚀沟）
17. 在 `ModDecorations.java` 中挂载
18. 创建 biome modifier JSON 绑定

### Phase 6: 植被
19. 注册新花/草（PDBlocksVegetation）
20. 创建 placed_feature JSON
21. 创建 biome modifier JSON
22. 补全语言文件

### Phase 7: 收尾
23. 迁移现有 ground_feature 指向新群系
24. 运行 DataGen 验证
25. 编译测试（`./gradlew compileJava`）
26. 运行游戏验证

---

## 11. 依赖与约束

### 已存在且可直接复用

| 项目 | 来源 | 说明 |
|------|------|------|
| DecorationBuilder API | PasterDreamAPI | 7种装饰物类型 |
| 风泊木全套 | PDBlocksWindJourney | 16项木系 + 3种树叶，纹理齐全 |
| 青石全套 | PDBlocksDyedreamPhase2 | cyan_stone + 砖/台阶/楼梯/墙/柱 |
| 风之旅途维度 | 已有 | 部分群系/特征 |
| 日更风向系统 | WindJourneyEvents | 复用，增强粒子方向适配 |
| 安全湖泊 Feature | SafeLakeFeature | 迁移至云渊湿地 |

### 需新建

| 项目 | 数量 |
|------|------|
| 群系 JSON 文件 | 4 |
| 自定义 FoliagePlacer/TrunkPlacer | 2 |
| 自定义装饰物生成器 | 3 |
| 粒子纹理 | 8 |
| 花/草纹理 | 7 |
| 装饰物注册类 | 1 |
| BiomeModifier JSON | ~18 |
| Configured/Placed Feature JSON | ~30 |

### 约束条件

1. 所有地表装饰物通过 `DecorationBuilder` API 注册，不手写 Feature 类
2. 群系绑定通过 JSON biome modifier（`neoforge:add_features`），不写 Java codec
3. 装饰物命名空间统一为 `wind_journey_` 前缀（如 `wind_journey_solo_rock`）
4. 装饰物只通过 `#is_wind_journey` 标签或具体群系 ID 绑定

---

## 12. 风险与注意事项

| 风险 | 影响 | 缓解措施 |
|------|------|---------|
| 噪声设置变更导致地形不兼容 | 存档地形断裂 | 在地形生成前进行充分测试 |
| 6个群系的 multi_noise 参数难调 | 群系边界生硬 | 先在 benchmark 中调试参数 |
| 自定义 FoliagePlacer 序列化错误 | 游戏崩溃 | 编写正确的 MapCodec，先测试单种树 |
| 大量新粒子性能开销 | FPS 下降 | 各群系粒子密度控制在 0.002~0.008 |
| 装饰物与现有 feature 重叠 | 方块冲突 | 设置合理的 occupiedCheck 和 regionCheck |

---

*设计文档结束*
