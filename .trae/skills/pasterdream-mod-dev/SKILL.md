---
name: "pasterdream-mod-dev"
description: "PasterDream NeoForge 1.21.1 模组开发指南。提供项目结构、注册系统、实体系统、物品系统等的开发规范，以及常见崩溃问题的解决方案。Invoke when developing or modifying PasterDream mod features, creating new items/blocks/entities, fixing crashes, or when needing to understand the mod's architecture."
---

# PasterDream NeoForge 1.21.1 模组开发指南

## 🚨 开发前必读：关键注意事项

### ⚠️ 第一步：确定方块/物品类型

在创建任何方块或物品之前，**必须先确定它属于哪种类型**：

| 类型 | 判断标准 | 核心注意点 |
|------|---------|-----------|
| **普通方块** | 无方向、无特殊功能 | 使用 `registerSimpleBlock()` |
| **方向性方块** | 有 facing 属性 | 必须创建 `HorizontalDirectionalBlock` 子类 |
| **TESR 方块** ⚠️ | 原模组有 TileEntity | 必须替换 `builtin/entity` 模型 |
| **GeckoLib 方块** | 有 .geo.json 模型 | 需要 TileEntity 和特殊渲染器 |

**如何识别 TESR 方块**：
```bash
# 在原模组中查找
ls libs/FixPasterDream-main/src/main/java/net/pasterdream/block/display/
# 或检查 displaysettings
ls libs/FixPasterDream-main/src/main/resources/assets/pasterdream/models/displaysettings/
```

### ⚠️ 第二步：纹理文件用途必须正确

**这是渲染问题的最大根源！**

| 纹理类型 | 路径 | 用途 | 错误后果 |
|---------|------|------|---------|
| **方块纹理** | `textures/block/*.png` | 可平铺的材质 | 用于方块六面贴图 |
| **物品图标** | `textures/item/*.png` | 单个小图标 | 用于背包/手持显示 |
| **精灵表** | `textures/block/*.png` | 多子图大图 | 原模组专用，一般不直接使用 |

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

## 核心理念

1. **不看代码，只看效果**: 参考原模组呈现效果，但不直接复制或修改原代码
2. **重新实现，思路不同**: 相同效果，用不同技术方案
3. **MCreator 代码不可移植**: 原模组是 MCreator 生成，必须重写

## 项目结构

```
NeoPasterDream1/
├── src/main/java/com/pasterdream/pasterdreammod/
│   ├── PasterDreamMod.java          # 主模组类
│   ├── block/                        # 方块类
│   ├── entity/                       # 实体类
│   ├── item/                         # 物品类
│   ├── client/renderer/              # 渲染器
│   └── registry/                     # 注册系统
│       ├── PDBlocks.java             # 方块注册
│       ├── PDItems.java              # 物品注册
│       ├── PDEntities.java           # 实体注册
│       ├── PDBlockEntities.java      # 方块实体注册
│       ├── PDCreativeTabs.java       # 创造模式标签注册
│       ├── PDEffects.java            # 状态效果注册（BUFF/DEBUFF）
│       ├── PDDimensions.java         # 维度注册
│       ├── PDStructures.java         # 结构/遗迹注册
│       ├── PDAdvancements.java       # 成就引用常量
│       └── PDLootTables.java         # 战利品表引用常量
├── src/main/resources/
│   ├── assets/pasterdream/
│   │   ├── textures/                 # 纹理文件
│   │   ├── geo/                      # GeckoLib 模型
│   │   └── animations/               # GeckoLib 动画
│   └── data/pasterdream/
│       ├── advancements/             # 成就 JSON（手动编写）
│       └── loot_tables/              # 战利品表 JSON（手动编写）
│           ├── blocks/
│           ├── entities/
│           └── chests/
└── libs/FixPasterDream-main/         # 原模组（只读参考）
```

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
public static final DeferredHolder<EntityType<?>, EntityType<MyEntity>> MY_ENTITY = 
    ENTITY_TYPES.register("my_entity",
        () -> EntityType.Builder.of(MyEntity::new, MobCategory.CREATURE)
            .sized(0.6F, 1.8F)
            .build("my_entity"));
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

### 3. 实体系统 (GeckoLib)

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
    
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
    }
    
    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(Items.WHEAT_SEEDS);
    }
    
    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return null;
    }
}
```

---

### 4. 状态效果注册（PDEffects）

**使用 `DeferredRegister<MobEffect>` 注册自定义状态效果：**

```java
// 注册器定义
public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(
        Registries.MOB_EFFECT, PasterDreamMod.MOD_ID);

// 注册效果（需先创建 MobEffect 子类）
public static final DeferredHolder<MobEffect, MobEffect> DREAMWISH_BUFF =
        MOB_EFFECTS.register("dreamwish_buff",
                () -> new DreamwishEffect(MobEffectCategory.BENEFICIAL, 0xFF69B4));
```

**状态效果类模板：**
```java
public class DreamwishEffect extends MobEffect {
    public DreamwishEffect(MobEffectCategory category, int color) {
        super(category, color);
    }
    
    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        // 每 tick 效果逻辑
        return super.applyEffectTick(entity, amplifier);
    }
    
    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        // 控制效果触发频率
        return true;
    }
}
```

---

### 5. 维度注册（PDDimensions）

**使用 `DeferredRegister<DimensionType>` 和 `DeferredRegister<LevelStem>` 注册维度：**

```java
// 维度类型注册器
public static final DeferredRegister<DimensionType> DIMENSION_TYPES = DeferredRegister.create(
        Registries.DIMENSION_TYPE, PasterDreamMod.MOD_ID);

// 维度实例注册器
public static final DeferredRegister<LevelStem> LEVEL_STEMS = DeferredRegister.create(
        Registries.LEVEL_STEM, PasterDreamMod.MOD_ID);
```

**维度注册后还需要：**
1. `data/<modid>/dimension/<dimension_name>.json` — 维度 JSON
2. `data/<modid>/dimension_type/<dimension_name>.json` — 维度类型 JSON
3. 对应的生物群系生成器配置

---

### 6. 结构/遗迹注册（PDStructures）

**使用 `DeferredRegister<StructureType<?>>` 注册自定义结构：**

```java
// 结构类型注册器
public static final DeferredRegister<StructureType<?>> STRUCTURE_TYPES = DeferredRegister.create(
        Registries.STRUCTURE_TYPE, PasterDreamMod.MOD_ID);

// 注册结构类型（需先创建 Structure 子类）
public static final DeferredHolder<StructureType<?>, StructureType<MyStructure>> MY_STRUCTURE =
        STRUCTURE_TYPES.register("my_structure",
                () -> () -> MyStructure.CODEC);
```

**结构注册后还需要：**
1. `data/<modid>/worldgen/structure/<name>.json` — 结构配置
2. `data/<modid>/worldgen/structure_set/<name>.json` — 结构集配置
3. `data/<modid>/worldgen/template_pool/<name>.json` — 模板池
4. `data/<modid>/structures/<name>.nbt` — 实际建筑文件

---

### 7. 成就系统（Advances + JSON）

**成就完全通过 JSON 文件定义**，Java 代码中只需定义 `ResourceLocation` 常量便于引用：

```java
public static final ResourceLocation MY_ACHIEVEMENT = ResourceLocation.fromNamespaceAndPath(
        PasterDreamMod.MOD_ID, "story/my_achievement");
```

**JSON 路径：** `data/<modid>/advancements/<category>/<name>.json`

```json
{
    "display": {
        "icon": {"item": "minecraft:diamond"},
        "title": {"translate": "advancement.pasterdream.my_achievement"},
        "description": {"translate": "advancement.pasterdream.my_achievement.desc"},
        "frame": "task",
        "show_toast": true,
        "announce_to_chat": true,
        "hidden": false
    },
    "criteria": {
        "impossible": {"trigger": "minecraft:impossible"}
    }
}
```

---

### 8. 战利品表（Loot Tables + JSON）

**战利品表完全通过 JSON 文件定义**，Java 中定义 `ResourceLocation` 常量便于引用：

```java
public static final ResourceLocation BLOCK_LOOT = ResourceLocation.fromNamespaceAndPath(
        PasterDreamMod.MOD_ID, "blocks/my_block");
```

**JSON 路径：** `data/<modid>/loot_tables/<type>/<name>.json`

```json
{
    "type": "minecraft:block",
    "pools": [
        {
            "rolls": 1,
            "entries": [
                {"type": "minecraft:item", "name": "minecraft:diamond"}
            ],
            "conditions": [
                {"condition": "minecraft:survives_explosion"}
            ]
        }
    ]
}
```

---

### 9. 注册系统汇总表

| 注册类 | 注册器 | 注册内容 | 注册时机 |
|--------|--------|---------|---------|
| `PDBlocks.java` | `DeferredRegister.Blocks` | 方块 | 主构造函数 |
| `PDItems.java` | `DeferredRegister.Items` | 物品 | 主构造函数 |
| `PDEntities.java` | `DeferredRegister<EntityType<?>>` | 实体 | 主构造函数 |
| `PDBlockEntities.java` | `DeferredRegister<BlockEntityType<?>>` | 方块实体 | 主构造函数 |
| `PDCreativeTabs.java` | `DeferredRegister<CreativeModeTab>` | 创造标签 | 主构造函数 |
| `PDEffects.java` | `DeferredRegister<MobEffect>` | 状态效果 | 主构造函数 |
| `PDDimensions.java` | `DeferredRegister<DimensionType>` | 维度类型 | 主构造函数 |
| `PDDimensions.java` | `DeferredRegister<LevelStem>` | 维度实例 | 主构造函数 |
| `PDStructures.java` | `DeferredRegister<StructureType<?>>` | 结构类型 | 主构造函数 |
| `PDAdvancements.java` | 常量类（无注册器） | 成就引用 | 无需注册 |
| `PDLootTables.java` | 常量类（无注册器） | 战利品表引用 | 无需注册 |

---

### 10. BlockAPI — 方块批量注册系统 ⭐

**`BlockAPI`** 是一个 Facade + Builder 模式的方块注册 API，提供三种注册模式，配合 `BlockConfig` 实现**纹理/模型/挖掘标签/交互/动画**一站式配置。

#### 三种注册模式

| 模式 | Builder | 适用场景 | 示例 |
|------|---------|---------|------|
| **模式一** | `SimpleBlockBuilder` | 基础换皮方块 | 染梦木板、染梦玻璃 |
| **模式二** | `VariantSetBuilder` | 建筑变体族 | 楼梯+台阶+墙+栅栏家具套 |
| **模式三** | `BatchBlockBuilder` | 编号同类方块 | 花蕾1~17号、粉丁菇0~3 |

#### BlockConfig 链式配置

`BlockConfig.of()` 提供以下可选配置：

| 方法 | 参数 | 说明 | 对应数据生成器 |
|------|------|------|---------------|
| `.mineable("axe")` | `"axe"`/`"pickaxe"`/`"shovel"`/`"hoe"` | 工具标签 | `PDBlockTagProvider` → `tags/block/mineable/` |
| `.model("cube_all")` | 模型标识 | 方块模型类型 | `PDBlockModelProvider` → `models/block/` + `blockstates/` |
| `.tex("layer", "path")` | 纹理层名+路径 | 纹理映射 | `PDBlockModelProvider` 读取生成 |
| `.interact(handler)` | Lambda 回调 | 右键交互 | 运行时注册（非数据生成） |
| `.animated("geo/...")` | GeckoLib 路径 | 动画支持 | 运行时注册 GeckoLib（待完善） |

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
BlockAPI.registerSimpleBlocks()
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
    .build();

// ===== 模式二：VariantSetBuilder（建筑变体族）=====
// 注册 stair/slab/wall/fence/gate/door/trapdoor/button/pressure_plate
BlockAPI.registerVariantSet("dyedream_planks", Blocks.OAK_PLANKS)
    .build();

// ===== 模式三：BatchBlockBuilder（编号同类）=====
BlockAPI.registerBatchBlocks()
    .add("pinkagaric", 0, 3, Blocks.RED_MUSHROOM)       // pinkagaric_0~3
    .add("dyedream_bud", 0, 2, Blocks.STONE)             // dyedream_bud_0~2
    .build();
```

> **注意**：`PDBlockTagProvider` 和 `PDBlockModelProvider` 会自动读取 `BlockAPI.putConfig()` 存储的配置，
> 运行 `runData` 即可生成对应的 `tags/`、`models/`、`blockstates/` JSON 文件。

#### 数据生成器工作流

1. 在 `PDBlocks.java` 中用 `BlockAPI` + `BlockConfig` 注册方块
2. 运行 `.\gradlew runData` 生成标签 + 模型 JSON
3. 生成的资源在 `src/generated/resources/` 目录
4. 启动游戏验证工具图标和模型显示

> ⚠️ **当前状态**：`PDBlockTagProvider` 和 `PDBlockModelProvider` 已完成并注册到 `PasterDreamMod.gatherData()`。
> 但 `compileJava` 被 EntityAPI/ParticleAPI 中的前置错误阻塞（与 BlockAPI 无关），
> 修复后方可运行 `runData`。

#### 交互与动画（预留）

`BlockConfig` 已预留 `.interact()` 和 `.animated()` 支持位，但目前的 `SimpleBlockBuilder` 只注册 `SelfDropBlock`（纯换皮方块），
不会自动创建带交互或动画的自定义方块类。如需交互/动画，当前仍需手写 Block 子类 + BlockEntity。