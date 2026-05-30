package com.pasterdream.pasterdreammod.api.itemmigration.gen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.pasterdream.pasterdreammod.PasterDreamMod;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * 战利品表生成器 —— 自动生成 Minecraft 方块战利品表 JSON 字符串
 * <p>
 * 纯工具类，提供一系列静态方法用于生成不同种类的方块战利品表。
 * 支持自掉落、精准采集掉落、矿石掉落（时运+爆炸衰减）、
 * 自定义掉落以及多物品混合掉落等场景。
 * 所有方法均返回格式化后的 JSON 字符串，可直接写入文件使用。
 */
public class LootTableGenerator {

    /** Gson 实例 —— 启用漂亮打印输出 */
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    /** 战利品表类型：方块 */
    private static final String LOOT_TABLE_TYPE = "minecraft:block";
    /** 条目类型：物品 */
    private static final String ENTRY_TYPE_ITEM = "minecraft:item";
    /** 条件类型：存活爆炸 */
    private static final String CONDITION_SURVIVES_EXPLOSION = "minecraft:survives_explosion";
    /** 条件类型：匹配工具 */
    private static final String CONDITION_MATCH_TOOL = "minecraft:match_tool";
    /** 条件类型：随机概率 */
    private static final String CONDITION_RANDOM_CHANCE = "minecraft:random_chance";
    /** 附魔类型：精准采集 */
    private static final String ENCHANTMENT_SILK_TOUCH = "minecraft:silk_touch";
    /** 附魔类型：时运 */
    private static final String ENCHANTMENT_FORTUNE = "minecraft:fortune";
    /** 函数类型：应用加成 */
    private static final String FUNCTION_APPLY_BONUS = "minecraft:apply_bonus";
    /** 函数类型：爆炸衰减 */
    private static final String FUNCTION_EXPLOSION_DECAY = "minecraft:explosion_decay";
    /** 函数类型：设置数量 */
    private static final String FUNCTION_SET_COUNT = "minecraft:set_count";
    /** 时运公式：矿石掉落 */
    private static final String FORMULA_ORE_DROPS = "minecraft:ore_drops";

    private LootTableGenerator() {
    }

    /**
     * 掉落条目记录 —— 描述一个战利品表条目所需的全部参数
     *
     * @param itemId         掉落物品 ID（含命名空间，如 "pasterdream:dyedream_dust"）
     * @param minCount       最小掉落数量
     * @param maxCount       最大掉落数量
     * @param chance         掉落概率（0.0 ~ 1.0），1.0 表示必然掉落
     * @param silkTouchOnly  是否仅在使用精准采集附魔时掉落
     * @param fortuneEnabled 是否启用时运加成（矿石掉落公式）
     */
    public record DropEntry(String itemId, int minCount, int maxCount, float chance,
                            boolean silkTouchOnly, boolean fortuneEnabled) {
    }

    // ======================== 公开方法 ========================

    /**
     * 生成最简单的自掉落战利品表
     * <p>
     * 方块被破坏时掉落自身，仅有 survives_explosion 条件。
     *
     * @param blockId 方块物品 ID（含命名空间，如 "pasterdream:dyedream_planks"）
     * @return 格式化后的 JSON 字符串
     */
    public static String generateSelfDrop(String blockId) {
        PasterDreamMod.LOGGER.info("[LootTableGenerator] ===== generateSelfDrop() 被调用 =====");
        PasterDreamMod.LOGGER.info("[LootTableGenerator] 目标方块ID: {}", blockId);
        JsonObject pool = createBasePool();
        pool.getAsJsonArray("entries").add(createItemEntry(blockId));
        pool.getAsJsonArray("conditions").add(createSurvivesExplosionCondition());

        JsonObject root = createRoot();
        root.getAsJsonArray("pools").add(pool);
        String json = GSON.toJson(root);
        PasterDreamMod.LOGGER.info("[LootTableGenerator] 生成自掉落JSON (前200字符): {}", json.substring(0, Math.min(json.length(), 200)));
        PasterDreamMod.LOGGER.info("[LootTableGenerator] ===== generateSelfDrop() 完成 =====");
        return json;
    }

    /**
     * 生成需要精准采集才掉落自身的战利品表
     * <p>
     * 使用 match_tool + silk_touch 附魔条件，非精准采集时不掉落任何物品。
     *
     * @param blockId 方块物品 ID（含命名空间，如 "pasterdream:dyedream_glass"）
     * @return 格式化后的 JSON 字符串
     */
    public static String generateSilkTouchDrop(String blockId) {
        PasterDreamMod.LOGGER.info("[LootTableGenerator] ===== generateSilkTouchDrop() 被调用 =====");
        PasterDreamMod.LOGGER.info("[LootTableGenerator] 目标方块ID: {}", blockId);
        JsonObject pool = createBasePool();
        pool.getAsJsonArray("entries").add(createItemEntry(blockId));
        pool.getAsJsonArray("conditions").add(createMatchToolSilkTouchCondition());
        pool.getAsJsonArray("conditions").add(createSurvivesExplosionCondition());

        JsonObject root = createRoot();
        root.getAsJsonArray("pools").add(pool);
        String json = GSON.toJson(root);
        PasterDreamMod.LOGGER.info("[LootTableGenerator] 生成精准采集掉落JSON (前200字符): {}", json.substring(0, Math.min(json.length(), 200)));
        PasterDreamMod.LOGGER.info("[LootTableGenerator] ===== generateSilkTouchDrop() 完成 =====");
        return json;
    }

    /**
     * 生成矿石掉落战利品表（时运加成 + 爆炸衰减）
     * <p>
     * 掉落指定物品（不是方块自身），应用 apply_bonus + fortune + ore_drops
     * 以及 explosion_decay 函数。参考格式: dyedreamdust_ore.json
     *
     * @param dropItemId 掉落物品 ID（含命名空间，如 "pasterdream:dyedream_dust"）
     * @param blockId    方块 ID（仅用于参数语义完整性，不参与 JSON 生成）
     * @return 格式化后的 JSON 字符串
     */
    public static String generateOreDrop(String dropItemId, String blockId) {
        PasterDreamMod.LOGGER.info("[LootTableGenerator] ===== generateOreDrop() 被调用 =====");
        PasterDreamMod.LOGGER.info("[LootTableGenerator] 掉落物品ID: {}, 方块ID: {}", dropItemId, blockId);
        JsonObject pool = createBasePool();
        JsonObject entry = createItemEntry(dropItemId);
        getOrCreateArray(entry, "functions").add(createApplyBonusFortuneFunction());
        getOrCreateArray(entry, "functions").add(createExplosionDecayFunction());
        pool.getAsJsonArray("entries").add(entry);
        pool.getAsJsonArray("conditions").add(createSurvivesExplosionCondition());

        JsonObject root = createRoot();
        root.getAsJsonArray("pools").add(pool);
        String json = GSON.toJson(root);
        PasterDreamMod.LOGGER.info("[LootTableGenerator] 生成矿石掉落JSON (前200字符): {}", json.substring(0, Math.min(json.length(), 200)));
        PasterDreamMod.LOGGER.info("[LootTableGenerator] ===== generateOreDrop() 完成 =====");
        return json;
    }

    /**
     * 生成自定义掉落战利品表
     * <p>
     * 根据三个布尔参数灵活组合条件和函数：
     * <ul>
     *   <li>silkTouchOnly: 是否添加精准采集 match_tool 条件</li>
     *   <li>fortuneEnabled: 是否添加时运 apply_bonus 函数</li>
     *   <li>explosionDecay: 是否添加爆炸衰减函数</li>
     * </ul>
     * 始终包含 survives_explosion 条件。
     *
     * @param dropItemId     掉落物品 ID（含命名空间）
     * @param blockId        方块 ID（仅用于参数语义完整性，不参与 JSON 生成）
     * @param silkTouchOnly  是否仅精准采集掉落
     * @param fortuneEnabled 是否启用时运加成
     * @param explosionDecay 是否启用爆炸衰减
     * @return 格式化后的 JSON 字符串
     */
    public static String generateCustomDrop(String dropItemId, String blockId,
                                            boolean silkTouchOnly, boolean fortuneEnabled,
                                            boolean explosionDecay) {
        PasterDreamMod.LOGGER.info("[LootTableGenerator] ===== generateCustomDrop() 被调用 =====");
        PasterDreamMod.LOGGER.info("[LootTableGenerator] 掉落物品ID: {}, 方块ID: {}", dropItemId, blockId);
        PasterDreamMod.LOGGER.info("[LootTableGenerator] 参数: silkTouchOnly={}, fortuneEnabled={}, explosionDecay={}",
            silkTouchOnly, fortuneEnabled, explosionDecay);
        JsonObject pool = createBasePool();
        JsonObject entry = createItemEntry(dropItemId);

        if (silkTouchOnly) {
            pool.getAsJsonArray("conditions").add(createMatchToolSilkTouchCondition());
        }
        if (fortuneEnabled) {
            getOrCreateArray(entry, "functions").add(createApplyBonusFortuneFunction());
        }
        if (explosionDecay) {
            getOrCreateArray(entry, "functions").add(createExplosionDecayFunction());
        }

        pool.getAsJsonArray("conditions").add(createSurvivesExplosionCondition());
        pool.getAsJsonArray("entries").add(entry);

        JsonObject root = createRoot();
        root.getAsJsonArray("pools").add(pool);
        String json = GSON.toJson(root);
        PasterDreamMod.LOGGER.info("[LootTableGenerator] 生成自定义掉落JSON (前200字符): {}", json.substring(0, Math.min(json.length(), 200)));
        PasterDreamMod.LOGGER.info("[LootTableGenerator] ===== generateCustomDrop() 完成 =====");
        return json;
    }

    /**
     * 生成多物品掉落战利品表
     * <p>
     * 一个战利品池中包含多个条目，每个条目可以独立配置掉落概率、
     * 数量范围、精准采集要求以及时运加成。
     * 适用于树叶同时掉落自身+树苗等混合掉落场景。
     * <p>
     * 掉落池始终添加 survives_explosion 条件。
     *
     * @param entries 掉落条目列表，每个条目描述一种物品的掉落参数
     * @param blockId 方块 ID（仅用于参数语义完整性，不参与 JSON 生成）
     * @return 格式化后的 JSON 字符串
     */
    public static String generateMultiDrop(List<DropEntry> entries, String blockId) {
        PasterDreamMod.LOGGER.info("[LootTableGenerator] ===== generateMultiDrop() 被调用 =====");
        PasterDreamMod.LOGGER.info("[LootTableGenerator] 方块ID: {}, 条目数量: {}", blockId, entries.size());
        for (int i = 0; i < entries.size(); i++) {
            DropEntry e = entries.get(i);
            PasterDreamMod.LOGGER.info("[LootTableGenerator]   条目[{}]: itemId={}, count=[{}-{}], chance={}, silkTouchOnly={}, fortuneEnabled={}",
                i, e.itemId(), e.minCount(), e.maxCount(), e.chance(), e.silkTouchOnly(), e.fortuneEnabled());
        }
        JsonObject pool = createBasePool();

        for (DropEntry entry : entries) {
            JsonObject entryObj = createItemEntry(entry.itemId());

            if (entry.fortuneEnabled()) {
                getOrCreateArray(entryObj, "functions").add(createApplyBonusFortuneFunction());
            }

            boolean hasCountRange = entry.minCount() != 1 || entry.maxCount() != 1;
            if (hasCountRange) {
                getOrCreateArray(entryObj, "functions").add(createSetCountFunction(entry.minCount(), entry.maxCount()));
            }

            if (entry.chance() < 1.0f) {
                getOrCreateArray(entryObj, "conditions").add(createRandomChanceCondition(entry.chance()));
            }

            if (entry.silkTouchOnly()) {
                getOrCreateArray(entryObj, "conditions").add(createMatchToolSilkTouchCondition());
            }

            pool.getAsJsonArray("entries").add(entryObj);
        }

        pool.getAsJsonArray("conditions").add(createSurvivesExplosionCondition());

        JsonObject root = createRoot();
        root.getAsJsonArray("pools").add(pool);
        String json = GSON.toJson(root);
        PasterDreamMod.LOGGER.info("[LootTableGenerator] 生成多物品掉落JSON (前200字符): {}", json.substring(0, Math.min(json.length(), 200)));
        PasterDreamMod.LOGGER.info("[LootTableGenerator] ===== generateMultiDrop() 完成 =====");
        return json;
    }

    /**
     * 保存战利品表 JSON 到文件
     * <p>
     * 自动创建目标目录（如果不存在），将 JSON 字符串写入指定路径。
     * 文件路径格式：{basePath}/data/{modId}/loot_tables/blocks/{blockName}.json
     *
     * @param lootJson 战利品表 JSON 字符串
     * @param modId    模组 ID（如 "pasterdream"），用于构建目录路径
     * @param blockName 方块注册名（如 "dyedream_planks"），作为文件名
     * @param basePath 基础路径（如项目资源目录的父级），通常传 "src/main/resources"
     * @throws IOException 如果目录创建或文件写入失败
     */
    public static void saveLootTableToFile(String lootJson, String modId, String blockName, String basePath)
            throws IOException {
        Path outputDir = Paths.get(basePath, "data", modId, "loot_tables", "blocks");
        Files.createDirectories(outputDir);

        Path outputFile = outputDir.resolve(blockName + ".json");
        PasterDreamMod.LOGGER.info("[LootTableGenerator] ===== saveLootTableToFile() =====");
        PasterDreamMod.LOGGER.info("[LootTableGenerator] 保存路径: {}", outputFile.toAbsolutePath().normalize());
        PasterDreamMod.LOGGER.info("[LootTableGenerator] 方块名称: {}", blockName);
        PasterDreamMod.LOGGER.info("[LootTableGenerator] 目录是否存在: {}", Files.exists(outputDir));
        PasterDreamMod.LOGGER.info("[LootTableGenerator] 文件大小: {} 字符", lootJson.length());
        try (FileWriter writer = new FileWriter(outputFile.toFile())) {
            writer.write(lootJson);
            writer.flush();
        }
        PasterDreamMod.LOGGER.info("[LootTableGenerator] ✅ 文件保存成功! 路径: {}", outputFile.toAbsolutePath().normalize());
        PasterDreamMod.LOGGER.info("[LootTableGenerator] ===== saveLootTableToFile() 完成 =====");
    }

    // ======================== 私有辅助方法 ========================

    /**
     * 创建战利品表根对象
     * <p>
     * 包含 type 和空的 pools 数组。
     *
     * @return 根 JsonObject
     */
    private static JsonObject createRoot() {
        JsonObject root = new JsonObject();
        root.addProperty("type", LOOT_TABLE_TYPE);
        root.add("pools", new JsonArray());
        return root;
    }

    /**
     * 创建基础战利品池
     * <p>
     * 包含 rolls=1 以及空的 entries 和 conditions 数组。
     *
     * @return 战利品池 JsonObject
     */
    private static JsonObject createBasePool() {
        JsonObject pool = new JsonObject();
        pool.addProperty("rolls", 1);
        pool.add("entries", new JsonArray());
        pool.add("conditions", new JsonArray());
        return pool;
    }

    /**
     * 创建物品类型条目
     * <p>
     * 仅包含 type 和 name，不预加空的 functions/conditions 数组。
     * 需要添加函数或条件的方法请使用 {@link #getOrCreateArray} 安全获取。
     *
     * @param itemId 物品 ID（含命名空间）
     * @return 条目 JsonObject
     */
    private static JsonObject createItemEntry(String itemId) {
        JsonObject entry = new JsonObject();
        entry.addProperty("type", ENTRY_TYPE_ITEM);
        entry.addProperty("name", itemId);
        return entry;
    }

    /**
     * 安全获取或创建 JsonArray
     * <p>
     * 如果指定 key 已存在则返回已有数组，否则创建一个新数组并添加到对象中。
     * 用于替代直接调用 {@code getAsJsonArray()}，避免空数组残留问题。
     *
     * @param obj 目标 JsonObject
     * @param key 数组的 key 名称
     * @return 始终非空的 JsonArray
     */
    private static JsonArray getOrCreateArray(JsonObject obj, String key) {
        if (obj.has(key)) {
            return obj.getAsJsonArray(key);
        }
        JsonArray arr = new JsonArray();
        obj.add(key, arr);
        return arr;
    }

    /**
     * 创建 survives_explosion 条件
     *
     * @return 条件 JsonObject
     */
    private static JsonObject createSurvivesExplosionCondition() {
        JsonObject condition = new JsonObject();
        condition.addProperty("condition", CONDITION_SURVIVES_EXPLOSION);
        return condition;
    }

    /**
     * 创建 match_tool + silk_touch 附魔条件
     * <p>
     * 要求工具至少拥有 1 级精准采集附魔。
     *
     * @return 条件 JsonObject
     */
    private static JsonObject createMatchToolSilkTouchCondition() {
        JsonObject condition = new JsonObject();
        condition.addProperty("condition", CONDITION_MATCH_TOOL);

        JsonObject predicate = new JsonObject();
        JsonArray enchantments = new JsonArray();

        JsonObject enchantmentObj = new JsonObject();
        enchantmentObj.addProperty("enchantment", ENCHANTMENT_SILK_TOUCH);

        JsonObject levels = new JsonObject();
        levels.addProperty("min", 1);
        enchantmentObj.add("levels", levels);

        enchantments.add(enchantmentObj);
        predicate.add("enchantments", enchantments);
        condition.add("predicate", predicate);

        return condition;
    }

    /**
     * 创建 apply_bonus + fortune + ore_drops 函数
     *
     * @return 函数 JsonObject
     */
    private static JsonObject createApplyBonusFortuneFunction() {
        JsonObject function = new JsonObject();
        function.addProperty("function", FUNCTION_APPLY_BONUS);
        function.addProperty("enchantment", ENCHANTMENT_FORTUNE);
        function.addProperty("formula", FORMULA_ORE_DROPS);
        return function;
    }

    /**
     * 创建 explosion_decay 函数
     *
     * @return 函数 JsonObject
     */
    private static JsonObject createExplosionDecayFunction() {
        JsonObject function = new JsonObject();
        function.addProperty("function", FUNCTION_EXPLOSION_DECAY);
        return function;
    }

    /**
     * 创建 set_count 函数 —— 设置掉落数量范围为 [min, max] 之间的均匀分布
     *
     * @param min 最小掉落数量
     * @param max 最大掉落数量
     * @return 函数 JsonObject
     */
    private static JsonObject createSetCountFunction(int min, int max) {
        JsonObject function = new JsonObject();
        function.addProperty("function", FUNCTION_SET_COUNT);

        JsonObject count = new JsonObject();
        count.addProperty("min", min);
        count.addProperty("max", max);
        function.add("count", count);

        return function;
    }

    /**
     * 创建 random_chance 条件 —— 以指定概率判定是否掉落
     *
     * @param chance 掉落概率（0.0 ~ 1.0）
     * @return 条件 JsonObject
     */
    private static JsonObject createRandomChanceCondition(float chance) {
        JsonObject condition = new JsonObject();
        condition.addProperty("condition", CONDITION_RANDOM_CHANCE);
        condition.addProperty("chance", chance);
        return condition;
    }
}
