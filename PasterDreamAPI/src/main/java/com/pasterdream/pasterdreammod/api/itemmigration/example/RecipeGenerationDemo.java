package com.pasterdream.pasterdreammod.api.itemmigration.example;

import com.pasterdream.pasterdreammod.api.PasterDreamAPI;
import com.pasterdream.pasterdreammod.api.itemmigration.gen.RecipeGenerator;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 配方生成示例类
 * <p>
 * 演示如何使用 RecipeGenerator 为已移植物品生成配方 JSON 文件。
 * 覆盖所有配方类型：有序合成、无序合成、熔炉/高炉冶炼、切石机加工。
 * 生成的 JSON 文件直接保存到 {@code src/main/resources/data/pasterdream/recipe/} 下。
 * </p>
 *
 * <h3>使用方式：</h3>
 * <ol>
 *   <li>直接在 {@code main} 方法中运行本类</li>
 *   <li>或在模组初始化时调用 {@link #generateAllRecipes()} 批量生成</li>
 * </ol>
 */
public class RecipeGenerationDemo {

    /** 资源文件基础路径 */
    private static final String BASE_PATH = "src/main/resources";
    /** 模组 ID */
    private static final String MOD_ID = PasterDreamAPI.MOD_ID;

    // ========================================================================
    // 入口方法
    // ========================================================================

    /**
     * 入口 —— 运行本方法即可生成所有示例配方
     *
     * @param args 命令行参数（未使用）
     */
    public static void main(String[] args) {
        try {
            generateAllRecipes();
            System.out.println("✅ 所有配方已生成到 " + BASE_PATH + "/data/" + MOD_ID + "/recipe/");
        } catch (IOException e) {
            System.err.println("❌ 配方生成失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ========================================================================
    // 批量生成全部配方
    // ========================================================================

    /**
     * 生成所有示例配方 —— 覆盖所有配方类型
     * <p>
     * 调用此方法会在 {@code src/main/resources/data/pasterdream/recipe/} 下生成 JSON 文件。
     * 如果配方文件已存在，会被覆盖。
     * </p>
     *
     * @throws IOException 当文件写入失败时抛出
     */
    public static void generateAllRecipes() throws IOException {
        generateShapedRecipes();
        generateShapelessRecipes();
        generateSmeltingRecipes();
        generateStonecuttingRecipes();
    }

    // ========================================================================
    // 1. 有序合成配方 (Shaped)
    // ========================================================================

    /**
     * 生成有序合成配方的 JSON 文件
     * <p>
     * 包括铜剑、铜镐、铜锹、铜斧、铜锄等工具的合成方式，
     * 以及玻璃杯、黑棍等材料的合成。
     * </p>
     */
    private static void generateShapedRecipes() throws IOException {
        save("copper_sword", RecipeGenerator.generateShaped(
            "pasterdream:copper_sword", 1, "equipment",
            new String[]{"a", "a", "b"},
            Map.of("a", "minecraft:copper_ingot", "b", "minecraft:stick")
        ));

        save("copper_pickaxe", RecipeGenerator.generateShaped(
            "pasterdream:copper_pickaxe", 1, "equipment",
            new String[]{"aaa", " b ", " b "},
            Map.of("a", "minecraft:copper_ingot", "b", "minecraft:stick")
        ));

        save("copper_axe", RecipeGenerator.generateShaped(
            "pasterdream:copper_axe", 1, "equipment",
            new String[]{"aa", "ab", " b"},
            Map.of("a", "minecraft:copper_ingot", "b", "minecraft:stick")
        ));

        save("copper_shovel", RecipeGenerator.generateShaped(
            "pasterdream:copper_shovel", 1, "equipment",
            new String[]{"a", "b", "b"},
            Map.of("a", "minecraft:copper_ingot", "b", "minecraft:stick")
        ));

        save("copper_hoe", RecipeGenerator.generateShaped(
            "pasterdream:copper_hoe", 1, "equipment",
            new String[]{"aa", " b", " b"},
            Map.of("a", "minecraft:copper_ingot", "b", "minecraft:stick")
        ));

        save("glass_cup", RecipeGenerator.generateShaped(
            "pasterdream:glass_cup", 3, "misc",
            new String[]{"a a", "a a", " a "},
            Map.of("a", "minecraft:glass_pane")
        ));

        save("blackstick", RecipeGenerator.generateShaped(
            "pasterdream:blackstick", 1, "misc",
            new String[]{"a", "b", "a"},
            Map.of("a", "minecraft:blackstone", "b", "minecraft:obsidian")
        ));

        save("dyedream_dust", RecipeGenerator.generateShaped(
            "pasterdream:dyedream_dust", 1, "misc",
            new String[]{"aa", "aa"},
            Map.of("a", "pasterdream:dyedream_dust_piece")
        ));

        save("titanium_ingot", RecipeGenerator.generateShaped(
            "pasterdream:titanium_ingot", 1, "misc",
            new String[]{"aaa", "aaa", "aaa"},
            Map.of("a", "pasterdream:titanium_nugget")
        ));
    }

    // ========================================================================
    // 2. 无序合成配方 (Shapeless)
    // ========================================================================

    /**
     * 生成无序合成配方的 JSON 文件
     * <p>
     * 包括食物合成、材料合成等不需要固定模板的配方。
     * </p>
     */
    private static void generateShapelessRecipes() throws IOException {
        save("apple_juice", RecipeGenerator.generateShapeless(
            "pasterdream:apple_juice", 1, "misc",
            List.of("pasterdream:glass_cup", "minecraft:apple")
        ));

        save("sandwich", RecipeGenerator.generateShapeless(
            "pasterdream:sandwich", 1, "food",
            List.of("minecraft:bread", "minecraft:cooked_chicken",
                    "pasterdream:fried_egg", "minecraft:kelp")
        ));

        save("dough", RecipeGenerator.generateShapeless(
            "pasterdream:dough", 1, "misc",
            List.of("pasterdream:water_glassjar", "pasterdream:flour", "pasterdream:flour")
        ));

        save("watermelon_juice", RecipeGenerator.generateShapeless(
            "pasterdream:watermelon_juice", 1, "misc",
            List.of("pasterdream:glass_cup", "minecraft:melon_slice")
        ));

        save("honey_juice", RecipeGenerator.generateShapeless(
            "pasterdream:honey_juice", 1, "misc",
            List.of("pasterdream:glass_cup", "minecraft:honey_bottle")
        ));

        save("unknownnotes_0", RecipeGenerator.generateShapeless(
            "pasterdream:unknownnotes_0", 1, "misc",
            List.of("pasterdream:brokennotes_0", "pasterdream:brokennotes_0",
                    "pasterdream:brokennotes_0", "pasterdream:brokennotes_0")
        ));

        save("blackmetal_ingot", RecipeGenerator.generateShapeless(
            "pasterdream:blackmetal_ingot", 1, "misc",
            List.of("pasterdream:blackmetal_grain", "pasterdream:blackmetal_grain",
                    "pasterdream:blackmetal_grain", "pasterdream:blackmetal_grain")
        ));
    }

    // ========================================================================
    // 3. 烧炼配方 (Smelting / Blasting)
    // ========================================================================

    /**
     * 生成熔炉和高炉冶炼配方的 JSON 文件
     * <p>
     * 包括矿石冶炼、食物烹饪等。
     * </p>
     */
    private static void generateSmeltingRecipes() throws IOException {
        save("bread_slice_from_smelting", RecipeGenerator.generateSmelting(
            "pasterdream:bread_slice", "pasterdream:dough", 0.35f, 200
        ));

        save("bread_slice_from_campfire", RecipeGenerator.generateCampfire(
            "pasterdream:bread_slice", "pasterdream:dough", 0.35f, 400
        ));

        save("bread_slice_from_smoking", RecipeGenerator.generateSmoking(
            "pasterdream:bread_slice", "pasterdream:dough", 0.35f, 100
        ));

        save("bread_slice_from_blasting", RecipeGenerator.generateBlasting(
            "pasterdream:bread_slice", "pasterdream:dough", 0.35f, 100
        ));

        save("titanium_ingot_from_blasting", RecipeGenerator.generateBlasting(
            "pasterdream:titanium_ingot", "pasterdream:raw_titanium", 0.7f, 100
        ));

        save("titanium_ingot_from_smelting", RecipeGenerator.generateSmelting(
            "pasterdream:titanium_ingot", "pasterdream:raw_titanium", 0.7f, 200
        ));

        save("moltengold_ingot", RecipeGenerator.generateSmelting(
            "pasterdream:moltengold_ingot", "pasterdream:raw_moltengold", 0.8f, 200
        ));

        save("bread_slice_from_flour", RecipeGenerator.generateSmelting(
            "pasterdream:bread_slice", "pasterdream:flour", 0.35f, 200
        ));
    }

    // ========================================================================
    // 4. 切石机配方 (Stonecutting)
    // ========================================================================

    /**
     * 生成切石机加工配方的 JSON 文件
     * <p>
     * 包括当前新模组已注册的染梦石英系列方块的切石加工。
     * </p>
     */
    private static void generateStonecuttingRecipes() throws IOException {
        save("bricks_dyedreamquartz_block", RecipeGenerator.generateStonecutting(
            "pasterdream:bricks_dyedreamquartz_block", 1,
            "pasterdream:dyedreamquartz_block"
        ));

        save("meltdream_crystal_lamp", RecipeGenerator.generateStonecutting(
            "pasterdream:meltdream_crystal_lamp", 1,
            "pasterdream:dyedreamquartz_block"
        ));

        save("smooth_dyedreamquartz_block", RecipeGenerator.generateStonecutting(
            "pasterdream:smooth_dyedreamquartz_block", 1,
            "pasterdream:dyedreamquartz_block"
        ));

        save("pillar_dyedreamquartz_block", RecipeGenerator.generateStonecutting(
            "pasterdream:pillar_dyedreamquartz_block", 1,
            "pasterdream:dyedreamquartz_block"
        ));

        save("dyedreamquartz_block_stairs", RecipeGenerator.generateStonecutting(
            "pasterdream:dyedreamquartz_block_stairs", 1,
            "pasterdream:dyedreamquartz_block"
        ));

        save("dyedreamquartz_block_slab", RecipeGenerator.generateStonecutting(
            "pasterdream:dyedreamquartz_block_slab", 2,
            "pasterdream:dyedreamquartz_block"
        ));

        save("dyedreamquartz_block_wall", RecipeGenerator.generateStonecutting(
            "pasterdream:dyedreamquartz_block_wall", 1,
            "pasterdream:dyedreamquartz_block"
        ));
    }

    // ========================================================================
    // 内部辅助方法
    // ========================================================================

    /**
     * 将配方 JSON 保存到文件，并打印日志
     *
     * @param recipeName 配方文件名（不含扩展名）
     * @param recipeJson 配方 JSON 字符串
     * @throws IOException 当文件写入失败时抛出
     */
    private static void save(String recipeName, String recipeJson) throws IOException {
        RecipeGenerator.saveRecipeToFile(recipeJson, MOD_ID, recipeName, BASE_PATH);
        System.out.println("  ✅ 生成配方: " + recipeName + ".json");
    }
}