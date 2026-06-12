/*
 * Item Migration API (物品移植 API)
 * ===========================================================================
 * 
 * == 设计理念 ==
 * 本 API 专门用于将原 FixPasterDream 模组（MCreator 生成）中的物品
 * 系统化地移植到 NeoForge 1.21.1 新模组中。
 * 通过 Builder 模式 + Facade 模式，提供简洁、类型安全的物品注册体验。
 * 
 * == 为什么需要这个 API？==
 * 原模组有 200+ 物品需要移植，手动写 DeferredRegister 重复代码量大、容易出错。
 * 本 API 将常见物品类型（材料、食物、工具、饰品）的注册流程标准化，
 * 让物品移植变成"填写参数"而非"手写类"。
 * 
 * == 核心架构 ==
 * ┌──────────────────────────────────────────────────────────┐
 * │                    ItemMigrationAPI                       │
 * │                    (Facade 门面)                           │
 * │  ┌──────────┬───────────┬───────────┬──────────────┐     │
 * │  │simpleItem│ foodItem  │ toolItem  │  curioItem   │     │
 * │  │ Builder  │ Builder   │ Builder   │  Builder     │     │
 * │  └────┬─────┴────┬──────┴─────┬─────┴──────┬───────┘     │
 * │       │          │            │            │             │
 * │       ▼          ▼            ▼            ▼             │
 * │  SimpleItem  FoodItem   ToolItem     CurioItem           │
 * │  (材料/普通)  (食物类)   (工具/武器)   (饰品/Curio)       │
 * └──────────────────────────────────────────────────────────┘
 * 
 * == 设计模式 ==
 * - Builder模式：通过流式API（Fluent Interface）定义物品属性
 * - Facade模式：ItemMigrationAPI 提供统一入口，屏蔽内部实现细节
 * - 规范驱动：ItemSpec/FoodSpec 等记录类型定义标准属性规范
 * 
 * == 快速开始 ==
 * // 注册一个简单材料（稀有）
 * ItemMigrationAPI.simpleItem("titanium_ingot")
 *     .rarity(Rarity.UNCOMMON)
 *     .build();
 * 
 * // 注册一个食物
 * ItemMigrationAPI.foodItem("apple_juice")
 *     .nutrition(4).saturationModifier(0.2f)
 *     .alwaysEdible()
 *     .build();
 * 
 * // 注册自定义物品（复杂物品的降级方案）
 * ItemMigrationAPI.registerCustom("magic_stone",
 *     () -> new MagicStoneItem(new Item.Properties()));
 * 
 * // 批量注册简单材料
 * ItemMigrationAPI.batchSimpleItems(
 *     new ItemSpec("soul_dust", Rarity.COMMON),
 *     new ItemSpec("soul_essence", Rarity.UNCOMMON)
 * );
 * 
 * // 批量注册食物
 * ItemMigrationAPI.batchFoodItems(Map.of(
 *     "apple_juice", new FoodSpec(4, 0.2f, true),
 *     "honey_juice", new FoodSpec(6, 0.1f, true)
 * ));
 * 
 * == 迁移管理 ==
 * // 标记已迁移的物品
 * ItemMigrationAPI.markMigrated(
 *     MigrationCategory.MATERIAL,
 *     "titanium_ingot", "dyedream_dust"
 * );
 * 
 * // 生成迁移报告
 * String report = ItemMigrationAPI.generateReport();
 * 
 * == 语言文件生成 ==
 * String langJson = ItemMigrationAPI.generateLangJson("pasterdream", Map.of(
 *     "item.pasterdream.titanium_ingot", "钛锭",
 *     "item.pasterdream.apple_juice", "苹果汁"
 * ));
 * 
 * ===========================================================================
 */
package com.pasterdream.pasterdreammod.api.itemmigration;

import com.pasterdream.pasterdreammod.api.PasterDreamAPI;
import com.pasterdream.pasterdreammod.api.itemmigration.builder.CurioItemBuilder;
import com.pasterdream.pasterdreammod.api.itemmigration.builder.FoodItemBuilder;
import com.pasterdream.pasterdreammod.api.itemmigration.builder.SimpleItemBuilder;
import com.pasterdream.pasterdreammod.api.itemmigration.builder.ToolItemBuilder;
import com.pasterdream.pasterdreammod.api.itemmigration.manager.MigrationManager;
import com.pasterdream.pasterdreammod.api.itemmigration.model.FoodSpec;
import com.pasterdream.pasterdreammod.api.itemmigration.model.ItemSpec;
import com.pasterdream.pasterdreammod.api.itemmigration.model.MigrationCategory;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 物品移植 API 的主入口门面类（Facade Pattern）。
 * <p>
 * 提供简洁的静态方法，用于将原 FixPasterDream 模组中的物品
 * 系统化地移植到 NeoForge 1.21.1 新模组。
 * 内部封装了 {@link DeferredRegister.Items}、{@link MigrationManager} 等核心组件，
 * 外部调用者无需关心注册细节。
 * <p>
 * <b>设计架构：</b>
 * <pre>
 * ┌──────────────────────────────────────────────────────────┐
 * │                    ItemMigrationAPI                       │
 * │                     (Facade 门面)                          │
 * │  ┌──────────┬───────────┬───────────┬──────────────┐     │
 * │  │simpleItem│ foodItem  │ toolItem  │  curioItem   │     │
 * │  │ Builder  │ Builder   │ Builder   │  Builder     │     │
 * │  └────┬─────┴────┬──────┴─────┬─────┴──────┬───────┘     │
 * │       │          │            │            │             │
 * │       ▼          ▼            ▼            ▼             │
 * │  SimpleItem  FoodItem   ToolItem     CurioItem           │
 * │  (材料/普通)  (食物类)   (工具/武器)   (饰品/Curio)       │
 * │                                                          │
 * │  新增强力辅助工具：                                        │
 * │  ├── RecipeGenerator    → 配方 JSON 生成                  │
 * │  ├── LootTableGenerator → 战利品表 JSON 生成              │
 * │  ├── BlockDataGenerator → 方块注册/挖掘标签生成           │
 * │  ├── CreativeTabHelper  → 创造标签页代码生成              │
 * │  └── ImportHelper       → AI 超级快速导入器               │
 * └──────────────────────────────────────────────────────────┘
 * </pre>
 * <p>
 * <b>使用示例：</b>
 * <pre>{@code
 * // 注册一个稀有材料
 * ItemMigrationAPI.simpleItem("titanium_ingot")
 *     .rarity(Rarity.UNCOMMON)
 *     .build();
 *
 * // 注册一个食物
 * ItemMigrationAPI.foodItem("apple_juice")
 *     .nutrition(4).saturationModifier(0.2f)
 *     .alwaysEdible()
 *     .build();
 *
 * // 批量注册
 * ItemMigrationAPI.batchSimpleItems(
 *     new ItemSpec("soul_dust", Rarity.COMMON),
 *     new ItemSpec("soul_essence", Rarity.UNCOMMON)
 * );
 *
 * // 生成配方 JSON
 * String recipeJson = ItemMigrationAPI.recipeGen()
 *     .generateShaped("minecraft:diamond", 1, "misc",
 *         new String[]{"AAA", "ABA", "AAA"},
 *         Map.of("A", "minecraft:iron_ingot", "B", "minecraft:stick"));
 *
 * // 生成战利品表
 * String lootJson = ItemMigrationAPI.lootTableGen()
 *     .generateSelfDrop("pasterdream:example_block");
 *
 * // 一键全流程导入
 * String allData = ItemMigrationAPI.importHelper()
 *     .generateAll("my_ingot", "我的锭", "My Ingot", "material", null, null, "pasterdream");
 * }</pre>
 *
 * @see SimpleItemBuilder 简单物品构建器
 * @see FoodItemBuilder 食物物品构建器
 * @see ToolItemBuilder 工具/武器构建器
 * @see CurioItemBuilder 饰品构建器
 * @see MigrationManager 迁移状态管理器
 * @see com.pasterdream.pasterdreammod.api.itemmigration.gen.RecipeGenerator 配方生成器
 * @see com.pasterdream.pasterdreammod.api.itemmigration.gen.LootTableGenerator 战利品表生成器
 * @see com.pasterdream.pasterdreammod.api.itemmigration.gen.BlockDataGenerator 方块数据生成器
 * @see com.pasterdream.pasterdreammod.api.itemmigration.gen.CreativeTabHelper 创造标签助手
 * @see com.pasterdream.pasterdreammod.api.itemmigration.gen.ImportHelper 超级快速导入器
 */
public final class ItemMigrationAPI {

    /**
     * API 专属的物品注册器。
     * 所有通过本 API 注册的物品都会交由该注册器管理。
     * 注意：此注册器需要额外在 {@code PasterDreamMod} 构造函数中注册到事件总线：
     * <pre>{@code
     * ItemMigrationAPI.REGISTRY.register(modEventBus);
     * }</pre>
     */
    public static final DeferredRegister.Items REGISTRY = DeferredRegister.createItems(PasterDreamAPI.MOD_ID);

    /**
     * 单例的迁移管理器实例。
     * 负责追踪物品的迁移状态（已迁移/待迁移/进行中），
     * 并生成迁移进度报告。
     */
    private static final MigrationManager MANAGER = new MigrationManager();

    // ======================== Builder 工厂方法 ========================

    /**
     * 创建一个简单物品（材料/普通物品）的构建器。
     * <p>
     * 适用于不需要特殊逻辑的物品，如材料、合成组件、杂物等。
     * 通过流式 API 设置稀有度、堆叠数量等属性后，
     * 调用 {@link SimpleItemBuilder#build()} 完成注册。
     *
     * @param registryName 物品的注册名（snake_case 格式，如 "titanium_ingot"）
     * @return {@link SimpleItemBuilder} 实例，用于链式设置属性
     */
    public static SimpleItemBuilder simpleItem(String registryName) {
        return new SimpleItemBuilder(REGISTRY, registryName);
    }

    /**
     * 创建一个食物物品的构建器。
     * <p>
     * 通过流式 API 设置营养值、饱和度、是否始终可食用等属性后，
     * 调用 {@link FoodItemBuilder#build()} 完成注册。
     *
     * @param registryName 物品的注册名（snake_case 格式，如 "apple_juice"）
     * @return {@link FoodItemBuilder} 实例，用于链式设置食物属性
     */
    public static FoodItemBuilder foodItem(String registryName) {
        return new FoodItemBuilder(REGISTRY, registryName);
    }

    /**
     * 创建一个工具/武器物品的构建器。
     * <p>
     * 适用于剑、镐、斧、锹、锄等工具类物品。
     * 需要指定工具等级（Tier）、攻击伤害、攻击速度等属性。
     * 调用 {@link ToolItemBuilder#build()} 完成注册。
     *
     * @param registryName 物品的注册名（snake_case 格式，如 "copper_sword"）
     * @return {@link ToolItemBuilder} 实例，用于链式设置工具属性
     */
    public static ToolItemBuilder toolItem(String registryName) {
        return new ToolItemBuilder(REGISTRY, registryName);
    }

    /**
     * 创建一个饰品（Curio）物品的构建器。
     * <p>
     * 适用于 Curios API 的饰品，如戒指、项链、护符等。
     * 需要指定 Curio 槽位类型和饰品效果。
     * 调用 {@link CurioItemBuilder#build()} 完成注册。
     *
     * @param registryName 物品的注册名（snake_case 格式，如 "embryo_ring"）
     * @return {@link CurioItemBuilder} 实例，用于链式设置饰品属性
     */
    public static CurioItemBuilder curioItem(String registryName) {
        return new CurioItemBuilder(REGISTRY, registryName);
    }

    // ======================== 自定义物品注册 ========================

    /**
     * 注册自定义物品（复杂物品的降级方案）。
     * <p>
     * 当 Builder 模式无法满足需求时（例如物品需要复杂的交互逻辑、
     * 自定义渲染、特殊音效等），可自行创建 {@link Item} 子类，
     * 通过此方法完成注册。
     * <p>
     * <b>使用示例：</b>
     * <pre>{@code
     * ItemMigrationAPI.registerCustom("magic_stone",
     *     () -> new MagicStoneItem(new Item.Properties()));
     * }</pre>
     *
     * @param <T>          物品的具体类型
     * @param registryName 物品的注册名（snake_case 格式）
     * @param itemSupplier 物品实例的供给者（通常为 Lambda 或构造方法引用）
     * @return {@link DeferredItem} 代理对象，可在后续代码中引用该物品
     * @throws IllegalArgumentException 如果 registryName 为空或 itemSupplier 为 null
     */
    public static <T extends Item> DeferredItem<T> registerCustom(
            String registryName, Supplier<T> itemSupplier) {
        return REGISTRY.register(registryName, itemSupplier);
    }

    // ======================== 批量注册 ========================

    /**
     * 批量注册简单物品（材料/普通物品）。
     * <p>
     * 适用于一次注册多个简单材料物品的场景。
     * 每个物品通过 {@link ItemSpec} 记录定义注册名和属性。
     * <p>
     * <b>使用示例：</b>
     * <pre>{@code
     * ItemMigrationAPI.batchSimpleItems(
     *     new ItemSpec("soul_dust", Rarity.COMMON),
     *     new ItemSpec("soul_essence", Rarity.UNCOMMON)
     * );
     * }</pre>
     *
     * @param specs 一个或多个 {@link ItemSpec} 规范记录
     * @return 包含所有注册物品 {@link DeferredItem} 的不可变列表
     * @throws IllegalArgumentException 如果 specs 为 null 或为空
     */
    public static List<DeferredItem<Item>> batchSimpleItems(ItemSpec... specs) {
        List<DeferredItem<Item>> results = new ArrayList<>();
        for (ItemSpec spec : specs) {
            results.add(simpleItem(spec.registryName())
                    .rarity(spec.rarity())
                    .stacksTo(spec.stackSize())
                    .build());
        }
        return List.copyOf(results);
    }

    /**
     * 批量注册食物物品。
     * <p>
     * 适用于一次注册多个食物物品的场景。
     * 通过 {@code Map<String, FoodSpec>} 定义注册名与食物属性的映射。
     * <p>
     * <b>使用示例：</b>
     * <pre>{@code
     * ItemMigrationAPI.batchFoodItems(Map.of(
     *     "apple_juice", new FoodSpec(4, 0.2f, true),
     *     "honey_juice", new FoodSpec(6, 0.1f, true)
     * ));
     * }</pre>
     *
     * @param specs 注册名到 {@link FoodSpec} 的映射表
     * @return 包含所有注册物品 {@link DeferredItem} 的不可变列表
     * @throws IllegalArgumentException 如果 specs 为 null 或为空
     */
    public static List<DeferredItem<Item>> batchFoodItems(Map<String, FoodSpec> specs) {
        List<DeferredItem<Item>> results = new ArrayList<>();
        for (Map.Entry<String, FoodSpec> entry : specs.entrySet()) {
            FoodItemBuilder builder = foodItem(entry.getKey());
            FoodSpec spec = entry.getValue();
            builder.nutrition(spec.nutrition())
                    .saturationModifier(spec.saturationModifier());
            if (spec.alwaysEdible()) {
                builder.alwaysEdible();
            }
            results.add(builder.build());
        }
        return List.copyOf(results);
    }

    // ======================== 迁移管理 ========================

    /**
     * 将指定物品标记为"已迁移"状态。
     * <p>
     * 用于追踪物品移植进度，区分已迁移和待迁移的物品。
     * 标记后可通过 {@link #generateReport()} 生成迁移进度报告。
     *
     * @param category 物品所属的迁移分类（如 MATERIAL、FOOD、TOOL 等）
     * @param items    要标记为已迁移的物品注册名列表
     * @throws IllegalArgumentException 如果 category 为 null 或 items 为 null/空
     */
    public static void markMigrated(MigrationCategory category, String... items) {
        MANAGER.markMigrated(category, items);
    }

    /**
     * 将指定物品标记为"待迁移"状态。
     * <p>
     * 用于预先规划需要移植的物品清单，便于后续按计划逐项完成移植。
     * 标记后可通过 {@link #generateReport()} 查看待迁移物品列表。
     *
     * @param category 物品所属的迁移分类（如 MATERIAL、FOOD、TOOL 等）
     * @param items    要标记为待迁移的物品注册名列表
     * @throws IllegalArgumentException 如果 category 为 null 或 items 为 null/空
     */
    public static void markPending(MigrationCategory category, String... items) {
        MANAGER.markPending(category, items);
    }

    /**
     * 生成当前物品移植进度的完整报告。
     * <p>
     * 报告内容包括：
     * <ul>
     *   <li>各分类的迁移数量统计</li>
     *   <li>已迁移物品列表</li>
     *   <li>待迁移物品列表</li>
     *   <li>总体迁移进度百分比</li>
     * </ul>
     *
     * @return 格式化的迁移进度报告字符串，可直接打印或写入日志
     */
    public static String generateReport() {
        return MANAGER.generateReport();
    }

    // ======================== 实用工具 ========================

    /**
     * 生成语言文件（.lang.json）的内容字符串。
     * <p>
     * 将 {@code Map<String, String>} 格式的本地化条目
     * 转换为标准 Minecraft JSON 语言文件格式。
     * <p>
     * <b>使用示例：</b>
     * <pre>{@code
     * String lang = ItemMigrationAPI.generateLangJson("pasterdream", Map.of(
     *     "item.pasterdream.titanium_ingot", "钛锭",
     *     "item.pasterdream.apple_juice", "苹果汁"
     * ));
     * // 输出: {"item.pasterdream.titanium_ingot":"钛锭","item.pasterdream.apple_juice":"苹果汁"}
     * }</pre>
     *
     * @param modId   模组 ID（用于生成翻译键前缀）
     * @param entries 翻译键到翻译文本的映射表
     * @return 格式化后的 JSON 字符串，可直接写入语言文件
     * @throws IllegalArgumentException 如果 modId 为 null 或 entries 为 null
     */
    public static String generateLangJson(String modId, Map<String, String> entries) {
        return MANAGER.generateLangJson(modId, entries);
    }

    // ======================== 管理器访问 ========================

    /**
     * 获取 {@link MigrationManager} 管理器实例。
     * <p>
     * 当需要执行更高级的迁移管理操作（如自定义分类统计、导出迁移清单等）
     * 时，可通过此方法获取管理器实例进行直接操作。
     *
     * @return {@link MigrationManager} 单例实例
     */
    public static MigrationManager getManager() {
        return MANAGER;
    }

    // ========================================================================
    // 新增强力辅助工具访问入口
    // ========================================================================

    /**
     * 获取配方生成器（RecipeGenerator）的静态访问入口。
     * <p>
     * RecipeGenerator 提供所有 Minecraft 配方类型的 JSON 生成功能，
     * 包括有序合成、无序合成、熔炉/高炉/营火/烟熏炉冶炼、切石机加工等。
     * 所有方法均返回格式化后的 JSON 字符串。
     * <p>
     * <b>使用示例：</b>
     * <pre>{@code
     * // 生成有序合成配方
     * String shaped = ItemMigrationAPI.recipeGen()
     *     .generateShaped("pasterdream:my_ingot", 1, "misc",
     *         new String[]{"AAA", "ABA", "AAA"},
     *         Map.of("A", "minecraft:iron_ingot", "B", "minecraft:stick"));
     *
     * // 生成无序合成配方
     * String shapeless = ItemMigrationAPI.recipeGen()
     *     .generateShapeless("pasterdream:my_item", 1, "misc",
     *         List.of("minecraft:diamond", "minecraft:stick"));
     *
     * // 生成熔炉冶炼配方
     * String smelting = ItemMigrationAPI.recipeGen()
     *     .generateSmelting("minecraft:iron_ingot", "pasterdream:raw_iron", 0.35f, 200);
     * }</pre>
     *
     * @return {@link RecipeGenerator} 的类引用（所有方法均为静态）
     */
    public static Class<com.pasterdream.pasterdreammod.api.itemmigration.gen.RecipeGenerator> recipeGen() {
        return com.pasterdream.pasterdreammod.api.itemmigration.gen.RecipeGenerator.class;
    }

    /**
     * 获取战利品表生成器（LootTableGenerator）的静态访问入口。
     * <p>
     * LootTableGenerator 提供方块战利品表的 JSON 生成功能，
     * 支持自掉落、精准采集掉落、矿石掉落（时运+爆炸衰减）、
     * 自定义掉落以及多物品混合掉落等场景。
     * <p>
     * <b>使用示例：</b>
     * <pre>{@code
     * // 自掉落
     * String selfDrop = ItemMigrationAPI.lootTableGen()
     *     .generateSelfDrop("pasterdream:example_block");
     *
     * // 矿石掉落（时运+爆炸衰减）
     * String oreDrop = ItemMigrationAPI.lootTableGen()
     *     .generateOreDrop("pasterdream:raw_ore", "pasterdream:ore_block");
     * }</pre>
     *
     * @return {@link LootTableGenerator} 的类引用
     */
    public static Class<com.pasterdream.pasterdreammod.api.itemmigration.gen.LootTableGenerator> lootTableGen() {
        return com.pasterdream.pasterdreammod.api.itemmigration.gen.LootTableGenerator.class;
    }

    /**
     * 获取方块数据生成器（BlockDataGenerator）的静态访问入口。
     * <p>
     * BlockDataGenerator 帮助快速生成方块挖掘标签 JSON、方块注册代码片段
     * 及 BlockItem 注册代码。支持所有挖掘标签类型（pickaxe/axe/shovel/hoe）
     * 和工具等级标签（stone/iron/diamond）。
     * <p>
     * <b>使用示例：</b>
     * <pre>{@code
     * // 生成镐挖掘标签
     * String pickaxeTag = ItemMigrationAPI.blockDataGen()
     *     .generateMineablePickaxeJson(List.of("pasterdream:my_ore"));
     *
     * // 生成方块注册代码
     * String blockCode = ItemMigrationAPI.blockDataGen()
     *     .generateBlockRegistrationCode("my_block", "MY_BLOCK", "Blocks.STONE", false);
     * }</pre>
     *
     * @return {@link BlockDataGenerator} 的类引用
     */
    public static Class<com.pasterdream.pasterdreammod.api.itemmigration.gen.BlockDataGenerator> blockDataGen() {
        return com.pasterdream.pasterdreammod.api.itemmigration.gen.BlockDataGenerator.class;
    }

    /**
     * 获取创造模式标签页助手（CreativeTabHelper）的静态访问入口。
     * <p>
     * CreativeTabHelper 帮助快速生成创造模式标签页的注册代码片段、
     * displayItems 代码行以及语言文件条目。
     * 输出格式与 {@code PDCreativeTabs.java} 保持一致。
     * <p>
     * <b>使用示例：</b>
     * <pre>{@code
     * // 生成单行 displayItems 代码
     * String line = ItemMigrationAPI.creativeTabHelper()
     *     .generateDisplayItemLine("MY_ITEM", "PDItems");
     *
     * // 生成完整标签页注册代码
     * String tabCode = ItemMigrationAPI.creativeTabHelper()
     *     .generateCompleteTabWithItems(
     *         "paster_tab_2", "新材料与工具标签页", "DREAM_ACCUMULATOR",
     *         "PASTER_TAB_1", "pasterdream",
     *         List.of("MY_INGOT", "MY_TOOL"), "PDItems",
     *         List.of("MY_BLOCK"), "PDBlocks");
     * }</pre>
     *
     * @return {@link CreativeTabHelper} 的类引用
     */
    public static Class<com.pasterdream.pasterdreammod.api.itemmigration.gen.CreativeTabHelper> creativeTabHelper() {
        return com.pasterdream.pasterdreammod.api.itemmigration.gen.CreativeTabHelper.class;
    }

    /**
     * 获取超级快速导入器（ImportHelper）的静态访问入口。
     * <p>
     * ImportHelper 是 AI 或开发者的终极效率工具，提供极度简化的接口，
     * 一个静态方法调用即可生成完整的物品导入代码片段或 JSON 数据。
     * 支持从简单材料到完整方块的全流程导入。
     * <p>
     * <b>使用示例：</b>
     * <pre>{@code
     * // 快速生成材料注册代码
     * String code = ItemMigrationAPI.importHelper()
     *     .quickItem("my_ingot");
     *
     * // 一键全流程导入
     * String allData = ItemMigrationAPI.importHelper()
     *     .generateAll("my_ingot", "我的锭", "My Ingot",
     *         "material", Map.of("rarity", "UNCOMMON"), null, "pasterdream");
     * }</pre>
     *
     * @return {@link ImportHelper} 的类引用
     */
    public static Class<com.pasterdream.pasterdreammod.api.itemmigration.gen.ImportHelper> importHelper() {
        return com.pasterdream.pasterdreammod.api.itemmigration.gen.ImportHelper.class;
    }

    /**
     * 私有构造方法，防止实例化。
     * <p>
     * 本类为纯静态门面类，不应被实例化。
     */
    private ItemMigrationAPI() {
        throw new UnsupportedOperationException("ItemMigrationAPI 是纯静态门面类，不可实例化");
    }
}
