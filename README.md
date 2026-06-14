# PasterDream — NeoForge 1.21.1 模组

> **精神续作，而非代码移植** — 原模组为 MCreator 生成的 FixPasterDream（1.20.1 Forge），本项目在 1.21.1 NeoForge 下完全重写。

## 项目概览

PasterDream 是一个 Minecraft 模组，基于 **NeoForge 1.21.1** 框架开发。它创造了一个名为 **「染梦」**（Dyedream）的全新维度，包含了丰富的生物群系、怪物、植物、矿物、工具、饰品和独特的梦境主题游戏机制。

| 属性 | 值 |
|------|-----|
| **Minecraft 版本** | 1.21.1 |
| **NeoForge 版本** | 21.1.219 |
| **GeckoLib 版本** | 4.8.4 |
| **Java 版本** | 21 |
| **项目版本** | 0.0.3.3 |
| **许可证** | MIT |

### 核心功能

- **染梦维度** — 一个梦幻主题的次元，拥有独特的生物群系、地形和天空渲染
- **数十种新生物** — 包括暗影魔像、恐怖喙、雷云、风骑士等敌对生物，以及萤火虫、金狐等中立/被动生物
- **丰富的物品系统** — 包括工具、武器、饰品（支持 Curios API）、食物、唱片等 100+ 物品
- **自定义方块** — 云块、染梦系列方块、钙华变体系列、装饰植物等
- **饰品系统** — 通过 Curios API 支持戒指、项链、腰带、护符、斗篷等多槽位饰品
- **世界生成** — 自定义矿脉、植被、遗迹结构（列车、小屋、蘑菇屋等）
- **粒子效果** — 多种自定义粒子营造沉浸式环境氛围
- **音乐系统** — 自定义唱片、生物群系背景音乐、渐变切换

---

## 项目结构总览

```
NeoPasterDream1/
├── PasterDreamAPI/          # API 模块 — 架构抽象层
├── PasterDream/             # 主模块 — 业务实现层
├── libs/                    # 外部依赖库（只读）
├── .trae/                   # AI 辅助开发配置
├── .github/                 # CI/CD 配置
├── settings.gradle          # Gradle 多模块配置
├── gradle.properties        # 全局构建属性
├── CHANGELOG.md             # 变更日志
└── LICENSE                  # MIT 许可证
```

---

## 模块详解

### 1. PasterDreamAPI（API 模块）

**定位**：架构抽象层，提供所有注册体系的 Facade + Builder 模式 API。

```
PasterDreamAPI/src/main/java/com/pasterdream/pasterdreammod/api/
├── block/                   # 方块注册 API
│   ├── BlockAPI.java              # Facade 入口
│   ├── BlockConfig.java           # 方块配置（挖掘等级/工具类型等）
│   ├── SelfDropBlock.java         # 自掉落方块基类
│   ├── builder/
│   │   ├── SimpleBlockBuilder.java    # 单一方块构建器
│   │   ├── BatchBlockBuilder.java     # 批量方块构建器
│   │   └── VariantSetBuilder.java     # 变体系列构建器（楼梯/台阶/墙等）
│   ├── loot/
│   │   └── BlockLootAPI.java         # 战利品表生成 API
│   └── example/
│       └── BlockApiDemo.java         # 使用示例
├── compat/                  # 兼容层
│   ├── CompatLayer.java            # 跨版本兼容抽象
│   └── package-info.java
├── dimension/               # 维度注册 API
│   ├── DimensionAPI.java           # Facade 入口
│   ├── DimensionResult.java        # 注册结果封装
│   ├── builder/
│   │   └── DimensionBuilder.java   # 维度构建器
│   ├── gen/
│   │   ├── DimensionGenerator.java      # 维度 JSON 生成
│   │   ├── DimensionTypeGenerator.java  # 维度类型 JSON 生成
│   │   └── SoundsJsonGenerator.java     # 声音 JSON 生成
│   ├── terrain/
│   │   ├── TerrainAdjuster.java             # 地形调节器
│   │   ├── TerrainAssessment.java           # 地形评估
│   │   ├── TerrainRequirements.java         # 地形需求
│   │   ├── StructurePlacementRecord.java    # 结构放置记录
│   │   └── StructureTerrainNegotiator.java  # 结构-地形协调
│   └── example/
│       ├── DimensionApiDemo.java           # API 演示
│       └── DimensionJsonGenerator.java     # JSON 生成演示
├── effect/                  # 药水效果注册 API
│   ├── MobEffectAPI.java           # Facade 入口
│   ├── MobEffectResult.java        # 注册结果
│   ├── base/
│   │   └── PasterDreamEffect.java  # 基类
│   └── builder/
│       └── MobEffectBuilder.java   # 构建器
├── entity/                  # 实体注册 API
│   ├── EntityAPI.java              # Facade 入口
│   ├── EntityResult.java           # 注册结果
│   ├── builder/
│   │   └── EntityBuilder.java      # 实体构建器
│   ├── gen/
│   │   └── EntityAttributesGenerator.java  # 属性生成器
│   └── skill/
│       ├── EntitySkill.java              # 技能接口
│       ├── EntitySkillBuilder.java       # 技能构建器
│       └── EntitySkillManager.java       # 技能管理器
├── itemmigration/           # 物品迁移 API（旧模组→新注册）
│   ├── ItemMigrationAPI.java        # 主入口
│   ├── builder/
│   │   ├── SimpleItemBuilder.java   # 简单物品
│   │   ├── FoodItemBuilder.java     # 食物
│   │   ├── ToolItemBuilder.java     # 工具
│   │   └── CurioItemBuilder.java    # 饰品
│   ├── gen/
│   │   ├── RecipeGenerator.java     # 配方生成
│   │   ├── LootTableGenerator.java  # 战利品表生成
│   │   ├── LanguageGenerator.java   # 语言文件生成
│   │   ├── BlockDataGenerator.java  # 方块数据生成
│   │   ├── CreativeTabHelper.java   # 创造标签页辅助
│   │   └── ImportHelper.java        # 批量导入辅助
│   ├── manager/
│   │   ├── MigrationManager.java    # 迁移管理器
│   │   └── MigrationReport.java     # 迁移报告
│   └── model/                      # 数据模型
│       ├── ItemSpec.java, FoodSpec.java, ToolSpec.java, ...
│       └── MigrationCategory.java  # 迁移分类枚举
├── particle/                # 粒子注册 API
│   ├── ParticleAPI.java            # Facade 入口
│   ├── ParticleResult.java         # 注册结果
│   ├── builder/
│   │   └── ParticleBuilder.java    # 粒子构建器
│   └── gen/
│       ├── ParticleGenerator.java         # 粒子 JSON 生成
│       └── ParticleTextureGenerator.java  # 粒子纹理生成
├── ruin/                    # 遗迹结构注册 API
│   ├── RuinAPI.java                # Facade 入口
│   ├── RuinResult.java             # 注册结果
│   ├── builder/
│   │   ├── RuinBuilder.java             # 遗迹构建器
│   │   └── StructureSetBuilder.java     # 结构集构建器
│   └── gen/
│       ├── StructureTypeGenerator.java   # 结构类型生成
│       ├── TemplatePoolGenerator.java    # 模板池生成
│       └── StructureSetGenerator.java    # 结构集生成
├── ApiCodeGenConfig.java    # 代码生成配置
├── ApiSoundRegistry.java    # 声音注册
└── PasterDreamAPI.java      # API 模块常量和日志
```

**关键设计模式**：

| 模式 | 示例 |
|------|------|
| **Facade** | `BlockAPI.createBlock(...)`、`EntityAPI.createEntity(...)` 等静态入口方法 |
| **Builder** | `.size(2.2f, 3.5f).category(MONSTER).build()` 链式调用 |
| **Result** | 注册返回 `EntityResult<T>` / `DimensionResult` 等封装对象，便于后续引用 |
| **Strategy** | 装饰物放置器使用 `Map<Type, Handler>` 分发 |

---

### 2. PasterDream（主模块）

**定位**：业务实现层，包含所有具体的方块、物品、实体、渲染、注册代码。

```
PasterDream/src/main/java/com/pasterdream/pasterdreammod/
├── PasterDreamMod.java          # 模组主入口（@Mod 注解）
├── block/                       # 方块类
│   ├── entity/                      # 方块实体（BlockEntity）
│   │   ├── DreamAccumulatorBlockEntity.java
│   │   ├── DreamCauldronBlockEntity.java
│   │   ├── DyedreamDeskBlockEntity.java
│   │   ├── LifeCrystalBlockEntity.java
│   │   ├── MeltdreamChestBlockEntity.java
│   │   ├── MeltdreamChestOpenBlockEntity.java
│   │   ├── ShadowChestBlockEntity.java
│   │   └── TheEndlessBookOfDreamSeekersBlockEntity.java
│   ├── CloudBlock.java             # 云块
│   ├── DarkCloudBlock.java         # 厚云块
│   ├── DyedreamCrackBlock.java     # 染梦裂隙（传送入口）
│   ├── DyedreamLogBlock.java       # 染梦原木
│   ├── DyedreamFlowerBlock.java    # 染梦花
│   ├── MeltdreamLiquidBlock.java   # 融梦涌泉流体块
│   ├── ... +30 个方块类
├── client/                      # 客户端代码
│   ├── ClientSetup.java              # 客户端初始化（渲染器注册、粒子注册、屏幕注册）
│   ├── DyeDreamSkyRenderer.java      # 染梦维度极光天幕渲染器
│   ├── PDClientEvents.java           # 客户端事件（环境粒子生成、暂停检测）
│   ├── PDClientItemExtensions.java   # 物品客户端扩展
│   ├── PDResourceLogger.java         # 资源日志
│   ├── audio/                        # 音乐系统（核心）
│   │   ├── ModMusicManager.java          # 主管理器
│   │   ├── MusicPlaybackController.java  # 播放控制器
│   │   ├── CrossfadeManager.java         # 渐变切换
│   │   ├── BiomeMusicRegistry.java       # 生物群系音乐注册
│   │   └── ... (BgmDeduplication, CooldownManager, FadeState, ...)
│   ├── model/                         # 自定义模型
│   │   ├── DreamAccumulatorDisplayModel.java
│   │   ├── DreamMeterItemModel.java
│   │   └── Modelslime.java
│   ├── particle/                      # 粒子效果
│   │   ├── CalleParticle.java, CrackParticle.java, ...
│   │   ├── DreamAmbientParticle.java, DyedreamParticle.java
│   │   ├── FeatherWhiteParticle.java, SilverParticle.java
│   │   └── ... 共 12 种粒子
│   ├── renderer/
│   │   ├── block/          # 方块实体渲染器（6 个）
│   │   ├── entity/         # 实体渲染器（31 个，含 GeckoLib 模型）
│   │   └── item/           # 物品渲染器（包含 GeckoLib 显示物品）
│   ├── screen/             # GUI 屏幕（DreamCauldron, DyedreamDesk, ShadowChest...）
│   └── util/
│       └── AnimUtils.java          # 动画工具类
├── command/                    # 命令
│   └── PDCommands.java
├── data/                       # 数据生成器
│   ├── PDBlockModelProvider.java
│   └── PDBlockTagProvider.java
├── entity/                     # 实体类
│   ├── mob/                        # 生物实体（29 个）
│   │   ├── ShadowGolemEntity.java      # 暗影魔像
│   │   ├── TerrorbeakEntity.java       # 恐怖喙
│   │   ├── ThundercloudEntity.java     # 雷云
│   │   ├── WindKnightEntity.java       # 风骑士
│   │   ├── FriendlyGhostEntity.java    # 怨魂
│   │   ├── PinkSlimeEntity.java        # 粉红史莱姆
│   │   ├── ... (AshBoneWing, BoneWing, FoxFire, Jellyfish, ...)
│   │   └── SporeEntityEntity.java      # 孢子实体
│   ├── projectile/                   # 弹射物
│   │   └── BoneWingFireBallProjectileEntity.java
│   └── GeckoLibMonsterEntity.java    # 基类（GeckoLib + Monster）
├── fluid/                      # 流体
│   ├── MeltdreamLiquidFluid.java         # 融梦涌泉流体
│   └── types/
│       └── MeltdreamLiquidFluidType.java  # 流体类型
├── item/                       # 物品类
│   ├── AbstractGeoDisplayItem.java   # GeckoLib 显示物品基类
│   ├── BlueDewItem.java, BubbleTeaItem.java, ...
│   ├── EmbryoBeltItem.java, CounterRingItem.java, ...  # 饰品
│   ├── DyedreamTeleportCrystal.java  # 染梦传送水晶
│   ├── WindKnightFlagItem.java       # 风骑士旗
│   ├── ... 共 50+ 物品类
├── menu/                       # 容器菜单
│   ├── DreamCauldronMenu.java
│   ├── DyedreamDeskMenu.java
│   ├── MeltdreamChestMenu.java
│   ├── ShadowChestMenu.java
│   └── TheEndlessBookOfDreamSeekersMenu.java
├── mixin/                      # ASM Mixin
│   └── MinecraftMixin.java
├── registry/                   # 注册中心（核心）
│   ├── PDItems.java                # 物品注册（100+ 条目）
│   ├── PDBlocks.java               # 方块注册（60+ 条目）
│   ├── PDEntities.java             # 实体注册（31 个生物 + 弹射物）
│   ├── PDBlockEntities.java        # 方块实体注册
│   ├── PDCreativeTabs.java         # 创造标签页（9 个分类）
│   ├── PDEntityEvents.java         # 实体事件
│   ├── PDFeatures.java             # 自定义地物注册
│   ├── PDPlacedFeatures.java       # 地物放置注册
│   ├── PDDimensions.java           # 维度注册
│   ├── PDFluids.java               # 流体注册
│   ├── PDFluidsType.java           # 流体类型注册
│   ├── PDEffects.java              # 药水效果注册
│   ├── PDPotions.java              # 药水注册
│   ├── PDParticles.java            # 粒子注册
│   ├── PDSounds.java               # 声音注册
│   ├── PDMenus.java                # 菜单注册
│   ├── PDLootTables.java           # 战利品表注册
│   ├── PDStructures.java           # 结构注册
│   ├── PDAdvancements.java         # 进度注册
│   ├── PDRuinsRegistration.java    # 遗迹结构注册（6 个）
│   ├── ModDecorations.java         # 通用装饰物注册
│   ├── IceDecorations.java         # 冰系装饰物
│   └── OceanDecorations.java       # 海洋装饰物
├── test/                       # API 测试
│   ├── EntityApiTestRunner.java
│   ├── ParticleApiTestRunner.java
│   ├── RuinApiTestRunner.java
│   └── RunAllApiTests.java
├── worldgen/                   # 世界生成
│   ├── decor/
│   │   ├── DecorationBuilder.java       # 装饰物构建器
│   │   ├── DecorationConfig.java        # 装饰物配置
│   │   └── DecorationJsonGenerator.java # 装饰物 JSON 生成
│   ├── PDBiomeModifiers.java            # 染色维度群系修饰器
│   ├── PDDyedreamBiomeModifier.java     # 染色维度特殊修饰器
│   └── WorldGenUtils.java               # 世界生成工具
```

#### 注册中心（`registry/`） — 项目的枢纽

注册中心是整个项目的**调度核心**，负责将 Java 代码中定义的物品、方块、实体等内容注册到 Minecraft 的游戏引擎中。

```
PasterDreamMod.java（主入口）
  │
  ├── 调用注册 → PDItems.java        ←→  block/ (方块类)
  ├── 调用注册 → PDBlocks.java       ←→  item/ (物品类)
  ├── 调用注册 → PDEntities.java     ←→  entity/mob/ (实体类)
  ├── 调用注册 → PDBlockEntities.java ←→  block/entity/ (方块实体)
  ├── 调用注册 → PDCreativeTabs.java
  ├── 调用注册 → PDSounds.java       ←→  resources/assets/sounds.json
  ├── 调用注册 → PDParticles.java    ←→  client/particle/
  ├── 调用注册 → PDFluids.java       ←→  fluid/
  ├── 调用注册 → PDStructures.java   ←→  worldgen/
  ├── 调用注册 → PDRuinsRegistration.java
  ├── 调用注册 → ModDecorations.java
  └── 调用 DataGen → PDBlockModelProvider.java / PDBlockTagProvider.java
```

每个注册类内部使用 `DeferredRegister`，这是 NeoForge 推荐的安全注册方式，保证在正确的 Mod 加载阶段完成注册。

#### 资源文件（`resources/`）

```
PasterDream/src/main/resources/
├── assets/pasterdream/          # 客户端资源
│   ├── blockstates/             # 方块状态 JSON（控制不同状态的模型）
│   ├── geo/                     # GeckoLib 3D 模型 JSON（实体动画模型）
│   ├── lang/                    # 语言文件
│   │   ├── zh_cn.json               # 简体中文（完整翻译）
│   │   └── en_us.json               # 英文翻译
│   ├── models/                  # 物品/方块模型 JSON
│   │   ├── block/                   # 方块模型
│   │   ├── item/                    # 物品模型
│   │   └── custom/                  # 自定义模型
│   ├── sounds/                  # 音频文件（OGG 格式）
│   ├── textures/                # 纹理 PNG
│   │   ├── block/                   # 方块纹理
│   │   └── item/                    # 物品纹理
│   └── sounds.json              # 声音定义
│
└── data/                        # 游戏数据
    ├── c/tags/block/            # Common Convention 标签
    ├── minecraft/tags/          # 原版标签
    │   ├── block/                   # 方块标签（crops, flowers, fences...）
    │   └── item/                    # 物品标签（music_discs）
    ├── pasterdream/             # 模组数据
    │   ├── dimension/               # 维度定义
    │   ├── dimension_type/          # 维度类型定义
    │   ├── jukebox_song/            # 唱片歌曲定义（10 个）
    │   ├── loot_table/blocks/       # 方块战利品表（70+ 个方块）
    │   ├── loot_table/entities/     # 实体战利品表
    │   ├── neoforge/biome_modifier/ # 生物群系修饰器（13 个）
    │   └── recipe/                  # 合成配方（150+ 个）
    └── neoforge/tags/block/     # NeoForge 标签
```

---

### 3. libs/（外部依赖）

```
libs/
├── curios-neoforge-9.5.1+1.21.1.jar    # Curios API 饰品系统
├── geckolib-neoforge-1.21.1-4.8.4.jar  # GeckoLib 动画引擎
├── player-animation-lib-forge-2.0.4+1.21.1.jar  # 玩家动画库
├── FixPasterDream-main/                # 原模组（MCreator 生成，只读参考）
│   └── src/main/java/net/pasterdream/  # 原模组 Java 源码
└── Curios-1.21.1/                      # Curios 完整源码（参考）
```

---

### 4. .trae/（AI 辅助开发配置）

```
.trae/
├── rules/
│   └── project_rules.md          # 开发规范（API-Split 策略、命名规则、踩坑记录）
└── skills/                       # 可调用的 AI 技能文档
    ├── api-split-multi-module/   # 多模块架构决策指南
    ├── item-migration-api/       # 物品迁移 API 参考
    ├── pasterdream-dimension-api/    # 维度 API 参考
    ├── pasterdream-entity-api/       # 实体 API 参考
    ├── pasterdream-particle-api/     # 粒子 API 参考
    ├── pasterdream-ruin-api/         # 遗迹 API 参考
    ├── world-decoration-api/         # 世界装饰 API 参考
    ├── pasterdream-mod-dev/          # 模组开发综合指南
    └── neoforge-block-drops/         # 方块掉落诊断指南
```

---

## 框架与技术栈

| 技术 | 用途 | 版本 |
|------|------|------|
| **NeoForge** | Mod 加载框架 | 21.1.219 |
| **GeckoLib** | 3D 实体动画引擎 | 4.8.4 |
| **Curios API** | 饰品槽位系统 | 9.5.1 |
| **Player Animation Lib** | 玩家动画扩展 | 2.0.4 |
| **Parchment** | 官方映射名称 | 2024.11.17 |
| **Java** | 编程语言 | 21 |
| **Gradle** | 构建工具 | 8.x |

---

## 架构设计模式

### 1. API-Split 多模块架构

项目分为 **API 模块** 和 **主模块**，遵循严格的依赖方向：

```
PasterDreamAPI（抽象层）
    ↑ 依赖
PasterDream（实现层）
```

**归属决策规则**：

| 组件类型 | 归属模块 | 示例 |
|---------|---------|------|
| API 接口 / Builder / Facade / Result / Config | PasterDreamAPI | `BlockAPI`、`EntityBuilder` |
| 会被多个模块引用的类 | PasterDreamAPI | `BlockConfig`、`EntityResult` |
| 注册体系（DeferredRegister） | PasterDreamAPI | `EntityAPI.REGISTRY` |
| 方块 / 物品 / 实体 / 渲染 / 客户端代码 | PasterDream | 所有具体实现 |

### 2. Facade + Builder 注册模式

所有注册体系统一遵循：

```java
// 1. 通过 Facade 入口创建
EntityResult<ShadowGolemEntity> result =
    EntityAPI.createEntity("shadow_golem")
// 2. 链式配置
        .category(MobCategory.MONSTER)
        .size(2.2f, 3.5f)
        .attributes(ShadowGolemEntity.createAttributes())
// 3. 构建并注册
        .buildAndRegister();
```

| 系统 | Facade | Builder | 注册方法 |
|------|--------|---------|---------|
| 方块 | `BlockAPI` | `SimpleBlockBuilder` | `.register()` |
| 实体 | `EntityAPI` | `EntityBuilder` | `.buildAndRegister()` |
| 粒子 | `ParticleAPI` | `ParticleBuilder` | `.build()` |
| 维度 | `DimensionAPI` | `DimensionBuilder` | `.register()` |
| 遗迹 | `RuinAPI` | `RuinBuilder` | `.register()` |

### 3. 注册系统全景

```
DeferredRegister（NeoForge 机制）
  ├── ITEMS       → PDItems.java        → 100+ 物品
  ├── BLOCKS      → PDBlocks.java       → 60+ 方块
  ├── ENTITY_TYPES → PDEntities.java    → 31 生物 + 弹射物
  ├── BLOCK_ENTITIES → PDBlockEntities.java
  ├── CREATIVE_MODE_TABS → PDCreativeTabs.java  → 9 标签页
  ├── SOUND_EVENTS → PDSounds.java
  ├── PARTICLE_TYPES → PDParticles.java
  ├── MENU_TYPES  → PDMenus.java
  ├── FLUIDS      → PDFluids.java
  ├── FLUID_TYPES → PDFluidsType.java
  ├── MOB_EFFECTS → PDEffects.java
  ├── POTIONS     → PDPotions.java
  ├── FEATURES    → PDFeatures.java
  ├── PLACED_FEATURES → PDPlacedFeatures.java
  ├── STRUCTURES  → PDStructures.java
  └── LOOT_TABLES → PDLootTables.java
```

### 4. 客户端-服务端分离

```
服务端（Server）
├── 所有 registry/ *
├── block/ *
├── item/ *
├── entity/ *
├── fluid/ *
├── menu/ *
├── command/
└── worldgen/

客户端（Client Only）
├── client/ *
│   ├── renderer/
│   ├── particle/
│   ├── screen/
│   ├── audio/
│   └── DyeDreamSkyRenderer.java
├── 资源文件（assets/）

通过 @EventBusSubscriber(value = Dist.CLIENT) 确保服务端不加载客户端类
```

---

## 9 个创造标签页分类

| 标签页 | 包含内容 |
|--------|---------|
| 生物实体 | 所有刷怪蛋 |
| 染梦世界 | 染梦维度方块和物品 |
| 云世界 | 云系列方块 |
| 暗影世界 | 暗影系列方块和物品 |
| 功能方块 | 工作台类方块（蓄梦池、书桌、炼药锅等） |
| 食物 | 所有食物和饮品 |
| 染梦饰品 | 染梦相关饰品 |
| 工具与武器 | 工具、武器、装备 |
| 杂项 | 唱片、材料、其他 |

---

## 数据生成与构建

### 开发工作流

```bash
# 1. 编译检查
.\gradlew compileJava

# 2. 运行数据生成器
.\gradlew runData

# 3. 启动客户端测试
.\gradlew runClient
```

### 自定义构建任务

| 任务 | 描述 |
|------|------|
| `runRecipeDemo` | 运行配方生成示例 |
| `runDimensionApiDemo` | 验证维度 API 功能 |
| `generateDimensionTestJsons` | 生成维度测试 JSON |
| `runParticleApiTest` | 粒子 API 测试 |
| `runEntityApiTest` | 实体 API 测试 |
| `runRuinApiTest` | 遗迹 API 测试 |
| `runAllApiTests` | 全部 API 测试 |

---

## ⚠️ 重要开发规范

### 1. 1.21 数据目录命名变更（高频踩坑点）

Minecraft 1.21 将数据文件夹从复数改为**单数形式**：

| 功能 | 1.20 旧路径（错误） | 1.21 新路径（正确） |
|------|-------------------|-------------------|
| 战利品表 | `loot_tables/` | `loot_table/` |
| 配方 | `recipes/` | `recipe/` |

### 2. 语言文件完整性

每次新增注册项后，需在 `zh_cn.json` 中添加对应翻译键，格式为：
- `item.pasterdream.<name>` — 物品
- `block.pasterdream.<name>` — 方块
- `entity.pasterdream.<name>` — 实体

### 3. 配方 JSON 格式

1.21 将 `result.item` 改为 `result.id`：
```json
// 1.20（旧）
"result": { "item": "pasterdream:xxx", "count": 1 }
// 1.21（新）
"result": { "id": "pasterdream:xxx", "count": 1 }
```

### 4. 标签命名空间

- `c/` = Common Convention 标签
- `minecraft/` = 原版标签
- `neoforge/` = NeoForge 专属标签

---

## 项目文件统计

> 截至 v0.0.3.3

| 类别 | 数量 |
|------|------|
| Java 源文件（主模块） | 200+ |
| Java 源文件（API 模块） | 50+ |
| 注册物品 | 100+ |
| 注册方块 | 60+ |
| 注册实体 | 31 生物 + 弹射物 |
| 合成配方 | 150+ |
| 方块战利品表 | 70+ |
| 遗迹结构 | 6 |
| GeckoLib 模型 | 8 |
| 自定义粒子 | 12 |
| 自定义音乐/唱片 | 10 |
| 生物群系修饰器 | 13 |