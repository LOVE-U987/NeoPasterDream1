---
name: "pasterdream-dimension-api"
description: "PasterDream模组维度注册专用API，提供Facade+Builder模式一键注册自定义维度。在需要创建新维度、配置维度类型/生物群系/背景音乐/大型结构地形协商或生成维度JSON时调用。"
---

# PasterDream Dimension API

本 Skill 提供 PasterDream 模组维度注册专用 API 的使用指南，采用 **Facade + Builder** 模式（与 BlockAPI 风格一致），通过链式调用即可完成完整维度的配置、注册和资源文件生成。

## 适用场景

- 创建新的自定义维度（dimension）
- 配置维度类型参数（dimension_type JSON）
- 配置生物群系源（fixed / multi_noise）
- 为维度添加背景音乐
- 启用维度的大型结构地形协商（StructureTerrainNegotiator）
- 自动生成 dimension_type JSON 和 dimension JSON 文件

## 快速开始

```java
// 1. 在 PDDimensions.java 中创建维度
DimensionResult myWorld = DimensionAPI.createDimension("my_world")
    .natural()
    .hasSkylight()
    .bedWorks()
    .withAmbientLight(0.5)
    .minY(-64).height(384)
    .monsterSpawnLight(0, 7)
    .withDefaultBlock("minecraft:stone")
    .withDefaultFluid("minecraft:water")
    .build();

// 2. 在 ClientSetup.java 中注册维度特效
@SubscribeEvent
public static void registerEffects(RegisterDimensionSpecialEffectsEvent event) {
    event.register(
        ResourceLocation.fromNamespaceAndPath("pasterdream", "my_world"),
        new DimensionSpecialEffects(192.0f, true, SkyType.NORMAL, false, false) {
            @Override
            public Vec3 getBrightnessDependentFogColor(Vec3 color, float sunHeight) {
                return color.multiply(0.94, 0.94, 0.91);
            }
            @Override
            public boolean isFoggyAt(int x, int y) { return false; }
        });
}

// 3. 判断当前维度
if (DimensionAPI.isInDimension(level, myWorld)) {
    // 在自定义维度中...
}
```

## 前置条件

在 `PasterDreamMod` 构造函数中通过统一入口注册（DimensionAPI 无需单独注册，已含在 `registerAll` 中）：

```java
PasterDreamAPI.registerAll(modEventBus);
```

## API 架构

```
DimensionAPI                        ← Facade 门面
  ├── createDimension(name)         ← 工厂方法 → DimensionBuilder
  ├── isInDimension(level, result)  ← 维度判断
  ├── getRegisteredDimension(name)  ← 查询维度结果 → Optional
  ├── getMusicEvent(name)           ← 音乐事件查询 → Optional<Supplier<SoundEvent>>
  ├── getTerrainNegotiator()        ← 获取大型结构地形协商器单例
  ├── enableLargeStructureSupport(result/dimensionId)  ← 启用大型结构支持
  └── cacheDimension(result)        ← 缓存维度结果（Builder 内部调用）

DimensionBuilder                    ← Builder 构建器
  ├── DimensionType 配置（natural/ultraWarm/hasSkylight/...）
  ├── Dimension 配置（withDefaultBlock/addBiome/withFixedBiome/...）
  ├── withMusic()                   ← 背景音乐（@Deprecated，建议 PDSounds 管理）
  ├── generateJson(boolean)         ← 是否自动生成 JSON（默认 true）
  ├── basePath(String)              ← 资源文件基础路径（默认 src/main/resources）
  └── build()                       ← 生成 JSON + 返回 DimensionResult

DimensionResult                     ← Record 结果
  ├── dimensionName()               → String（维度注册名）
  ├── dimensionTypeId()             → String（如 "pasterdream:my_world"）
  ├── typeKey()                     → ResourceKey<DimensionType>
  ├── levelKey()                    → ResourceKey<Level>
  ├── effectsId()                   → String（DimensionSpecialEffects 注册 ID）
  └── isDimension(level)            → boolean
```

> ⚠️ 注意：`DimensionAPI.registerEffects()` 和 `generateDimensionTypeJson()/generateDimensionJson()` **已不存在**。特效注册直接在事件中调用 `event.register(...)`；JSON 生成由 `DimensionBuilder.build()` 内部自动完成。

## Builder 配置参考

### DimensionType 参数

| 方法 | 参数 | 说明 | 默认值 |
|------|------|------|:------:|
| `natural()` / `natural(boolean)` | — / bool | 是否为自然维度（天气/床爆炸） | true |
| `hasSkylight()` / `hasSkylight(boolean)` | — / bool | 是否有天空光照 | true |
| `bedWorks()` / `bedWorks(boolean)` | — / bool | 床能否使用/爆炸 | true |
| `ultraWarm()` / `ultraWarm(boolean)` | — / bool | 是否超热（水蒸发、可燃） | false |
| `piglinSafe()` / `piglinSafe(boolean)` | — / bool | 猪灵是否安全 | false |
| `respawnAnchorWorks()` / `respawnAnchorWorks(boolean)` | — / bool | 重生锚能否使用 | false |
| `hasCeiling()` / `hasCeiling(boolean)` | — / bool | 是否有基岩天花板 | false |
| `hasRaids()` / `hasRaids(boolean)` | — / bool | 是否有袭击事件 | true |
| `coordinateScale(double)` | 缩放倍数 | 坐标缩放（下界为 8.0） | 1.0 |
| `withAmbientLight(double)` | 0.0~1.0 | 环境光照强度 | 0.5 |
| `logicalHeight(int)` | 高度值 | 逻辑构建高度 | 384 |
| `infiniburn(String)` | 标签 ID | 无限燃烧标签（如 `#minecraft:infiniburn_overworld`） | `#minecraft:infiniburn_overworld` |
| `minY(int)` | Y 坐标 | 世界最小 Y | -64 |
| `height(int)` | 高度值 | 世界总高度 | 384 |
| `monsterSpawnLight(int, int)` | 最小, 最大 | 怪物生成光照均匀分布范围 | 0, 7 |
| `monsterSpawnBlockLightLimit(int)` | 光照值 | 方块光照限制 | 0 |

### Dimension 参数

| 方法 | 说明 | 默认值 |
|------|------|:------:|
| `withDimensionType(String)` | 手动指定维度类型引用 ID | 自动 `{modId}:{dimensionName}` |
| `withDefaultBlock(String)` | 默认方块（如 `minecraft:calcite`） | `minecraft:stone` |
| `withDefaultFluid(String)` | 默认流体（如 `minecraft:water`） | `minecraft:water` |
| `seaLevel(int)` | 海平面高度 | 63 |
| `disableMobGeneration(boolean)` | 是否禁用怪物生成 | false |
| `aquifersEnabled(boolean)` | 是否启用含水层 | true |
| `oreVeinsEnabled(boolean)` | 是否启用矿脉 | false |
| `legacyRandomSource(boolean)` | 是否使用旧版随机源 | false |
| `withNoiseSettings(String)` | 噪声设置 ID（如 `minecraft:overworld`） | null（不写入） |
| `withFixedBiome(String)` | 固定单一生物群系 | 无 |
| `addBiome(id, temp[], humid[], cont[], weird[], eros[])` | 添加多噪声生物群系（5 个双值范围数组） | 首次调用自动切 multi_noise |

> `addBiome` 参数顺序：`(biomeId, temperature, humidity, continentalness, weirdness, erosion)`，每个都是 `[min, max]` 双值数组；首次调用会把 biomeSourceType 切换为 `minecraft:multi_noise`。

### 背景音乐（@Deprecated ⚠️）

| 方法 | 说明 |
|------|------|
| `withMusic(String)` | 注册背景音乐（默认音量 1.0），自动注册 SoundEvent + 生成 sounds.json |
| `withMusic(String, float)` | 注册背景音乐并自定义音量（钳制 0~1） |

> ⚠️ **已标记 @Deprecated**：仅用于开发阶段快速原型验证，建议在主模组声音注册类（`PDSounds`）中统一管理。

### Builder 通用配置

| 方法 | 说明 | 默认值 |
|------|------|:------:|
| `generateJson(boolean)` | 是否自动生成 JSON 文件 | true |
| `basePath(String)` | 资源文件基础路径 | `src/main/resources` |

## 完整示例

### 主世界风格维度

```java
DimensionResult overworldLike = DimensionAPI.createDimension("my_overworld")
    .natural()
    .hasSkylight()
    .bedWorks()
    .hasRaids()
    .withAmbientLight(0.5)
    .minY(-64).height(384)
    .monsterSpawnLight(0, 7)
    .withDefaultBlock("minecraft:stone")
    .withDefaultFluid("minecraft:water")
    .seaLevel(63)
    .withNoiseSettings("minecraft:overworld")
    .build();
```

### 下界风格维度

```java
DimensionResult netherLike = DimensionAPI.createDimension("my_nether")
    .ultraWarm(true)
    .natural(false)
    .piglinSafe(true)
    .respawnAnchorWorks(true)
    .bedWorks(false)
    .hasSkylight(false)
    .hasCeiling(true)
    .coordinateScale(8.0)
    .withAmbientLight(0.1)
    .logicalHeight(128)
    .infiniburn("#minecraft:infiniburn_nether")
    .minY(0).height(256)
    .monsterSpawnLight(7, 15)
    .withDefaultBlock("minecraft:netherrack")
    .withDefaultFluid("minecraft:lava")
    .withNoiseSettings("minecraft:nether")
    .build();
```

### 固定生物群系维度

```java
DimensionResult desertWorld = DimensionAPI.createDimension("desert_world")
    .natural().hasSkylight().bedWorks()
    .withAmbientLight(0.5)
    .minY(0).height(256)
    .monsterSpawnLight(0, 7)
    .withDefaultBlock("minecraft:sandstone")
    .withDefaultFluid("minecraft:water")
    .withFixedBiome("minecraft:desert")
    .build();
```

### 多噪声生物群系维度

```java
DimensionResult customBiomes = DimensionAPI.createDimension("custom_biomes")
    .natural().hasSkylight().bedWorks()
    .withAmbientLight(0.5)
    .minY(-64).height(384)
    .monsterSpawnLight(0, 7)
    .withDefaultBlock("minecraft:stone")
    .withDefaultFluid("minecraft:water")
    .withNoiseSettings("pasterdream:custom_noise")
    .addBiome("minecraft:plains",
        new double[]{-0.5, 0.5},    // temperature [min, max]
        new double[]{-0.5, 0.5},    // humidity
        new double[]{-0.5, 0.5},    // continentalness
        new double[]{-0.5, 0.5},    // weirdness
        new double[]{-0.5, 0.5})    // erosion
    .addBiome("pasterdream:custom_biome",
        new double[]{0.1, 0.8}, new double[]{-0.3, 0.2},
        new double[]{0.3, 0.9}, new double[]{-0.7, 0.3},
        new double[]{0.1, 0.6})
    .build();
```

## 大型结构地形协商（StructureTerrainNegotiator）

DimensionAPI 集成了大型结构地形协商系统：当维度生成区块时，如果附近有已注册的大型结构（通过 RuinAPI 的 `largeStructure()` 注册），会自动调整地形以适应结构。

```java
// 启用指定维度的地形协商支持（需在维度 build() 后调用）
DimensionAPI.enableLargeStructureSupport(dyedreamWorld);

// 或按维度 ID 启用
DimensionAPI.enableLargeStructureSupport("pasterdream:dyedream_world");

// 获取协商器单例（可注册/诊断/评估）
StructureTerrainNegotiator negotiator = DimensionAPI.getTerrainNegotiator();
```

> 大型结构的定义与注册见 Skill「pasterdream-ruin-api」的 `RuinBuilder.largeStructure()` / `withTerrainPlatform()`。

## 生成的 JSON 文件位置

```
PasterDream/src/main/resources/
├── data/pasterdream/dimension_type/{name}.json    ← 维度类型配置
├── data/pasterdream/dimension/{name}.json         ← 维度实例配置
└── assets/pasterdream/sounds.json                 ← 声音配置（withMusic 时自动追加）
```

> ⚠️ **1.21 维度 JSON 格式要求**：
> - 维度 JSON 必须包含 `noise_router` 和 `surface_rule` 字段，否则游戏崩溃
> - 虚空维度推荐参考 `data/pasterdream/dimension/aaroncos_arena_world.json`
> - 文件必须放在 `data/<modid>/dimension/`，不是 `assets/`

### .ogg 音频文件放置位置

```
assets/pasterdream/sounds/music/{musicName}.ogg    ← 背景音乐文件（withMusic 时）
```

## 客户端特效注册

在 `ClientSetup.java` 中注册自定义天空和雾气效果：

```java
@SubscribeEvent
public static void registerDimensionSpecialEffects(RegisterDimensionSpecialEffectsEvent event) {
    event.register(
        ResourceLocation.fromNamespaceAndPath("pasterdream", "my_world"),
        new DimensionSpecialEffects(192.0f, true, SkyType.NORMAL, false, false) {
            @Override
            public Vec3 getBrightnessDependentFogColor(Vec3 fogColor, float sunHeight) {
                return fogColor.multiply(
                    sunHeight * 0.94 + 0.06,
                    sunHeight * 0.94 + 0.06,
                    sunHeight * 0.91 + 0.09
                );
            }
            @Override
            public boolean isFoggyAt(int x, int y) {
                return false;
            }
        }
    );
}
```

> 特效注册 ID 必须与 `DimensionResult.effectsId()`（即 `{modId}:{dimensionName}`）一致。

## 引用文件

- [DimensionAPI.java](file:///C:/Users/97128/Documents/GitHub/NeoPasterDream1/PasterDreamAPI/src/main/java/com/pasterdream/pasterdreammod/api/dimension/DimensionAPI.java) — 门面类
- [DimensionBuilder.java](file:///C:/Users/97128/Documents/GitHub/NeoPasterDream1/PasterDreamAPI/src/main/java/com/pasterdream/pasterdreammod/api/dimension/builder/DimensionBuilder.java) — 构建器
- [DimensionResult.java](file:///C:/Users/97128/Documents/GitHub/NeoPasterDream1/PasterDreamAPI/src/main/java/com/pasterdream/pasterdreammod/api/dimension/DimensionResult.java) — 结果类
- [StructureTerrainNegotiator.java](file:///C:/Users/97128/Documents/GitHub/NeoPasterDream1/PasterDreamAPI/src/main/java/com/pasterdream/pasterdreammod/api/dimension/terrain/StructureTerrainNegotiator.java) — 大型结构地形协商器
- [TerrainRequirements.java](file:///C:/Users/97128/Documents/GitHub/NeoPasterDream1/PasterDreamAPI/src/main/java/com/pasterdream/pasterdreammod/api/dimension/terrain/TerrainRequirements.java) — 地形需求配置
- [PDDimensions.java](file:///C:/Users/97128/Documents/GitHub/NeoPasterDream1/PasterDream/src/main/java/com/pasterdream/pasterdreammod/registry/PDDimensions.java) — 维度注册示例
- [PDSounds.java](file:///C:/Users/97128/Documents/GitHub/NeoPasterDream1/PasterDream/src/main/java/com/pasterdream/pasterdreammod/registry/PDSounds.java) — 声音注册类
