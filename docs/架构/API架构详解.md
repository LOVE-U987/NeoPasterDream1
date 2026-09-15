# API 架构详解

> 本文档详述 PasterDreamAPI 模块的完整内部架构：包结构、核心模式、逐包职责、依赖方向与成熟度。
> 分工说明：[架构总览](架构总览.md) 讲宏观设计；本文讲 API 模块内部详图；[API 边界](../参考/API边界.md) 讲上收判定；[模块边界](模块边界.md) 讲归属决策。

---

## 1. 模块定位与设计原则

### 1.1 定位

| 项 | 值 |
|---|---|
| 模块类型 | 独立前置库模组（`java-library` + `maven-publish`） |
| mod ID | `pasterdreamapi` |
| 包根 | `com.pasterdream.pasterdreammod.api` |
| 规模 | 23 个包、194 个类（main sourceSet） |
| 下游 | PasterDream 主模 + Spells / Sanity / MeltDream 三个附属 |

### 1.2 双命名空间

```java
// PasterDreamAPI.java
public static final String MOD_ID = "pasterdreamapi";        // 前置模组自身命名空间
public static final String DATA_NAMESPACE = "pasterdream";   // 数据命名空间
```

数据命名空间沿用 `pasterdream`，保证 Attachment、网络包等既有存档数据的前后兼容。新增数据类资源（Attachment、Payload 通道）应使用 `DATA_NAMESPACE` 而非 `MOD_ID`。

### 1.3 生命周期

PasterDreamAPI 是独立加载的前置模组，注册入口由自身主类完成：

```java
@Mod(PasterDreamAPI.MOD_ID)
public class PasterDreamAPIMod {
    public PasterDreamAPIMod(IEventBus modEventBus) {
        PasterDreamAPI.registerAll(modEventBus);  // 统一注册 API 层全部 DeferredRegister
    }
}
```

**下游模组不再调用 `registerAll`**——重复注册会触发 `Cannot register DeferredRegister to more than one event bus`。下游的职责只有两件事：

1. 在 Gradle 中声明 `implementation project(':PasterDreamAPI')`（附属模块为编译期依赖）
2. 注册**自己的** DeferredRegister（`PD*` 系列注册类）

### 1.4 设计原则

| 原则 | 说明 |
|------|------|
| 零内容依赖 | 不含具体方块/物品/实体实现、玩法数值、资产路径、维度 ID |
| 三件套模式 | 所有注册系统遵循 Facade（API 门面）+ Builder（链式构建）+ Result（结果句柄） |
| 统一注册 | 全部 DeferredRegister 由 `registerAll` 集中挂总线 |
| 数据驱动 | JSON 优先，DataGen Provider 提供薄壳基类 |
| thin 发行 | 附属模块不内嵌 API，运行时由主模组打包提供 |

---

## 2. 包结构总览

```
com.pasterdream.pasterdreammod.api
├── PasterDreamAPI / PasterDreamAPIMod      顶层常量与模组主类
├── ApiSoundRegistry / ApiCodeGenConfig     声音注册器 / 代码生成配置
├── attachment  attribute  config           横切定义（附件/属性/配置）
├── block  blockentity  item  entity        注册与内容域
├── fluid  menu  curio  particle
├── effect                                  药水效果 + VFX 特效系统
├── client                                  客户端基础设施（渲染/特效执行/天空/着色）
├── network                                 S2C / C2S Payload 定义
├── dimension  ruin  worldgen               世界与结构域
├── san  meltdream  spell  audio            玩法系统接口域
└── util  data                              横切工具 / DataGen 薄壳
```

### 统计表（23 包 + 顶层）

| 包 | 类数 | 职责一句话 |
|----|-----|-----------|
| `attachment` | 1 | 玩家数据附件类型注册（San/融梦能量） |
| `attribute` | 1 | 自定义属性注册（APIAttributes） |
| `audio` | 6 | BGM 状态机与生物群系音乐表 |
| `block` | 8 | 方块注册门面 + 通用方块基类 + Builder |
| `blockentity` | 4 | 方块实体注册门面 + 自由数据 BE 基类 |
| `client` | 36 | 客户端基础设施：特效执行/渲染/天空/着色/工具 |
| `config` | 1 | 附属模组配置注册表 |
| `curio` | 5 | Curios 饰品注册门面 + 槽位模型 |
| `data` | 2 | 方块模型/标签 DataGen 薄壳 Provider |
| `dimension` | 9 | 维度注册门面 + 地形协商 |
| `effect` | 31 | 药水效果注册 + VFX 七子系统公共定义 |
| `entity` | 15 | 实体注册门面 + GeckoLib 基类 + 技能/免疫 |
| `fluid` | 3 | 流体/流体类型注册门面 |
| `item` | 17 | 物品注册门面 + 五类 Builder + 迁移模型 |
| `meltdream` | 4 | 融梦能量系统配置接口 |
| `menu` | 3 | 菜单注册门面 + 简单容器菜单 |
| `network` | 10 | Payload 定义（8 S2C + 2 C2S） |
| `particle` | 3 | 粒子注册门面 |
| `ruin` | 4 | 遗迹结构注册门面 + Builder |
| `san` | 6 | 理智系统配置接口 |
| `spell` | 2 | 法术系统接口（预留） |
| `util` | 8 | 横切工具（调度/定位/调试/检测等） |
| `worldgen` | 11 | 世界生成工具 + 装饰/树木注册体系 |
| 顶层 | 4 | 常量类/主类/声音注册器/代码生成配置 |

---

## 3. 核心架构模式

### 3.1 Facade + Builder + Result

每个注册子系统由三个角色构成：

| 角色 | 职责 | 示例 |
|------|------|------|
| **Facade（门面）** | 持有 `DeferredRegister`，暴露 `createXxx` 入口 | `BlockAPI`、`EntityAPI`、`ItemAPI` |
| **Builder（构建器）** | 链式配置注册参数，`build()` 收口 | `SimpleBlockBuilder`、`EntityBuilder` |
| **Result（结果）** | 持有注册句柄，供后续引用 | `EntityResult`、`ParticleResult`、`RuinResult`、`DimensionResult` |

典型调用链（实体注册）：

```java
EntityResult<ShadowGolemEntity> result = EntityAPI.createEntity("shadow_golem")
    .category(MobCategory.MONSTER)
    .size(2.2f, 3.5f)
    .entityClass(ShadowGolemEntity.class)
    .attributes(ShadowGolemEntity::createAttributes)
    .skill(EntitySkill.builder("roar").build())
    .spawnEgg(0x2C2C2C, 0x6B3FAF)
    .build();
```

**优势**：类型安全、编译期检查、链式调用可读性强、新增配置项不改调用方结构。

### 3.2 统一注册

`PasterDreamAPI.registerAll(IEventBus)` 集中注册 15 个 DeferredRegister：

| # | 注册器 | 注册对象 |
|---|--------|---------|
| 1 | `BlockAPI.REGISTRY` | Block |
| 2 | `BlockEntityAPI.REGISTRY` | BlockEntityType |
| 3 | `ItemAPI.REGISTRY` | Item |
| 4 | `EntityAPI.REGISTRY` | EntityType |
| 5 | `APIAttributes.ATTRIBUTES` | Attribute |
| 6 | `MobEffectAPI.REGISTRY` | MobEffect |
| 7 | `ParticleAPI.REGISTRY` | ParticleType |
| 8 | `RuinAPI.REGISTRY` | Structure |
| 9 | `CurioAPI.REGISTRY` | Item（Curios 饰品） |
| 10 | `MenuAPI.REGISTRY` | MenuType |
| 11 | `FluidAPI.REGISTRY` | Fluid |
| 12 | `FluidTypeAPI.REGISTRY` | FluidType |
| 13 | `ApiSoundRegistry.DIMENSION_SOUNDS` | SoundEvent |
| 14 | `DecorationRegistry.FEATURES` | Feature（世界装饰） |
| 15 | `PDPlayerAttachments.ATTACHMENT_TYPES` | AttachmentType |

另有 2 处特殊注册：

- `BuiltinEmitterProcessors.registerAll()` — 内置粒子发射器处理器类型（纯静态表）
- `CutsceneAPI.ENTITY_REGISTRY` — 过场相机实体类型

### 3.3 DataGen 薄壳

`data` 包提供两个可继承的 Provider 基类：

| 类 | 用途 |
|----|------|
| `ApiBlockModelProvider` | 方块模型生成的通用骨架 |
| `ApiBlockTagProvider` | 方块标签生成的通用骨架 |

主模组以薄壳形式继承（如 `PDBlockModelProvider`），内容侧仅补充具体条目。

---

## 4. 分域逐包详解

> `effect` 包横跨两个域：药水效果注册（本节 4.1）与 VFX 特效系统（4.2），按类划分。

### 4.1 注册与内容域

#### block（8 类）

| 类 | 说明 |
|----|------|
| `BlockAPI` | 门面：`REGISTRY` + `createBlock` 入口 |
| `BlockConfig` | 方块通用配置载体 |
| `builder/SimpleBlockBuilder` | 单方块链式构建 |
| `builder/BatchBlockBuilder` | 批量方块构建 |
| `builder/VariantSetBuilder` + `VariantSetResult` | 变体方块集（多朝向/多状态）构建与结果 |
| `SelfDropBlock` | 通用基类：掉落自身（战利品表兜底） |
| `HorizontalWaterloggedBlock` | 通用基类：水平朝向 + 可含水 |

#### blockentity（4 类）

| 类 | 说明 |
|----|------|
| `BlockEntityAPI` | 门面：BE 类型注册 |
| `builder/BlockEntityBuilder` | BE 类型链式构建 |
| `base/FreeDataBlockEntity` | 自由 Compound 数据 BE 基类（零字段,数据全存 NBT） |
| `base/GeoFreeDataBlockEntity` | 上一行的 GeckoLib 渲染版 |

#### item（17 类）

| 类组 | 说明 |
|------|------|
| `ItemAPI` | 门面：与主模 `PDItems.ITEMS` 同 modid 双轨注册 |
| `builder/`（5 类） | `SimpleItemBuilder` / `FoodItemBuilder` / `ToolItemBuilder` / `CurioItemBuilder` / `BaseItemBuilder` |
| `model/`（7 类） | `ItemSpec` 及 `FoodSpec`/`ToolSpec`/`ArmorSpec`/`CurioSpec`/`AttributeModSpec`/`MigrationCategory`——物品迁移描述模型 |
| `manager/`（2 类） | `ItemManager`（批量迁移执行）、`MigrationReport`（迁移报告） |
| `base/AbstractGeoDisplayItem` | GeckoLib 展示物品基类 |

#### entity（15 类）

| 类组 | 说明 |
|------|------|
| `EntityAPI` + `EntityResult` + `builder/EntityBuilder` | 注册三件套 |
| `base/`（3 类） | `GeckoLibMobEntity` / `GeckoLibMonsterEntity` / `GeckoLibProjectileEntity`——GeckoLib 实体基类 |
| `skill/`（3 类） | `EntitySkill` / `EntitySkillBuilder` / `EntitySkillManager`——实体技能时序框架 |
| `damage/`（2 类） | `ConfigurableImmunityEntity` + `DamageImmunityConfig`——可配置伤害免疫（数据在主模填表） |
| `projectile/AbstractWandProjectileEntity` | 法杖投射物基类 |
| `anim/ProcedureAnimationHandler` | 过程式动画驱动 |
| `tag/`（2 类） | `EntityTag` / `EntityTagRegistry`——实体标签 |

#### fluid（3 类）

`FluidAPI`（Fluid 注册）+ `FluidTypeAPI`（FluidType 注册）+ `builder/FluidBuilder`（`buildPair` 同时产出流体对）。主模 `PDFluidsType` 仅保留 Holder。

#### menu（3 类）

`MenuAPI`（MenuType 注册）+ `SimpleContainerMenu`（简单容器菜单通用实现，简单箱/桌直接继承）+ `builder/MenuBuilder`。

#### curio（5 类）

`CurioAPI` + `CurioBuilder`（饰品注册）+ `model/CurioSlot`、`model/CurioAttributeMod`（槽位/属性修饰模型）+ `DefaultCurioClientBridge`（客户端桥接口，主模提供实现或日志兜底）。

#### particle（3 类）

`ParticleAPI`（ParticleType 注册）+ `builder/ParticleBuilder` + `ParticleResult`。

#### effect 中的药水效果部分（4 类）

`MobEffectAPI`（门面）+ `base/PasterDreamEffect`（效果基类）+ `builder/MobEffectBuilder` + `MobEffectResult`。

### 4.2 特效系统域（VFX）

API 的核心特色系统：七个子系统 + 后处理，统一遵循「**公共定义在 `effect`，客户端执行在 `client/effect`，跨端同步走 `network` Payload**」的三层结构。

| 子系统 | 公共定义（effect） | 客户端执行（client/effect） | Payload |
|--------|-------------------|---------------------------|---------|
| 氛围 atmosphere | `AtmosphereEffectAPI` | `AtmosphereHandler` | `AtmospherePayload` |
| 过场相机 cutscene | `CutsceneAPI`、`CutsceneData`、`CameraPos`、`CurveType`、`EasingType`、`CutsceneScreenEffectData` | `ClientCameraEntity`、`CutsceneCameraHandler`、`CutsceneExecutor`、`motion/`（线性 + CatmullRom） | `StartCutscenePayload` / `StopCutscenePayload`（C2S） |
| 幽灵拖尾 ghost | `GhostEffectAPI` | `GhostHandler` | `GhostPayload` |
| 冲击帧 impact | `ImpactFrameAPI`、`ImpactFrame` | `ImpactFramesHandler` | `ImpactFramesPayload` |
| 粒子发射器 particle | `ParticleEmitterAPI`、`EmitterProcessor` 系列（4 类）+ 内置处理器（4 类：绑定实体/圆环/空/注册表） | `ParticleEmitter`、`ParticleEmitterHandler` | `ParticleEmitterPayload` |
| 屏幕效果 screen | `ScreenEffectAPI`、`ScreenEffectRegistry`、`ScreenEffectFactory`、`ScreenEffectType`、`ScreenEffectData`、`ScreenColorData` | `ScreenEffect` 系列（6 类）+ 内置效果（2 类） | `ScreenEffectPayload` |
| 屏幕震动 shake | `ScreenShakeAPI`、`ScreenShakeData` | `ScreenShakeHandler` | `ScreenShakePayload` |
| 后处理 post | —（仅客户端） | `PostShaderManager`、`PostShaderEvent` | — |

发射器处理器是可扩展点：`EmitterProcessorRegistry` 注册处理器类型，新处理器实现 `EmitterProcessor` 后注册即可（内置示例见 `processors/`）。

### 4.3 世界与结构域

#### dimension（9 类）

| 类组 | 说明 |
|------|------|
| `DimensionAPI` + `DimensionResult` + `builder/DimensionBuilder` + `APIDimensions` | 维度注册三件套 + ID 常量辅助 |
| `terrain/`（5 类） | 结构-地形协商：`TerrainRequirements`（需求）→ `TerrainAssessment`（评估）→ `TerrainAdjuster`（调整）→ `StructureTerrainNegotiator`（协商器）→ `StructurePlacementRecord`（落位记录） |

#### ruin（4 类）

`RuinAPI`（Structure/结构集注册）+ `builder/RuinBuilder` + `builder/StructureSetBuilder` + `RuinResult`。

#### worldgen（11 类）

| 类组 | 说明 |
|------|------|
| `WorldGenUtils` | 世界生成通用工具 |
| `decor/`（10 类） | 通用装饰体系：`DecorationType`（类型枚举）、`DecorationRegistry`（Feature 注册）、`DecorationBuilder`/`DecorationConfig`（构建）、`GenericDecorationFeature` + `DecorationPlacer`（放置执行）、`ICustomDecorationGenerator`（自定义扩展点）、`RegionClaimManager`（区域认领防重叠）、`TreeRegistry` + `TreePlacerAPI`（树木放置门面） |

#### attribute（1 类）

`APIAttributes`：自定义属性注册（`ATTRIBUTES` DeferredRegister）。

#### attachment（1 类）

`PDPlayerAttachments`：玩家数据附件类型注册（San 理智值 / 融梦能量）。附件的**读写逻辑与生命周期 glue 留在主模/附属**。

### 4.4 玩法系统接口域

此类包只定义「配置接口 + 注册表 + 数据载体」，具体数值与实现由对应附属模块提供：

| 包 | 接口约定 | 实现方 |
|----|---------|--------|
| `san`（6 类） | `ISanSystemConfig` + `SanConfigRegistry` + `APISanGameRules`；`SanAPI`/`SanHelper`/`SanData` 为访问层 | PasterDreamSanity |
| `meltdream`（4 类） | `IMeltDreamEnergySystemConfig` + `MeltDreamEnergyConfigRegistry`；`MeltDreamEnergyAPI`/`MeltDreamEnergyData` | PasterDreamMeltDream |
| `spell`（2 类） | `ISpell` + `SpellAPI`——预留接口，当前无下游消费 | （预留） |
| `audio`（6 类） | `BgmAPI`（状态机门面）、`BiomeMusicTable`（群系音乐表）、`IMusicEventLookup`（事件查找）、`FadeState`/`CooldownManager`/`LoopRestartManager`（状态管理） | 主模 `client.audio` 播放层（Crossfade/Volume/Manager） |

### 4.5 客户端基础设施（client，36 类）

| 子包 | 类数 | 说明 |
|------|-----|------|
| `client/effect` | 20 | VFX 七子系统的客户端执行器（见 4.2 表） |
| `client/sky` | 7 | 数据驱动天空盒：`SkyboxAPI`/`Registry`/`Entry` + `SkyCondition`/`SkyContent` + `preset/SkyboxPresets` |
| `client/shading` | 4 | 群系着色：`BiomeShadingAPI`/`Registry`/`Entry` + `BiomeShading` |
| `client/util` | 2 | `AnimUtils`（动画工具）、`ColoredVertexConsumer`（顶点着色包装） |
| `client/block` | 1 | `BlockTintClient`（方块染色） |
| `client/particle` | 1 | `ApiParticleRenderTypes`（粒子 RenderType） |
| `client/renderer` | 1 | `DefaultedGeoBlockRenderer`（Geo 方块渲染通用骨架） |

**约束**：`client` 包只允许被客户端代码引用；公共 API（`effect` 包）不得反向依赖 `client`。

### 4.6 横切工具域

#### util（8 类）

| 类 | 说明 |
|----|------|
| `ServerScheduler` | 服务端 tick 任务调度 |
| `StructureLocator` | 结构 nearest 定位 |
| `StructureInventoryHelper` | 结构内箱子库存操作 |
| `DimensionRegionHelper` | 维度 region 文件读写 |
| `CustomItemData` | 物品自定义 Compound 数据 |
| `AddonDetector` | 附属模组检测 |
| `BookLocalization` | 图鉴本地化辅助 |
| `PDDebugLogger` | 统一调试日志 |

#### data（2 类）、config（1 类）

见 3.3 与 `PDAddonConfigRegistry`（附属配置注册表）。

#### 顶层（4 类）

`PasterDreamAPI`（常量 + registerAll）、`PasterDreamAPIMod`（模组主类）、`ApiSoundRegistry`（维度声音注册器）、`ApiCodeGenConfig`（代码生成配置）。

### 4.7 特例：api.doll（位于主模，未上收）

`com.pasterdream.pasterdreammod.api.doll`（`DollAPI`/`DollBuilder`/`DollConfig`/`DollResult`/`DollModelType` 共 5 类）**包名属于 `api.*` 命名空间，但物理位于 PasterDream 主模**。主模 10+ 处引用（方块/DataGen/KubeJS 集成/创造模式标签）。

该包是历史遗留的组织方式，符合上收条件时应在遵循 [API 边界](../参考/API边界.md) 判定的前提下迁移至 PasterDreamAPI 模块。**新代码不得模仿此模式。**

该架构将在后续版本中移除

---

## 5. 网络层架构

`network` 包只定义 Payload（10 个），**处理器全部留在主模**（`PDNetwork` 注册与 handler 实现）：

| 方向 | Payload | 用途 |
|------|---------|------|
| S2C | `AtmospherePayload` | 氛围效果 |
| S2C | `GhostPayload` | 幽灵拖尾 |
| S2C | `ImpactFramesPayload` | 冲击帧 |
| S2C | `ParticleEmitterPayload` | 粒子发射器 |
| S2C | `ScreenEffectPayload` | 屏幕效果 |
| S2C | `ScreenShakePayload` | 屏幕震动 |
| S2C | `SanDataPayload` | 理智值同步 |
| S2C | `MeltDreamEnergyPayload` | 融梦能量同步 |
| C2S | `StartCutscenePayload` | 请求开始过场 |
| C2S | `StopCutscenePayload` | 请求停止过场 |

设计要点：Payload 定义（数据形状）属于可复用协议 → 收 API；业务处理逻辑（如何渲染/如何扣减）属于内容 → 留主模。

---

## 6. 依赖规则

### 6.1 模块间依赖（编译期）

```
PasterDreamAPI ← PasterDream（主模）
PasterDreamAPI ← PasterDreamSpells（编译期依赖，thin 发行）
PasterDreamAPI ← PasterDreamSanity（同上）
PasterDreamAPI ← PasterDreamMeltDream（同上）
```

运行时：附属不内嵌 API，由 PasterDream 主模组打包提供 API jar，附属需主模作为前置。

### 6.2 实际引用分布（取证数据）

| 下游 | 引用文件数 | 高频包 |
|------|-----------|--------|
| PasterDream | 302 | util(119)、entity(57)、item(32)、worldgen(31)、client(24)、effect(20) |
| PasterDreamSpells | 7 | util、config、attribute、particle |
| PasterDreamSanity | 5 | san、config、attribute |
| PasterDreamMeltDream | 5 | meltdream、config |

### 6.3 包内依赖方向约束

```
util / config / attribute / attachment  （底层，无内部依赖）
        ↑
block / item / entity / effect / ...    （注册域，可依赖底层）
        ↑
client / network                        （端相关层，可依赖注册域）
```

- 禁止反向依赖：公共包不得 import `client` 包
- 禁止横向循环：注册域各包之间避免相互依赖
- 新增依赖前先用 [API 边界](../参考/API边界.md) 的三零判定（零 PD* 注册表 / 零玩法数值 / 零资产路径）

---

## 7. API 成熟度

| 子系统 | 成熟度 | 依据 |
|--------|--------|------|
| block / item / entity 注册门面 | 稳定 | 主模高频引用（100+ 处），双轨注册约定成型 |
| util / attribute / attachment / data | 稳定 | 全下游通用，DataGen 薄壳已接入主模 |
| worldgen（decor/树木） | 稳定 | P1/P2 上收完成，主模 31 文件引用 |
| curio / menu / blockentity | 稳定 | P1/P2 上收项，主模稳定消费 |
| client/shading / client/sky | 稳定 | 数据驱动 API 已发布并有独立提交记录 |
| san / meltdream 接口 | 稳定 | 对应附属模块全程依赖 |
| effect VFX 七子系统 | 演进中 | 主模 20 文件引用，处理器/屏幕效果仍在扩展 |
| dimension（terrain 协商） | 演进中 | 协商器设计较新，接口可能随需求调整 |
| audio（BGM） | 演进中 | 状态机已上收（P1），播放层 glue 在主模迭代 |
| fluid / particle / ruin / menu | 演进中 | 可用，但下游用量少，接口覆盖面有限 |
| spell | 预留 | 仅 2 个接口类，当前无下游消费 |
| api.doll（主模内） | 待上收 | 包名属 api 命名空间但物理在主模（见 4.7） |

---

## 8. 扩展指引

向 API 新增能力时的流程：

1. **判定是否该上收** — 用 [API 边界](../参考/API边界.md) 的三零判定与勿上收清单
2. **确定归属** — 用 [模块边界](模块边界.md) 的归属决策表
3. **遵循三件套** — 新注册系统按 Facade + Builder + Result 组织，DeferredRegister 加入 `registerAll` 清单
4. **补 DataGen 薄壳** — 可数据驱动的部分在 `data` 包提供 Provider 基类
5. **验证** — `.\gradlew :PasterDreamAPI:compileJava` 编译 + 全量 `.\gradlew compileJava`

---

## 9. 相关文档

- [架构总览](架构总览.md) — 整体架构与模块依赖
- [主模组架构详解](主模组架构详解.md) — 主模组逐包架构与测试体系
- [附属模块架构详解](附属模块架构详解.md) — Spells/Sanity/MeltDream
- [模块边界](模块边界.md) — 代码归属决策
- [API 边界](../参考/API边界.md) — 上收判定规则与历史
- [注册指南](../开发指南/注册指南.md) — 各 API 注册方法详解
- [注册流程](注册流程.md) — DeferredRegister 与生命周期
