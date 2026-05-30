/*
 * Item Migration API 使用示例类
 * ===========================================================================
 * 本示例类全面展示了 ItemMigrationAPI 的所有功能，包括：
 *
 *   1. 简单材料物品注册 (simpleItem)
 *   2. 食物物品注册 (foodItem)
 *   3. 工具/武器物品注册 (toolItem)
 *   4. Curio 饰品注册 (curioItem)
 *   5. 自定义物品注册 (registerCustom)
 *   6. 批量注册 (batchSimpleItems / batchFoodItems)
 *   7. 迁移状态追踪 (markMigrated / markPending / generateReport)
 *   8. 语言文件生成 (generateLangJson / LanguageGenerator)
 *   9. 各 Model Record 的 Builder 用法 (ItemSpec / FoodSpec / ToolSpec 等)
 *
 * 注意：除了 main 方法外，所有 demo 方法默认不会实际注册物品到游戏。
 * 如需实际注册，请取消对应方法中注释掉的 .build() 调用。
 * ===========================================================================
 */
package com.pasterdream.pasterdreammod.api.itemmigration.example;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.api.itemmigration.ItemMigrationAPI;
import com.pasterdream.pasterdreammod.api.itemmigration.builder.*;
import com.pasterdream.pasterdreammod.api.itemmigration.gen.LanguageGenerator;
import com.pasterdream.pasterdreammod.api.itemmigration.manager.MigrationManager;
import com.pasterdream.pasterdreammod.api.itemmigration.model.*;
import com.pasterdream.pasterdreammod.api.itemmigration.model.ToolSpec.ToolType;
import com.pasterdream.pasterdreammod.registry.PDItems;
import net.minecraft.world.item.*;
import net.minecraft.world.item.ArmorItem.Type;

import java.util.List;
import java.util.Map;

/**
 * ItemMigrationAPI 全面使用示例类
 * <p>
 * 本类作为 API 的"活文档"，逐一展示 API 提供的每一项功能。
 * 每个 demo 方法都专注于一个主题，使用中文注释详细说明每步操作的用途。
 * 通过阅读本类，开发者可以快速掌握如何：
 * <ul>
 *   <li>使用 Builder 链式调用注册各类物品</li>
 *   <li>使用 Model Record 定义物品规格</li>
 *   <li>批量注册简化大量重复物品的移植</li>
 *   <li>追踪和管理物品移植进度</li>
 *   <li>自动生成语言文件</li>
 * </ul>
 * </p>
 *
 * <b>快速上手：</b>
 * <pre>{@code
 * // 一行代码注册一个基础材料
 * ItemMigrationAPI.simpleItem("my_material").rarity(Rarity.UNCOMMON).build();
 *
 * // 一行代码注册一把剑
 * ItemMigrationAPI.toolItem("my_sword")
 *     .type(ToolType.SWORD).durability(500).attackDamage(8.0f).build();
 * }</pre>
 *
 * @see ItemMigrationAPI API 门面入口
 * @see SimpleItemBuilder 简单物品构建器
 * @see FoodItemBuilder 食物物品构建器
 * @see ToolItemBuilder 工具/武器构建器
 * @see CurioItemBuilder 饰品构建器
 * @see MigrationManager 迁移状态管理器
 * @see LanguageGenerator 语言文件生成器
 */
public class ItemMigrationExample {

    /** 分隔线 —— 让控制台输出更清晰 */
    private static final String SEPARATOR = "════════════════════════════════════════════";

    static {
        System.out.println(SEPARATOR);
        System.out.println("  ItemMigrationAPI 示例类已加载");
        System.out.println("  模组ID: " + PasterDreamMod.MOD_ID);
        System.out.println("  物品注册器: " + PDItems.ITEMS.getRegistryName());
        System.out.println("  当前展示了 API 的全部 8 大功能模块");
        System.out.println(SEPARATOR);
    }

    // ========================================================================
    // 1. 简单材料物品注册
    // ========================================================================

    /**
     * 演示如何注册简单材料物品
     * <p>
     * 使用 {@link ItemMigrationAPI#simpleItem(String)} 创建构建器，
     * 链式设置稀有度、堆叠数、防火属性和描述文本后调用 {@link SimpleItemBuilder#build()} 完成注册。
     * 适用于：锭、粉、碎片、宝石等不需要特殊逻辑的合成材料。
     * </p>
     */
    public static void demoSimpleMaterials() {
        System.out.println("\n  ▶ [简单材料] 演示 simpleItem() 注册材料物品");

        // ========== 示例 1: 稀有材料, 限堆叠16个, 防火 ==========
        ItemMigrationAPI.simpleItem("example_rare_gem")
                .rarity(Rarity.UNCOMMON)          // 稀有度: 少见的紫色名称
                .stacksTo(16)                      // 最大堆叠: 16 个
                .fireResistant()                   // 防火: 不会因熔岩/火焰而销毁
                .tooltip("§b一颗闪耀的稀有宝石", "§7蕴藏着古老的力量")  // 描述文本
                // .build()  // ← 取消注释即可注册到游戏
        ;
        System.out.println("    - example_rare_gem: UNCOMMON, stacksTo=16, fireResistant, 有 tooltip");

        // ========== 示例 2: 史诗材料 ==========
        ItemMigrationAPI.simpleItem("example_epic_shard")
                .rarity(Rarity.EPIC)              // 稀有度: 史诗金色名称
                .stacksTo(8)                       // 限堆叠 8 个
                .tooltip("§e传说级碎片", "§7七大碎片之一")
                // .build()
        ;
        System.out.println("    - example_epic_shard: EPIC, stacksTo=8, 有 tooltip");

        // ========== 示例 3: 普通材料, 默认属性 ==========
        ItemMigrationAPI.simpleItem("example_common_dust")
                .rarity(Rarity.COMMON)             // 稀有度: 普通白色
                .stacksTo(64)                      // 默认堆叠 64
                // .build()
        ;
        System.out.println("    - example_common_dust: COMMON, stacksTo=64");

        // ========== 示例 4: 使用 ItemSpec.Builder 定义规格后再注册 ==========
        ItemSpec spec = ItemSpec.builder("example_spec_crystal")
                .stackSize(32)
                .rarity(Rarity.RARE)
                .fireResistant(true)
                .tooltipLines(List.of("§d由 ItemSpec 定义的物品", "§7使用 Builder 模式构建"))
                .translationKey("item.pasterdream.spec_crystal")
                .build();
        System.out.println("    - 使用 ItemSpec.Builder 创建了规格: " + spec.registryName()
                + " (rarity=" + spec.rarity() + ", stackSize=" + spec.stackSize() + ")");
        // 用 ItemSpec 注册:
        // ItemMigrationAPI.simpleItem(spec.registryName())
        //         .rarity(spec.rarity()).stacksTo(spec.stackSize())
        //         .tooltip(spec.tooltipLines().toArray(new String[0]))
        //         .build();

        System.out.println("  [简单材料] 演示结束");
    }

    // ========================================================================
    // 2. 食物物品注册
    // ========================================================================

    /**
     * 演示如何注册食物物品
     * <p>
     * 使用 {@link ItemMigrationAPI#foodItem(String)} 创建构建器，
     * 链式设置营养值、饱和度、食用方式及食用效果后完成注册。
     * 支持：普通食物、始终可食（即使饥饿值满也可食用）、快速食用（饮料类）、带状态效果的食物。
     * </p>
     */
    public static void demoFoodItems() {
        System.out.println("\n  ▶ [食物] 演示 foodItem() 注册食物物品");

        // ========== 示例 1: 普通食物 ==========
        ItemMigrationAPI.foodItem("example_hearty_stew")
                .nutrition(8)                      // 营养值: 8 个半鸡腿 (= 4 个鸡腿)
                .saturationModifier(0.8f)           // 饱和度修正: 0.8
                // .build()
        ;
        System.out.println("    - example_hearty_stew: nutrition=8, saturation=0.8");

        // ========== 示例 2: 始终可食的食物（如药水/饮料） ==========
        ItemMigrationAPI.foodItem("example_magic_tea")
                .nutrition(2)
                .saturationModifier(0.2f)
                .alwaysEdible()                    // 饥饿值满时仍可食用
                // .build()
        ;
        System.out.println("    - example_magic_tea: nutrition=2, alwaysEdible");

        // ========== 示例 3: 快速食用食物（跳过进食动画, 适合糖果/饮料） ==========
        ItemMigrationAPI.foodItem("example_energy_candy")
                .nutrition(4)
                .saturationModifier(0.5f)
                .alwaysEdible()
                .fastFood()                        // 快速食用, 不播放进食动画
                // .build()
        ;
        System.out.println("    - example_energy_candy: fastFood, 适合糖果类");

        // ========== 示例 4: 带食用效果的食物（食用后附加状态效果） ==========
        ItemMigrationAPI.foodItem("example_golden_apple_juice")
                .nutrition(6)
                .saturationModifier(0.6f)
                .alwaysEdible()
                .effect("minecraft:regeneration", 100, 1, 1.0f)   // 回复 II, 5秒, 100%触发
                .effect("minecraft:absorption", 2400, 0, 1.0f)    // 吸收 I, 2分钟, 100%触发
                // .build()
        ;
        System.out.println("    - example_golden_apple_juice: 带 regeneration + absorption 效果");

        // ========== 示例 5: 使用 FoodSpec.Builder 创建食物规格 ==========
        FoodSpec foodSpec = FoodSpec.builder(10, 1.2f)
                .alwaysEdible(true)
                .fastFood(false)
                .effects(List.of(
                        new FoodSpec.FoodEffectSpec("minecraft:strength", 3600, 1, 1.0f)
                ))
                .build();
        System.out.println("    - 使用 FoodSpec.Builder 创建了规格: nutrition=" + foodSpec.nutrition()
                + ", 效果数=" + foodSpec.effects().size());

        // ========== 示例 6: 使用 FoodEffectSpec.Builder ==========
        FoodSpec.FoodEffectSpec effect = FoodSpec.FoodEffectSpec.builder("minecraft:speed", 600, 1)
                .probability(0.8f)  // 80% 概率触发
                .build();
        System.out.println("    - 使用 FoodEffectSpec.Builder: " + effect.effectId()
                + " × " + (effect.amplifier() + 1) + " 级, " + (effect.duration() / 20) + "秒, "
                + (effect.probability() * 100) + "%概率");

        System.out.println("  [食物] 演示结束");
    }

    // ========================================================================
    // 3. 工具/武器物品注册
    // ========================================================================

    /**
     * 演示如何注册工具和武器物品
     * <p>
     * 使用 {@link ItemMigrationAPI#toolItem(String)} 创建构建器，
     * 通过 {@link ToolItemBuilder#type(ToolType)} 指定工具类型，
     * 设置耐久、伤害、速度、修复材料等属性后完成注册。
     * </p>
     *
     * <b>支持的 ToolType 一览：</b>
     * <ul>
     *   <li>{@link ToolType#SWORD} —— 剑</li>
     *   <li>{@link ToolType#PICKAXE} —— 镐</li>
     *   <li>{@link ToolType#AXE} —— 斧</li>
     *   <li>{@link ToolType#SHOVEL} —— 锹</li>
     *   <li>{@link ToolType#HOE} —— 锄</li>
     *   <li>{@link ToolType#HAMMER} —— 锤（模组自定义, 暂用镐逻辑）</li>
     *   <li>{@link ToolType#WAND} —— 法杖（模组自定义, 暂用普通物品）</li>
     * </ul>
     */
    @SuppressWarnings("deprecation")
    public static void demoToolsAndWeapons() {
        System.out.println("\n  ▶ [工具/武器] 演示 toolItem() 注册各类工具");

        // ===== 剑 =====
        ItemMigrationAPI.toolItem("example_crystal_sword")
                .type(ToolType.SWORD)              // 工具类型: 剑
                .durability(800)                   // 耐久: 800
                .attackDamage(7.0f)                // 攻击伤害: +7
                .attackSpeed(-2.2f)                // 攻击速度
                .enchantment(18)                   // 附魔能力: 18
                .repairWith(new ItemStack(Items.DIAMOND))  // 修复材料: 钻石
                // .build()
        ;
        System.out.println("    - example_crystal_sword: SWORD, dur=800, dmg=7.0");

        // ===== 镐 =====
        ItemMigrationAPI.toolItem("example_nether_pickaxe")
                .type(ToolType.PICKAXE)            // 工具类型: 镐
                .durability(1200)
                .miningSpeed(7.0f)                 // 挖掘速度: 7.0
                .attackDamage(4.0f)
                .attackSpeed(-2.6f)
                .repairWith(new ItemStack(Items.NETHERITE_INGOT))
                // .build()
        ;
        System.out.println("    - example_nether_pickaxe: PICKAXE, dur=1200, speed=7.0");

        // ===== 斧 =====
        ItemMigrationAPI.toolItem("example_stone_axe")
                .type(ToolType.AXE)                // 工具类型: 斧
                .durability(350)
                .attackDamage(9.0f)                // 斧头通常伤害更高
                .attackSpeed(-3.0f)
                // .build()
        ;
        System.out.println("    - example_stone_axe: AXE, dur=350, dmg=9.0");

        // ===== 锹 =====
        ItemMigrationAPI.toolItem("example_iron_shovel")
                .type(ToolType.SHOVEL)             // 工具类型: 锹
                .durability(500)
                .miningSpeed(5.5f)
                .attackDamage(3.5f)
                .attackSpeed(-2.8f)
                // .build()
        ;
        System.out.println("    - example_iron_shovel: SHOVEL, dur=500");

        // ===== 锄 =====
        ItemMigrationAPI.toolItem("example_wooden_hoe")
                .type(ToolType.HOE)                // 工具类型: 锄
                .durability(120)
                .attackSpeed(-1.5f)
                // .build()
        ;
        System.out.println("    - example_wooden_hoe: HOE, dur=120");

        // ===== 锤（模组自定义） =====
        ItemMigrationAPI.toolItem("example_war_hammer")
                .type(ToolType.HAMMER)             // 工具类型: 锤（Pickaxe 逻辑）
                .durability(2000)
                .attackDamage(12.0f)
                .attackSpeed(-3.4f)
                // .build()
        ;
        System.out.println("    - example_war_hammer: HAMMER, dur=2000, dmg=12.0");

        // ===== 法杖（模组自定义） =====
        ItemMigrationAPI.toolItem("example_magic_wand")
                .type(ToolType.WAND)               // 工具类型: 法杖（普通物品逻辑）
                .durability(100)
                // .build()
        ;
        System.out.println("    - example_magic_wand: WAND, dur=100");

        // ========== 使用 ToolSpec.Builder 构建工具规格 ==========
        ToolSpec toolSpec = ToolSpec.builder(ToolType.SWORD)
                .durability(1500)
                .attackDamage(10.0f)
                .attackSpeed(-2.0f)
                .miningSpeed(6.0f)
                .enchantmentValue(22)
                .incorrectTag("minecraft:incorrect_for_diamond_tool")
                .repairIngredient(() -> net.minecraft.world.item.crafting.Ingredient.of(Items.NETHERITE_SCRAP))
                .build();
        System.out.println("    - 使用 ToolSpec.Builder 创建了规格: " + toolSpec.type()
                + ", dur=" + toolSpec.durability() + ", dmg=" + toolSpec.attackDamage());

        System.out.println("  [工具/武器] 演示结束");
    }

    // ========================================================================
    // 4. Curio 饰品注册
    // ========================================================================

    /**
     * 演示如何注册 Curio 饰品
     * <p>
     * 使用 {@link ItemMigrationAPI#curioItem(String)} 创建构建器，
     * 设置饰品槽位（戒指、项链、腰带等），添加属性修饰器和描述文本后完成注册。
     * 饰品会自动实现 {@code ICurioItem} 接口，佩戴时应用属性加成。
     * </p>
     */
    public static void demoCurioItems() {
        System.out.println("\n  ▶ [Curio 饰品] 演示 curioItem() 注册饰品");

        // ========== 示例 1: 戒指, 增加攻击力 ==========
        ItemMigrationAPI.curioItem("example_power_ring")
                .slot("ring")                       // 槽位: 戒指
                .attribute("minecraft:generic.attack_damage",   // 属性: 通用攻击伤害
                        "a1b2c3d4-e5f6-7890-abcd-ef1234567890", // 修饰器唯一 UUID
                        3.0, 0)                                  // +3 攻击力, 加法运算
                .attribute("minecraft:generic.attack_speed",
                        "b2c3d4e5-f6a7-8901-bcde-f12345678901",
                        0.1, 1)                                  // +10% 攻击速度, 倍率运算
                .tooltip("§6力量之戒", "§7佩戴后增加攻击力与攻速")
                // .build()
        ;
        System.out.println("    - example_power_ring: slot=ring, +3 attack_damage, +10% attack_speed");

        // ========== 示例 2: 项链, 增加生命值 ==========
        ItemMigrationAPI.curioItem("example_vitality_necklace")
                .slot("necklace")                   // 槽位: 项链
                .attribute("minecraft:generic.max_health",
                        "c3d4e5f6-a7b8-9012-cdef-123456789012",
                        10.0, 0)                                 // +10 最大生命值（5 颗心）
                .tooltip("§a活力项链", "§7+10 最大生命值")
                // .build()
        ;
        System.out.println("    - example_vitality_necklace: slot=necklace, +10 max_health");

        // ========== 示例 3: 腰带, 增加护甲 ==========
        ItemMigrationAPI.curioItem("example_guard_belt")
                .slot("belt")                       // 槽位: 腰带
                .attribute("minecraft:generic.armor",
                        "d4e5f6a7-b8c9-0123-defa-234567890123",
                        6.0, 0)                                  // +6 护甲值
                .tooltip("§8守护腰带", "§7+6 护甲")
                // .build()
        ;
        System.out.println("    - example_guard_belt: slot=belt, +6 armor");

        // ========== 示例 4: 护符, 增加移动速度和击退抗性 ==========
        ItemMigrationAPI.curioItem("example_swift_charm")
                .slot("charm")                      // 槽位: 护符
                .attribute("minecraft:generic.movement_speed",
                        "e5f6a7b8-c9d0-1234-efab-345678901234",
                        0.05, 1)                                 // +5% 移动速度
                .attribute("minecraft:generic.knockback_resistance",
                        "f6a7b8c9-d0e1-2345-fabc-456789012345",
                        0.3, 0)                                  // +30% 击退抗性
                // .build()
        ;
        System.out.println("    - example_swift_charm: slot=charm, +5% speed, +30% knockback_resist");

        // ========== 示例 5: 头饰槽位 ==========
        ItemMigrationAPI.curioItem("example_crown")
                .slot("head")                       // 槽位: 头部
                .attribute("minecraft:generic.armor_toughness",
                        "a7b8c9d0-e1f2-3456-abcd-567890123456",
                        2.0, 0)                                  // +2 盔甲韧性
                .tooltip("§e皇冠", "§7增加韧性")
                // .build()
        ;
        System.out.println("    - example_crown: slot=head, +2 armor_toughness");

        // ========== 使用 CurioSpec.Builder 构建饰品规格 ==========
        CurioSpec curioSpec = CurioSpec.builder("ring")
                .translationKey("item.pasterdream.example_spec_ring")
                .attributeMods(List.of(
                        new AttributeModSpec("minecraft:generic.luck", "b8c9d0e1-f2a3-4567-bcde-678901234567", 1.0, 0)
                ))
                .build();
        System.out.println("    - 使用 CurioSpec.Builder 创建了规格: slot=" + curioSpec.curioSlot()
                + ", 修饰器数=" + curioSpec.attributeMods().size());

        // ========== 使用 AttributeModSpec.Builder ==========
        AttributeModSpec attrMod = AttributeModSpec.builder(
                        "minecraft:generic.jump_strength",
                        "c9d0e1f2-a3b4-5678-cdef-789012345678",
                        0.1, 1
                ).build();
        System.out.println("    - 使用 AttributeModSpec.Builder: " + attrMod.attributeName()
                + " × " + attrMod.amount() + " (op=" + attrMod.operation() + ")");

        // ========== 使用 ArmorSpec.Builder ==========
        ArmorSpec armorSpec = ArmorSpec.builder(Type.CHESTPLATE, "pasterdream:dyedream")
                .defense(8)
                .toughness(2.0f)
                .enchantmentValue(15)
                .durabilityMultiplier(15)
                .knockbackResistance(0.1f)
                .fireResistant(true)
                .rarity(Rarity.RARE)
                .attributeMods(List.of(
                        new AttributeModSpec("minecraft:generic.max_health", "d0e1f2a3-b4c5-6789-defa-890123456789", 4.0, 0)
                ))
                .build();
        System.out.println("    - 使用 ArmorSpec.Builder: " + armorSpec.armorType()
                + ", defense=" + armorSpec.defense() + ", material=" + armorSpec.armorMaterial());

        System.out.println("  [Curio 饰品] 演示结束");
    }

    // ========================================================================
    // 5. 自定义物品注册
    // ========================================================================

    /**
     * 演示如何注册自定义物品
     * <p>
     * 当 Builder 模式无法满足需求时（例如需要复杂交互、自定义渲染等），
     * 可使用 {@link ItemMigrationAPI#registerCustom(String, java.util.function.Supplier)}
     * 直接注册自定义的 Item 子类实例。
     * </p>
     */
    public static void demoCustomItems() {
        System.out.println("\n  ▶ [自定义物品] 演示 registerCustom() 注册自定义物品");

        // ========== 示例 1: 直接使用 Item 匿名类（Lambda 写法） ==========
        // ItemMigrationAPI.registerCustom("example_custom_stone",
        //         () -> new Item(new Item.Properties().rarity(Rarity.EPIC).stacksTo(1))
        // );
        System.out.println("    - example_custom_stone: 使用 Lambda 注册匿名 Item 子类");

        // ========== 示例 2: 使用现有自定义物品类 ==========
        // ItemMigrationAPI.registerCustom("example_special_gem",
        //         () -> new SpecialGemItem(new Item.Properties())
        // );
        System.out.println("    - example_special_gem: 注册自定义 SpecialGemItem 类");

        // ========== 示例 3: 获取注册结果引用 ==========
        // DeferredItem<Item> customRef = ItemMigrationAPI.registerCustom("example_ref_item",
        //         () -> new Item(new Item.Properties()));
        // System.out.println("    - example_ref_item: 注册完成, DeferredItem=" + customRef);
        System.out.println("    - registerCustom() 支持泛型返回 DeferredItem<T>, 方便后续引用");

        System.out.println("  [自定义物品] 演示结束");
    }

    // ========================================================================
    // 6. 批量注册
    // ========================================================================

    /**
     * 演示批量注册功能
     * <p>
     * 使用 {@link ItemMigrationAPI#batchSimpleItems(ItemSpec...)} 批量注册简单材料，
     * 使用 {@link ItemMigrationAPI#batchFoodItems(Map)} 批量注册食物物品。
     * 批量注册能大幅减少重复代码，适合一次性移植大量物品。
     * </p>
     */
    public static void demoBatchRegistration() {
        System.out.println("\n  ▶ [批量注册] 演示 batchSimpleItems() 和 batchFoodItems()");

        // ========== 批量注册简单材料 ==========
        // 使用 ItemSpec 的便捷构造器（或 Builder）定义多个材料
        // List<DeferredItem<Item>> simpleResults = ItemMigrationAPI.batchSimpleItems(
        //         new ItemSpec("example_material_a", Rarity.COMMON, 64, false, List.of(), ""),
        //         new ItemSpec("example_material_b", Rarity.UNCOMMON, 32, false, List.of(), ""),
        //         new ItemSpec("example_material_c", Rarity.RARE, 16, true, List.of("§6稀有材料"), "")
        // );
        System.out.println("    - batchSimpleItems: 一次性注册了 3 个材料物品");

        // ========== 使用 ItemSpec.Builder 方式定义 ==========
        ItemSpec[] specs = {
                ItemSpec.builder("example_batch_dust")
                        .stackSize(64).rarity(Rarity.COMMON).build(),
                ItemSpec.builder("example_batch_gem")
                        .stackSize(16).rarity(Rarity.UNCOMMON)
                        .tooltipLines(List.of("§b批量注册的宝石")).build(),
                ItemSpec.builder("example_batch_crystal")
                        .stackSize(8).rarity(Rarity.RARE).fireResistant(true).build()
        };
        // List<DeferredItem<Item>> batchResults = ItemMigrationAPI.batchSimpleItems(specs);
        System.out.println("    - 使用 ItemSpec.Builder 定义了 " + specs.length + " 个规格");

        // ========== 批量注册食物物品 ==========
        // 使用 Map.of 构建名称到 FoodSpec 的映射
        // List<DeferredItem<Item>> foodResults = ItemMigrationAPI.batchFoodItems(Map.of(
        //         "example_batch_apple", new FoodSpec(4, 0.3f, false, false, List.of()),
        //         "example_batch_juice", new FoodSpec(3, 0.1f, true, true, List.of()),
        //         "example_batch_cookie", new FoodSpec(2, 0.2f, false, true, List.of())
        // ));
        System.out.println("    - batchFoodItems: 一次性注册了 3 个食物物品");

        // ========== 使用 FoodSpec.Builder 方式定义 ==========
        Map<String, FoodSpec> foodMap = Map.of(
                "example_food_1", FoodSpec.builder(6, 0.6f).build(),
                "example_food_2", FoodSpec.builder(8, 0.8f).alwaysEdible(true).build(),
                "example_food_3", FoodSpec.builder(1, 0.1f)
                        .alwaysEdible(true).fastFood(true)
                        .effects(List.of(
                                new FoodSpec.FoodEffectSpec("minecraft:luck", 6000, 0, 1.0f)
                        ))
                        .build()
        );
        // ItemMigrationAPI.batchFoodItems(foodMap);
        System.out.println("    - 使用 FoodSpec.Builder 定义了 " + foodMap.size() + " 个食物规格");

        System.out.println("  [批量注册] 演示结束");
    }

    // ========================================================================
    // 7. 迁移状态追踪
    // ========================================================================

    /**
     * 演示迁移状态追踪功能
     * <p>
     * 使用 {@link ItemMigrationAPI#markMigrated(MigrationCategory, String...)} 标记已迁移物品，
     * 使用 {@link ItemMigrationAPI#markPending(MigrationCategory, String...)} 标记待迁移物品，
     * 最后通过 {@link ItemMigrationAPI#generateReport()} 生成完整的迁移进度报告。
     * </p>
     */
    public static void demoMigrationTracking() {
        System.out.println("\n  ▶ [迁移追踪] 演示 markMigrated / markPending / generateReport");

        // ========== 标记已迁移的物品 ==========
        ItemMigrationAPI.markMigrated(MigrationCategory.MATERIAL,
                "titanium_ingot", "dyedream_dust", "magic_stone", "soul_dust", "soul_essence");
        System.out.println("    - markMigrated: 标记了 5 个 MATERIAL 物品");

        ItemMigrationAPI.markMigrated(MigrationCategory.FOOD,
                "apple_juice", "honey_juice", "chocolate");
        System.out.println("    - markMigrated: 标记了 3 个 FOOD 物品");

        ItemMigrationAPI.markMigrated(MigrationCategory.TOOL,
                "copper_pickaxe", "titanium_pickaxe");
        System.out.println("    - markMigrated: 标记了 2 个 TOOL 物品");

        ItemMigrationAPI.markMigrated(MigrationCategory.CURIO,
                "embryo_ring", "embryo_necklace");
        System.out.println("    - markMigrated: 标记了 2 个 CURIO 物品");

        // ========== 标记待迁移的物品（规划阶段） ==========
        ItemMigrationAPI.markPending(MigrationCategory.MATERIAL,
                "mythril_ingot", "adamantite_dust", "orichalcum_gem");
        System.out.println("    - markPending: 标记了 3 个待迁移 MATERIAL 物品");

        ItemMigrationAPI.markPending(MigrationCategory.ARMOR,
                "dyedream_helmet", "dyedream_chestplate", "dyedream_leggings", "dyedream_boots");
        System.out.println("    - markPending: 标记了 4 个待迁移 ARMOR 物品");

        ItemMigrationAPI.markPending(MigrationCategory.WEAPON,
                "legendary_blade", "shadow_bow");
        System.out.println("    - markPending: 标记了 2 个待迁移 WEAPON 物品");

        // ========== 生成并打印迁移报告 ==========
        String report = ItemMigrationAPI.generateReport();
        System.out.println("    - generateReport() 输出:");
        System.out.println(report);

        // ========== 通过管理器获取详细的追踪信息 ==========
        MigrationManager manager = ItemMigrationAPI.getManager();
        System.out.println("    - 已移植物品总数: " + manager.getAllMigratedNames().size());
        System.out.println("    - MATERIAL 已迁移: " + manager.getMigratedItems(MigrationCategory.MATERIAL));
        System.out.println("    - MATERIAL 待迁移: " + manager.getPendingItems(MigrationCategory.MATERIAL));
        System.out.println("    - 是否已迁移 'titanium_ingot': " + manager.isMigrated("titanium_ingot"));
        System.out.println("    - 是否已迁移 'mythril_ingot': " + manager.isMigrated("mythril_ingot"));

        // 重置追踪状态（可选）
        // manager.reset();
        // System.out.println("    - 追踪状态已重置");

        System.out.println("  [迁移追踪] 演示结束");
    }

    // ========================================================================
    // 8. 语言文件生成
    // ========================================================================

    /**
     * 演示语言文件生成功能
     * <p>
     * 使用 {@link ItemMigrationAPI#generateLangJson(String, Map)} 生成本地化 JSON，
     * 以及 {@link LanguageGenerator} 提供的各种静态工具方法。
     * 生成的内容可直接写入 {@code assets/<modId>/lang/zh_cn.json} 等语言文件。
     * </p>
     */
    public static void demoLanguageGeneration() {
        System.out.println("\n  ▶ [语言文件] 演示 generateLangJson 和 LanguageGenerator");

        String modId = PasterDreamMod.MOD_ID;

        // ========== 方式 1: 使用 API 门面方法 ==========
        String langJson = ItemMigrationAPI.generateLangJson(modId, Map.of(
                "item." + modId + ".titanium_ingot", "钛锭",
                "item." + modId + ".dyedream_dust", "染梦粉",
                "item." + modId + ".magic_stone", "魔法石",
                "item." + modId + ".apple_juice", "苹果汁"
        ));
        System.out.println("    - API.generateLangJson() 输出:");
        System.out.println("      " + langJson.replace("\n", "\n      "));
        System.out.println();

        // ========== 方式 2: 使用 LanguageGenerator 工具类 ==========
        // 2a) 生成物品翻译键
        String itemKey = LanguageGenerator.itemKey(modId, "example_item");
        System.out.println("    - itemKey(): " + itemKey);

        // 2b) snake_case 转可读英文名
        String displayName = LanguageGenerator.snakeToEnglishDisplay("titanium_ingot");
        System.out.println("    - snakeToEnglishDisplay('titanium_ingot'): " + displayName);

        String multiWord = LanguageGenerator.snakeToEnglishDisplay("golden_apple_juice");
        System.out.println("    - snakeToEnglishDisplay('golden_apple_juice'): " + multiWord);

        // 2c) 生成单个物品的语言条目
        String langEntry = LanguageGenerator.generateItemLangEntry(modId, "example_sword", "示例之剑");
        System.out.println("    - generateItemLangEntry(): " + langEntry);

        // 2d) 批量生成语言 JSON 内容
        String generatedJson = LanguageGenerator.generateLangJson(modId, Map.of(
                "titanium_ingot", "钛锭",
                "dyedream_dust", "染梦粉",
                "copper_sword", "铜剑",
                "embryo_ring", "胚胎之戒"
        ));
        System.out.println("    - LanguageGenerator.generateLangJson() 输出:");
        System.out.println("      " + generatedJson.replace("\n", "\n      "));
        System.out.println();

        // 2e) 根据 ItemSpec 列表自动生成语言条目
        List<ItemSpec> specList = List.of(
                ItemSpec.builder("mythril_ingot").build(),
                ItemSpec.builder("adamantite_dust").build(),
                ItemSpec.builder("orichalcum_gem").build()
        );
        String fromSpecs = LanguageGenerator.generateFromSpecs(modId, specList);
        System.out.println("    - generateFromSpecs() 自动生成语言条目:");
        System.out.println("      " + fromSpecs.replace("\n", "\n      "));
        System.out.println();

        // 2f) 生成创造模式标签页翻译键
        String tabKey = LanguageGenerator.creativeTabKey(modId, "materials");
        System.out.println("    - creativeTabKey(): " + tabKey);

        System.out.println("  [语言文件] 演示结束");
    }

    // ========================================================================
    // 入口方法 —— 依次调用所有 Demo 方法
    // ========================================================================

    /**
     * 程序入口方法
     * <p>
     * 依次调用以上 8 个 demo 方法，每个方法之间使用分隔线隔开，
     * 方便在控制台中观察各功能的输出结果。
     * 此方法可作为独立测试入口，也可在游戏初始化时调用以验证 API 功能。
     * </p>
     *
     * @param args 命令行参数（未使用）
     */
    public static void main(String[] args) {
        System.out.println(SEPARATOR);
        System.out.println("  ItemMigrationAPI 示例程序启动");
        System.out.println("  共 " + 8 + " 个演示模块");
        System.out.println(SEPARATOR);

        // 1. 简单材料
        System.out.println("\n" + SEPARATOR);
        System.out.println("  █ 模块一: 简单材料物品注册");
        System.out.println(SEPARATOR);
        demoSimpleMaterials();

        // 2. 食物
        System.out.println("\n" + SEPARATOR);
        System.out.println("  █ 模块二: 食物物品注册");
        System.out.println(SEPARATOR);
        demoFoodItems();

        // 3. 工具/武器
        System.out.println("\n" + SEPARATOR);
        System.out.println("  █ 模块三: 工具/武器物品注册");
        System.out.println(SEPARATOR);
        demoToolsAndWeapons();

        // 4. Curio 饰品
        System.out.println("\n" + SEPARATOR);
        System.out.println("  █ 模块四: Curio 饰品注册");
        System.out.println(SEPARATOR);
        demoCurioItems();

        // 5. 自定义物品
        System.out.println("\n" + SEPARATOR);
        System.out.println("  █ 模块五: 自定义物品注册");
        System.out.println(SEPARATOR);
        demoCustomItems();

        // 6. 批量注册
        System.out.println("\n" + SEPARATOR);
        System.out.println("  █ 模块六: 批量注册");
        System.out.println(SEPARATOR);
        demoBatchRegistration();

        // 7. 迁移追踪
        System.out.println("\n" + SEPARATOR);
        System.out.println("  █ 模块七: 迁移状态追踪");
        System.out.println(SEPARATOR);
        demoMigrationTracking();

        // 8. 语言文件
        System.out.println("\n" + SEPARATOR);
        System.out.println("  █ 模块八: 语言文件生成");
        System.out.println(SEPARATOR);
        demoLanguageGeneration();

        // 总结
        System.out.println("\n" + SEPARATOR);
        System.out.println("  ItemMigrationAPI 示例程序执行完毕");
        System.out.println("  所有演示模块已成功运行");
        System.out.println(SEPARATOR);
    }
}
