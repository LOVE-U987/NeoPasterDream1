---
name: "pasterdream-dimension-api"
description: "PasterDream模组维度注册专用API，提供Facade+Builder模式一键注册自定义维度。在需要创建新维度、配置维度类型/生物群系/背景音乐或生成维度JSON时调用。"
---

# PasterDream Dimension API

本 Skill 提供 PasterDream 模组维度注册专用 API 的使用指南，采用 **Facade + Builder** 模式（与 BlockAPI 风格一致），通过链式调用即可完成完整维度的配置、注册和资源文件生成。

## 适用场景

- 创建新的自定义维度（dimension）
- 配置维度类型参数（dimension_type JSON）
- 配置生物群系源（fixed / multi_noise / checkerboard）
- 为维度添加背景音乐
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
    .withMusic("my_world_music")
    .build();

// 2. 在 ClientSetup.java 中注册维度特效
@SubscribeEvent
public static void registerEffects(RegisterDimensionSpecialEffectsEvent event) {
    DimensionAPI.registerEffects(event, "my_world",
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

## API 架构

```
DimensionAPI                        ← Facade 门面
  ├── createDimension(name)         ← 工厂方法 → DimensionBuilder
  ├── registerEffects()             ← 特效注册
  ├── registerDimensionMusic()      ← 音乐注册
  ├── getMusicEvent()               ← 音乐查询
  ├── isInDimension()               ← 维度判断
  └── generateDimensionTypeJson()   ← 手动 JSON 生成
  └── generateDimensionJson()       ← 手动 JSON 生成

DimensionBuilder                    ← Builder 构建器
  ├── DimensionType 配置           ← 桥接到 DimensionTypeGenerator
  ├── Dimension 配置               ← 桥接到 DimensionGenerator
  ├── withMusic()                  ← 背景音乐配置
  └── build()                      ← 生成 JSON + 返回 DimensionResult

DimensionResult                     ← Record 结果
  ├── typeKey()                    → ResourceKey<DimensionType>
  ├── levelKey()                   → ResourceKey<Level>
  ├── effectsId()                  → String
  └── isDimension(level)           → boolean
```

## Builder 配置参考

### DimensionType 参数

| 方法 | 参数 | 说明 |
|------|------|------|
| `natural()` / `natural(boolean)` | — / bool | 是否为自然维度（床/重生锚行为） |
| `hasSkylight()` / `hasSkylight(boolean)` | — / bool | 是否有天空光照 |
| `bedWorks()` / `bedWorks(boolean)` | — / bool | 床能否使用/爆炸 |
| `ultraWarm()` / `ultraWarm(boolean)` | — / bool | 是否超热（水蒸发、可燃） |
| `piglinSafe()` / `piglinSafe(boolean)` | — / bool | 猪灵是否安全 |
| `respawnAnchorWorks()` / `respawnAnchorWorks(boolean)` | — / bool | 重生锚能否使用 |
| `hasCeiling()` / `hasCeiling(boolean)` | — / bool | 是否有基岩天花板 |
| `hasRaids()` / `hasRaids(boolean)` | — / bool | 是否有袭击事件 |
| `coordinateScale(double)` | 缩放倍数 | 坐标缩放（下界为 8.0） |
| `withAmbientLight(double)` | 0.0~1.0 | 环境光照强度 |
| `logicalHeight(int)` | 高度值 | 逻辑构建高度 |
| `infiniburn(String)` | 标签 ID | 无限燃烧标签（如 `#minecraft:infiniburn_overworld`） |
| `minY(int)` | Y 坐标 | 世界最小 Y |
| `height(int)` | 高度值 | 世界总高度 |
| `monsterSpawnLight(int, int)` | 最小, 最大 | 怪物生成光照均匀分布范围 |
| `monsterSpawnBlockLightLimit(int)` | 光照值 | 方块光照限制 |

### Dimension 参数

| 方法 | 说明 |
|------|------|
| `withDefaultBlock(String)` | 默认方块（如 `minecraft:calcite`） |
| `withDefaultFluid(String)` | 默认流体（如 `minecraft:water`） |
| `seaLevel(int)` | 海平面高度 |
| `disableMobGeneration(boolean)` | 是否禁用怪物生成 |
| `aquifersEnabled(boolean)` | 是否启用含水层 |
| `oreVeinsEnabled(boolean)` | 是否启用矿脉 |
| `legacyRandomSource(boolean)` | 是否使用旧版随机源 |
| `withNoiseSettings(String)` | 噪声设置 ID（如 `minecraft:overworld`） |
| `withFixedBiome(String)` | 固定单一生物群系 |
| `addBiome(biomeId, temp, humid, cont, weird, eros)` | 添加多噪声生物群系（5 个双值范围数组） |

### 背景音乐

| 方法 | 说明 |
|------|------|
| `withMusic(String)` | 注册背景音乐（自动注册 SoundEvent + 生成 sounds.json） |

### Builder 通用配置

| 方法 | 说明 |
|------|------|
| `generateJson(boolean)` | 是否自动生成 JSON 文件（默认 true） |
| `basePath(String)` | 资源文件基础路径（默认 `src/main/resources`） |

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
    .withMusic("my_overworld")
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
        new double[]{-0.5, 0.5},    // erosion
        new double[]{-0.5, 0.5})    // weirdness
    .addBiome("pasterdream:custom_biome",
        new double[]{0.1, 0.8}, new double[]{-0.3, 0.2},
        new double[]{0.3, 0.9}, new double[]{0.1, 0.6},
        new double[]{-0.7, 0.3})
    .withMusic("custom_biomes")
    .build();
```

## Minecraft 参考：`/minecraft` 指令风格

### 生成测试 JSON 文件

```bash
.\gradlew generateDimensionTestJsons
```

### 运行 API 示例程序

```bash
.\gradlew runDimensionApiDemo
```

### 生成的 JSON 文件位置

```
src/main/resources/
├── data/{modId}/dimension_type/{name}.json    ← 维度类型配置
├── data/{modId}/dimension/{name}.json         ← 维度实例配置
└── assets/{modId}/sounds.json                 ← 声音配置（自动追加音乐条目）
```

### .ogg 音频文件放置位置

```
assets/{modId}/sounds/music/{musicName}.ogg    ← 背景音乐文件
```

## 客户端特效注册

在 `ClientSetup.java` 中注册自定义天空和雾气效果：

```java
@SubscribeEvent
public static void registerDimensionSpecialEffects(RegisterDimensionSpecialEffectsEvent event) {
    DimensionAPI.registerEffects(event, "my_world",
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

## 引用文件

- [DimensionAPI.java](file:///c:/Users/97128/Documents/GitHub/NeoPasterDream1/src/main/java/com/pasterdream/pasterdreammod/api/dimension/DimensionAPI.java) — 门面类
- [DimensionBuilder.java](file:///c:/Users/97128/Documents/GitHub/NeoPasterDream1/src/main/java/com/pasterdream/pasterdreammod/api/dimension/builder/DimensionBuilder.java) — 构建器
- [DimensionResult.java](file:///c:/Users/97128/Documents/GitHub/NeoPasterDream1/src/main/java/com/pasterdream/pasterdreammod/api/dimension/DimensionResult.java) — 结果类
- [DimensionTypeGenerator.java](file:///c:/Users/97128/Documents/GitHub/NeoPasterDream1/src/main/java/com/pasterdream/pasterdreammod/api/dimension/gen/DimensionTypeGenerator.java) — dimension_type JSON 生成器
- [DimensionGenerator.java](file:///c:/Users/97128/Documents/GitHub/NeoPasterDream1/src/main/java/com/pasterdream/pasterdreammod/api/dimension/gen/DimensionGenerator.java) — dimension JSON 生成器
- [SoundsJsonGenerator.java](file:///c:/Users/97128/Documents/GitHub/NeoPasterDream1/src/main/java/com/pasterdream/pasterdreammod/api/dimension/gen/SoundsJsonGenerator.java) — sounds.json 生成器
- [PDSounds.java](file:///c:/Users/97128/Documents/GitHub/NeoPasterDream1/src/main/java/com/pasterdream/pasterdreammod/registry/PDSounds.java) — 声音注册类
- [PDDimensions.java](file:///c:/Users/97128/Documents/GitHub/NeoPasterDream1/src/main/java/com/pasterdream/pasterdreammod/registry/PDDimensions.java) — 维度注册示例
- [DimensionApiDemo.java](file:///c:/Users/97128/Documents/GitHub/NeoPasterDream1/src/main/java/com/pasterdream/pasterdreammod/api/dimension/example/DimensionApiDemo.java) — 完整示例
- [build.gradle](file:///c:/Users/97128/Documents/GitHub/NeoPasterDream1/build.gradle) — Gradle 任务定义