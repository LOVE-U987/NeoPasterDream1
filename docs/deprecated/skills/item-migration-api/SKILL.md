---
name: "item-migration-api"
description: "PasterDream 物品注册/移植 API —— 门面类 ItemAPI（原 ItemMigrationAPI），提供 Builder 模式注册简单材料、食物、工具、饰品（Curio），批量注册、迁移追踪、语言文件生成等一站式工具。Invoke when needing to register new items in PDItems.java / PDItems*.java, batch-create items with consistent patterns, track migration progress, or generate lang file entries."
---

# 🎯 PasterDream 物品 API (ItemAPI) 使用指南

> ⚠️ **命名变更**：原 `ItemMigrationAPI` 已重命名为 **`ItemAPI`**，包路径由 `api/itemmigration/` 迁移至 **`api/item/`**。旧 API 中的重型生成器（RecipeGenerator / LootTableGenerator / BlockDataGenerator / CreativeTabHelper / ImportHelper）**已全部移除**，本指南以最新代码为准。

## 🎯 适用范围

| 适用场景 | 不适用场景 |
|---------|-----------|
| 注册简单材料、合成组件 | 复杂方块行为逻辑（请使用 BlockAPI / PDBlocks） |
| 注册食物物品 | 需要 GeckoLib 3D 动画渲染的物品（请使用 ItemAPI.registerCustom + 自定义 Item 子类） |
| 注册工具/武器（剑镐斧锹锄锤杖） | 盔甲（未内置 ArmorBuilder，可扩展 ToolSpec 或手写） |
| 注册 Curio 饰品（戒指/项链/腰带等） | 需要复杂交互逻辑的物品（请用 registerCustom） |
| 批量注册具有相似属性的物品 | 配方/战利品表 JSON（请手动写 JSON 或手写生成器） |
| 追踪移植进度、生成迁移报告 | — |
| 生成语言文件条目 | — |

## 📦 API 包结构（最新路径）

```
PasterDreamAPI/src/main/java/com/pasterdream/pasterdreammod/api/item/
├── ItemAPI.java                      # ★ 门面类（Facade）
├── builder/
│   ├── BaseItemBuilder.java          #   抽象基类（stacksTo/rarity/fireResistant/tooltip）
│   ├── SimpleItemBuilder.java        #   简单材料物品构建器
│   ├── FoodItemBuilder.java          #   食物物品构建器
│   ├── ToolItemBuilder.java          #   工具/武器物品构建器
│   └── CurioItemBuilder.java         #   Curio 饰品物品构建器
├── manager/
│   ├── ItemManager.java              #   迁移管理器
│   └── MigrationReport.java          #   迁移报告生成
└── model/                            # 数据模型（Record）
    ├── ItemSpec.java                 #   物品基础属性规范（含 Builder）
    ├── FoodSpec.java                 #   食物属性规范（含 FoodEffectSpec）
    ├── ToolSpec.java                 #   工具属性规范（含 ToolType/IngredientSupplier）
    ├── CurioSpec.java                #   饰品属性规范
    ├── AttributeModSpec.java         #   属性修饰器规范
    └── MigrationCategory.java        #   迁移分类枚举（11 类）
```

## 🚀 快速开始

### 1. 注册一个简单材料

```java
ItemAPI.simpleItem("titanium_ingot")
    .rarity(Rarity.UNCOMMON)
    .stacksTo(64)
    .build();
```

### 2. 注册一个食物

```java
ItemAPI.foodItem("apple_juice")
    .nutrition(4).saturationModifier(0.2f)
    .alwaysEdible()
    .effect("minecraft:regeneration", 100, 0, 1.0f)
    .build();
```

### 3. 注册一个工具/武器

```java
ItemAPI.toolItem("copper_sword")
    .type(ToolSpec.ToolType.SWORD).durability(250)
    .attackDamage(6.0f).attackSpeed(-2.4f)
    .build();
```

### 4. 注册一个 Curio 饰品

```java
ItemAPI.curioItem("embryo_ring")
    .slot("ring")
    .attribute("minecraft:generic.attack_damage",
        "a1b2c3d4-e5f6-7890-abcd-ef1234567890", 2.0, 0)
    .tooltip("§7胚胎之戒", "§e可升级为更强大的戒指")
    .build();
```

### 5. 注册自定义复杂物品（降级方案）

```java
ItemAPI.registerCustom("magic_stone",
    () -> new MagicStoneItem(new Item.Properties()));
```

## 🔗 注册器与前置条件

`ItemAPI.REGISTRY` 是独立于 `PDItems.ITEMS` 的 `DeferredRegister.Items`（同 modid `pasterdream`，可并存）。**不需要手动 `.register(modEventBus)`** —— 已由 `PasterDreamAPI.registerAll(modEventBus)` 统一注册。

```java
// 在 PasterDreamMod 构造函数中（可选，registerAll 已包含）
PasterDreamAPI.registerAll(modEventBus);
```

> ⚠️ 注意：新物品应优先走 `PDItems` 系列注册类（`registry/items/PDItems*.java`）或本 API，不要重复注册同一注册名。

## 🧩 各 Builder 详细说明

### BaseItemBuilder（所有 Builder 的公共基类）

| 方法 | 参数 | 说明 |
|------|------|------|
| `stacksTo(int)` | 1~99 | 最大堆叠数（默认 64） |
| `rarity(Rarity)` | 稀有度 | 默认 COMMON |
| `fireResistant()` | - | 防火（不惧熔岩/火焰） |
| `tooltip(String...)` | 描述行 | 支持 § 颜色代码；`tooltip.` 前缀走 translatable，其余走 literal |

### SimpleItemBuilder

| 方法 | 参数 | 说明 |
|------|------|------|
| `build()` | - | 执行注册，返回 `DeferredItem<Item>` |
| `static create(registry, name)` | - | 静态工厂：默认属性快速注册 |

### FoodItemBuilder

| 方法 | 参数 | 说明 |
|------|------|------|
| `nutrition(int)` | 营养值 | 半鸡腿数 |
| `saturationModifier(float)` | 饱和度系数 | 值越高回饱越多 |
| `alwaysEdible()` | - | 饱腹也可食用 |
| `fastFood()` | - | 快速食用（不播进食动画） |
| `effect(String, int, int, float)` | effectId, duration, amplifier, probability | 食用后状态效果 |
| `static create(registry, name, nutrition, saturation)` | - | 静态工厂 |

### ToolItemBuilder

| 方法 | 参数 | 说明 |
|------|------|------|
| `type(ToolType)` | 工具类型 | SWORD/PICKAXE/AXE/SHOVEL/HOE/HAMMER/WAND |
| `durability(int)` | 耐久度 | 默认 250 |
| `miningSpeed(float)` | 挖掘速度 | 默认 2.0 |
| `attackDamage(float)` | 攻击伤害加成 | 默认 1.0（不含类型固有默认伤害） |
| `attackSpeed(float)` | 攻击速度 | 默认 -2.4 |
| `enchantment(int)` | 附魔能力 | 默认 5 |
| `incorrectTag(String)` | 不适用标签 | 默认 `minecraft:incorrect_for_wooden_tool` |
| `repairWith(Supplier<ItemStack> / ItemStack...)` | 修复材料 | 铁砧修复用（建议用 Supplier 版避免 DeferredItem 未绑定） |
| `static create(registry, name, type)` | - | 静态工厂 |

**ToolType 说明：**

| 类型 | 实际基类 | 说明 |
|------|---------|------|
| SWORD | SwordItem | 剑 |
| PICKAXE | PickaxeItem | 镐 |
| AXE | AxeItem | 斧 |
| SHOVEL | ShovelItem | 锹 |
| HOE | HoeItem | 锄 |
| HAMMER | PickaxeItem | 锤（暂用镐基类） |
| WAND | Item | 法杖（暂用普通物品） |

### CurioItemBuilder

| 方法 | 参数 | 说明 |
|------|------|------|
| `slot(String)` | 槽位 | ring/necklace/belt/charm/head/back/curio（默认 ring） |
| `attribute(String, String, double, int)` | attrName, uuid, amount, operation | 属性修饰器；operation: 0=加法 1=MULTIPLY_BASE 2=MULTIPLY_TOTAL |
| `static create(registry, name, slot)` | - | 静态工厂 |

**Curio 槽位参考：**

| 槽位名 | 说明 | 原模组示例 |
|--------|------|-----------|
| `ring` | 戒指 | EmbryoRing, RedDewRing |
| `necklace` | 项链 | EmbryoNecklace, RabbitNecklace |
| `belt` | 腰带 | EmbryoBelt, NatureBelt |
| `charm` | 护身符 | CarapaxCharm, SeaCharm |
| `head` | 头部 | GhostFaceHead |
| `back` | 背部 | AngelWing, WindKnightFlag |
| `curio` | 通用 | 其他饰品 |

## 📦 Spec 数据模型（Record + Builder）

### ItemSpec

```java
ItemSpec spec = ItemSpec.builder("soul_dust")
    .stackSize(64)
    .rarity(Rarity.UNCOMMON)
    .fireResistant(true)
    .tooltipLines(List.of("§7灵魂之尘"))
    .translationKey("item.pasterdream.soul_dust")
    .build();
```

组件：`registryName()`、`stackSize()`、`rarity()`、`fireResistant()`、`tooltipLines()`、`translationKey()`

### FoodSpec / FoodEffectSpec

```java
FoodSpec food = FoodSpec.builder(4, 0.2f)      // nutrition, saturationModifier 必填
    .alwaysEdible(true)
    .fastFood(false)
    .effects(List.of(
        FoodEffectSpec.builder("minecraft:regeneration", 100, 0).probability(1.0f).build()))
    .build();
```

### ToolSpec

```java
ToolSpec tool = ToolSpec.builder(ToolSpec.ToolType.SWORD)  // type 必填
    .durability(1725)
    .miningSpeed(2.0f)
    .attackDamage(8.0f)
    .attackSpeed(-2.4f)
    .enchantmentValue(15)
    .incorrectTag("minecraft:incorrect_for_wooden_tool")
    .repairIngredient(() -> Ingredient.of(Items.STICK))
    .build();
```

> `ToolType` 枚举定义在 `ToolSpec` 内部：`SWORD/PICKAXE/AXE/SHOVEL/HOE/HAMMER/WAND`。

### CurioSpec / AttributeModSpec

```java
CurioSpec curio = CurioSpec.builder("ring")  // curioSlot 必填
    .attributeMods(List.of(
        new AttributeModSpec("minecraft:generic.attack_damage", "a1b2...", 2.0, 0)))
    .build();
```

## 📊 批量注册

```java
// 批量简单材料（ItemSpec...）
List<DeferredItem<Item>> ingots = ItemAPI.batchSimpleItems(
    ItemSpec.builder("soul_dust").build(),
    ItemSpec.builder("soul_essence").rarity(Rarity.UNCOMMON).build(),
    ItemSpec.builder("magic_crystal").rarity(Rarity.RARE).build()
);

// 批量食物（Map<String, FoodSpec>）
ItemAPI.batchFoodItems(Map.of(
    "apple_juice", FoodSpec.builder(4, 0.2f).build(),
    "honey_juice", FoodSpec.builder(6, 0.1f).alwaysEdible(true).build()
));
```

## 🌐 语言文件生成

```java
String langJson = ItemAPI.generateLangJson("pasterdream", Map.of(
    "item.pasterdream.titanium_ingot", "钛锭",
    "item.pasterdream.apple_juice", "苹果汁"
));
// 输出按键排序的 pretty JSON 字符串
```

## 📋 迁移管理

```java
// 标记已移植（自动从待移植集合移除）
ItemAPI.markMigrated(MigrationCategory.MATERIAL, "titanium_ingot", "dyedream_dust");
ItemAPI.markMigrated(MigrationCategory.FOOD, "apple_juice", "honey_juice");

// 标记待移植
ItemAPI.markPending(MigrationCategory.TOOL, "copper_axe", "copper_shovel");

// 查询状态
ItemManager manager = ItemAPI.getManager();
boolean done = manager.isMigrated("titanium_ingot");
Set<String> pending = manager.getPendingItems(MigrationCategory.TOOL);

// 生成报告
String report = ItemAPI.generateReport();
System.out.println(report);
// ====================
//   物品移植报告 [pasterdream]
// ====================
//   总进度: 65.00%
//   类别             总数    已移植    待移植   完成率
//   ---------------------------------------------------
//   材料               45       30       15    66.67%
//   ...
```

**MigrationCategory 枚举（11 类）**：`MATERIAL`、`FOOD`、`TOOL`、`WEAPON`、`ARMOR`、`CURIO`、`BLOCK_ITEM`、`MUSIC_DISC`、`SPAWN_EGG`、`RECORD`、`MISC`

## ItemManager 完整方法

| 方法 | 说明 |
|------|------|
| `markMigrated(category, names...)` | 标记已移植 |
| `markPending(category, names...)` | 标记待移植 |
| `isMigrated(name)` | 查询是否已移植 |
| `getMigratedItems(category)` / `getPendingItems(category)` | 获取某类别集合（不可变） |
| `getAllMigratedNames()` | 全部已移植注册名（不可变） |
| `addWarning(String)` / `getWarnings()` | 迁移警告 |
| `batchRegisterSimple(List<ItemSpec>)` | 批量注册并自动标记 MATERIAL 已移植 |
| `batchRegisterFood(Map<String, FoodSpec>)` | 批量注册并自动标记 FOOD 已移植 |
| `generateLangJson(modId, entries)` | 生成语言 JSON |
| `generateReport()` | 生成迁移报告 |
| `reset()` | 重置追踪状态（不取消已注册物品） |

## ⚠️ 常见问题

### Q: Builder 的 `.build()` 何时调用？

必须在 `DeferredRegister` 注册到事件总线**之前**完成（静态初始化阶段）：

```java
// registry/items/PDItemsMaterials.java（或任意注册类）
public static final DeferredItem<Item> TITANIUM_INGOT =
    ItemAPI.simpleItem("titanium_ingot")
        .rarity(Rarity.UNCOMMON)
        .build();
```

### Q: `Registry is already frozen` 崩溃怎么办？

Builder 内部已使用 Supplier 懒加载，**不会**触发饿汉式注册。自己写 `registerCustom` 时也务必用 Supplier：

```java
// ✅ 正确：Supplier 懒加载
ItemAPI.registerCustom("magic_stone", () -> new MagicStoneItem(new Item.Properties()));
// ❌ 错误：注册表未就绪时直接 new
var item = new MagicStoneItem(new Item.Properties());
ItemAPI.registerCustom("magic_stone", () -> item);
```

### Q: 物品在游戏里显示为紫黑方块（missing texture）？

API 只负责注册，**不负责复制视觉资源**。需手动准备：
- 模型：`assets/pasterdream/models/item/<注册名>.json`（`parent: "item/generated"` + layer0 纹理）
- 纹理：`assets/pasterdream/textures/item/<纹理名>.png`（原模组纹理多为拼音命名，如 `bo_li_bei_.png`）

### Q: 如何注册带特殊行为的物品？

使用 `registerCustom` 降级方案，或直接在主模块 `registry/items/PDItems*.java` 中用 `DeferredRegister` 手写注册。

### Q: 配方/战利品表/方块标签怎么办？

旧 API 的自动生成器已移除。请：
- 配方 JSON：手动写入 `data/pasterdream/recipe/*.json`（1.21 单数路径！）
- 战利品表：手动写入 `data/pasterdream/loot_table/blocks/*.json`
- 方块挖掘标签：参考 `BlockConfig.mineable()` + runData 数据生成器

## 📁 相关注册类位置

主模块物品注册分散在 `PasterDream/src/main/java/com/pasterdream/pasterdreammod/registry/items/`：
```
PDItemsMaterials.java   # 材料
PDItemsFoods.java       # 食物
PDItemsTools.java       # 工具/武器
PDItemsCurios.java      # 饰品
PDItemsSpawnEggs.java   # 刷怪蛋
PDItemsDolls.java       # 娃娃
PDItemsMusic.java       # 唱片
PDItemsArmor.java       # 盔甲
... 等
```

> 原模组参考：`libs/FixPasterDream-main/src/main/java/net/pasterdream/init/PasterdreamModItems.java`

## 📎 引用文件

- [ItemAPI.java](file:///C:/Users/97128/Documents/GitHub/NeoPasterDream1/PasterDreamAPI/src/main/java/com/pasterdream/pasterdreammod/api/item/ItemAPI.java) — 门面类
- [BaseItemBuilder.java](file:///C:/Users/97128/Documents/GitHub/NeoPasterDream1/PasterDreamAPI/src/main/java/com/pasterdream/pasterdreammod/api/item/builder/BaseItemBuilder.java) — Builder 基类
- [SimpleItemBuilder.java](file:///C:/Users/97128/Documents/GitHub/NeoPasterDream1/PasterDreamAPI/src/main/java/com/pasterdream/pasterdreammod/api/item/builder/SimpleItemBuilder.java) — 简单物品 Builder
- [FoodItemBuilder.java](file:///C:/Users/97128/Documents/GitHub/NeoPasterDream1/PasterDreamAPI/src/main/java/com/pasterdream/pasterdreammod/api/item/builder/FoodItemBuilder.java) — 食物 Builder
- [ToolItemBuilder.java](file:///C:/Users/97128/Documents/GitHub/NeoPasterDream1/PasterDreamAPI/src/main/java/com/pasterdream/pasterdreammod/api/item/builder/ToolItemBuilder.java) — 工具 Builder
- [CurioItemBuilder.java](file:///C:/Users/97128/Documents/GitHub/NeoPasterDream1/PasterDreamAPI/src/main/java/com/pasterdream/pasterdreammod/api/item/builder/CurioItemBuilder.java) — 饰品 Builder
- [ItemManager.java](file:///C:/Users/97128/Documents/GitHub/NeoPasterDream1/PasterDreamAPI/src/main/java/com/pasterdream/pasterdreammod/api/item/manager/ItemManager.java) — 迁移管理器
- [ItemSpec.java](file:///C:/Users/97128/Documents/GitHub/NeoPasterDream1/PasterDreamAPI/src/main/java/com/pasterdream/pasterdreammod/api/item/model/ItemSpec.java) — 物品规范
- [FoodSpec.java](file:///C:/Users/97128/Documents/GitHub/NeoPasterDream1/PasterDreamAPI/src/main/java/com/pasterdream/pasterdreammod/api/item/model/FoodSpec.java) — 食物规范
- [ToolSpec.java](file:///C:/Users/97128/Documents/GitHub/NeoPasterDream1/PasterDreamAPI/src/main/java/com/pasterdream/pasterdreammod/api/item/model/ToolSpec.java) — 工具规范
- [CurioSpec.java](file:///C:/Users/97128/Documents/GitHub/NeoPasterDream1/PasterDreamAPI/src/main/java/com/pasterdream/pasterdreammod/api/item/model/CurioSpec.java) — 饰品规范
- [MigrationCategory.java](file:///C:/Users/97128/Documents/GitHub/NeoPasterDream1/PasterDreamAPI/src/main/java/com/pasterdream/pasterdreammod/api/item/model/MigrationCategory.java) — 迁移分类枚举
