package com.pasterdream.pasterdreammod.api.itemmigration.gen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

/**
 * 配方 JSON 生成器 —— 根据入参自动生成 Minecraft 各类配方的 JSON 字符串内容
 * <p>
 * 纯工具类，不依赖文件系统，只做 JSON 字符串构建。
 * 支持有序合成、无序合成、熔炉/高炉/营火/烟熏炉冶炼、切石机加工等配方类型。
 * 所有方法均返回使用 Gson 格式化后的 JSON 字符串，可直接写入配方文件。
 */
public class RecipeGenerator {

    /** Gson 实例 —— 漂亮输出格式，禁用 HTML 转义 */
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private RecipeGenerator() {
    }

    // ========================================================================
    // 内部辅助类型
    // ========================================================================

    /**
     * 表示一个合成材料，可以是物品 ID 或标签（Tag）
     * <p>
     * 当 {@code isTag} 为 {@code true} 时，{@code id} 被视为标签名（如 {@code "minecraft:planks"}）；
     * 否则视为物品 ID（如 {@code "minecraft:iron_ingot"}）。
     *
     * @param id    物品 ID 或标签名
     * @param isTag 是否为标签
     */
    public record TagOrItem(String id, boolean isTag) {
    }

    // ========================================================================
    // 合成配方 —— 有序合成 (Shaped)
    // ========================================================================

    /**
     * 生成有序合成配方的 JSON 字符串
     * <p>
     * pattern 为 1-3 行字符串数组，每行 1-3 字符；
     * keyMapping 将 pattern 中的 key 字符映射到对应的物品 ID。
     * category 可选值: "misc", "building", "equipment", "redstone"
     *
     * @param resultId   合成产物的物品 ID（如 {@code "minecraft:diamond"}）
     * @param count      合成产物数量
     * @param category   配方分类，可选 {@code "misc"}, {@code "building"}, {@code "equipment"}, {@code "redstone"}
     * @param pattern    合成模板，1-3 行字符串数组，每行 1-3 字符
     * @param keyMapping key 字符到物品 ID 的映射
     * @return 格式化后的有序合成配方 JSON 字符串
     * @throws IllegalArgumentException 当 pattern 或 keyMapping 无效时抛出
     */
    public static String generateShaped(String resultId, int count, String category, String[] pattern, Map<String, String> keyMapping) {
        JsonObject root = createShapedBase(category, pattern);
        JsonObject key = new JsonObject();
        for (Map.Entry<String, String> entry : keyMapping.entrySet()) {
            JsonObject entryObj = new JsonObject();
            entryObj.addProperty("item", entry.getValue());
            key.add(entry.getKey(), entryObj);
        }
        root.add("key", key);
        root.add("result", createResult(resultId, count));
        return GSON.toJson(root);
    }

    /**
     * 生成支持标签的有序合成配方 JSON 字符串
     * <p>
     * keyMapping 的 value 使用 {@link TagOrItem}，可指定为物品 ID 或标签。
     *
     * @param resultId   合成产物的物品 ID
     * @param count      合成产物数量
     * @param category   配方分类
     * @param pattern    合成模板
     * @param keyMapping key 字符到 {@link TagOrItem} 的映射
     * @return 格式化后的有序合成配方 JSON 字符串
     * @throws IllegalArgumentException 当 pattern 或 keyMapping 无效时抛出
     */
    public static String generateShapedWithTag(String resultId, int count, String category, String[] pattern, Map<String, TagOrItem> keyMapping) {
        JsonObject root = createShapedBase(category, pattern);
        JsonObject key = new JsonObject();
        for (Map.Entry<String, TagOrItem> entry : keyMapping.entrySet()) {
            JsonObject entryObj = new JsonObject();
            TagOrItem value = entry.getValue();
            if (value.isTag()) {
                entryObj.addProperty("tag", value.id());
            } else {
                entryObj.addProperty("item", value.id());
            }
            key.add(entry.getKey(), entryObj);
        }
        root.add("key", key);
        root.add("result", createResult(resultId, count));
        return GSON.toJson(root);
    }

    /**
     * 构建有序合成的基础 JSON 对象
     *
     * @param category 配方分类
     * @param pattern  合成模板字符串数组
     * @return 包含 type、category、pattern 的 JsonObject
     */
    private static JsonObject createShapedBase(String category, String[] pattern) {
        JsonObject root = new JsonObject();
        root.addProperty("type", "minecraft:crafting_shaped");
        root.addProperty("category", category);
        JsonArray patternArray = new JsonArray();
        for (String row : pattern) {
            patternArray.add(row);
        }
        root.add("pattern", patternArray);
        return root;
    }

    // ========================================================================
    // 合成配方 —— 无序合成 (Shapeless)
    // ========================================================================

    /**
     * 生成无序合成配方的 JSON 字符串
     * <p>
     * ingredientIds 中相同的 ID 重复出现表示需要多个该物品。
     *
     * @param resultId      合成产物的物品 ID
     * @param count         合成产物数量
     * @param category      配方分类
     * @param ingredientIds 原材料物品 ID 列表（重复表示需要多个）
     * @return 格式化后的无序合成配方 JSON 字符串
     */
    public static String generateShapeless(String resultId, int count, String category, List<String> ingredientIds) {
        JsonObject root = new JsonObject();
        root.addProperty("type", "minecraft:crafting_shapeless");
        root.addProperty("category", category);
        JsonArray ingredients = new JsonArray();
        for (String id : ingredientIds) {
            JsonObject entry = new JsonObject();
            entry.addProperty("item", id);
            ingredients.add(entry);
        }
        root.add("ingredients", ingredients);
        root.add("result", createResult(resultId, count));
        return GSON.toJson(root);
    }

    /**
     * 生成支持标签的无序合成配方 JSON 字符串
     * <p>
     * ingredients 可混合使用物品 ID 和标签。
     *
     * @param resultId    合成产物的物品 ID
     * @param count       合成产物数量
     * @param category    配方分类
     * @param ingredients 原材料列表（可混合物品和标签）
     * @return 格式化后的无序合成配方 JSON 字符串
     */
    public static String generateShapelessWithTag(String resultId, int count, String category, List<TagOrItem> ingredients) {
        JsonObject root = new JsonObject();
        root.addProperty("type", "minecraft:crafting_shapeless");
        root.addProperty("category", category);
        JsonArray ingredientsArray = new JsonArray();
        for (TagOrItem ingredient : ingredients) {
            JsonObject entry = new JsonObject();
            if (ingredient.isTag()) {
                entry.addProperty("tag", ingredient.id());
            } else {
                entry.addProperty("item", ingredient.id());
            }
            ingredientsArray.add(entry);
        }
        root.add("ingredients", ingredientsArray);
        root.add("result", createResult(resultId, count));
        return GSON.toJson(root);
    }

    // ========================================================================
    // 烧炼/烹饪配方
    // ========================================================================

    /**
     * 生成熔炉冶炼配方 JSON 字符串
     *
     * @param resultId     冶炼产物的物品 ID
     * @param ingredientId 被冶炼的物品 ID
     * @param experience   冶炼获得的经验值
     * @param cookingTime  冶炼所需 tick 数（熔炉标准为 200）
     * @return 格式化后的冶炼配方 JSON 字符串
     */
    public static String generateSmelting(String resultId, String ingredientId, float experience, int cookingTime) {
        return generateCooking("minecraft:smelting", resultId, ingredientId, experience, cookingTime);
    }

    /**
     * 生成高炉冶炼配方 JSON 字符串
     *
     * @param resultId     冶炼产物的物品 ID
     * @param ingredientId 被冶炼的物品 ID
     * @param experience   冶炼获得的经验值
     * @param cookingTime  冶炼所需 tick 数（高炉标准为 100）
     * @return 格式化后的冶炼配方 JSON 字符串
     */
    public static String generateBlasting(String resultId, String ingredientId, float experience, int cookingTime) {
        return generateCooking("minecraft:blasting", resultId, ingredientId, experience, cookingTime);
    }

    /**
     * 生成营火烹饪配方 JSON 字符串
     *
     * @param resultId     烹饪产物的物品 ID
     * @param ingredientId 被烹饪的物品 ID
     * @param experience   烹饪获得的经验值
     * @param cookingTime  烹饪所需 tick 数（营火标准为 400）
     * @return 格式化后的烹饪配方 JSON 字符串
     */
    public static String generateCampfire(String resultId, String ingredientId, float experience, int cookingTime) {
        return generateCooking("minecraft:campfire_cooking", resultId, ingredientId, experience, cookingTime);
    }

    /**
     * 生成烟熏炉烹饪配方 JSON 字符串
     *
     * @param resultId     烹饪产物的物品 ID
     * @param ingredientId 被烹饪的物品 ID
     * @param experience   烹饪获得的经验值
     * @param cookingTime  烹饪所需 tick 数（烟熏炉标准为 100）
     * @return 格式化后的烹饪配方 JSON 字符串
     */
    public static String generateSmoking(String resultId, String ingredientId, float experience, int cookingTime) {
        return generateCooking("minecraft:smoking", resultId, ingredientId, experience, cookingTime);
    }

    /**
     * 通用的烧炼/烹饪配方 JSON 构建方法
     *
     * @param type         配方类型 ID（如 {@code "minecraft:smelting"}）
     * @param resultId     产物的物品 ID
     * @param ingredientId 原材料物品 ID
     * @param experience   经验值
     * @param cookingTime  烹饪时间（tick）
     * @return 格式化后的烹饪配方 JSON 字符串
     */
    private static String generateCooking(String type, String resultId, String ingredientId, float experience, int cookingTime) {
        JsonObject root = new JsonObject();
        root.addProperty("type", type);
        JsonObject ingredient = new JsonObject();
        ingredient.addProperty("item", ingredientId);
        root.add("ingredient", ingredient);
        JsonObject result = new JsonObject();
        result.addProperty("id", resultId);
        root.add("result", result);
        root.addProperty("experience", experience);
        root.addProperty("cookingtime", cookingTime);
        return GSON.toJson(root);
    }

    // ========================================================================
    // 切石机配方 (Stonecutting)
    // ========================================================================

    /**
     * 生成切石机加工配方 JSON 字符串
     *
     * @param resultId      加工产物的物品 ID
     * @param count         加工产物数量
     * @param ingredientId  被加工的物品 ID
     * @return 格式化后的切石机加工配方 JSON 字符串
     */
    public static String generateStonecutting(String resultId, int count, String ingredientId) {
        JsonObject root = new JsonObject();
        root.addProperty("type", "minecraft:stonecutting");
        JsonObject ingredient = new JsonObject();
        ingredient.addProperty("item", ingredientId);
        root.add("ingredient", ingredient);
        root.add("result", createResult(resultId, count));
        return GSON.toJson(root);
    }

    // ========================================================================
    // 文件持久化
    // ========================================================================

    /**
     * 将配方 JSON 字符串保存到文件
     * <p>
     * 目标路径为: {@code basePath/data/modId/recipe/recipeName.json}
     * 会自动创建缺失的父级目录。
     *
     * @param recipeJson 配方 JSON 字符串内容
     * @param modId      模组 ID（作为命名空间）
     * @param recipeName 配方文件名（不含扩展名）
     * @param basePath   基础路径，通常为项目资源根目录（如 {@code "src/main/resources"}）
     * @throws IOException 当文件写入失败时抛出
     */
    public static void saveRecipeToFile(String recipeJson, String modId, String recipeName, String basePath) throws IOException {
        Path outputPath = Paths.get(basePath, "data", modId, "recipe", recipeName + ".json");
        Files.createDirectories(outputPath.getParent());
        Files.writeString(outputPath, recipeJson);
    }

    // ========================================================================
    // 内部辅助方法
    // ========================================================================

    /**
     * 创建配方产物 JSON 对象
     * <p>
     * Minecraft 1.21+ 的产物格式为 {@code {"id": "<item_id>", "count": <count>}}。
     *
     * @param id    物品 ID
     * @param count 物品数量
     * @return 产物 JsonObject
     */
    private static JsonObject createResult(String id, int count) {
        JsonObject result = new JsonObject();
        result.addProperty("id", id);
        result.addProperty("count", count);
        return result;
    }
}
