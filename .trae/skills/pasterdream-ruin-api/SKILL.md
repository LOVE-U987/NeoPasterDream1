---
name: "pasterdream-ruin-api"
description: "PasterDream模组自定义遗迹注册专用API，提供Facade+Builder模式一键注册自定义结构/遗迹。在需要创建新遗迹结构、配置结构类型/模板池/结构集或生成worldgen JSON时调用。"
---

# PasterDream Ruin API

本 Skill 提供 PasterDream 模组自定义遗迹注册专用 API 的使用指南，采用 **Facade + Builder** 模式（与 BlockAPI / DimensionAPI 风格一致），通过链式调用即可完成结构类型的注册、模板池配置、结构集放置配置和资源文件生成。

> ⚠️ **注意**：本 API 专用于"自定义遗迹"（结构），而非结构管理。它专注于单个结构的注册和配置，不涉及整体结构管理体系。

## 适用场景

- 创建新的自定义遗迹结构（Structure 子类）
- 配置结构生物群系、地形适应、起始模板池
- 配置结构集放置参数（间距、分离值、盐值）
- 生成模板池（template_pool）JSON 文件
- 自动生成 structure JSON 和 structure_set JSON 文件
- 查询已注册的结构 ResourceKey 信息

## 快速开始

```java
// ====== 1. 创建遗迹结构 ======
RuinResult result = RuinAPI.createRuin("dyedream_ruins")
    .biomeTag("pasterdream:is_dyedream")               // 生物群系标签
    .templatePool("pasterdream:dyedream_ruins_pool")    // 起始模板池
    .structureClass(DyedreamRuinsStructure.class)       // 结构类
    .codec(DyedreamRuinsStructure.CODEC)                // 结构 MapCodec
    .terrainAdaptation("beard_thin")                    // 地形适应
    .step("surface_structures")                         // 生成阶段
    .size(7)                                            // 扩展大小
    .startHeight(0)                                     // 起始高度
    .build();

// ====== 2. 创建结构集配置 ======
RuinResult resultWithSet = RuinAPI.createRuinSet("dyedream_ruins", "dyedream_ruins_set")
    .spacing(32)            // 间距（区块数）
    .separation(8)          // 分离值（区块数）
    .salt(12345)            // 随机种子盐值
    .build();

// ====== 3. 在代码中引用结构 ======
// 获取结构 ResourceKey
ResourceKey<Structure> structureKey = result.structureKey();
ResourceKey<StructureSet> setKey = resultWithSet.setKey();
```

## 前置条件

在 `PasterDreamMod` 构造函数中注册 RuinAPI 的 REGISTRY（或调用统一入口）：

```java
public PasterDreamMod(IEventBus modEventBus) {
    // 方式一：统一注册（推荐，包含所有 API）
    PasterDreamAPI.registerAll(modEventBus);
    // 方式二：单独注册
    // RuinAPI.REGISTRY.register(modEventBus);
}
```

## API 架构

```
RuinAPI                             ← Facade 门面
  ├── createRuin(name)              ← 工厂方法 → RuinBuilder
  ├── createRuinSet(ruinName, setName) ← 结构集工厂 → StructureSetBuilder
  ├── getRuin(name)                 ← 查询 RuinResult → Optional
  ├── getAllRuins()                 ← 所有已注册结构
  ├── hasRuin(name)                 ← 判断是否已注册
  ├── printStructureDiagnostics()   ← 打印所有结构生成诊断报告
  ├── printStructureDiagnostics(name) ← 打印指定结构诊断
  ├── assessTerrain(name, chunkX, chunkZ, level) ← 评估地形是否适合放置
  ├── reportPlacement(name, success, reason)     ← 报告放置结果
  └── getPlacementRecord(name)      ← 获取放置统计记录

RuinBuilder                         ← Builder 构建器
  ├── biomeTag(String)              ← 生物群系标签（自动加 #）
  ├── templatePool(String)          ← 起始模板池
  ├── structureClass(Class)         ← 结构类
  ├── codec(MapCodec)               ← 结构 MapCodec（必须）
  ├── terrainAdaptation(String)     ← 地形适应
  ├── step(String)                  ← 生成阶段（默认 surface_structures）
  ├── size(int)                     ← 扩展大小（默认 7）
  ├── startHeight(int)              ← 起始高度（默认 0）
  ├── extraFields(JsonObject)       ← 自定义额外 JSON 字段
  ├── largeStructure(TerrainRequirements) ← ★ 标记为大型结构（地形协商）
  ├── withTerrainPlatform(int flatRadius) ← ★ 大型结构快捷方法（平地平台）
  ├── generateJson(boolean)         ← 是否自动生成 JSON（默认 true）
  ├── basePath(String)              ← 资源文件基础路径
  └── build()                       ← 注册 + 生成 JSON → RuinResult

StructureSetBuilder                 ← 结构集 Builder
  ├── spacing(int)                  ← 间距（区块数，默认 32）
  ├── separation(int)               ← 分离值（区块数，默认 8）
  ├── salt(int)                     ← 随机种子盐值（默认 0）
  ├── placementType(String)         ← 放置类型（默认 minecraft:random_spread）
  ├── generateJson(boolean)         ← 是否自动生成 JSON
  ├── basePath(String)              ← 资源文件基础路径
  └── build()                       ← 生成 JSON → RuinResult（带 setKey）

RuinResult                          ← Record 结果
  ├── name()                        → String（结构注册名）
  ├── typeKey()                     → ResourceKey<StructureType<?>>
  ├── structureKey()                → ResourceKey<Structure>（用于 JSON 引用）
  ├── setKey()                      → ResourceKey<StructureSet>（可能为 null）
  ├── terrainRequirements()         → TerrainRequirements / null（大型结构用）
  ├── hasSetKey()                   → boolean
  ├── isLargeStructure()            → boolean（是否为大型结构）
  ├── withSetKey(setName, modId)    → 新的 RuinResult（带结构集 Key）
  └── of(modId, name[, terrainRequirements]) → 静态工厂
```

## Builder 配置参考

### RuinBuilder 参数

| 方法 | 参数 | 说明 | 必需 |
|------|------|------|:----:|
| `biomeTag(String)` | 标签 ID | 生物群系标签（如 `pasterdream:is_dyedream`，自动加 `#`） | ❌ |
| `templatePool(String)` | 池 ID | 起始模板池（如 `pasterdream:dyedream_ruins_pool`） | ❌ |
| `structureClass(Class)` | Class | 结构类（Structure 子类） | ❌ |
| `codec(MapCodec)` | MapCodec | 结构序列化编解码器 | ✅ |
| `terrainAdaptation(String)` | 类型名称 | 地形适应类型（`beard_thin`, `beard_box`, `none`） | ❌ |
| `step(String)` | 阶段名 | 生成阶段（默认 `surface_structures`） | ❌ |
| `size(int)` | 大小 | 结构扩展大小（默认 7） | ❌ |
| `startHeight(int)` | 高度 | 起始生成高度（默认 0） | ❌ |
| `extraFields(JsonObject)` | JSON | 自定义额外 JSON 字段 | ❌ |
| `largeStructure(TerrainRequirements)` | 地形需求 | ★ 标记为大型结构，build() 时注册到地形协商器 | ❌ |
| `withTerrainPlatform(int)` | 平地半径 | ★ 大型结构快捷方法：平地平台，自动计算 blend 半径 | ❌ |
| `generateJson(boolean)` | bool | 是否自动生成 JSON（默认 true） | ❌ |
| `basePath(String)` | 路径 | 资源文件基础路径（默认 `src/main/resources`） | ❌ |

### StructureSetBuilder 参数

| 方法 | 参数 | 说明 | 默认值 |
|------|------|------|:------:|
| `spacing(int)` | 区块数 | 结构生成间距 | 32 |
| `separation(int)` | 区块数 | 结构最小分离距离 | 8 |
| `salt(int)` | 整数 | 随机种子盐值 | 0 |
| `placementType(String)` | 类型 ID | 放置算法类型 | `minecraft:random_spread` |
| `generateJson(boolean)` | bool | 是否自动生成 JSON | true |
| `basePath(String)` | 路径 | 资源文件基础路径 | `src/main/resources` |

## 完整示例

### 基本遗迹结构

```java
// 1. 定义结构类
public class DyedreamRuinsStructure extends Structure {
    public static final MapCodec<DyedreamRuinsStructure> CODEC = simpleCodec(DyedreamRuinsStructure::new);

    public DyedreamRuinsStructure(StructureSettings settings) {
        super(settings);
    }

    @Override
    public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        // 实现结构生成逻辑...
        return Optional.empty();
    }

    @Override
    public StructureType<?> type() {
        return RuinAPI.getRuin("dyedream_ruins").structureType();
    }
}

// 2. 注册
RuinResult dyedream = RuinAPI.createRuin("dyedream_ruins")
    .biomeTag("pasterdream:is_dyedream")
    .templatePool("pasterdream:dyedream_ruins_pool")
    .structureClass(DyedreamRuinsStructure.class)
    .codec(DyedreamRuinsStructure.CODEC)
    .terrainAdaptation("beard_thin")
    .build();

// 3. 配置结构集
RuinAPI.createRuinSet("dyedream_ruins", "dyedream_ruins_set")
    .spacing(32)
    .separation(8)
    .salt(12345)
    .build();

// 4. 获取 ResourceKey 用于 JSON 引用
ResourceKey<Structure> key = dyedream.structureKey();
```

### 地下遗迹结构

```java
RuinResult underground = RuinAPI.createRuin("deep_ruins")
    .biomeTag("minecraft:is_overworld")
    .templatePool("pasterdream:deep_ruins_pool")
    .structureClass(DeepRuinsStructure.class)
    .codec(DeepRuinsStructure.CODEC)
    .terrainAdaptation("beard_box")
    .step("underground_structures")
    .size(12)
    .startHeight(-40)
    .build();
```

### 带自定义字段的结构

```java
JsonObject extra = new JsonObject();
extra.addProperty("allow_biome_surface_decoration", true);
extra.addProperty("allow_biome_underground_decoration", true);

RuinResult custom = RuinAPI.createRuin("custom_ruins")
    .biomeTag("pasterdream:special_biomes")
    .templatePool("pasterdream:custom_ruins_pool")
    .structureClass(CustomRuinsStructure.class)
    .codec(CustomRuinsStructure.CODEC)
    .extraFields(extra)
    .build();
```

### 仅手动生成模板池（不通过 Builder）

`TemplatePoolGenerator` 位于 `PasterDreamAPI/src/test/`（测试辅助类），也可直接手写模板池 JSON：

```java
// 直接使用 TemplatePoolGenerator 单独生成模板池 JSON
new TemplatePoolGenerator("pasterdream", "dyedream_ruins_pool")
    .fallback("minecraft:empty")
    .addSingleElement("pasterdream:dyedream_ruins/ruin_1", 3, "rigid", "minecraft:empty")
    .addSingleElement("pasterdream:dyedream_ruins/ruin_2", 2, "rigid", "minecraft:empty")
    .addSingleElement("pasterdream:dyedream_ruins/ruin_3", 1, "rigid", "minecraft:empty")
    .saveToFile("src/main/resources");
```

## 大型结构（LargeStructure）支持

通过 `largeStructure()` 或快捷方法 `withTerrainPlatform()` 将遗迹标记为大型结构，维度生成区块时地形协商器会自动平整地形，避免结构悬空或断层。

```java
// 方式一：完整地形需求配置
TerrainRequirements reqs = TerrainRequirements.builder()
    .requiredFlatRadius(24)          // 需要的平地半径（格）
    .terrainBlendRadius(12)          // 地形渐变半径
    .build();

RuinResult arena = RuinAPI.createRuin("mega_arena")
    .biomeTag("pasterdream:is_dyedream")
    .templatePool("pasterdream:mega_arena_pool")
    .structureClass(MegaArenaStructure.class)
    .codec(MegaArenaStructure.CODEC)
    .largeStructure(reqs)            // ★ 标记为大型结构
    .build();

// 方式二：快捷方法（平地平台）
RuinResult tower = RuinAPI.createRuin("wind_tower")
    .biomeTag("pasterdream:is_wind_journey")
    .templatePool("pasterdream:wind_tower_pool")
    .structureClass(WindTowerStructure.class)
    .codec(WindTowerStructure.CODEC)
    .withTerrainPlatform(16)         // 16 格平地平台，blend 半径自动 = max(5, 16/3)
    .build();

// 大型结构 + 维度联动（在 PDDimensions 中）
DimensionAPI.enableLargeStructureSupport(dyedreamWorld);   // 该维度生成时自动协商地形
```

> `TerrainRequirements` 来自 `api/dimension/terrain/` 包，Builder 模式配置。

### 结构诊断与地形评估

```java
// 打印所有结构的生成诊断报告（成功/失败率、失败原因）
RuinAPI.printStructureDiagnostics();

// 打印指定结构诊断
RuinAPI.printStructureDiagnostics("mega_arena");

// 评估某区块地形是否适合放置结构
TerrainAssessment assessment = RuinAPI.assessTerrain("mega_arena", chunkX, chunkZ, level);

// 报告放置结果（通常由结构生成逻辑调用）
RuinAPI.reportPlacement("mega_arena", true, "");

// 获取放置统计记录
StructurePlacementRecord record = RuinAPI.getPlacementRecord("mega_arena");
```

## 生成的 JSON 文件

```
PasterDream/src/main/resources/
└── data/pasterdream/
    └── worldgen/
        ├── structure/{name}.json              ← 结构定义（RuinBuilder 生成）
        ├── structure_set/{setName}.json       ← 结构集配置（StructureSetBuilder 生成）
        └── template_pool/{name}_pool.json     ← 模板池骨架（RuinBuilder 生成，elements 为空数组，需自行补充元素）
```

> ⚠️ **注意**：`RuinBuilder.build()` 生成 structure JSON + template_pool JSON（**elements 为空数组**，池元素需开发者自行补充）；
> `StructureSetBuilder.build()` 生成 structure_set JSON，并返回带 `setKey` 的新 `RuinResult`（原结果不变）。

### structure/{name}.json 格式

```json
{
  "type": "pasterdream:dyedream_ruins",
  "biomes": "#pasterdream:is_dyedream",
  "step": "surface_structures",
  "terrain_adaptation": "beard_thin",
  "spawn_overrides": {},
  "start_pool": "pasterdream:dyedream_ruins_pool",
  "size": 7,
  "start_height": {
    "type": "minecraft:absolute",
    "height": 0
  },
  "project_start_to_heightmap": "WORLD_SURFACE_WG",
  "max_distance_from_center": 80
}
```

### structure_set/{setName}.json 格式

```json
{
  "structures": {
    "pasterdream:dyedream_ruins": 1
  },
  "placement": {
    "type": "minecraft:random_spread",
    "spacing": 32,
    "separation": 8,
    "salt": 12345
  }
}
```

### template_pool/{name}_pool.json 格式

```json
{
  "name": "pasterdream:dyedream_ruins_pool",
  "fallback": "minecraft:empty",
  "elements": [
    {
      "weight": 3,
      "element": {
        "element_type": "minecraft:single_pool_element",
        "projection": "rigid",
        "location": "pasterdream:dyedream_ruins/ruin_1",
        "processors": "minecraft:empty"
      }
    }
  ]
}
```

## 结构 NBT 文件放置位置

```
data/{modId}/structure/{name}/{nbt_file}.nbt    ← 结构 NBT 文件
```

例如：
```
data/pasterdream/structure/dyedream_ruins/ruin_1.nbt
data/pasterdream/structure/dyedream_ruins/ruin_2.nbt
```

## 注意事项

1. **codec 是必需参数**：RuinBuilder 在 `build()` 时要求 codec 不能为 null，否则抛出异常
2. **结构类构造**：Structure 子类需要有 `(StructureSettings)` 构造方法，因为使用的是 `simpleCodec` 工厂
3. **createRuinSet 前置条件**：必须先通过 `createRuin().build()` 注册结构，再调用 `createRuinSet()`，否则会抛出异常
4. **RuinResult 不可变**：`withSetKey()` 返回新的 RuinResult 实例，原实例不变
5. **JSON 自动生成**：`generateJson` 默认为 true，会在 build 时自动覆盖已有 JSON 文件

## 引用文件

- [RuinAPI.java](file:///C:/Users/97128/Documents/GitHub/NeoPasterDream1/PasterDreamAPI/src/main/java/com/pasterdream/pasterdreammod/api/ruin/RuinAPI.java) — 门面类
- [RuinBuilder.java](file:///C:/Users/97128/Documents/GitHub/NeoPasterDream1/PasterDreamAPI/src/main/java/com/pasterdream/pasterdreammod/api/ruin/builder/RuinBuilder.java) — 结构构建器
- [StructureSetBuilder.java](file:///C:/Users/97128/Documents/GitHub/NeoPasterDream1/PasterDreamAPI/src/main/java/com/pasterdream/pasterdreammod/api/ruin/builder/StructureSetBuilder.java) — 结构集构建器
- [RuinResult.java](file:///C:/Users/97128/Documents/GitHub/NeoPasterDream1/PasterDreamAPI/src/main/java/com/pasterdream/pasterdreammod/api/ruin/RuinResult.java) — 结果类
- [TemplatePoolGenerator.java](file:///C:/Users/97128/Documents/GitHub/NeoPasterDream1/PasterDreamAPI/src/test/java/com/pasterdream/pasterdreammod/api/ruin/gen/TemplatePoolGenerator.java) — template_pool JSON 生成器（test 目录测试辅助）
- [StructureTerrainNegotiator.java](file:///C:/Users/97128/Documents/GitHub/NeoPasterDream1/PasterDreamAPI/src/main/java/com/pasterdream/pasterdreammod/api/dimension/terrain/StructureTerrainNegotiator.java) — 地形协商器
- [TerrainRequirements.java](file:///C:/Users/97128/Documents/GitHub/NeoPasterDream1/PasterDreamAPI/src/main/java/com/pasterdream/pasterdreammod/api/dimension/terrain/TerrainRequirements.java) — 地形需求配置
- [PDRuinsRegistration.java](file:///C:/Users/97128/Documents/GitHub/NeoPasterDream1/PasterDream/src/main/java/com/pasterdream/pasterdreammod/registry/PDRuinsRegistration.java) — 结构注册示例