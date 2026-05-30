package com.pasterdream.pasterdreammod.api.itemmigration.gen;

import com.pasterdream.pasterdreammod.api.itemmigration.model.FoodSpec;
import com.pasterdream.pasterdreammod.api.itemmigration.model.ItemSpec;
import com.pasterdream.pasterdreammod.api.itemmigration.model.ToolSpec;
import com.pasterdream.pasterdreammod.api.itemmigration.model.ToolSpec.ToolType;
import net.minecraft.world.item.Rarity;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 超级快速导入器 —— 帮助 AI 或开发者以最少的代码行数完成物品的注册、
 * 配方生成、战利品表生成、方块数据生成和创造标签页注册。
 * <p>
 * <b>设计理念：</b>提供极度简化的接口，一个静态方法调用即可生成
 * 完整的物品导入代码片段或 JSON 数据。所有方法均返回可读性强的字符串，
 * 可直接粘贴到对应的 Java 文件或 JSON 文件中。
 * <p>
 * <b>使用示例：</b>
 * <pre>{@code
 * // 快速生成一个简单材料的所有注册代码
 * System.out.println(ImportHelper.quickItem("my_ingot"));
 *
 * // 快速生成食物 + 配方 + 语言文件
 * System.out.println(ImportHelper.quickFoodWithRecipe("apple_juice", 4, 0.2f,
 *     List.of("minecraft:apple"), "pasterdream:apple_juice", 200, 0.35f));
 *
 * // 快速生成方块 + 战利品表 + 挖掘标签
 * System.out.println(ImportHelper.quickBlockWithLoot("my_ore",
 *     "pasterdream:my_ore", "pasterdream:raw_my", false, true, true));
 * }</pre>
 */
public class ImportHelper {

    private ImportHelper() {
    }

    // ========================================================================
    // 一级快速导入 —— 最简模式，仅需注册名
    // ========================================================================

    /**
     * 快速生成一个简单材料的完整 PDItems.java 注册代码段
     * <p>
     * 默认属性：堆叠 64、稀有度 COMMON、无特殊属性。
     * <p>
     * <b>示例输出：</b>
     * <pre>
     *     public static final DeferredItem&lt;Item&gt; MY_INGOT =
     *             ItemMigrationAPI.simpleItem("my_ingot")
     *                     .build();
     * </pre>
     *
     * @param registryName 物品注册名（snake_case 格式）
     * @return 可直接粘贴到 PDItems.java 的代码段
     */
    public static String quickItem(String registryName) {
        String fieldName = snakeToUpperSnake(registryName);
        return String.format(
                "    public static final DeferredItem<Item> %s =%n" +
                "            ItemMigrationAPI.simpleItem(\"%s\")%n" +
                "                    .build();",
                fieldName, registryName
        );
    }

    /**
     * 快速生成带属性的材料注册代码段
     *
     * @param registryName 物品注册名
     * @param stacksTo     最大堆叠数
     * @param rarity       稀有度（"COMMON", "UNCOMMON", "RARE", "EPIC"）
     * @return 可直接粘贴到 PDItems.java 的代码段
     */
    public static String quickItem(String registryName, int stacksTo, String rarity) {
        String fieldName = snakeToUpperSnake(registryName);
        String rarityEnum = parseRarity(rarity);
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(
                "    public static final DeferredItem<Item> %s =%n" +
                "            ItemMigrationAPI.simpleItem(\"%s\")%n",
                fieldName, registryName
        ));
        if (stacksTo != 64) {
            sb.append("                    .stacksTo(").append(stacksTo).append(")\n");
        }
        if (!"COMMON".equals(rarityEnum)) {
            sb.append("                    .rarity(Rarity.").append(rarityEnum).append(")\n");
        }
        sb.append("                    .build();");
        return sb.toString();
    }

    /**
     * 快速从 {@link ItemSpec} 生成注册代码段
     *
     * @param spec 物品属性规范
     * @return 注册代码段字符串
     */
    public static String quickItemFromSpec(ItemSpec spec) {
        String fieldName = snakeToUpperSnake(spec.registryName());
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(
                "    public static final DeferredItem<Item> %s =%n" +
                "            ItemMigrationAPI.simpleItem(\"%s\")%n",
                fieldName, spec.registryName()
        ));
        if (spec.stackSize() != 64) {
            sb.append("                    .stacksTo(").append(spec.stackSize()).append(")\n");
        }
        if (spec.rarity() != Rarity.COMMON) {
            sb.append("                    .rarity(Rarity.").append(spec.rarity().name()).append(")\n");
        }
        if (spec.fireResistant()) {
            sb.append("                    .fireResistant()\n");
        }
        if (spec.tooltipLines() != null && !spec.tooltipLines().isEmpty()) {
            sb.append("                    .tooltip(");
            for (int i = 0; i < spec.tooltipLines().size(); i++) {
                if (i > 0) sb.append(", ");
                sb.append("\"").append(escapeJavaString(spec.tooltipLines().get(i))).append("\"");
            }
            sb.append(")\n");
        }
        sb.append("                    .build();");
        return sb.toString();
    }

    // ========================================================================
    // 一级快速导入 —— 食物
    // ========================================================================

    /**
     * 快速生成食物注册代码段
     *
     * @param registryName 食物注册名
     * @param nutrition    营养值（半鸡腿数）
     * @param saturation   饱和度系数
     * @return 注册代码段字符串
     */
    public static String quickFood(String registryName, int nutrition, float saturation) {
        String fieldName = snakeToUpperSnake(registryName);
        return String.format(
                "    public static final DeferredItem<Item> %s =%n" +
                "            ItemMigrationAPI.foodItem(\"%s\")%n" +
                "                    .nutrition(%d)%n" +
                "                    .saturationModifier(%sf)%n" +
                "                    .build();",
                fieldName, registryName, nutrition, formatFloat(saturation)
        );
    }

    /**
     * 快速从 {@link FoodSpec} 生成食物注册代码段
     *
     * @param registryName 食物注册名
     * @param spec         食物属性规范
     * @return 注册代码段字符串
     */
    public static String quickFoodFromSpec(String registryName, FoodSpec spec) {
        String fieldName = snakeToUpperSnake(registryName);
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(
                "    public static final DeferredItem<Item> %s =%n" +
                "            ItemMigrationAPI.foodItem(\"%s\")%n",
                fieldName, registryName
        ));
        sb.append("                    .nutrition(").append(spec.nutrition()).append(")\n");
        sb.append("                    .saturationModifier(").append(formatFloat(spec.saturationModifier())).append("f)\n");
        if (spec.alwaysEdible()) {
            sb.append("                    .alwaysEdible()\n");
        }
        if (spec.fastFood()) {
            sb.append("                    .fastFood()\n");
        }
        if (spec.effects() != null) {
            for (FoodSpec.FoodEffectSpec effect : spec.effects()) {
                sb.append("                    .effect(\"").append(effect.effectId()).append("\", ")
                        .append(effect.duration()).append(", ")
                        .append(effect.amplifier()).append(", ")
                        .append(formatFloat(effect.probability())).append("f)\n");
            }
        }
        sb.append("                    .build();");
        return sb.toString();
    }

    // ========================================================================
    // 一级快速导入 —— 工具
    // ========================================================================

    /**
     * 快速生成工具注册代码段
     *
     * @param registryName 工具注册名
     * @param toolType     工具类型（SWORD/PICKAXE/AXE/SHOVEL/HOE/HAMMER/WAND）
     * @param attackDamage 攻击伤害
     * @param durability   耐久度
     * @return 注册代码段字符串
     */
    public static String quickTool(String registryName, String toolType, float attackDamage, int durability) {
        String fieldName = snakeToUpperSnake(registryName);
        return String.format(
                "    public static final DeferredItem<Item> %s =%n" +
                "            ItemMigrationAPI.toolItem(\"%s\")%n" +
                "                    .type(ToolType.%s)%n" +
                "                    .attackDamage(%sf)%n" +
                "                    .durability(%d)%n" +
                "                    .build();",
                fieldName, registryName, toolType.toUpperCase(),
                formatFloat(attackDamage), durability
        );
    }

    /**
     * 快速从 {@link ToolSpec} 生成工具注册代码段
     *
     * @param registryName 工具注册名
     * @param spec         工具属性规范
     * @return 注册代码段字符串
     */
    public static String quickToolFromSpec(String registryName, ToolSpec spec) {
        String fieldName = snakeToUpperSnake(registryName);
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(
                "    public static final DeferredItem<Item> %s =%n" +
                "            ItemMigrationAPI.toolItem(\"%s\")%n",
                fieldName, registryName
        ));
        sb.append("                    .type(ToolType.").append(spec.type().name()).append(")\n");
        sb.append("                    .attackDamage(").append(formatFloat(spec.attackDamage())).append("f)\n");
        sb.append("                    .durability(").append(spec.durability()).append(")\n");
        if (spec.miningSpeed() != 2.0f) {
            sb.append("                    .miningSpeed(").append(formatFloat(spec.miningSpeed())).append("f)\n");
        }
        if (spec.attackSpeed() != -2.4f) {
            sb.append("                    .attackSpeed(").append(formatFloat(spec.attackSpeed())).append("f)\n");
        }
        sb.append("                    .build();");
        return sb.toString();
    }

    // ========================================================================
    // 一级快速导入 —— 饰品（Curio）
    // ========================================================================

    /**
     * 快速生成 Curio 饰品注册代码段
     *
     * @param registryName 饰品注册名
     * @param slot         Curio 槽位（ring/necklace/belt/charm/head/back/curio）
     * @return 注册代码段字符串
     */
    public static String quickCurio(String registryName, String slot) {
        String fieldName = snakeToUpperSnake(registryName);
        return String.format(
                "    public static final DeferredItem<Item> %s =%n" +
                "            ItemMigrationAPI.curioItem(\"%s\")%n" +
                "                    .slot(\"%s\")%n" +
                "                    .build();",
                fieldName, registryName, slot
        );
    }

    // ========================================================================
    // 二级快速导入 —— 物品 + 配方
    // ========================================================================

    /**
     * 快速生成食物 + 熔炉配方的完整代码
     * <p>
     * 返回包含 PDItems 注册代码和 recipes JSON 的字符串。
     *
     * @param foodName       食物注册名
     * @param nutrition      营养值
     * @param saturation     饱和度系数
     * @param ingredientIds  合成材料 ID 列表（无序合成）
     * @param smeltResultId  熔炉冶炼结果物品 ID（如无可传 null）
     * @param smeltExperience 冶炼经验（当 smeltResultId 非 null 时有效）
     * @return 完整的代码 + JSON 字符串
     */
    public static String quickFoodWithRecipe(
            String foodName, int nutrition, float saturation,
            List<String> ingredientIds,
            String smeltResultId, float smeltExperience) {

        StringBuilder sb = new StringBuilder();
        sb.append("// ====== PDItems.java 注册代码 ======\n");
        sb.append(quickFood(foodName, nutrition, saturation));
        sb.append("\n\n");

        if (ingredientIds != null && !ingredientIds.isEmpty()) {
            sb.append("// ====== recipes/").append(foodName).append(".json ======\n");
            sb.append(RecipeGenerator.generateShapeless("pasterdream:" + foodName, 1,
                    "food", ingredientIds));
            sb.append("\n\n");
        }

        if (smeltResultId != null && !smeltResultId.isEmpty()) {
            sb.append("// ====== recipes/").append(foodName).append("_smelting.json ======\n");
            sb.append(RecipeGenerator.generateSmelting(smeltResultId,
                    "pasterdream:" + foodName, smeltExperience, 200));
            sb.append("\n");
        }

        return sb.toString();
    }

    /**
     * 快速生成工具 + 合成配方的完整数据
     *
     * @param toolName   工具注册名
     * @param toolType   工具类型
     * @param damage     攻击伤害
     * @param durability 耐久度
     * @param recipePattern  合成模式（3 行字符串数组）
     * @param keyMapping     合成键到物品 ID 的映射
     * @return 完整的代码 + JSON 数据
     */
    public static String quickToolWithRecipe(
            String toolName, String toolType, float damage, int durability,
            String[] recipePattern, Map<String, String> keyMapping) {

        StringBuilder sb = new StringBuilder();
        sb.append("// ====== PDItems.java 注册代码 ======\n");
        sb.append(quickTool(toolName, toolType, damage, durability));
        sb.append("\n\n");

        sb.append("// ====== recipes/").append(toolName).append(".json ======\n");
        sb.append(RecipeGenerator.generateShaped("pasterdream:" + toolName, 1,
                "equipment", recipePattern, keyMapping));
        sb.append("\n");

        return sb.toString();
    }

    // ========================================================================
    // 二级快速导入 —— 方块 + 战利品表 + 挖掘标签
    // ========================================================================

    /**
     * 快速生成方块 + 战利品表 + 挖掘标签 JSON 的完整数据
     * <p>
     * 适用于矿石类方块，生成自掉落 + 精准采集 + 时运 + 爆炸衰减的完整配置。
     *
     * @param blockName      方块注册名
     * @param blockId        方块完整 ID（含命名空间）
     * @param dropItemId     挖掘掉落物品 ID（含命名空间）
     * @param isOre          是否为矿石（启用时运 + 爆炸衰减）
     * @param needSilktouch  是否需要精准采集
     * @param hasPickaxeTag  是否添加 mineable/pickaxe 标签
     * @return 包含 lottable JSON 和 tag JSON 的完整字符串
     */
    public static String quickBlockWithLoot(
            String blockName, String blockId, String dropItemId,
            boolean isOre, boolean needSilktouch, boolean hasPickaxeTag) {

        StringBuilder sb = new StringBuilder();
        sb.append("// ====== loot_tables/blocks/").append(blockName).append(".json ======\n");

        if (isOre) {
            sb.append(LootTableGenerator.generateOreDrop(dropItemId, blockId));
        } else if (needSilktouch) {
            sb.append(LootTableGenerator.generateSilkTouchDrop(blockId));
        } else {
            sb.append(LootTableGenerator.generateSelfDrop(blockId));
        }
        sb.append("\n\n");

        if (hasPickaxeTag) {
            sb.append("// ====== tags/block/mineable/pickaxe.json ======\n");
            sb.append(BlockDataGenerator.generateMineablePickaxeJson(List.of(blockId)));
            sb.append("\n\n");
            sb.append("// ====== tags/block/needs_iron_tool.json ======\n");
            sb.append(BlockDataGenerator.generateNeedsIronToolJson(List.of(blockId)));
            sb.append("\n");
        }

        return sb.toString();
    }

    /**
     * 快速生成方块注册 + 战利品表 + 标签的完整数据包
     * <p>
     * 包含 PDBlocks.java 注册代码、BlockItem 注册代码、
     * 战利品表 JSON 和挖掘标签 JSON。
     *
     * @param blockName      方块注册名
     * @param templateBlock  模板方块（如 "Blocks.STONE"）
     * @param dropItemId     掉落物品 ID
     * @param isOre          是否为矿石
     * @param needSilktouch  是否需要精准采集
     * @return 从注册代码到 JSON 的完整字符串
     */
    public static String quickCompleteBlock(
            String blockName, String templateBlock, String dropItemId,
            boolean isOre, boolean needSilktouch) {

        String blockFieldName = snakeToUpperSnake(blockName);
        String blockId = "pasterdream:" + blockName;

        StringBuilder sb = new StringBuilder();
        sb.append("// ====== PDBlocks.java 注册代码 ======\n");
        sb.append(BlockDataGenerator.generateBlockRegistrationCode(blockName, blockFieldName, templateBlock, false));
        sb.append("\n\n");

        sb.append("// ====== PDItems.java BlockItem 注册代码 ======\n");
        sb.append(BlockDataGenerator.generateBlockItemRegistrationCode(blockName));
        sb.append("\n\n");

        sb.append("// ====== 战利品表与标签 ======\n");
        sb.append(quickBlockWithLoot(blockName, blockId, dropItemId, isOre, needSilktouch, true));

        return sb.toString();
    }

    // ========================================================================
    // 三级快速导入 —— 一键全流程导入
    // ========================================================================

    /**
     * 一键生成物品导入全流程数据
     * <p>
     * 包含 PDItems 注册代码、合成配方、熔炉配方、战利品表（如果是方块）、
     * 挖掘标签（如果是方块）、创造标签页代码、语言文件条目。
     * <p>
     * 这是最强大的导入方法，一次调用生成所有需要的数据。
     *
     * @param registryName    物品注册名
     * @param displayNameCN   中文显示名
     * @param displayNameEN   英文显示名
     * @param itemType        物品类型（"material"/"food"/"tool"/"curio"/"block"）
     * @param extraParams     额外参数映射，根据 itemType 需要不同键
     *                        <ul>
     *                          <li>material: rarity(可选)</li>
     *                          <li>food: nutrition(int), saturation(float)</li>
     *                          <li>tool: toolType(String), damage(float), durability(int)</li>
     *                          <li>curio: slot(String)</li>
     *                          <li>block: templateBlock(String), dropItem(String), isOre(bool)</li>
     *                        </ul>
     * @param recipeData      配方数据映射（可选），支持键：
     *                        <ul>
     *                          <li>shapedPattern: String[]</li>
     *                          <li>shapedKeys: Map&lt;String, String&gt;</li>
     *                          <li>shapelessIngredients: List&lt;String&gt;</li>
     *                          <li>smeltIngredient: String</li>
     *                          <li>smeltExperience: float</li>
     *                        </ul>
     * @param modId           模组 ID
     * @return 完整的全套数据字符串
     */
    public static String generateAll(String registryName,
                                     String displayNameCN, String displayNameEN,
                                     String itemType,
                                     Map<String, Object> extraParams,
                                     Map<String, Object> recipeData,
                                     String modId) {
        StringBuilder sb = new StringBuilder();
        String fieldName = snakeToUpperSnake(registryName);
        String itemId = modId + ":" + registryName;

        sb.append("========================================\n");
        sb.append("  ").append(displayNameCN).append(" (").append(displayNameEN).append(")\n");
        sb.append("  物品 ID: ").append(itemId).append("\n");
        sb.append("  类型: ").append(itemType).append("\n");
        sb.append("========================================\n\n");

        // ---- Step 1: 物品注册代码 ----
        sb.append("// ====== 第1步：PDItems.java 注册代码 ======\n");
        switch (itemType) {
            case "food":
                int nutrition = getIntParam(extraParams, "nutrition", 4);
                float saturation = getFloatParam(extraParams, "saturation", 0.2f);
                sb.append(quickFood(registryName, nutrition, saturation));
                break;
            case "tool":
                String toolType = getStringParam(extraParams, "toolType", "SWORD");
                float damage = getFloatParam(extraParams, "damage", 3.0f);
                int durability = getIntParam(extraParams, "durability", 250);
                sb.append(quickTool(registryName, toolType, damage, durability));
                break;
            case "curio":
                String slot = getStringParam(extraParams, "slot", "curio");
                sb.append(quickCurio(registryName, slot));
                break;
            case "block":
                String templateBlock = getStringParam(extraParams, "templateBlock", "Blocks.STONE");
                String dropItem = getStringParam(extraParams, "dropItem", itemId);
                sb.append(quickCompleteBlock(registryName, templateBlock, dropItem,
                        getBoolParam(extraParams, "isOre", false),
                        getBoolParam(extraParams, "needSilktouch", false)));
                break;
            default:
                String rarity = getStringParam(extraParams, "rarity", "COMMON");
                sb.append(quickItem(registryName, 64, rarity));
                break;
        }
        sb.append("\n\n");

        // ---- Step 2: 配方数据 ----
        if (recipeData != null && !recipeData.isEmpty()) {
            sb.append("// ====== 第2步：配方 JSON ======\n");
            boolean hasRecipe = false;

            if (recipeData.containsKey("shapedPattern") && recipeData.containsKey("shapedKeys")) {
                String[] pattern = (String[]) recipeData.get("shapedPattern");
                @SuppressWarnings("unchecked")
                Map<String, String> keys = (Map<String, String>) recipeData.get("shapedKeys");
                sb.append("// --- 有序合成 recipes/").append(registryName).append(".json ---\n");
                sb.append(RecipeGenerator.generateShaped(itemId, 1, "misc", pattern, keys));
                sb.append("\n\n");
                hasRecipe = true;
            }

            if (recipeData.containsKey("shapelessIngredients")) {
                @SuppressWarnings("unchecked")
                List<String> ingredients = (List<String>) recipeData.get("shapelessIngredients");
                sb.append("// --- 无序合成 recipes/").append(registryName).append(".json ---\n");
                sb.append(RecipeGenerator.generateShapeless(itemId, 1, "misc", ingredients));
                sb.append("\n\n");
                hasRecipe = true;
            }

            if (recipeData.containsKey("smeltIngredient")) {
                String smeltIngredient = (String) recipeData.get("smeltIngredient");
                float smeltExp = getFloatParam(recipeData, "smeltExperience", 0.35f);
                sb.append("// --- 熔炉冶炼 recipes/").append(registryName).append("_smelting.json ---\n");
                sb.append(RecipeGenerator.generateSmelting(itemId, smeltIngredient, smeltExp, 200));
                sb.append("\n\n");
                hasRecipe = true;
            }

            if (recipeData.containsKey("blastingIngredient")) {
                String blastingIngredient = (String) recipeData.get("blastingIngredient");
                float blastingExp = getFloatParam(recipeData, "blastingExperience", 0.35f);
                sb.append("// --- 高炉冶炼 recipes/").append(registryName).append("_blasting.json ---\n");
                sb.append(RecipeGenerator.generateBlasting(itemId, blastingIngredient, blastingExp, 100));
                sb.append("\n\n");
            }

            if (recipeData.containsKey("stonecuttingIngredient")) {
                String stonecuttingIngredient = (String) recipeData.get("stonecuttingIngredient");
                int stonecuttingCount = getIntParam(recipeData, "stonecuttingCount", 1);
                sb.append("// --- 切石机 recipes/").append(registryName).append("_stonecutting.json ---\n");
                sb.append(RecipeGenerator.generateStonecutting(itemId, stonecuttingCount, stonecuttingIngredient));
                sb.append("\n\n");
            }

            if (!hasRecipe) {
                sb.append("（未提供配方数据，跳过）\n\n");
            }
        }

        // ---- Step 3: 语言文件 ----
        sb.append("// ====== 第3步：语言文件条目 ======\n");
        sb.append("// zh_cn.json:\n");
        sb.append("  \"").append("item.").append(modId).append(".").append(registryName)
                .append("\": \"").append(displayNameCN).append("\"\n");
        sb.append("// en_us.json:\n");
        sb.append("  \"").append("item.").append(modId).append(".").append(registryName)
                .append("\": \"").append(displayNameEN).append("\"\n\n");

        // ---- Step 4: 创造标签页 ----
        sb.append("// ====== 第4步：PDCreativeTabs.java displayItems 代码 ======\n");
        sb.append(CreativeTabHelper.generateDisplayItemLine(fieldName, "PDItems"));
        sb.append("\n");

        return sb.toString();
    }

    // ========================================================================
    // 批量快速导入
    // ========================================================================

    /**
     * 从 {@link ItemSpec} 列表批量生成材料和对应的注册代码
     *
     * @param specs 物品属性规范列表
     * @return 包含所有物品注册代码的字符串
     */
    public static String batchQuickItems(List<ItemSpec> specs) {
        StringBuilder sb = new StringBuilder();
        sb.append("// ====== 批量材料注册（粘贴到 PDItems.java） ======\n\n");
        for (ItemSpec spec : specs) {
            sb.append(quickItemFromSpec(spec));
            sb.append("\n\n");
        }
        return sb.toString();
    }

    /**
     * 批量生成食物注册代码
     *
     * @param items 注册名到食物属性的映射
     * @return 包含所有食物注册代码的字符串
     */
    public static String batchQuickFoods(Map<String, FoodSpec> items) {
        StringBuilder sb = new StringBuilder();
        sb.append("// ====== 批量食物注册（粘贴到 PDItems.java） ======\n\n");
        for (Map.Entry<String, FoodSpec> entry : items.entrySet()) {
            sb.append(quickFoodFromSpec(entry.getKey(), entry.getValue()));
            sb.append("\n\n");
        }
        return sb.toString();
    }

    /**
     * 从 Map 格式的简单配置生成批量物品导入
     * <p>
     * 适合处理从 JSON/CSV 解析出来的数据。
     * Map 格式：{注册名: {type: "material"/"food"/"tool"/"curio", ...其他属性}}
     *
     * @param items itemName -> properties 的映射
     * @param modId 模组 ID
     * @return 包含所有注册代码和 JSON 数据的完整字符串
     */
    public static String batchImportFromMap(Map<String, Map<String, String>> items, String modId) {
        StringBuilder sb = new StringBuilder();
        sb.append("// ====== 批量导入（从 Map 配置） ======\n\n");

        for (Map.Entry<String, Map<String, String>> entry : items.entrySet()) {
            String name = entry.getKey();
            Map<String, String> props = entry.getValue();
            String type = props.getOrDefault("type", "material");
            String displayCN = props.getOrDefault("display_cn", name);
            String displayEN = props.getOrDefault("display_en", name);

            Map<String, Object> extraParams = new LinkedHashMap<>();
            switch (type) {
                case "food":
                    extraParams.put("nutrition", Integer.parseInt(props.getOrDefault("nutrition", "4")));
                    extraParams.put("saturation", Float.parseFloat(props.getOrDefault("saturation", "0.2")));
                    break;
                case "tool":
                    extraParams.put("toolType", props.getOrDefault("toolType", "SWORD"));
                    extraParams.put("damage", Float.parseFloat(props.getOrDefault("damage", "3.0")));
                    extraParams.put("durability", Integer.parseInt(props.getOrDefault("durability", "250")));
                    break;
                case "curio":
                    extraParams.put("slot", props.getOrDefault("slot", "curio"));
                    break;
                case "block":
                    extraParams.put("templateBlock", props.getOrDefault("templateBlock", "Blocks.STONE"));
                    extraParams.put("dropItem", modId + ":" + props.getOrDefault("dropItem", name));
                    extraParams.put("isOre", Boolean.parseBoolean(props.getOrDefault("isOre", "false")));
                    break;
                default:
                    extraParams.put("rarity", props.getOrDefault("rarity", "COMMON"));
                    break;
            }

            sb.append(generateAll(name, displayCN, displayEN, type, extraParams, null, modId));
            sb.append("\n");
            sb.append("----------------------------------------\n\n");
        }

        return sb.toString();
    }

    // ========================================================================
    // 内部辅助方法
    // ========================================================================

    /**
     * 将 snake_case 转换为 UPPER_SNAKE_CASE
     */
    private static String snakeToUpperSnake(String snakeCase) {
        if (snakeCase == null || snakeCase.isEmpty()) return "";
        return snakeCase.toUpperCase();
    }

    /**
     * 解析稀有度字符串为枚举名
     */
    private static String parseRarity(String rarity) {
        if (rarity == null) return "COMMON";
        return switch (rarity.toUpperCase()) {
            case "UNCOMMON" -> "UNCOMMON";
            case "RARE" -> "RARE";
            case "EPIC" -> "EPIC";
            default -> "COMMON";
        };
    }

    /**
     * 转义 Java 字符串中的特殊字符
     */
    private static String escapeJavaString(String input) {
        if (input == null) return "";
        return input
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\t", "\\t");
    }

    /**
     * 格式化浮点数，省略多余的零
     */
    private static String formatFloat(float value) {
        if (value == (int) value) {
            return String.valueOf((int) value);
        }
        return String.valueOf(value);
    }

    /**
     * 从 Map 中安全获取字符串参数
     */
    private static String getStringParam(Map<String, Object> params, String key, String defaultValue) {
        if (params == null || !params.containsKey(key)) return defaultValue;
        Object val = params.get(key);
        return val != null ? val.toString() : defaultValue;
    }

    /**
     * 从 Map 中安全获取 int 参数
     */
    private static int getIntParam(Map<String, Object> params, String key, int defaultValue) {
        if (params == null || !params.containsKey(key)) return defaultValue;
        Object val = params.get(key);
        if (val instanceof Number) return ((Number) val).intValue();
        try {
            return Integer.parseInt(val.toString());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * 从 Map 中安全获取 float 参数
     */
    private static float getFloatParam(Map<String, Object> params, String key, float defaultValue) {
        if (params == null || !params.containsKey(key)) return defaultValue;
        Object val = params.get(key);
        if (val instanceof Number) return ((Number) val).floatValue();
        try {
            return Float.parseFloat(val.toString());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * 从 Map 中安全获取 boolean 参数
     */
    private static boolean getBoolParam(Map<String, Object> params, String key, boolean defaultValue) {
        if (params == null || !params.containsKey(key)) return defaultValue;
        Object val = params.get(key);
        if (val instanceof Boolean) return (Boolean) val;
        return Boolean.parseBoolean(val.toString());
    }
}
