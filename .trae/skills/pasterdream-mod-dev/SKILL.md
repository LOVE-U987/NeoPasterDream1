---
name: "pasterdream-mod-dev"
description: "PasterDream NeoForge 1.21.1 模组开发指南。提供多模块项目结构、注册系统、BlockAPI、实体系统、物品系统等的开发规范，以及常见崩溃问题的解决方案。Invoke when developing or modifying PasterDream mod features, creating new items/blocks/entities, fixing crashes, or when needing to understand the mod's architecture."
---

# PasterDream NeoForge 1.21.1 模组开发指南

## 🚨 开发前必读：关键注意事项

### ⚠️ 第一步：确定方块/物品类型

在创建任何方块或物品之前，**必须先确定它属于哪种类型**：

| 类型 | 判断标准 | 核心注意点 |
|------|---------|-----------|
| **普通方块** | 无方向、无特殊功能 | 使用 `registerSimpleBlock()` 或 BlockAPI |
| **方向性方块** | 有 facing 属性 | 必须创建 `HorizontalDirectionalBlock` 子类 |
| **TESR 方块** ⚠️ | 原模组有 TileEntity | 必须替换 `builtin/entity` 模型 |
| **GeckoLib 方块** | 有 .geo.json 模型 | 需要 TileEntity 和特殊渲染器 |

**如何识别 TESR 方块**：
```bash
# 在原模组中查找
ls libs/FixPasterDream-main/src/main/java/net/pasterdream/block/display/
```

### ⚠️ 第二步：纹理文件用途必须正确

| 纹理类型 | 路径 | 用途 | 错误后果 |
|---------|------|------|---------|
| **方块纹理** | `textures/block/*.png` | 可平铺的材质 | 用于方块六面贴图 |
| **物品图标** | `textures/item/*.png` | 单个小图标 | 用于背包/手持显示 |

**❌ 典型错误**：把物品图标当方块纹理用 → 显示为"展开图"

### ⚠️ 第三步：模型 Parent 必须正确

| 方块类型 | 正确的 Parent | 错误后果 |
|---------|--------------|---------|
| 普通方块 | `block/cube` 或 `block/cube_all` | 紫黑错误纹理 |
| 原 TESR 方块 | `block/cube_all`（简化版）| 透明/紫黑 |
| Item 模型 | `item/generated` | 创造模式透明 |

**❌ 绝对不要**：使用 `builtin/entity`（除非有 TileEntity 渲染器）

---

## 项目概述

**PasterDream** 是一个从 1.20.1 Forge 移植到 1.21.1 NeoForge 的模组，核心理念是"精神续作，而非代码移植"。

- **版本**: Minecraft 1.21.1 | NeoForge 21.1.219 | GeckoLib 4.7.3 | Java 21
- **项目路径**: `c:\Users\97128\Documents\GitHub\NeoPasterDream1`
- **原模组参考**: `libs/FixPasterDream-main/` (只读)

## 多模块项目结构

```
NeoPasterDream1/
├── PasterDreamAPI/                     # API 模块（独立前置 modid: pasterdreamapi）
│   └── src/main/java/com/pasterdream/pasterdreammod/api/
│       ├── block/          # BlockAPI + SimpleBlockBuilder/VariantSetBuilder/BatchBlockBuilder
│       ├── blockentity/    # BlockEntityAPI + BlockEntityBuilder
│       ├── item/           # ItemAPI + 4 个 Builder + Spec 模型
│       ├── entity/         # EntityAPI + EntityBuilder + skill/tag/anim
│       ├── particle/       # ParticleAPI + ParticleBuilder
│       ├── effect/         # MobEffectAPI + MobEffectBuilder
│       ├── dimension/      # DimensionAPI + DimensionBuilder + terrain/
│       ├── ruin/           # RuinAPI + RuinBuilder + StructureSetBuilder
│       ├── menu/           # MenuAPI + MenuBuilder
│       ├── fluid/          # FluidAPI + FluidTypeAPI + FluidBuilder
│       ├── curio/          # CurioAPI + CurioBuilder
│       ├── audio/          # BgmAPI（背景音乐系统）
│       ├── san/            # SanAPI（理智系统）
│       ├── meltdream/      # MeltDreamEnergyAPI（融梦能量系统）
│       ├── spell/          # SpellAPI + ISpell
│       ├── worldgen/decor/ # DecorationBuilder + TreePlacerAPI + TreeRegistry
│       ├── attachment/     # PDPlayerAttachments
│       ├── attribute/      # APIAttributes
│       ├── PasterDreamAPI.java   # MOD_ID / DATA_NAMESPACE / registerAll() 统一注册入口
│       └── PasterDreamAPIMod.java
├── PasterDream/                        # 主模块（业务实现）
│   └── src/main/java/com/pasterdream/pasterdreammod/
│       ├── PasterDreamMod.java         # 主模组类（构造函数调用 registerAll）
│       ├── block/                      # 方块类（含 entity/ 子目录）
│       ├── entity/                     # 实体类
│       ├── item/                       # 物品类
│       ├── client/                     # 渲染器/粒子/GUI/HUD/音频
│       ├── registry/                   # ★ 注册系统（按类别拆分）
│       │   ├── PDBlocks.java           # 方块注册总入口
│       │   ├── blocks/PDBlocks*.java   # 方块分目录（Simple/Custom/Functional/...）
│       │   ├── PDItems.java            # 物品注册总入口
│       │   ├── items/PDItems*.java     # 物品分目录（Materials/Foods/Tools/...）
│       │   ├── PDEntities.java         # 实体注册
│       │   ├── PDBlockEntities.java    # 方块实体注册
│       │   ├── PDCreativeTabs.java     # 创造标签注册总入口
│       │   ├── creativetabs/PDCreativeTabs*.java  # 标签分目录
│       │   ├── PDEffects.java          # 状态效果注册
│       │   ├── PDParticles.java        # 粒子注册
│       │   ├── PDDimensions.java       # 维度注册
│       │   ├── PDRuinsRegistration.java # 遗迹注册
│       │   ├── PDFeatures.java / PDPlacedFeatures.java # 地物
│       │   ├── ModDecorations.java     # 装饰物（云团等，跨群系）
│       │   ├── IceDecorations.java     # 冰雪群系装饰物
│       │   ├── OceanDecorations.java   # 海洋群系装饰物
│       │   ├── PDSounds.java / PDPotions.java / PDMenus*.java
│       │   └── ... 等
│       └── worldgen/                   # 树/地物/自定义生成器
└── libs/FixPasterDream-main/           # 原模组（只读参考）
```

### 模块归属决策（口诀）

> **API/Builder/注册门面 → API 模块；方块/物品/实体/渲染 → 主模块。**
> 详见 Skill「api-split-multi-module」。

### PasterDreamAPI 统一注册入口

主模组构造函数开头调用一次，替代所有 API 的单独注册：

```java
public PasterDreamMod(IEventBus modEventBus, ModContainer modContainer) {
    // 统一注册所有 API 的 DeferredRegister（13 个注册器）
    PasterDreamAPI.registerAll(modEventBus);
    // ... 其他初始化
}
```

> `PasterDreamAPI.DATA_NAMESPACE = "pasterdream"`（沿用旧数据命名空间），`MOD_ID = "pasterdreamapi"`。

---

## 🔥 关键开发规范（必看）

### 1. 注册系统 (DeferredRegister)

**必须使用 `DeferredRegister` 模式进行注册：**

```java
// 方块注册
public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MOD_ID);
public static final DeferredBlock<Block> MY_BLOCK = BLOCKS.registerSimpleBlock("my_block",
    BlockBehaviour.Properties.ofFullCopy(Blocks.STONE));

// 方向性方块必须用 registerBlock
public static final DeferredBlock<Block> MY_DIRECTIONAL_BLOCK = BLOCKS.registerBlock("my_block",
    MyDirectionalBlock::new,  // 传入方块类构造器
    BlockBehaviour.Properties.of()...);

// 物品注册
public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MOD_ID);
public static final DeferredItem<Item> MY_ITEM = ITEMS.registerSimpleItem("my_item",
    new Item.Properties());

// 实体注册
public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(
    BuiltInRegistries.ENTITY_TYPE, MOD_ID);
```

### 2. HorizontalDirectionalBlock 模板

**任何有 facing 属性的方块都必须使用此模板：**

```java
public class MyDirectionalBlock extends HorizontalDirectionalBlock {
    public static final MapCodec<MyDirectionalBlock> CODEC = simpleCodec(MyDirectionalBlock::new);
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 16, 16);

    public MyDirectionalBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() { return CODEC; }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
    }
}
```

---

### 3. BlockAPI — 方块批量注册系统 ⭐

**`BlockAPI`** 是 Facade + Builder 模式的方块注册 API（位于 PasterDreamAPI 模块），提供三种注册模式，配合 `BlockConfig` 实现**纹理/模型/挖掘标签/交互/动画**一站式配置。

#### 三种注册模式（方法名以最新代码为准！）

| 模式 | 工厂方法 | Builder | 适用场景 | 示例 |
|------|---------|---------|---------|------|
| **模式一** | `registerSimpleBlocks()` | `SimpleBlockBuilder` | 基础换皮方块 | 染梦木板、染梦玻璃 |
| **模式二** | `createVariantSet(name, block)` | `VariantSetBuilder` | 建筑变体族 | 楼梯+台阶+墙+栅栏家具套 |
| **模式三** | `batchRegister(name)` | `BatchBlockBuilder` | 编号同类方块 | 花蕾1~17号、粉丁菇0~3 |

> ⚠️ **注意**：旧方法名 `registerVariantSet` / `registerBatchBlocks` 已**不存在**，请使用 `createVariantSet` / `batchRegister`。

#### BlockConfig 链式配置

`BlockConfig.of()` 提供以下可选配置：

| 方法 | 参数 | 说明 | 对应数据生成器 |
|------|------|------|---------------|
| `.mineable("axe")` | `"axe"`/`"pickaxe"`/`"shovel"`/`"hoe"` | 工具标签 | `PDBlockTagProvider` → `tags/block/mineable/` |
| `.model("cube_all")` | 模型标识 | 方块模型类型 | `PDBlockModelProvider` → `models/block/` + `blockstates/` |
| `.tex("layer", "path")` | 纹理层名+路径 | 纹理映射 | `PDBlockModelProvider` 读取生成 |
| `.renderType("translucent")` | 渲染类型 | 玻璃/冰等透明方块 | 运行时渲染类型 |
| `.interact(handler)` | Lambda 回调 | 右键交互 | 运行时注册 |
| `.animated("geo/...")` | GeckoLib 路径 | 动画支持 | 运行时注册 GeckoLib |
| `.blockFactory(BlockFactory)` | `(Properties) -> Block` | 自定义方块类（如 `GlassBlock::new`） | 覆盖默认 `SelfDropBlock::new` |
| `.plantable()` | - | 可种植地面，自动加 `pasterdream:plantable_on` 标签 | 标签生成 |

**支持模型类型：**

| `model()` 参数 | 说明 | 需要 `tex()` 的层 |
|---------------|------|------------------|
| `"cube_all"` | 六面相同纹理 | `"all"` |
| `"cube_column"` | 柱状（侧面+顶底） | `"side"`, `"end"` |
| `"cube_top_bottom"` | 顶底不同 | `"top"`, `"side"`, `"bottom"` |
| `"cube_6"` | 六面不同 | `"north"`, `"south"`, `"east"`, `"west"`, `"up"`, `"down"` |

#### 完整使用示例

```java
// ===== 模式一：SimpleBlockBuilder（换皮方块）=====
Map<String, DeferredBlock<Block>> blocks = BlockAPI.registerSimpleBlocks()
    .add("dyedream_dirt", Blocks.DIRT)                                 // 无配置，纯换皮
    .add("dyedream_planks", Blocks.OAK_PLANKS, BlockConfig.of()        // 带配置
        .mineable("axe")                                               // → 自动生成斧头标签
        .model("cube_all")                                             // → 自动生成模型 JSON
        .tex("all", "pasterdream:block/dyedream_planks")              // → 纹理引用
    )
    .add("dyedream_log", Blocks.OAK_LOG, BlockConfig.of()
        .mineable("axe")
        .model("cube_column")
        .tex("end", "pasterdream:block/dyedream_log_top")
        .tex("side", "pasterdream:block/dyedream_log_side")
    )
    .addCustom("chiseled_dyedreamquartz_block",
        BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).lightLevel(s -> 10))  // 自定义属性
    .build();   // 返回 Map<String, DeferredBlock<Block>>

// ===== 模式二：VariantSetBuilder（建筑变体族）=====
BlockAPI.createVariantSet("dyedream_planks", Blocks.OAK_PLANKS)
    .mineable("axe")                                      // 所有变体自动 axe 标签 ✨
    .withStairs()
    .withSlab()
    .withFence()
    .withFenceGate(WoodType.OAK)
    .build();

// ===== 模式三：BatchBlockBuilder（编号同类）=====
BlockAPI.batchRegister("pinkagaric")
    .indexList(0, 1, 2, 3)                                // 生成 pinkagaric_0~3
    .factory(index -> new PinkagaricBlock(flowerProps()))  // 按编号工厂创建
    .build();
```

> **注意**：`PDBlockTagProvider` 和 `PDBlockModelProvider` 会自动读取 `BlockAPI.putConfig()` 存储的配置，
> 运行 `runData` 即可生成对应的 `tags/`、`models/`、`blockstates/` JSON 文件。

#### BlockAPI.putConfig() — 手动注册的方块配置

对于**不通过 Builder** 注册的方块，必须在 `PDBlocks.java` 的 `static {}` 块中手动调用：

```java
static {
    // Phase 1 移植方块
    BlockAPI.putConfig("titanium_block", BlockConfig.of().mineable("pickaxe"));
    BlockAPI.putConfig("dream_accumulator", BlockConfig.of().mineable("pickaxe"));
}
```

> ⚠️ **必须调用**：未调 `putConfig` 的方块不会被标签生成器识别，导致 Jade 不显示工具图标、`requiresCorrectToolForDrops()` 无法正常工作。
> **已覆盖的（无需重复添加）**：SimpleBlockBuilder / VariantSetBuilder / BatchBlockBuilder 的 build() 自动调用 ✅

#### SelfDropBlock — 掉落物混合策略

通过 `SimpleBlockBuilder.add()` 注册的方块使用 `SelfDropBlock`（`api/block/SelfDropBlock.java`），
其 `getDrops()` 采用**"战利品表优先，空则回退自掉落"**的混合策略：

```java
@Override
public List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
    List<ItemStack> drops = super.getDrops(state, params);  // 优先战利品表
    if (drops.isEmpty()) {
        return List.of(new ItemStack(this));                 // 空则自掉落
    }
    return drops;
}
```

- **有战利品表的方块（矿石等）** → 使用战利品表（精准采集+时运）✅
- **无战利品表的方块（装饰方块）** → 回退为掉落自身 ✅
- **手动注册的方块** → 需要自己在 Java 中 override `getDrops()` 或创建战利品表 JSON

#### 方块掉落物完整性检查

| 注册方式 | 掉落机制 | 校验要点 |
|---------|---------|---------|
| `SimpleBlockBuilder.add()` | `SelfDropBlock` 混合策略 | ✅ 自动，无需额外操作 |
| `VariantSetBuilder` + `.mineable()` | 战利品表 JSON | ✅ Builder 处理标签，需手动批量生成战利品表 |
| `BatchBlockBuilder` + `.factory()` | 自定义类 getDrops() | ⚠️ 确保工厂类有 `getDrops()` |
| 手动 `registerBlock(Block::new)` | 需要战利品表 JSON | ⚠️ 必须创建对应 loot_table |
| 手动 `registerSimpleBlock()` | 需要战利品表 JSON | ⚠️ 必须创建对应 loot_table |

**自掉落战利品表模板**（路径 `data/pasterdream/loot_table/blocks/<block_id>.json`，**1.21 单数路径！**）：
```json
{
  "type": "minecraft:block",
  "pools": [
    {
      "bonus_rolls": 0.0,
      "conditions": [{"condition": "minecraft:survives_explosion"}],
      "entries": [{"type": "minecraft:item", "name": "pasterdream:<block_id>"}],
      "rolls": 1.0
    }
  ]
}
```

---

### 4. 实体系统（EntityAPI + GeckoLib）

**动物实体继承 `GeckoLibAnimalEntity`：**

```java
public class PinkChickenEntity extends GeckoLibAnimalEntity {

    public PinkChickenEntity(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createLivingAttributes()
            .add(Attributes.MAX_HEALTH, 4.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.25D);
    }
    // ...
}
```

**推荐使用 EntityAPI 注册**（详见 Skill「pasterdream-entity-api」）：

```java
EntityResult<ShadowGolemEntity> golem = EntityAPI.createEntity("shadow_golem")
    .category(MobCategory.MONSTER)
    .size(2.2f, 3.5f)
    .entityClass(ShadowGolemEntity.class)
    .attributes(ShadowGolemEntity::createAttributes)
    .skill(EntitySkill.builder("roar")
        .animationName("roar").damage(12.0f).range(5.0f).cooldownTicks(200).build())
    .spawnEgg(0x2C2C2C, 0x6B3FAF)
    .build();
```

---

### 5. 状态效果注册（MobEffectAPI）

**推荐使用 MobEffectAPI**（详见 Skill「pasterdream-effect-api」）：

```java
MobEffectResult dreamwish = MobEffectAPI.createEffect("dreamwish_buff")
    .beneficial()
    .color(0xFF69B4)
    .build();
```

---

### 6. 维度注册（DimensionAPI）

**推荐使用 DimensionAPI**（详见 Skill「pasterdream-dimension-api」）：

```java
DimensionResult dyedreamWorld = DimensionAPI.createDimension("dyedream_world")
    .natural()
    .hasSkylight()
    .withAmbientLight(0.5)
    .minY(-64).height(384)
    .withDefaultBlock("minecraft:calcite")
    .withNoiseSettings("pasterdream:dyedream_world")
    .build();
```

维度 JSON 文件位于 `data/pasterdream/dimension/` 和 `data/pasterdream/dimension_type/`。

---

### 7. 遗迹注册（RuinAPI）

**推荐使用 RuinAPI**（详见 Skill「pasterdream-ruin-api」）。

---

### 8. 成就系统（Advances + JSON）

**成就完全通过 JSON 文件定义**，Java 代码中只需定义 `ResourceLocation` 常量便于引用：

```java
public static final ResourceLocation MY_ACHIEVEMENT = ResourceLocation.fromNamespaceAndPath(
        PasterDreamMod.MOD_ID, "story/my_achievement");
```

**JSON 路径：** `data/pasterdream/advancement/<name>.json`（1.21 单数路径！）

---

### 9. 战利品表（Loot Tables + JSON）

**JSON 路径：** `data/pasterdream/loot_table/<type>/<name>.json`（**1.21 单数 `loot_table`，不是 `loot_tables`！**）

---

### 10. 注册系统汇总表

| 注册类 | 注册器 | 注册内容 | 注册时机 |
|--------|--------|---------|---------|
| `registry/PDBlocks.java` | `DeferredRegister.Blocks` | 方块（含分目录 `blocks/`） | 主构造函数 |
| `registry/PDItems.java` | `DeferredRegister.Items` | 物品（含分目录 `items/`） | 主构造函数 |
| `registry/PDEntities.java` | `DeferredRegister<EntityType<?>>` | 实体 | 主构造函数 |
| `registry/PDBlockEntities.java` | `DeferredRegister<BlockEntityType<?>>` | 方块实体 | 主构造函数 |
| `registry/PDCreativeTabs.java` | `DeferredRegister<CreativeModeTab>` | 创造标签（含分目录 `creativetabs/`） | 主构造函数 |
| `registry/PDEffects.java` | `DeferredRegister<MobEffect>` | 状态效果 | 主构造函数 |
| `registry/PDDimensions.java` | DimensionAPI | 维度 | 主构造函数 |
| `registry/PDRuinsRegistration.java` | RuinAPI | 结构类型 | 主构造函数 |
| `registry/PDParticles.java` | ParticleAPI | 粒子 | 主构造函数 |
| 各 API 模块 | `PasterDreamAPI.registerAll()` | 所有 API 注册器统一挂总线 | 主构造函数开头 |

---

## 🌍 1.21 数据目录命名（反复踩坑警告 ⚠️）

| 功能 | ❌ 旧路径（1.20） | ✅ 新路径（1.21） |
|------|-----------------|-----------------|
| 战利品表 | `loot_tables/` | `loot_table/` |
| 配方 | `recipes/` | `recipe/` |
| 结构模板 | `structures/` | `structure/` |
| 成就 | `advancements/` | `advancement/` |
| 挖掘标签 | `tags/blocks/` | `tags/block/` |

**配方 JSON 格式（1.21 变更）**：
```json
{
  "result": {
    "id": "pasterdream:xxx",     // ✅ 1.21 用 id，不用 item
    "count": 1
  }
}
```

**`category` 字段有效值**：`building`、`redstone`、`equipment`、`misc`、`food`

---

## 🔧 常用开发工作流

```bash
.\gradlew compileJava    # 编译两个模块（自动）
.\gradlew runData        # 运行数据生成器（标签/模型/blockstate）
.\gradlew runClient      # 启动游戏
.\gradlew runServer      # 启动服务器
```

---

## 引用文件

- [PasterDreamAPI.java](file:///C:/Users/97128/Documents/GitHub/NeoPasterDream1/PasterDreamAPI/src/main/java/com/pasterdream/pasterdreammod/api/PasterDreamAPI.java) — API 模块常量与统一注册入口
- [PasterDreamMod.java](file:///C:/Users/97128/Documents/GitHub/NeoPasterDream1/PasterDream/src/main/java/com/pasterdream/pasterdreammod/PasterDreamMod.java) — 主模组类
- [BlockAPI.java](file:///C:/Users/97128/Documents/GitHub/NeoPasterDream1/PasterDreamAPI/src/main/java/com/pasterdream/pasterdreammod/api/block/BlockAPI.java) — 方块注册门面
- [BlockConfig.java](file:///C:/Users/97128/Documents/GitHub/NeoPasterDream1/PasterDreamAPI/src/main/java/com/pasterdream/pasterdreammod/api/block/BlockConfig.java) — 方块配置
- [SimpleBlockBuilder.java](file:///C:/Users/97128/Documents/GitHub/NeoPasterDream1/PasterDreamAPI/src/main/java/com/pasterdream/pasterdreammod/api/block/builder/SimpleBlockBuilder.java) — 模式一 Builder
- [SelfDropBlock.java](file:///C:/Users/97128/Documents/GitHub/NeoPasterDream1/PasterDreamAPI/src/main/java/com/pasterdream/pasterdreammod/api/block/SelfDropBlock.java) — 混合掉落策略方块
- [PDBlocks.java](file:///C:/Users/97128/Documents/GitHub/NeoPasterDream1/PasterDream/src/main/java/com/pasterdream/pasterdreammod/registry/PDBlocks.java) — 方块注册入口
- [PDItems.java](file:///C:/Users/97128/Documents/GitHub/NeoPasterDream1/PasterDream/src/main/java/com/pasterdream/pasterdreammod/registry/PDItems.java) — 物品注册入口
- [PDEntities.java](file:///C:/Users/97128/Documents/GitHub/NeoPasterDream1/PasterDream/src/main/java/com/pasterdream/pasterdreammod/registry/PDEntities.java) — 实体注册入口
