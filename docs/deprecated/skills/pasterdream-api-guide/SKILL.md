---
name: "pasterdream-api-guide"
description: "PasterDream 模组 API 使用总览教程：三件套模式（Facade+Builder+Result）、registerAll 注册生命周期、模块归属决策，以及尚无独立技能包的 API 域（方块/物品/维度/流体/菜单/饰品/理智/融梦能量/法术/BGM/附属配置/玩偶）快速上手。Invoke when adding content via BlockAPI/ItemAPI/DimensionAPI/FluidAPI/MenuAPI/CurioAPI/SanAPI/MeltDreamEnergyAPI/BgmAPI/DollAPI, wiring DeferredRegister into a mod constructor, or deciding where new code belongs."
---

# PasterDream API 使用教程（总览 + 缺失域）

> 适用版本：Minecraft 1.21.1 · NeoForge 21.1.219 · PasterDream 0.10.0-pre.3+ · Java 21
> 更新：2026-09-26（签名均经源码核签）
>
> 本文是 API 体系的「总览 + 补缺」教程：已有独立技能包的域（实体 / 特效 / 粒子 / 遗迹 / VFX /
> 世界装饰 / 物品迁移 / 方块掉落）见 §5 索引，不在此重复。
> 文中路径均为仓库根相对路径。

## 1. 总览

### 1.1 多模块结构与归属决策

| 模块 | modid | 职责 |
|------|-------|------|
| PasterDreamAPI | `pasterdreamapi` | 全部 Facade/Builder/Result/Config、注册体系、DataGen 薄壳（独立前置 mod，jarJar 内嵌于主模 jar 发布） |
| PasterDream | `pasterdream` | 具体方块/物品/实体/渲染/客户端代码；含 `api.doll` 历史特例（见 §2） |
| PasterDreamSpells | `pasterdreamspells` | 法术系统（thin 发行，运行时靠主模提供 API） |
| PasterDreamSanity | `pasterdreamsanity` | 理智系统（thin 发行） |
| PasterDreamMeltDream | `pasterdreammeltdream` | 融梦能量系统（thin 发行） |

新代码归属口诀：API/Builder/注册门面 → API 模块；法术 → Spells；理智 → Sanity；融梦 → MeltDream；
其余 → 主模块。完整判定规则见 `docs/架构/模块边界.md` 与 `docs/参考/API边界.md`。

### 1.2 三件套模式：Facade + Builder + Result

每个注册子系统由三个角色构成（详见 `docs/架构/API架构详解.md` §3.1）：

| 角色 | 职责 | 示例 |
|------|------|------|
| Facade（门面） | 持有 `DeferredRegister`，暴露 `createXxx` 入口 | `BlockAPI`、`EntityAPI`、`ItemAPI` |
| Builder（构建器） | 链式配置注册参数，`build()`/`register()` 收口 | `SimpleBlockBuilder`、`EntityBuilder` |
| Result（结果） | 持有注册句柄，供后续引用 | `EntityResult`、`ParticleResult`、`DollResult` |

典型调用链（实体域，完整用法见 `pasterdream-entity-api` 技能包）：

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

### 1.3 注册生命周期（⚠️ 最容易踩的雷）

PasterDreamAPI 是独立加载的前置 mod，全部 **15 个 DeferredRegister + 2 个特殊注册**由它自己的主类统一挂总线：

```java
// PasterDreamAPI/src/main/java/com/pasterdream/pasterdreammod/api/PasterDreamAPIMod.java
@Mod(PasterDreamAPI.MOD_ID)
public class PasterDreamAPIMod {
    public PasterDreamAPIMod(IEventBus modEventBus) {
        PasterDreamAPI.registerAll(modEventBus);
    }
}
```

`PasterDreamAPI.registerAll(IEventBus)` 挂接的注册器（全部清单）：

- `BlockAPI.REGISTRY` / `BlockEntityAPI.REGISTRY` / `ItemAPI.REGISTRY` / `EntityAPI.REGISTRY`
- `APIAttributes.ATTRIBUTES` / `MobEffectAPI.REGISTRY` / `ParticleAPI.REGISTRY` / `RuinAPI.REGISTRY`
- `CurioAPI.REGISTRY` / `MenuAPI.REGISTRY` / `FluidAPI.REGISTRY` / `FluidTypeAPI.REGISTRY`
- `ApiSoundRegistry.DIMENSION_SOUNDS` / `DecorationRegistry.FEATURES` / `PDPlayerAttachments.ATTACHMENT_TYPES`
- 特殊注册：`BuiltinEmitterProcessors.registerAll()` + `CutsceneAPI.ENTITY_REGISTRY`

**下游模组（主模 / 附属 / 第三方）的三条纪律：**

1. ❌ **绝不**自己调用 `PasterDreamAPI.registerAll(...)`，也绝不把上述 15 个注册器再挂一次总线——
   重复挂会抛 `Cannot register DeferredRegister to more than one event bus`。
2. ✅ 正确姿势：在**模组构造器阶段**调用 API 门面的 `createXxx(...)...build()`，把条目填进共享注册器
   （注册事件稍后统一触发）；你只挂**自己的** DeferredRegister。
3. ✅ 主模采用「显式引用触发类加载」确保注册分区被填充（如 `Object unused = PDBlocks.BLOCKS;`），
   `PDRegistrySanityCheck` 在 commonSetup 校验，防止漏引用导致条目静默缺失。

**唯一例外是 DollAPI**（物理在主模，见 §2）：PasterDream 主模构造器亲自挂
`DollAPI.BLOCK_REGISTRY` 与 `DollAPI.ITEM_REGISTRY`，然后才调 `PDCustomDolls.register()`。

时序约束：一切注册调用都必须发生在注册表冻结之前（模组构造器 / mod 事件总线监听阶段）。
冻结后只能走各 API 提供的 `registerDirect()` 类直写入口（如有，例如 DollAPI），否则抛
IllegalStateException。

外部模组（mymod）最小注册模板：

```java
@Mod("mymod")
public class MyMod {
    public MyMod(IEventBus modEventBus, ModContainer modContainer) {
        // 1. 绝不调用 PasterDreamAPI.registerAll(modEventBus)（会抛 more-than-one-bus）
        // 2. 用共享门面（内容进 pasterdream: 命名空间），静态字段触发类加载：
        Object unused = MyBlocks.SIMPLE;   // 类内用 BlockAPI.registerSimpleBlocks()...build()
        // 3. 需要自有命名空间的条目才自建 DR 并挂总线：
        MyModItems.ITEMS.register(modEventBus);
        // 4. 纯静态注册表（San/MeltDream/Spell/AddonConfig）构造期或 commonSetup 调用：
        modEventBus.addListener(this::setup);
    }
    private void setup(FMLCommonSetupEvent e) {
        e.enqueueWork(() -> SanConfigRegistry.register(new MySanConfig()));
    }
}
```

### 1.4 API 成熟度速查

摘自 `docs/架构/API架构详解.md` §7，选择依赖时参考：

| 子系统 | 成熟度 |
|--------|--------|
| block / item / entity 注册门面；util / attribute / attachment / data；worldgen（decor/树木）；curio / menu / blockentity；client/shading / client/sky；san / meltdream 接口 | **稳定** |
| effect VFX 七子系统；dimension（terrain 协商）；audio（BGM）；fluid / particle / ruin 覆盖面 | **演进中**（可用，接口可能调整） |
| spell | **预留**（仅 `ISpell` + `SpellAPI`，无下游消费） |
| api.doll | **待上收**（包名属 `api.*` 但物理在主模） |

## 2. Doll API（玩偶，Java 侧）

### 2.1 位置特例

`com.pasterdream.pasterdreammod.api.doll.DollAPI` **物理位于 PasterDream 主模**（渲染与方块实体
强依赖主模），包名挂在 `api.*` 下是历史遗留——**新代码不要模仿这种组织方式**。包内 5 类：
`DollAPI` / `DollBuilder` / `DollConfig` / `DollModelType` / `DollResult`。

### 2.2 注册入口

| 入口 | 签名 | 说明 |
|------|------|------|
| Builder | `DollAPI.create(String name) -> DollBuilder` | 链式注册（下表） |
| 一步注册 | `DollAPI.registerDoll(String namespace, String name, String model, String texture, boolean canHoldItems, String holdingModel) -> DollResult` | `null` 参数走默认；内部 `registerDirect()` |
| 换皮专用 | `DollAPI.registerDollWithSkin(String namespace, String name, String skinTexture, boolean canHoldItems) -> DollResult` | 复用 `eoul_doll` 骨骼与抱物模型，只换皮肤 |

`DollBuilder` 流式方法（含默认值）：

| 方法 | 默认值 | 备注 |
|------|--------|------|
| `namespace(String)` | `"pasterdream"` | 第三方命名空间传自己的 modid |
| `model(ResourceLocation)` | `pasterdream:geo/block/<name>.geo.json` | |
| `texture(ResourceLocation)` | `pasterdream:textures/block/<name>.png` | |
| `canHoldItems(boolean)` | `false` | `false` 时 `holdingModel` 被强制置为 `= model` |
| `holdingModel(ResourceLocation)` | `pasterdream:geo/block/<name>_holding.geo.json` | |
| `modelType(DollModelType)` | `DollModelType.NEW` | `legacy()` 等价于 `modelType(LEGACY)` |
| `legacy()` | — | 切到旧模型工作流 |
| `itemProperties(Item.Properties)` | `new Item.Properties()` | |
| `blockProperties(BlockBehaviour.Properties)` | `strength(1.0f).sound(SoundType.DECORATED_POT).noOcclusion()` | |
| `register() -> DollResult` | — | DeferredRegister 路径，模组构造阶段调用 |
| `registerDirect() -> DollResult` | — | 注册表冻结前直写（KubeJS startup 用）；冻结后抛带指引的 IllegalStateException |

⚠️ **legacy 工作流限制**：`LegacyDollBlockModel` 只拿注册名、硬编码 `pasterdream` 命名空间，
**忽略** builder 上的 `.model()/.texture()/.holdingModel()` 显式路径；抱物状态复用同一张纹理。

### 2.3 查询与战利品池

```java
DollAPI.getRegistrations()                    // Collection<DollResult> 全部
DollAPI.getRegistration(String name)          // Optional<DollResult>
DollAPI.getConfig(Block block)                 // Optional<DollConfig>（带缓存）
DollAPI.getBlockEntityHolder(Block)           // Optional<DeferredHolder<..., BlockEntityType<DollBlockEntity>>>
DollAPI.getBlockEntityType(Block)             // Optional<BlockEntityType<DollBlockEntity>>
DollAPI.getBlocks() / getItems()              // 全部 DeferredBlock / DeferredItem
DollAPI.registerLootItem(Supplier<? extends Item>)  // 登记进可掉落战利品池（融梦水晶箱附加掉落数据源）
DollAPI.getLootItems()                        // List<Item> 懒解析（过滤空气/重复/未注册）
```

`DollResult` 为 record：`name()` / `block()`（`DeferredBlock<DollBlock>`）/
`item()`（`DeferredItem<DollDisplayItem>`）/ `blockEntityType()` / `config()`。
`DollBuilder.register()` 会自动把玩偶物品登记进战利品池。

### 2.4 资源与语言约定

| 资源 | 路径 | 来源 |
|------|------|------|
| 基础模型 | `assets/<ns>/geo/block/<name>.geo.json` | 手工提供 |
| 抱物模型 | `assets/<ns>/geo/block/<name>_holding.geo.json` | `canHoldItems=true` 时必需 |
| 皮肤纹理 | `assets/<ns>/textures/block/<name>.png` | 手工提供；抱物状态复用 |
| 动画 | **不需要** | 固定用 `pasterdream:animations/block/empty.animation.json` |
| blockstate / 物品模型 | `pasterdream` 命名空间由 DataGen 自动生成 | `PDBlockModelProvider`；第三方 ns 需自带 |
| 战利品表 | `data/<ns>/loot_table/blocks/<name>.json` | **必须手写，无任何兜底**（见 §4） |
| 语言键 | `block.<ns>.<name>` / `item.<ns>.<name>` / `block.<ns>.<name>.desc` | **`.desc` 必需**（tooltip 无条件追加，block 前缀） |
| 挖掘标签 | `minecraft:mineable/with_axe` | DataGen 仅为 `pasterdream` ns 生成 |

要点：

- **`bb_main` 骨骼几何约定**：抱物模型必须有 `bb_main` 骨骼，渲染器按**固定几何**计算物品位置
  （pivot `[0,0,0]`、cube `origin=[1,6,-6]`、`size=[2,2,2]`、`inflate=2`），被抱物品以 0.5 缩放
  渲染在骨骼中心。骨骼几何偏离该约定 → 物品位置漂移。
- `registerDirect()` 在 `namespace != "pasterdream"` 时自动在 `<游戏目录>/kubejs/assets/<ns>/`
  生成 blockstate 与物品模型 JSON（依赖 KubeJS 资源包机制，需 KubeJS 在场）。
- 配置查不到时回退 `pasterdream:geo/block/missing_doll` 占位模型（不是紫黑块）。
- 模组内置 `eoul_doll` / `love_u_doll` **不走 DollAPI**（手写旧类，专属渲染器），仅经
  `PDDollLootRegistrations` 登记进战利品池；仓库内 DollAPI 真实用例是 `PDCustomDolls.register()`
  （`phantom_daze`、`mini_beixu_doll`、`wuyu_doll`、`momonyako_doll`）。

### 2.5 KubeJS（摘要）

完整教程见 `docs/教程/KubeJS玩偶.md`。要点：

- 推荐时序：`StartupEvents.registry('block')` 事件内 `Java.loadClass('...DollAPI')` 后调
  `registerDollWithSkin(...)` / `registerDoll(...)`——该事件恰在注册表冻结前触发。
- 备选：`PasterDreamEvents.dollRegistry`（startup 事件组，内部走 `registerDirect()`）；
  **JS 侧没有 `.legacy()` 绑定**，legacy 工作流只能走 Java。

## 3. 缺失域快速开始

> ⚠️ **命名空间语义**（所有域共用）：共享门面的 DeferredRegister 全部用
> `PasterDreamAPI.DATA_NAMESPACE = "pasterdream"`（唯一例外 `FluidTypeAPI.REGISTRY` 用
> `"pasterdreamapi"`）——外部模组经这些门面注册的内容会落在 `pasterdream:` 命名空间而非
> `mymod:`。要自有命名空间必须自建 DeferredRegister（维度例外：
> `new DimensionBuilder("mymod", name)`）。
>
> 以下签名均经源码逐项核签（2026-09-26）。

### 3.1 BlockAPI（方块）

门面：`com.pasterdream.pasterdreammod.api.block.BlockAPI`（`REGISTRY` + 三种批量注册模式）。
无需挂任何注册器，静态字段 / 构造器直接调用即可。

**模式一：`BlockAPI.registerSimpleBlocks()` → SimpleBlockBuilder**（换皮基础方块）

| Builder 方法 | 说明 |
|---|---|
| `add(String name, Block reference)` | 拷贝参考方块属性（`Properties.ofFullCopy`） |
| `addCustom(String name, BlockBehaviour.Properties)` | 手写属性 |
| `add / addCustom(..., @Nullable BlockConfig)` 重载 | 带配置 |
| `Map<String, DeferredBlock<Block>> build()` | 空则抛异常；默认方块工厂 `SelfDropBlock::new`（可用 `BlockConfig.blockFactory` 覆盖） |

`DeferredRegister.Blocks.registerBlock` 自动创建 BlockItem，无需手动注册物品。

**模式二：`BlockAPI.createVariantSet(String baseName, Supplier<? extends Block> baseBlock)` → VariantSetBuilder**（建筑变体族）

`withStairs()` / `withSlab()` / `withWall()` / `withFence()` / `withFenceGate(WoodType)` /
`withDoor(BlockSetType)` / `withTrapdoor(BlockSetType)` / `withPressurePlate(BlockSetType)` /
`withButton(BlockSetType, tickDelay)`（默认参考原版木质方块；`mineable(String)` 设挖掘工具）。
`build()` 返回 `VariantSetResult`（record：`baseName` + 10 个 @Nullable
`DeferredBlock<具体类型>` 组件 + `hasStairs()...hasButton()`；一个变体都没启用则抛异常；
注册名后缀 `_stairs/_slab/_wall/_fence/_fencegate/_door/_trapdoor/_pressure_plate/_button`）。

**模式三：`BlockAPI.batchRegister(String baseName)` → BatchBlockBuilder**（编号批量）

`range(int start, int end)`（闭区间）/ `indexList(Integer...)` / `exclude(Integer...)` /
`factory(BiFunction<Integer, Properties, Block>)`（**必填**） / `withProperties(Properties)` /
`mineable(String)`；`build()` → 不可变 `Map<String, DeferredBlock<Block>>`，名字 `{baseName}_{index}`。

**BlockConfig**（可选配置载体，`BlockConfig.of()` 起链）：`mineable(String)` / `renderType(String)` /
`model(String)` / `tex(String layer, String path)` / `interact(InteractionHandler)` /
`animated(String geoFile)` / `blockFactory(BlockFactory)` / `plantable()` /
`tintFoliage() / tintGrass() / tintFixed(int argb)`。

**辅助基类**：`SelfDropBlock`（掉落优先走战利品表，未配置则掉自身）；`HorizontalWaterloggedBlock`
（抽象类：水平朝向 + 可含水，需实现 `getShape`）。

真实示例（`PDBlocksSimple.java:30-79` 模式一 / `:156-175` 模式二 /
`PDBlocksVegetation.java:169-181` 模式三）：

```java
// 模式一：换皮 + 配置
BlockAPI.registerSimpleBlocks()
    .add("dyedream_dirt", Blocks.DIRT,
         BlockConfig.of().mineable("shovel").model("cube_all").tex("all", "...").plantable())
    .addCustom("dyedream_block",
         Properties.ofFullCopy(Blocks.STONE).requiresCorrectToolForDrops(), BlockConfig.of()...)
    .build();

// 模式二：变体族
VariantSetResult variants = BlockAPI
    .createVariantSet("dyedream_planks", () -> DYEDREAM_PLANKS.get())
    .mineable("axe").withStairs().withSlab().withWall().build();
DeferredBlock<StairBlock> stairs = variants.stairs();

// 模式三：编号批量
Map<String, DeferredBlock<Block>> flowers = BlockAPI.batchRegister("flower")
    .indexList(1, 2, 3, 5, 8, 13)
    .factory((index, props) -> new DyedreamFlowerBlock(props))
    .withProperties(flowerProps()).build();
```

⚠️ `docs/开发指南/注册指南.md` 中的 `BlockAPI.createBlock(...)` / `BlockResult` /
`BlockAPI.registerBlockItem(result, tab)` **均不存在**，勿参考。

### 3.2 ItemAPI（物品）

门面：`com.pasterdream.pasterdreammod.api.item.ItemAPI`。`REGISTRY` 与主模 `PDItems.ITEMS`
同命名空间（`pasterdream`）双轨并存——**勿同名重复注册**。

**入口**（全部 public static）：

| 入口 | 返回 | 说明 |
|---|---|---|
| `simpleItem(String name)` | `SimpleItemBuilder` | 简单物品 |
| `foodItem(String name)` | `FoodItemBuilder` | 食物 |
| `toolItem(String name)` | `ToolItemBuilder` | 工具 |
| `curioItem(String name)` | `CurioItemBuilder` | 饰品轻量版（String 槽位） |
| `registerCustom(String name, Supplier<T>)` | `DeferredItem<T>` | 自定义物品类 |
| `batchSimpleItems(ItemSpec...)` | `List<DeferredItem<Item>>` | 批量（Spec 驱动） |
| `batchFoodItems(Map<String, FoodSpec>)` | 同上 | 批量食物 |
| `markMigrated(...)` / `markPending(...)` / `generateReport()` / `generateLangJson(modId, entries)` / `getManager()` | — | 移植迁移管理 |

**Builder 方法**（`BaseItemBuilder` 通用：`stacksTo(int)` / `rarity(Rarity)` /
`fireResistant()` / `tooltip(String...)`——自动加 `tooltip.` 前缀转可翻译组件）：

- `SimpleItemBuilder.build()` → `DeferredItem<Item>`
- `FoodItemBuilder`：`nutrition(int)` / `saturationModifier(float)` / `alwaysEdible()` /
  `fastFood()` / `effect(String effectId, int duration, int amplifier, float probability)`
  ——效果**按字符串 ID 解析**，不接受 `MobEffectInstance`
- `ToolItemBuilder`：`type(ToolType)`（枚举 `SWORD/PICKAXE/AXE/SHOVEL/HOE/HAMMER/WAND`）/
  `durability(250)` / `miningSpeed(2.0f)` / `attackDamage(1.0f)` / `attackSpeed(-2.4f)` /
  `enchantment(5)` / `incorrectTag(String)` / `repairWith(Supplier<ItemStack>|ItemStack...)`
  ——内部自建 SimpleTier，无需提供 Tier；`WAND` 类型退化为 `stacksTo(1)` 普通物品
- `CurioItemBuilder`：`slot(String)`（默认 `"ring"`）/
  `attribute(String attributeName, String id, double amount, int operation)`（operation
  0/1/2 → ADD_VALUE/ADD_MULTIPLIED_BASE/ADD_MULTIPLIED_TOTAL）；修饰器 ID 被强制加
  `pasterdream:` 前缀；需要枚举槽位或 `AttributeModifier.Operation` 重载请改用 `CurioAPI`（§3.6）

真实示例：

```java
// 工具（PDItemsTools.java:42-54）
ItemAPI.toolItem("copper_sword").type(ToolType.SWORD).durability(225)
    .attackDamage(4.5f).attackSpeed(-2.4f).enchantment(12)
    .repairWith(new ItemStack(Items.COPPER_INGOT)).build();

// 食物（PDItemsFunctional.java:68-71）
ItemAPI.foodItem("amber_candy").nutrition(0).saturationModifier(0f).build();

// 自定义物品类（PDItemsMusic.java:47 唱片）
ItemAPI.registerCustom("my_record", MyRecordItem::new);
```

**Spec 模型**（`item/model/`，批量注册与迁移用）：`ItemSpec`（默认 stackSize=64、COMMON）/
`FoodSpec`（内嵌 `FoodEffectSpec(effectId, duration, amplifier, probability)`）/ `ToolSpec` /
`CurioSpec` / `AttributeModSpec` / `ArmorSpec`（**预留，暂无对应 Builder**）/ `MigrationCategory`
枚举。`AbstractGeoDisplayItem` 是 GeckoLib 展示物品基类（构造 `(Block, Properties)`，抽象
`getControllerName()` / `getTransitionTicks()` / `predicate()`）。

⚠️ `注册指南.md` 的 `createItem/createFood/createTool/createCurio`、`ItemResult`、
`registerToTab`、`.tool(Tier, ...)`、`.effect(MobEffectInstance, ...)` **均不存在**，勿参考。

### 3.3 DimensionAPI（维度）

门面：`com.pasterdream.pasterdreammod.api.dimension.DimensionAPI`。

```java
// 命名空间固定 pasterdream
DimensionResult result = DimensionAPI.createDimension("my_dim").build();

// 要自有命名空间：DimensionBuilder 的构造器是 public 的
DimensionResult result2 = new DimensionBuilder("mymod", "my_dim").build();
```

**DimensionBuilder 关键方法与默认值**（一次 `build()` 同时产出 dimension_type 与 dimension 两份 JSON）：

- DimensionType：`natural(true)` / `piglinSafe(false)` / `bedWorks(true)` / `hasSkylight(true)` /
  `hasCeiling(false)` / `coordinateScale(1.0)` / `withAmbientLight(0.5)` / `logicalHeight(384)` /
  `minY(-64)` / `height(384)` / `monsterSpawnLight(0, 7)` / `monsterSpawnBlockLightLimit(0)` 等
- Dimension：`withNoiseSettings(String)` / `seaLevel(63)` / `aquifersEnabled(true)` /
  `oreVeinsEnabled(false)` / `withDefaultBlock("minecraft:stone")` /
  `withDefaultFluid("minecraft:water")` / `addBiome(biomeId, temperature[], humidity[],
  continental[], weirdness[], erosion[])` / `withFixedBiome(String)` /
  `generateJson(true)` / `basePath("src/main/resources")`
- `@Deprecated withMusic(String)` / `withMusic(String, 0.3f)`（推荐音量 0.3）

`build()` 写 `data/{modId}/dimension_type/{name}.json` 与 `data/{modId}/dimension/{name}.json`
（附属/生产环境常配 `.generateJson(false)` 关闭写盘），返回 record `DimensionResult`
（`typeKey()` / `levelKey()` / `effectsId()` / `isDimension(Level)`——**非泛型**）。

**查询与常量**：`DimensionAPI.isInDimension(Level, DimensionResult)` /
`getRegisteredDimension(String)` / `getMusicEvent(String)`；`APIDimensions` 预置四维度常量
（`DYEDREAM_WORLD` / `AARONCOS_ARENA_WORLD` / `LAMP_SHADOW_WORLD` / `WIND_JOURNEY_WORLD`）与
`isDyedreamWorld(Level)` 等判断方法。

**terrain 协商五类**（结构-地形协商链）：`TerrainRequirements`（需求声明，Builder 构造）→
`TerrainAssessment`（评估结果 SUCCESS/PARTIAL/FAILURE）→ `TerrainAdjuster`（余弦插值把起伏
地形抬升/抹平为平台）→ `StructureTerrainNegotiator`（单例协调器：`getInstance()` /
`registerLargeStructure(...)` / `enableDimensionSupport(String)` / `assessTerrain(...)` /
`printDiagnostics()`）→ `StructurePlacementRecord`（放置统计与失败诊断）。
⚠️ `enableLargeStructureSupport` 与 RuinBuilder 的地形平台选项在主模中暂无调用（API 就绪未接线）。

真实示例：`APIDimensions.java:55-67`（dyedream_world 完整链）。

⚠️ `注册指南.md` 的 `.ambientLight(0.0f)`（真实为 `withAmbientLight(double)`）与
`DimensionResult<DimensionType>`（真实非泛型）有误，勿参考。

### 3.4 FluidAPI / FluidTypeAPI（流体）

门面：`api.fluid.FluidAPI`（Fluid 注册，`pasterdream` 命名空间）与 `api.fluid.FluidTypeAPI`
（FluidType 注册，**全项目唯一用 `pasterdreamapi` 命名空间的注册器**）。

```java
// 推荐：buildPair 一条龙（Type 与 source 同名，可选流动形态）
FluidPairResult<MySource, MyFlowing, MyType> pair = FluidAPI
    .createFluid("my_liquid")
    .fluidType(MyFluidType::new)
    .source(MyFluid.Source::new)
    .flowing("flowing_my_liquid", MyFluid.Flowing::new)
    .buildPair();   // record：sourceHolder / flowingHolder / typeHolder（未配置项为 null）

// 或仅注册 source：... .build()
```

- `FluidBuilder` 方法：`factory(Supplier)` / `source(Supplier)`（factory 别名）/
  `fluidType(Supplier)` / `flowing(String name, Supplier)`；`build()` 仅注册 source，
  `buildPair()` 依次注册 FluidType → source → 可选 flowing；`factory` 缺失抛 NPE。
- **边界**：`BaseFlowingFluid.Properties`、桶物品、流体方块绑定**不在 Builder 内**——在具体
  Fluid 类 / 主模中完成。
- 现状：主模走 `FluidAPI.register("meltdream_liquid", MeltdreamLiquidFluid.Source::new)` 直注册
  （`PDFluids.java:20-42`）+ `PDFluidsType` 单独持有 Type，`buildPair()` 目前零调用——但它是
  P1 上收后的**推荐统一新姿势**。

### 3.5 MenuAPI（菜单）

门面：`api.menu.MenuAPI`（`REGISTRY`，`pasterdream` 命名空间）。

```java
// 注册菜单类型（注意显式类型见证 MenuAPI.<T>，真实示例 PDMenus.java:32-35）
DeferredHolder<MenuType<?>, MenuType<ShadowChestMenu>> SHADOW_CHEST =
    MenuAPI.<ShadowChestMenu>createMenu("shadow_chest")
        .factory(ShadowChestMenu::new)   // IContainerFactory<T>：(containerId, inv, buf)
        .build();                        // 内部 IMenuTypeExtension.create（非 IForgeMenuType）
```

**SimpleContainerMenu**（抽象基类，简单箱/桌直接继承）：

| 方法 | 说明 |
|---|---|
| 构造 `SimpleContainerMenu(@Nullable MenuType<?>, int containerId, int containerSlotCount)` | |
| `bindBlockEntity(@Nullable BlockEntity)` | 绑定 BE（`stillValid` 默认按它校验） |
| `addContainerGrid(IItemHandler, int cols, int rows, int x, int y[, int slotSize=18])` | 容器网格 |
| `addPlayerInventory(Inventory, int invY[, int invX=8[, int hotbarY]])` | 玩家背包 |
| `quickMoveBetweenContainerAndPlayer(Player, int index)` | `quickMoveStack` 默认委托它 |
| `static stillValidBlockEntity(Player, @Nullable BlockEntity)` | |

继承示例（`MeltdreamChestMenu.java:41-58`）：

```java
super(PDMenus.MELTDREAM_CHEST.get(), id, 9);
bindBlockEntity(be);
addContainerGrid(handler, 3, 3, 62, 17);
addPlayerInventory(inv, 100);
```

### 3.6 CurioAPI（饰品）

门面：`api.curio.CurioAPI`（`REGISTRY`，`pasterdream` 命名空间）。**收口方法是
`register()`（不是 build）**，且带同名防重复保护（重复注册时 warn 并复用既有条目）。

```java
// 服务端/数据侧注册（默认 stacksTo=1、rarity=COMMON、slotId="curio"、renderType="none"）
DeferredItem<Item> MY_RING = CurioAPI.create("my_ring")
    .slot(CurioSlot.RING)                                  // 或 slot("ring")
    .attribute("minecraft:generic.attack_damage", "uuid-xxx", 2.0,
               AttributeModifier.Operation.ADD_VALUE)      // 或 attribute(CurioAttributeMod) / attributes(List)
    .stacksTo(1).rarity(Rarity.RARE)
    .tooltip("tooltip.my_ring.desc")
    .renderer(MyRingRenderer::new)                         // 也可 renderBuiltin() / renderCustom() / renderType(String)
    .register();                                           // ← 注意是 register()
```

- `CurioSlot` 枚举：`HEAD / NECKLACE / BACK / BODY / RING / BELT / CHARM / BRACELET / HANDS / CURIO`
  （`getSlotId()` / `static fromSlotId(String)`，未匹配回 CURIO）。
- `CurioAttributeMod`：record `(attributeId, uuid, amount, operation)` + builder；
  `customizeProperties(Consumer<Item.Properties>)` 可进一步定制物品属性；
  `withItemClass(CurioItemFactory)` 用自定义物品类。
- **客户端接线**：`CurioAPI.setClientBridge(new DefaultCurioClientBridge(回调...))` →
  `bridge.registerAll()`（按 fullName 查 `BuiltInRegistries.ITEM` 后
  `CuriosRendererRegistry.register`）；主模实现在 `CurioClientHandler.java:22-27`。
- 与 `ItemAPI.curioItem` 的分工：后者是**轻量版**（String 槽位 + int operation + 强制
  `pasterdream:` 修饰器前缀）；枚举槽位 / `AttributeModifier.Operation` / 渲染器配置请用 `CurioAPI`。

真实示例：`PDItemsCurios.java:62`（`create("embryo_ring").slot(CurioSlot.RING).register()`）、
`PDItemsCurios.java:73`（`withItemClass(...)` 链）。

### 3.7 SanAPI（理智）

位置：`api.san.*`（6 类，**无 DeferredRegister**）；实现与数值由 PasterDreamSanity 附属提供。

**两层访问，按需选择**：

| 层 | 类 | 特点 |
|---|---|---|
| 读值 / 简单写 | `SanAPI` | 纯附件访问，**无网络同步**（客户端读本地镜像） |
| 服务端改值 | `SanHelper` | 自动 S2C 同步，且带**双闸**：游戏规则 `SAN_CHECK_SYSTEM` **且** `SanConfigRegistry.get().enabled()` 同时开才生效 |

```java
// 读
double san = SanAPI.getSanValue(player);            // null 安全，默认 100
boolean check = SanAPI.isSanCheckEnabled(player);
Optional<SanData> data = SanAPI.getSanData(player);

// 服务端改值（推荐 SanHelper：双闸 + 自动同步）
SanHelper.setPlayerSanWithCheck(player, 100);       // 真实用例 QymArmorItem.java:61
SanHelper.addPlayerSanWithCheck(player, delta);
SanHelper.syncSan((ServerPlayer) player);           // 需要手动补同步时

// SanAPI 直写（无闸门语义，慎用）
SanAPI.setSanValue(player, 50);                     // 自动钳制 0~100
SanAPI.addSanValue(player, delta);
SanAPI.addPlayerSanWithCheck(player, delta, () -> myMasterSwitch);
```

- `SanData`：record `(sanValue, sanCheck)`，范围 0~100，默认 `(100.0, true)`；附件
  `PDPlayerAttachments.PLAYER_SAN`；NBT 键 `pasterdreamsanvalue` / `pasterdreamsancheck`。
- **附属接入共享系统**：实现 `ISanSystemConfig`（`enabled` / `enableLowSanDebuff` /
  `overworldNightLowersSan` / `netherLowersSan` / `endLowersSan` / `rainLowersSan` /
  `thunderLowersSan` / `recoverInterval` / `recoverAmount` / `cheerupThreshold` /
  `tickUpdateInterval`，全部 Supplier 返回），并在 commonSetup 的
  `event.enqueueWork(...)` 中 `SanConfigRegistry.register(config)`——未注册时
  `SanConfigRegistry.get()` 返回全关 EMPTY 兜底。
- 游戏规则（`APISanGameRules`，主模 `PDGameRules` re-export 三条）：`SAN_CHECK_SYSTEM`
  （默认 true）/ `START_SAN_ON_REVIVE`（默认 90）/ `SAN_VARIABILITY_PER_TICK`（默认 5）。
- 真实示例：附属 `PasterDreamSanityMod.java:67-68`（注册配置）、`PDSanityConfig.java:17`
  （实现接口）、`PDSanityHelper.java:60`（tick 环境修饰）；San 增减效果走
  `MobEffectAPI.createEffect(...).harmful().color(...).onApply(...).onRemove(...).build()`
  （附属 `PDSanityEffects` 实例）；主模侧还有 `attachment.PDAttachments` re-export 门面
  （`getSan(Player)` / `addPlayerSanWithCheck(Player, double)` 等）。

### 3.8 MeltDreamEnergyAPI（融梦能量）

位置：`api.meltdream.*`（4 类）；实现与数值由 PasterDreamMeltDream 附属提供。

```java
double energy = MeltDreamEnergyAPI.getEnergy(player);           // null 安全，默认 0
MeltDreamEnergyData data = MeltDreamEnergyAPI.getData(player);
MeltDreamEnergyAPI.setEnergy(player, value);                    // 钳制 0~100 + 自动 S2C
MeltDreamEnergyAPI.addEnergy(player, 0.0025);                   // 饰品 tick 回能（MeltdreamEnergy0RingItem.java:57）
MeltDreamEnergyAPI.addPlayerEnergyWithCheck(player, amount, () -> mySwitch);
boolean ok = MeltDreamEnergyAPI.consumeEnergy(player, cost);    // 免消耗或足额才 true 并扣除（MeltdreamToolHelper.java:43）
MeltDreamEnergyAPI.updateNoNeedConsume(player, true);           // 免消耗计数 ±1
MeltDreamEnergyAPI.setNoNeedConsumeValue(player, n);
```

- `MeltDreamEnergyData`：record `(meltDreamEnergy, noNeedConsume)`，范围 0~100；
  `COMMAND_NO_NEED_CONSUME = 100000000`（命令级免消耗标记）；
  `isNoNeedConsume()` / `isNoNeedConsumeByCommand()` / `withEnergy(...)` / `addEnergy(...)` /
  `withNoNeedConsume(boolean)`；附件 `PDPlayerAttachments.PLAYER_MELTDREAM_ENERGY`；
  S2C 同步 `MeltDreamEnergyPayload`（处理器在主模 `PDNetwork`）。
- **附属接入**：实现 `IMeltDreamEnergySystemConfig`（`enabled` / `recoverInterval` /
  `recoverAmount` / `chestGenerationMultiplier` / `chestHurtMultiplier` /
  `chestKillMultiplier` / `chestMaxEnergy` / `chestCooldownTicks` 默认 12000，全部
  Supplier），在 commonSetup 的 `event.enqueueWork(...)` 中
  `MeltDreamEnergyConfigRegistry.register(config)`（`PDMeltDreamMod.java:67-68`）；
  未注册回退全关 EMPTY。
- 真实示例：`PDMeltDreamEvents.java:50/62`（读配置、周期回能）、`PDMeltDreamConfig.java:17`
  （实现接口）、`MeltdreamToolHelper.java:43`（`consumeEnergy` 扣能量修工具）、
  `MeltdreamChestBlock.java:260`。

### 3.9 SpellAPI（法术，预留契约）

位置：`api.spell.SpellAPI` + `ISpell`（仅 2 类，**纯内存 Map，非 DeferredRegister**）。

```java
public interface ISpell<T extends ISpell<T>> {
    String getSpellName();
    int getCooldown();
    double getCost();
    boolean cast(Level level, Player player, InteractionHand hand);
}

// commonSetup 中注册
SpellAPI.registerSpell(ResourceLocation.fromNamespaceAndPath("mymod", "my_spell"), new MySpell());
Optional<ISpell<?>> spell = SpellAPI.getSpell(id);
boolean has = SpellAPI.hasSpell(id);

// 预施法回调（附属可在此扣 San / 融梦能量）
SpellAPI.registerPreCastCallback(id, player -> SanHelper.addPlayerSanWithCheck(player, -1));
SpellAPI.runPreCastCallbacks(id, player);
```

⚠️ **现状**：全仓无任何调用方——附属 Spells 自有 `SpellItem` / `SpellProjectileEntity` 体系，
尚未接入此契约。属**预留 API**：使用前先与团队确认契约是否已定稿。

### 3.10 BgmAPI（BGM 状态机）

位置：`api.audio.*`（`BgmAPI` / `BiomeMusicTable` / `IMusicEventLookup` / `FadeState` /
`CooldownManager` / `LoopRestartManager`）。**边界**：API 只含状态机与数据表；
播放层（`ModMusicManager` / `CrossfadeManager` / `VolumeSoundInstance`）在主模客户端。

```java
// 群系音乐表（主模 BiomeMusicRegistry.java:12-16 即继承此表）
BiomeMusicTable table = new BiomeMusicTable("mymod");
table.registerBiomeMusic("my_plains", "my_theme");   // 短 id 自动补命名空间
table.registerBiomeMusic(biomeRl, "other_theme");    // 同群系多曲目随机
table.registerCustomDimension(ResourceLocation.fromNamespaceAndPath("mymod", "my_world"));

// 音源注册（ApiSoundRegistry，pasterdream 命名空间，ID 约定 music.<name>）
ApiSoundRegistry.registerDimensionMusic("my_theme");

// 事件查找约定：<ns>:music.<name>（主模 SoundEventLookup 实现 IMusicEventLookup）
```

- `CooldownManager(int switchCooldownTicks)`：`enterCooldown(biomeId, musicName, tick)` /
  `updateCooldown(current, previous, tick) -> boolean`（true = 冷却结束可触发交叉淡化）/
  `setPendingMusicName(String)` / `cancelCooldown()`；`FadeState` 枚举
  `IDLE / CROSSFADE / FADING_OUT`。
- `BgmAPI.setClientBridge(bridge)` + `initClientIfPresent()` 是**可选**客户端钩子——主模
  当前未使用（客户端由 `PDClientEvents` 直接组装），如实说明。
- 真实示例：`ModMusicManager.java:151-161`（填表）、`PDClientEvents.java:190-193`（注册自定义
  维度）、`PDSounds.java:46-49`（音源注册）。

### 3.11 PDAddonConfigRegistry（附属配置注册）

位置：`api.config.PDAddonConfigRegistry`。附属把配置条目登记进 API，主模配置界面
（`PDConfigScreen`）统一展示/保存——避免主模对附属硬编码依赖。无需挂任何注册器。

```java
// ① 附属构造器：注册 TOML 并登记引用（PDMeltDreamMod.java:50-51）
commonModConfig = ConfigTracker.INSTANCE.registerConfig(
        ModConfig.Type.COMMON, MyConfig.SPEC, modContainer, "mymod-common.toml");
PDAddonConfigRegistry.registerCommonConfig("mymod", commonModConfig);

// ② MyConfig 静态块内（define 完 ConfigValue 后即可，早于 registerConfig 也行）：
PDAddonConfigRegistry.registerBooleanEntry("mymod", "my_category", MY_FLAG, "my_flag");
PDAddonConfigRegistry.registerIntegerEntry("mymod", "my_category", MY_COUNT, 0, 100, "my_count");
PDAddonConfigRegistry.registerDoubleEntry("mymod", "my_category", MY_RATE, 0.0, 1.0, "my_rate");
```

- 条目类型：`BOOLEAN` / `INTEGER` / `DOUBLE`（Entry record：modId、categoryKey、类型、
  ConfigValue、min/max、@Nullable translationKey）。
- 翻译键生成 `gui.pasterdream.config.<translationKey>`（为 null 时主模界面按配置 path
  自动生成）。
- 三个附属完全一致地使用此模式（`PDMeltDreamMod.java:50-51` / `PDSanityMod.java:49-50` /
  `PDSpellsMod.java:56-57`，条目登记在各自 Config 类静态块，如 `PDMeltDreamConfig.java:74-81`）。

## 4. 通用坑速查

1. **战利品表目录是单数**：`data/<ns>/loot_table/blocks/<name>.json`（1.21.1）；1.20 的复数
   `loot_tables/` 会被静默忽略——方块不掉落且**无任何报错**。多数注册 API 不自动生成战利品表，
   也无代码兜底，不写就是挖了白挖。改完可用 `tools/verify_resource_closure.py` 校验。
2. **GeckoLib 目录规范**：`entity → entity/`、`block → block/`、`item → item/`
   （`geo/`、`animations/`、`textures/` 下的二级子目录按 `DefaultedGeoModel` 的 subtype 解析）；
   放错目录 = 加载不到且无报错。
3. **registerAll 只属于 PasterDreamAPIMod**：下游重复挂 15 个注册器必炸（见 §1.3）。
4. **玩偶 `.desc` 语言键挂在 `block.` 前缀**：`block.<ns>.<name>.desc`，写成 `item.` 前缀无效。
5. **客户端包边界**：`api.client` 只允许客户端代码引用；公共包禁止反向 `import` `client` 包。
6. **编译验证**：`.\gradlew compileJava`（全模块）/ `.\gradlew :PasterDreamAPI:compileJava` /
   `.\gradlew :PasterDream:compileJava`。
7. **版本以根 `gradle.properties` 为准**（各模块无独立 gradle.properties）：
   MC 1.21.1 / NeoForge 21.1.219 / mod 0.10.0-pre.3 / GeckoLib 4.8.4 / Curios 9.5.1+1.21.1。
8. **共享门面的命名空间是 `pasterdream`**：外部模组经 BlockAPI/ItemAPI 等注册的内容落在
   `pasterdream:` 而非自己的 modid（唯一例外 `FluidTypeAPI` 用 `pasterdreamapi`）；要自有
   命名空间的条目必须自建 DeferredRegister（维度可用 `new DimensionBuilder("mymod", name)`）。

## 5. 相关文档与技能索引

技能包（本目录 `docs/deprecated/skills/`）：

| 技能 | 覆盖域 |
|------|--------|
| `pasterdream-mod-dev` | 通用开发指南（项目结构 / 注册 / 常见崩溃） |
| `pasterdream-entity-api` | 实体注册 |
| `pasterdream-effect-api` | 药水效果 |
| `pasterdream-particle-api` | 粒子 |
| `pasterdream-ruin-api` | 遗迹结构 |
| `pasterdream-vfx-api` | VFX 七子系统 |
| `world-decoration-api` | 世界装饰 |
| `item-migration-api` | 物品迁移 |
| `neoforge-block-drops` | 方块掉落 |
| `pasterdream-api-guide`（本文） | 总览 + 缺失域 |

> ⚠️ 2026-09-26 核签结论：9 个既有技能包均判「部分过时」——横切问题是其中 7 个仍教开发者在
> 主模构造器调用 `registerAll` / `XXAPI.REGISTRY.register`（当前机制下照做即 double-register
> 崩溃，真实机制见 §1.3）；另有 `neoforge-block-drops` 的 `toItem()` 修复代码不可编译、
> `pasterdream-mod-dev` 的 `GeckoLibAnimalEntity` 基类不存在等点状失效。修正落地前，
> 与 §1.3 冲突处以本文为准。

正式文档：

- `docs/架构/API架构详解.md` —— API 模块内部架构（权威）
- `docs/架构/模块边界.md` —— 归属决策 / 双注册器模式
- `docs/参考/API边界.md` —— 上收判定与勿上收清单
- `docs/开发指南/注册指南.md` —— ❌ 核签结论：其方块/物品/维度/遗迹/菜单段的示例签名**大面积
  失效**（`createBlock` / `createItem` 系列、`registerBlockItem`、`registerToTab`、
  `TerrainNegotiation` 等均不存在，`registerAll` 归属也已变更）——请勿参考，以本文件与源码为准
- `docs/教程/KubeJS玩偶.md` —— KubeJS 玩偶完整教程
- `docs/教程/添加方块.md` / `添加实体.md` / `添加物品.md` —— 内容添加流程教程
