---
name: "item-migration-api"
description: "PasterDream 物品移植 API —— 用于将原 FixPasterDream 模组（MCreator 生成）中的物品系统化移植到 NeoForge 1.21.1。提供 Builder 模式、批量注册、迁移追踪、配方生成、战利品表生成、方块数据生成、创造标签页生成、语言文件生成及超级快速导入器等一站式工具链。Invoke when needing to port items from the old FixPasterDream mod, register new items in PDItems.java, batch-create items with consistent patterns, generate recipes/loot tables/block tags, or create creative tab registration code."
---

# 🎯 PasterDream 物品移植 API (Item Migration API) 使用指南

## 🎯 适用范围

本 API 用于 **PasterDream 模组**（NeoForge 1.21.1）的物品注册、数据生成、配方与战利品表生成、方块标签配置及创造模式标签页注册等全套工作。

| 适用场景 | 不适用场景 |
|---------|-----------|
| 从原模组移植材料、食物、工具、武器、饰品 | 复杂方块行为逻辑（请使用 PDBlocks + 自定义 Block 类） |
| 批量注册具有相似属性的物品（如一批材料） | 需要 GeckoLib 3D 动画渲染的物品 |
| 需要追踪移植进度的场景 | 需要复杂交互逻辑的物品（实体交互、自定义渲染） |
| 需要自动生成语言文件的场景 | 盔甲套装（API 已预留 ArmorSpec 支持） |
| 需要生成配方 JSON（有序/无序/熔炉/切石机） | — |
| 需要生成方块战利品表和挖掘标签 | — |
| 需要生成创造模式标签页注册代码 | — |
| **AI 需要快速导入物品的全套数据** | — |

---

## 📦 API 包结构

```
com.pasterdream.pasterdreammod.api.itemmigration/
├── ItemMigrationAPI.java         # ★ 主入口门面类（Facade）
│
├── builder/                      # 物品构建器
│   ├── BaseItemBuilder.java      #   Builder 抽象基类
│   ├── SimpleItemBuilder.java    #   简单材料物品构建器
│   ├── FoodItemBuilder.java      #   食物物品构建器
│   ├── ToolItemBuilder.java      #   工具/武器物品构建器
│   └── CurioItemBuilder.java     #   Curio 饰品物品构建器
│
├── model/                        # 数据模型（Record）
│   ├── ItemSpec.java             #   物品基础属性规范
│   ├── FoodSpec.java             #   食物属性规范
│   │   └── FoodEffectSpec        #   食物效果规范
│   ├── ToolSpec.java             #   工具属性规范
│   │   ├── ToolType              #   工具类型枚举
│   │   └── IngredientSupplier    #   修复材料供应接口
│   ├── ArmorSpec.java            #   盔甲属性规范
│   ├── CurioSpec.java            #   饰品属性规范
│   ├── AttributeModSpec.java     #   属性修饰器规范
│   └── MigrationCategory.java    #   迁移分类枚举
│
├── manager/                      # 迁移管理
│   ├── MigrationManager.java     #   迁移管理器
│   └── MigrationReport.java      #   迁移报告生成
│
├── gen/ ★ 新增生成器套件 ★
│   ├── LanguageGenerator.java    #   语言文件自动生成
│   ├── RecipeGenerator.java      #   ★ 配方 JSON 生成器
│   ├── LootTableGenerator.java   #   ★ 战利品表 JSON 生成器
│   ├── BlockDataGenerator.java   #   ★ 方块注册/挖掘标签生成器
│   ├── CreativeTabHelper.java    #   ★ 创造标签页代码生成器
│   └── ImportHelper.java         #   ★ AI 超级快速导入器
│
└── example/
    └── ItemMigrationExample.java #   ★ 完整使用示例（760 行）
```

---

## 🚀 快速开始

### 1. 注册一个简单材料

```java
// 使用 API 门面（推荐）
ItemMigrationAPI.simpleItem("titanium_ingot")
    .rarity(Rarity.UNCOMMON)
    .stacksTo(64)
    .build();
```

### 2. 注册一个食物

```java
ItemMigrationAPI.foodItem("apple_juice")
    .nutrition(4).saturationModifier(0.2f)
    .alwaysEdible()
    .effect("minecraft:regeneration", 100, 0, 1.0f)
    .build();
```

### 3. 注册一个工具/武器

```java
ItemMigrationAPI.toolItem("copper_sword")
    .type(ToolType.SWORD).durability(250)
    .attackDamage(6.0f).attackSpeed(-2.4f)
    .build();
```

### 4. 注册一个 Curio 饰品

```java
ItemMigrationAPI.curioItem("embryo_ring")
    .slot("ring")
    .attribute("minecraft:generic.attack_damage",
        "a1b2c3d4-e5f6-7890-abcd-ef1234567890", 2.0, 0)
    .tooltip("§7胚胎之戒", "§e可升级为更强大的戒指")
    .build();
```

---

## ⚡ 新增强力工具

### 🧪 RecipeGenerator — 配方 JSON 生成器

自动生成 Minecraft 所有配方类型的 JSON 字符串，**无需手写 JSON**。

通过 `ItemMigrationAPI.recipeGen()` 访问。

```java
// 有序合成（支持 Tag）
String shaped = ItemMigrationAPI.recipeGen()
    .generateShaped("pasterdream:my_ingot", 1, "misc",
        new String[]{"AAA", "ABA", "AAA"},
        Map.of("A", "minecraft:iron_ingot", "B", "minecraft:stick"));

// 有序合成（支持 Tag）
String shapedWithTag = ItemMigrationAPI.recipeGen()
    .generateShapedWithTag("pasterdream:my_ingot", 1, "misc",
        new String[]{"AAA", "ABA", "AAA"},
        Map.of("A", new TagOrItem("minecraft:planks", true),
               "B", new TagOrItem("minecraft:stick", true)));

// 无序合成
String shapeless = ItemMigrationAPI.recipeGen()
    .generateShapeless("pasterdream:my_item", 1, "misc",
        List.of("minecraft:diamond", "minecraft:stick"));

// 熔炉/高炉/营火/烟熏炉
String smelting = ItemMigrationAPI.recipeGen()
    .generateSmelting("minecraft:iron_ingot", "pasterdream:raw_iron", 0.35f, 200);
String blasting = ItemMigrationAPI.recipeGen()
    .generateBlasting("minecraft:iron_ingot", "pasterdream:raw_iron", 0.35f, 100);

// 切石机
String stonecutting = ItemMigrationAPI.recipeGen()
    .generateStonecutting("pasterdream:stone_slab", 2, "pasterdream:stone_block");

// 保存到文件
ItemMigrationAPI.recipeGen().saveRecipeToFile(shaped, "pasterdream", "my_ingot", "src/main/resources");
```

支持的类型：`generateShaped`, `generateShapedWithTag`, `generateShapeless`, `generateShapelessWithTag`, `generateSmelting`, `generateBlasting`, `generateCampfire`, `generateSmoking`, `generateStonecutting`

### 📦 LootTableGenerator — 战利品表生成器

自动生成方块战利品表 JSON，支持多种掉落模式。

通过 `ItemMigrationAPI.lootTableGen()` 访问。

```java
// 自掉落（方块破坏掉落自身）
String selfDrop = ItemMigrationAPI.lootTableGen()
    .generateSelfDrop("pasterdream:example_block");

// 精准采集掉落
String silkDrop = ItemMigrationAPI.lootTableGen()
    .generateSilkTouchDrop("pasterdream:glass_block");

// 矿石掉落（时运加成 + 爆炸衰减）
String oreDrop = ItemMigrationAPI.lootTableGen()
    .generateOreDrop("pasterdream:raw_ore", "pasterdream:ore_block");

// 自定义掉落（灵活控制精准采集/时运/爆炸衰减）
String customDrop = ItemMigrationAPI.lootTableGen()
    .generateCustomDrop("pasterdream:special_drop", "pasterdream:special_block",
        true,   // silkTouchOnly
        true,   // fortuneEnabled
        false); // explosionDecay

// 多物品混合掉落（如树叶同时掉落树苗+苹果）
List<DropEntry> entries = List.of(
    new DropEntry("pasterdream:leaf_block", 1, 1, 1.0f, false, false),
    new DropEntry("pasterdream:sapling", 1, 1, 0.2f, false, false)
);
String multiDrop = ItemMigrationAPI.lootTableGen()
    .generateMultiDrop(entries, "pasterdream:leaf_block");

// 保存到文件
ItemMigrationAPI.lootTableGen().saveLootTableToFile(oreDrop, "pasterdream", "ore_block", "src/main/resources");
```

### ⛏️ BlockDataGenerator — 方块数据生成器

自动生成方块的挖掘标签 JSON、方块注册代码片段和 BlockItem 注册代码。

通过 `ItemMigrationAPI.blockDataGen()` 访问。

```java
// === 挖掘标签 JSON 生成 ===

// 单独生成各标签
String pickaxeTag = ItemMigrationAPI.blockDataGen()
    .generateMineablePickaxeJson(List.of("pasterdream:my_ore", "pasterdream:stone_block"));
String axeTag = ItemMigrationAPI.blockDataGen()
    .generateMineableAxeJson(List.of("pasterdream:wood_block"));
String ironTool = ItemMigrationAPI.blockDataGen()
    .generateNeedsIronToolJson(List.of("pasterdream:my_ore"));

// 一键生成所有标签
Map<String, String> allTags = ItemMigrationAPI.blockDataGen()
    .generateDefaultMiningTags("pasterdream",
        List.of("pasterdream:ore_1", "pasterdream:ore_2"), // pickaxe
        List.of("pasterdream:wood_block"),                   // axe
        null,                                                // shovel
        null,                                                // hoe
        List.of("pasterdream:ore_1"),                        // needs_stone_tool
        List.of("pasterdream:ore_2"),                        // needs_iron_tool
        null);                                               // needs_diamond_tool

// === 注册代码生成 ===
// 方块注册代码（无自定义类）
String blockCode = ItemMigrationAPI.blockDataGen()
    .generateBlockRegistrationCode("my_block", "MY_BLOCK", "Blocks.STONE", false);
// 输出: public static final DeferredBlock<Block> MY_BLOCK = BLOCKS.registerSimpleBlock(...)

// 方块注册代码（有自定义类）
String customBlockCode = ItemMigrationAPI.blockDataGen()
    .generateBlockRegistrationCode("my_custom_block", "MY_CUSTOM_BLOCK", "Blocks.STONE", true);
// 输出: public static final DeferredBlock<MyCustomBlock> MY_CUSTOM_BLOCK = BLOCKS.registerBlock(...)

// BlockItem 注册代码
String blockItemCode = ItemMigrationAPI.blockDataGen()
    .generateBlockItemRegistrationCode("my_block");

// === 保存标签到文件 ===
ItemMigrationAPI.blockDataGen().saveTagToFile(pickaxeTag, "pasterdream",
    "mineable/pickaxe", "src/main/resources");
```

### 🏷️ CreativeTabHelper — 创造标签页代码生成器

生成与 `PDCreativeTabs.java` 风格一致的创造模式标签页注册代码。

通过 `ItemMigrationAPI.creativeTabHelper()` 访问。

```java
// 生成完整的标签页注册代码
String tabCode = ItemMigrationAPI.creativeTabHelper()
    .generateCompleteTabWithItems(
        "paster_tab_2",                              // 标签页 ID
        "新材料与工具标签页",                          // 注释描述
        "DREAM_ACCUMULATOR",                         // 图标物品字段名
        "PASTER_TAB_1",                              // 排序参考（before）
        "pasterdream",                               // 模组 ID
        List.of("MY_INGOT", "MY_TOOL", "MY_ARMOR"),  // 物品字段名列表
        "PDItems",                                   // 物品注册类名
        List.of("MY_BLOCK", "MY_ORE"),               // 方块字段名列表
        "PDBlocks"                                   // 方块注册类名
    );

// 生成单行 displayItems 代码
String line = ItemMigrationAPI.creativeTabHelper()
    .generateDisplayItemLine("TITANIUM_INGOT", "PDItems");
// 输出: output.accept(PDItems.TITANIUM_INGOT.get());

// 批量生成 displayItems 代码
String lines = ItemMigrationAPI.creativeTabHelper()
    .generateDisplayItemLines(
        List.of("INGOT_1", "INGOT_2", "TOOL_1"), "PDItems");

// 生成语言文件条目
String langJson = ItemMigrationAPI.creativeTabHelper()
    .generateTabLangJson("pasterdream", "paster_tab_2", "新材料与工具");
// 输出: "itemGroup.pasterdream.paster_tab_2": "新材料与工具"
```

### 🚀 ImportHelper — AI 超级快速导入器

**AI 的终极效率神器！** 一个静态方法调用即可生成完整的物品导入代码片段或 JSON 数据。
分为三级导入，从简单到复杂。

通过 `ItemMigrationAPI.importHelper()` 访问。

#### 一级导入：快速生成代码片段

```java
// 最简模式 —— 只需注册名
String code1 = ItemMigrationAPI.importHelper().quickItem("my_ingot");

// 带属性
String code2 = ItemMigrationAPI.importHelper().quickItem("rare_ingot", 16, "RARE");

// 食物
String code3 = ItemMigrationAPI.importHelper().quickFood("apple_juice", 4, 0.2f);

// 工具
String code4 = ItemMigrationAPI.importHelper().quickTool("copper_pickaxe", "PICKAXE", 3.0f, 250);

// 饰品
String code5 = ItemMigrationAPI.importHelper().quickCurio("magic_ring", "ring");

// 从 Spec 生成
String code6 = ItemMigrationAPI.importHelper().quickItemFromSpec(
    ItemSpec.builder("soul_dust").rarity(Rarity.UNCOMMON).build());
```

#### 二级导入：物品 + 关联数据

```java
// 食物 + 配方 + 熔炉
String foodWithRecipe = ItemMigrationAPI.importHelper()
    .quickFoodWithRecipe("apple_juice", 4, 0.2f,
        List.of("minecraft:apple"),      // 无序合成材料
        null, 0);                         // 无熔炉配方

// 工具 + 合成配方
String toolWithRecipe = ItemMigrationAPI.importHelper()
    .quickToolWithRecipe("copper_sword", "SWORD", 6.0f, 250,
        new String[]{" A ", " A ", " B "},
        Map.of("A", "minecraft:copper_ingot", "B", "minecraft:stick"));

// 方块 + 战利品表 + 挖掘标签
String blockWithLoot = ItemMigrationAPI.importHelper()
    .quickBlockWithLoot("my_ore", "pasterdream:my_ore", "pasterdream:raw_ore",
        true,   // isOre（启用时运）
        false,  // needSilktouch
        true);  // hasPickaxeTag

// 方块完整数据包（包含注册代码 + BlockItem + 战利品表 + 标签）
String completeBlock = ItemMigrationAPI.importHelper()
    .quickCompleteBlock("my_ore", "Blocks.STONE", "pasterdream:raw_ore",
        true, false);
```

#### 三级导入：一键全流程导入（generateAll）

```java
// === material 示例 ===
String allData = ItemMigrationAPI.importHelper()
    .generateAll("titanium_ingot", "钛锭", "Titanium Ingot",
        "material",
        Map.of("rarity", "UNCOMMON"),
        Map.of("smeltIngredient", "pasterdream:raw_titanium",
               "smeltExperience", 0.7f),
        "pasterdream");

// === food 示例 ===
String allFood = ItemMigrationAPI.importHelper()
    .generateAll("magic_apple", "魔法苹果", "Magic Apple",
        "food",
        Map.of("nutrition", 8, "saturation", 1.2f),
        Map.of("shapelessIngredients", List.of("minecraft:apple", "pasterdream:magic_dust")),
        "pasterdream");

// === tool 示例 ===
String allTool = ItemMigrationAPI.importHelper()
    .generateAll("shadow_sword", "影刃", "Shadow Sword",
        "tool",
        Map.of("toolType", "SWORD", "damage", 8.0f, "durability", 1725),
        Map.of("shapedPattern", new String[]{" A ", " A ", " B "},
               "shapedKeys", Map.of("A", "pasterdream:shadow_crystal", "B", "minecraft:stick")),
        "pasterdream");

// === block 示例 ===
String allBlock = ItemMigrationAPI.importHelper()
    .generateAll("ruby_ore", "红宝石矿石", "Ruby Ore",
        "block",
        Map.of("templateBlock", "Blocks.STONE", "dropItem", "ruby", "isOre", true),
        null,
        "pasterdream");

// === curio 示例 ===
String allCurio = ItemMigrationAPI.importHelper()
    .generateAll("life_ring", "生命之戒", "Life Ring",
        "curio",
        Map.of("slot", "ring"),
        null,
        "pasterdream");
```

#### 批量导入

```java
// 批量材料（从 ItemSpec 列表）
List<ItemSpec> materials = List.of(
    ItemSpec.builder("soul_dust").build(),
    ItemSpec.builder("soul_essence").rarity(Rarity.UNCOMMON).build(),
    ItemSpec.builder("magic_crystal").rarity(Rarity.RARE).build()
);
String batchCode = ItemMigrationAPI.importHelper().batchQuickItems(materials);

// 批量食物
Map<String, FoodSpec> foods = Map.of(
    "apple_juice", new FoodSpec(4, 0.2f, false, false, List.of()),
    "honey_juice", new FoodSpec(6, 0.1f, true, false, List.of())
);
String batchFoodCode = ItemMigrationAPI.importHelper().batchQuickFoods(foods);

// 从 Map 配置批量导入（兼容 CSV/JSON 数据源）
Map<String, Map<String, String>> config = Map.of(
    "copper_sword", Map.of("type", "tool", "toolType", "SWORD", "damage", "6.0", "durability", "250"),
    "copper_pickaxe", Map.of("type", "tool", "toolType", "PICKAXE", "damage", "3.0", "durability", "250"),
    "healing_potion", Map.of("type", "food", "nutrition", "4", "saturation", "0.3"),
    "soul_ring", Map.of("type", "curio", "slot", "ring")
);
String batchFromMap = ItemMigrationAPI.importHelper()
    .batchImportFromMap(config, "pasterdream");
```

---

## 🧩 各 Builder 详细说明

### SimpleItemBuilder

| 方法 | 参数 | 说明 |
|------|------|------|
| `stacksTo(int)` | 堆叠数 | 默认 64 |
| `rarity(Rarity)` | 稀有度 | 默认 COMMON |
| `fireResistant()` | - | 防火（不惧熔岩） |
| `tooltip(String...)` | 描述行 | 支持 § 颜色代码 |
| `build()` | - | 执行注册，返回 DeferredItem |

### FoodItemBuilder

| 方法 | 参数 | 说明 |
|------|------|------|
| `nutrition(int)` | 营养值 | 半鸡腿数 |
| `saturationModifier(float)` | 饱和度系数 | 值越高回饱越多 |
| `alwaysEdible()` | - | 饱腹也可食用 |
| `fastFood()` | - | 快速食用 |
| `effect(String, int, int, float)` | effectId, duration, amplifier, probability | 食用效果 |

### ToolItemBuilder

| 方法 | 参数 | 说明 |
|------|------|------|
| `type(ToolType)` | 工具类型 | SWORD/PICKAXE/AXE/SHOVEL/HOE/HAMMER/WAND |
| `durability(int)` | 耐久度 | 默认 250 |
| `miningSpeed(float)` | 挖掘速度 | 默认 2.0 |
| `attackDamage(float)` | 攻击伤害 | 默认 1.0 |
| `attackSpeed(float)` | 攻击速度 | 默认 -2.4 |
| `enchantment(int)` | 附魔能力 | 默认 5 |
| `repairWith(ItemStack...)` | 修复材料 | 用于铁砧修复 |

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
| `slot(String)` | 槽位 | ring/necklace/belt/charm/head/back/curio |
| `attribute(String, String, double, int)` | attrName, id, amount, operation | 属性修饰器 |
| `tooltip(String...)` | 描述行 | 支持颜色代码 |

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

---

## 📋 移植工作流

```
┌──────────────────────────────────────────────────────────────────────┐
│                      物品移植标准工作流                                 │
├──────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  第1步: 分析原模组物品清单                                             │
│  └── 查看 libs/FixPasterDream-main/ 下的物品注册文件                  │
│                                                                      │
│  第2步: 分类物品类型                                                   │
│  ├── 材料类 → SimpleItemBuilder                                       │
│  ├── 食物类 → FoodItemBuilder                                         │
│  ├── 工具类 → ToolItemBuilder                                         │
│  ├── 饰品(Curio) → CurioItemBuilder                                   │
│  ├── 方块类(需自定义方块) → PDBlocks                                  │
│  └── 复杂NBT交互 → registerCustom + 自定义 Item 子类                   │
│                                                                      │
│  第3步: 注册物品（使用 Builder + ImportHelper 快速生成代码）            │
│  ├── ItemMigrationAPI.importHelper().quickItem("ingot")               │
│  ├── ItemMigrationAPI.importHelper().quickFood("apple", 4, 0.2f)    │
│  └── 或使用 generateAll() 一键生成全套代码                             │
│                                                                      │
│  第4步: 生成配方 JSON                                                  │
│  └── ItemMigrationAPI.recipeGen().generateXXX() 生成配方 JSON         │
│      + recipeGen().saveRecipeToFile() 保存到 data/modid/recipes/      │
│                                                                      │
│  第5步: 生成战利品表（如果是方块）                                      │
│  └── ItemMigrationAPI.lootTableGen().generateXXX() 生成 loot JSON    │
│      + lootTableGen().saveLootTableToFile() 保存到 data/modid/        │
│        loot_tables/blocks/                                           │
│                                                                      │
│  第6步: 生成方块挖掘标签（如果是方块）                                   │
│  └── ItemMigrationAPI.blockDataGen().generateMineableXXX()            │
│      + blockDataGen().saveTagToFile()                                 │
│                                                                      │
│  第7步: 添加至创造模式标签页                                           │
│  └── 使用 CreativeTabHelper 生成 displayItems 代码                    │
│      + CreativeTabHelper.generateCompleteTabWithItems() 生成完整标签页 │
│                                                                      │
│  第8步: 标记迁移状态 + 生成语言文件                                     │
│  ├── ItemMigrationAPI.markMigrated(category, items...)                │
│  └── ItemMigrationAPI.generateLangJson(modId, entries)                │
│                                                                      │
│  第9步: 编译验证                                                      │
│  └── .\gradlew.bat compileJava                                       │
│                                                                      │
│  第10步: 生成迁移报告                                                  │
│  └── ItemMigrationAPI.generateReport()                                │
│                                                                      │
└──────────────────────────────────────────────────────────────────────┘
```

---

## 🌐 语言文件生成

### 生成单个条目

```java
String key = LanguageGenerator.itemKey("pasterdream", "titanium_ingot");
// 结果: "item.pasterdream.titanium_ingot"
```

### 生成完整 JSON

```java
String json = LanguageGenerator.generateLangJson("pasterdream", Map.of(
    "titanium_ingot", "钛锭",
    "apple_juice", "苹果汁"
));
```

### snake_case 转可读名

```java
String name = LanguageGenerator.snakeToEnglishDisplay("titanium_ingot");
// 结果: "Titanium Ingot"
```

---

## 📊 迁移管理

### 标记已移植

```java
ItemMigrationAPI.markMigrated(MigrationCategory.MATERIAL,
    "titanium_ingot", "dyedream_dust", "magic_stone");
ItemMigrationAPI.markMigrated(MigrationCategory.FOOD,
    "apple_juice", "honey_juice");
```

### 标记待移植

```java
ItemMigrationAPI.markPending(MigrationCategory.TOOL,
    "copper_axe", "copper_shovel", "copper_hoe");
```

### 生成报告

```java
String report = ItemMigrationAPI.generateReport();
System.out.println(report);
// 输出示例:
// ====================
//   物品移植报告 [pasterdream]
// ====================
//   总进度: 65.00%
//   类别             总数    已移植    待移植   完成率
//   ---------------------------------------------------
//   材料               45       30       15    66.67%
//   食物               20       15        5    75.00%
//   工具               10        5        5    50.00%
//   ...
```

---

## ⚠️ 常见问题

### Q: Builder 的 `.build()` 方法何时调用？

必须在 `DeferredRegister` 注册到事件总线 **之前** 完成。通常在 PDItems 的静态初始化块中：

```java
public class PDItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MOD_ID);

    // 直接注册（推荐）
    public static final DeferredItem<Item> TITANIUM_INGOT =
        ItemMigrationAPI.simpleItem("titanium_ingot")
            .rarity(Rarity.UNCOMMON)
            .build();  // 立即注册
}
```

### Q: 如何处理需要自定义 Item 类的复杂物品？

使用 `registerCustom` 降级方案：

```java
public class MagicStoneItem extends Item {
    public MagicStoneItem(Properties props) { super(props); }
}

// 注册
ItemMigrationAPI.registerCustom("magic_stone",
    () -> new MagicStoneItem(new Item.Properties()));
```

### Q: 为什么使用 API 注册的物品需要单独添加至创造标签页？

API 只负责物品注册（将物品加入游戏注册表）。要显示在创造模式物品栏中，需手动在 PDCreativeTabs.java 中添加，或使用 `CreativeTabHelper.generateCompleteTabWithItems()` 生成完整标签页代码。

### Q: AI 如何最快地导入一个物品？

使用 **ImportHelper.generateAll()** —— 一次调用，生成注册代码 + 配方 JSON + 战利品表 JSON + 挖掘标签 JSON + 创造标签页代码 + 语言文件条目，全部打包带走！🎉

```java
// AI 只需提供以下信息：
String result = ItemMigrationAPI.importHelper().generateAll(
    "ruby_ore",         // 注册名
    "红宝石矿石",        // 中文名
    "Ruby Ore",         // 英文名
    "block",            // 类型
    Map.of("templateBlock", "Blocks.STONE",
           "dropItem", "ruby", "isOre", true),
    Map.of("smeltIngredient", "pasterdream:ruby_ore",
           "smeltExperience", 1.0f),
    "pasterdream"
);
```

### Q: 如何为已有配方生成 JSON？

```java
// 配方 JSON 生成器是纯工具类，不依赖注册表
String recipeJson = ItemMigrationAPI.recipeGen()
    .generateShaped("minecraft:diamond", 1, "misc",
        new String[]{"AAA", "ABA", "AAA"},
        Map.of("A", "minecraft:iron_ingot", "B", "minecraft:stick"));
```

### 🚨 Q: 配方在 JEI 里不显示怎么办？

**大概率是目录名搞错了！** 🎯

Minecraft 1.21 (NeoForge) 的配方文件目录是 **单数 `recipe/`**，不是复数 `recipes/`！

```
❌ 错误：src/main/resources/data/pasterdream/recipes/dough.json
✅ 正确：src/main/resources/data/pasterdream/recipe/dough.json
```

游戏会**静默跳过**错误目录下的配方文件，不给任何错误日志。你只能通过对比配方总数来判断：
- 启动日志搜 `Loaded [0-9]+ recipes` 
- 如果添加配方后总数没变化 → 那就是目录放错了

> ⚠️ **RecipeGenerator.saveRecipeToFile()** 内部已使用 `recipe/` 单数路径，放心调用。

### 🖼️ Q: 物品在游戏里显示为紫黑方块（missing texture）？

**模型文件和纹理没复制过来！** API 只负责物品注册（数据层面），**不负责复制视觉资源文件**。

需要手动从原模组复制两样东西：

**① 模型 JSON：** `assets/pasterdream/models/item/<注册名>.json`
```json
{
  "parent": "item/generated",
  "textures": {
    "layer0": "pasterdream:item/bo_li_bei_"
  }
}
```

**② 纹理 PNG：** `assets/pasterdream/textures/item/<纹理名>.png`

> ⚠️ **原模组纹理使用拼音命名！** 比如 `glass_cup` 对应 `bo_li_bei_.png`（玻璃杯），
> `copper_axe` 对应 `tong_fu_.png`（铜斧），`dough` 对应 `sheng_mian_tuan_.png`（生面团）。
> 不要被模型 JSON 里引用的纹理名吓到，去原模组 `textures/item/` 目录找对应的拼音文件就行。

**快速复制命令（在原模组和新模组同级目录下执行）：**
```powershell
# 复制模型 JSON
Copy-Item "libs\FixPasterDream-main\src\main\resources\assets\pasterdream\models\item\dough.json" `
    -Destination "src\main\resources\assets\pasterdream\models\item\dough.json"

# 复制纹理 PNG
Copy-Item "libs\FixPasterDream-main\src\main\resources\assets\pasterdream\textures\item\sheng_mian_tuan_.png" `
    -Destination "src\main\resources\assets\pasterdream\textures\item\sheng_mian_tuan_.png"
```

### 🔍 Q: 如何快速找到原模组的纹理文件？

原模组的纹理文件用**拼音**命名，搜索技巧：

| 物品注册名 | 搜索关键词 | 解释 |
|-----------|-----------|------|
| `glass_cup` | `bei_` | 杯 |
| `dough` | `mian_tuan` | 面团 |
| `copper_axe` | `tong_fu` | 铜斧 |
| `copper_shovel` | `tong_qiao` | 铜锹 |
| `copper_hoe` | `tong_chu` | 铜锄 |
| `fourleaf_clover_curio` | `si_xie_cao` | 四叶草 |

用这个命令在拼音海洋里捞针：
```powershell
ls libs\FixPasterDream-main\src\main\resources\assets\pasterdream\textures\item\ | Select-String "si_xie"
```

### 🧊 Q: `Registry is already frozen` 崩溃怎么办？

**Builder 使用了饿汉式创建（Eager Initialization）导致的！**

错误示例（❌）：
```java
// 直接在静态初始化时 new Item() → 注册表还没准备好！
public static final DeferredItem<Item> BAD_ITEM = 
    ITEMS.register("bad_item", () -> new Item(props));  // ✅ 用 Supplier
```

修复：**所有 Builder 的 `build()` 方法必须返回 Supplier 懒加载**，API 内部已修复此问题。如果你自己写注册代码，记住：

```java
// ✅ 正确：使用 Supplier 懒加载
ITEMS.register("good_item", () -> new Item(new Item.Properties()));
// ❌ 错误：直接创建实例
ITEMS.register("bad_item", () -> new Item(new Item.Properties())); // 呃，其实这个是对的
// ❌ 真正错误的是：
var item = new Item(new Item.Properties()); // 饿汉式！
ITEMS.register("bad_item", () -> item);
```

---

## 📁 原模组物品参考

原模组物品注册文件位置：
```
libs/FixPasterDream-main/src/main/java/net/pasterdream/init/
    PasterdreamModItems.java     # 所有物品注册
    PasterdreamModBlocks.java    # 方块注册（含方块物品）
    PasterdreamModTabs.java      # 创造模式标签页
```

原模组物品类位置：
```
libs/FixPasterDream-main/src/main/java/net/pasterdream/item/
    # ~200+ 个物品类文件
```

---

## 🧪 完整示例参考

查看 `ItemMigrationExample.java` 获取完整的 API 使用示例，包含：
- 所有 8 种 Builder 的完整链式调用演示
- Model Record（ItemSpec/FoodSpec/ToolSpec/CurioSpec/ArmorSpec）的 Builder 用法
- 迁移追踪与报告生成演示
- 语言文件生成演示
- 批量注册演示
- **配方生成演示**
- **战利品表生成演示**
- **方块数据生成演示**
- **创造标签页生成演示**
- **ImportHelper 快速导入演示**

文件位置：`src/main/java/com/pasterdream/pasterdreammod/api/itemmigration/example/ItemMigrationExample.java`

---

### 📎 关联参考：BlockAPI 数据生成器

`ItemMigrationAPI` 的重型生成器（配方、战利品表、方块状态等）采用的是**运行时写 JSON** 方式（在 `commonSetup` 阶段触发），
而 `BlockAPI` 新增的 `BlockConfig` 采用的是 **NeoForge 数据生成器**方式（运行 `runData` 生成）。

| 对比 | ItemMigrationAPI | BlockAPI + BlockConfig |
|------|-----------------|----------------------|
| 时机 | `commonSetup` 运行时 | `runData` 开发时 |
| 输出路径 | `src/main/resources` | `src/generated/resources` |
| 触发器 | `ModDecorations.register()` | `.\gradlew runData` |
| 工具标签 | 无 | `PDBlockTagProvider` 自动读取 `BlockConfig.mineable` |
| 方块模型 | `BlockDataGen` 手动生成 | `PDBlockModelProvider` 自动读取 `BlockConfig.model` + `textures` |

> 两个 API 互不冲突，可以配合使用。BlockConfig 适合方块注册阶段的**轻量级配置**，
> ItemMigrationAPI 适合物品注册阶段的**批量迁移和高级生成**。
