package com.pasterdream.pasterdreammod.api.dimension.example;

import com.pasterdream.pasterdreammod.api.dimension.gen.DimensionGenerator;
import com.pasterdream.pasterdreammod.api.dimension.gen.DimensionTypeGenerator;
import com.pasterdream.pasterdreammod.api.dimension.gen.SoundsJsonGenerator;

import java.io.IOException;

/**
 * DimensionAPI JSON 生成测试 —— 独立运行，不依赖 Minecraft 运行时
 * <p>
 * 本类仅使用 Gson 和纯 Java 标准库，可在任何环境下独立运行。
 * 用于快速验证 {@link DimensionTypeGenerator} 和 {@link DimensionGenerator}
 * 生成的 JSON 文件是否正确。
 * <p>
 * 使用方式：
 * <pre>{@code
 * .\gradlew generateDimensionTestJsons
 * }</pre>
 * 或直接在 IDE 中运行 main 方法。
 */
public class DimensionJsonGenerator {

    private static final String SEPARATOR = "══════════════════════════════════════════════════";
    private static final String BASE_PATH = "src/main/resources";

    /**
     * 生成并保存基础维度的 dimension_type JSON
     */
    public static void generateBasicDimensionTypeJson() throws IOException {
        System.out.println("\n  ▶ [生成] 基础维度类型 JSON");

        new DimensionTypeGenerator("pasterdream", "demo_basic")
                .natural(true)
                .hasSkylight(true)
                .bedWorks(true)
                .hasRaids(true)
                .ambientLight(0.5)
                .minY(-64).height(384)
                .monsterSpawnLight(0, 7)
                .effectsId("pasterdream:demo_basic")
                .saveToFile(BASE_PATH);

        System.out.println("    ✅ 已生成: data/pasterdream/dimension_type/demo_basic.json");
    }

    /**
     * 生成并保存下界风格维度的 dimension_type JSON（全参数演示）
     */
    public static void generateFullConfigTypeJson() throws IOException {
        System.out.println("\n  ▶ [生成] 完整配置维度类型 JSON（下界风格）");

        new DimensionTypeGenerator("pasterdream", "demo_nether_like")
                .ultraWarm(true)
                .natural(false)
                .piglinSafe(true)
                .respawnAnchorWorks(true)
                .bedWorks(false)
                .hasRaids(false)
                .hasSkylight(false)
                .hasCeiling(true)
                .coordinateScale(8.0)
                .ambientLight(0.1)
                .logicalHeight(128)
                .infiniburn("#minecraft:infiniburn_nether")
                .minY(0).height(256)
                .monsterSpawnLight(7, 15)
                .monsterSpawnBlockLightLimit(15)
                .effectsId("pasterdream:demo_nether_like")
                .saveToFile(BASE_PATH);

        System.out.println("    ✅ 已生成: data/pasterdream/dimension_type/demo_nether_like.json");
    }

    /**
     * 生成并保存固定生物群系维度的 dimension JSON
     */
    public static void generateFixedBiomeDimensionJson() throws IOException {
        System.out.println("\n  ▶ [生成] 固定生物群系维度 JSON");

        new DimensionGenerator("pasterdream", "demo_desert_world")
                .dimensionTypeId("pasterdream:demo_desert_world")
                .noiseSettings("minecraft:overworld")
                .defaultBlock("minecraft:sandstone")
                .defaultFluid("minecraft:water")
                .seaLevel(63)
                .minY(0).height(256)
                .fixedBiome("minecraft:desert")
                .saveToFile(BASE_PATH);

        System.out.println("    ✅ 已生成: data/pasterdream/dimension/demo_desert_world.json");
    }

    /**
     * 生成并保存多噪声生物群系维度的 dimension JSON
     */
    public static void generateMultiNoiseDimensionJson() throws IOException {
        System.out.println("\n  ▶ [生成] 多噪声生物群系维度 JSON");

        new DimensionGenerator("pasterdream", "demo_biomes")
                .dimensionTypeId("pasterdream:demo_biomes")
                .noiseSettings("pasterdream:demo_noise")
                .defaultBlock("minecraft:stone")
                .defaultFluid("minecraft:water")
                .seaLevel(63)
                .minY(-64).height(384)
                .addBiome("pasterdream:biome_dyedream_0",
                        new double[]{-0.35, 0.1}, new double[]{-0.1, 0.3},
                        new double[]{-0.11, 0.5}, new double[]{-0.19, 0.3},
                        new double[]{-0.85, 0.2})
                .addBiome("pasterdream:biome_dyedream_1",
                        new double[]{0.1, 0.5}, new double[]{0.3, 1.2},
                        new double[]{0.5, 1.2}, new double[]{0.25, 0.7},
                        new double[]{0.2, 0.6})
                .addBiome("pasterdream:biome_dyedream_2",
                        new double[]{-1.5, -0.4}, new double[]{-0.35, -0.1},
                        new double[]{0.2, 0.5}, new double[]{0.7, 1.0},
                        new double[]{0.2, 1.0})
                .saveToFile(BASE_PATH);

        System.out.println("    ✅ 已生成: data/pasterdream/dimension/demo_biomes.json");
    }

    // ========================================================================
    // 控制台预览方法（不写文件，仅打印 JSON 内容）
    // ========================================================================

    /**
     * 在控制台打印生成的 JSON 内容，方便预览
     */
    public static void previewGeneratedJsons() {
        System.out.println("\n  ▶ [预览] dimension_type JSON 内容");

        String typeJson = new DimensionTypeGenerator("pasterdream", "demo_preview")
                .natural(true).hasSkylight(true).bedWorks(true)
                .ambientLight(0.5)
                .minY(-64).height(384)
                .monsterSpawnLight(0, 7)
                .effectsId("pasterdream:demo_preview")
                .generateJson();

        System.out.println("    ┌─ dimension_type/demo_preview.json ───────┐");
        for (String line : typeJson.split("\n")) {
            System.out.println("    │ " + line);
        }
        System.out.println("    └──────────────────────────────────────────┘");

        System.out.println("\n  ▶ [预览] dimension JSON 内容（fixed biome）");

        String dimJson = new DimensionGenerator("pasterdream", "demo_preview")
                .dimensionTypeId("pasterdream:demo_preview")
                .noiseSettings("minecraft:overworld")
                .defaultBlock("minecraft:stone")
                .defaultFluid("minecraft:water")
                .seaLevel(63)
                .fixedBiome("minecraft:plains")
                .generateJson();

        System.out.println("    ┌─ dimension/demo_preview.json ───────────┐");
        for (String line : dimJson.split("\n")) {
            System.out.println("    │ " + line);
        }
        System.out.println("    └──────────────────────────────────────────┘");
    }

    /**
     * 测试背景音乐 sounds.json 条目的生成
     */
    public static void generateMusicJson() throws IOException {
        System.out.println("\n  ▶ [生成] 维度背景音乐 sounds.json 条目");

        String musicName = "demo_music";
        new SoundsJsonGenerator("pasterdream")
                .addDimensionMusic(musicName)
                .addDimensionMusic("dyedream_world")
                .saveToFile(BASE_PATH);

        System.out.println("    ✅ 已更新: assets/pasterdream/sounds.json");
        System.out.println("    ├ 新增条目: music." + musicName);
        System.out.println("    ├ 新增条目: music.dyedream_world");
        System.out.println("    └ 原有条目: life_crystal, roar0（保留未改动）");
        System.out.println("\n    📌 请放置 .ogg 音频文件:");
        System.out.println("       assets/pasterdream/sounds/music/" + musicName + ".ogg");
        System.out.println("       assets/pasterdream/sounds/music/dyedream_world.ogg");
    }

    /**
     * 程序入口
     * <p>
     * 依次执行所有生成和预览方法。
     *
     * @param args 命令行参数（未使用）
     */
    public static void main(String[] args) {
        System.out.println(SEPARATOR);
        System.out.println("  DimensionAPI JSON 生成测试");
        System.out.println("  输出目录: " + BASE_PATH + "/data/pasterdream/");
        System.out.println(SEPARATOR);

        System.out.println("\n" + SEPARATOR);
        System.out.println("  █ 批量生成 JSON 文件");
        System.out.println(SEPARATOR);

        // 生成所有类型
        try {
            generateBasicDimensionTypeJson();
            generateFullConfigTypeJson();
            generateFixedBiomeDimensionJson();
            generateMultiNoiseDimensionJson();
        } catch (Exception e) {
            System.err.println("❌ JSON 文件生成失败: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        // 预览
        System.out.println("\n" + SEPARATOR);
        System.out.println("  █ 控制台 JSON 预览");
        System.out.println(SEPARATOR);
        previewGeneratedJsons();

        // 音乐条目
        System.out.println("\n" + SEPARATOR);
        System.out.println("  █ 背景音乐 sounds.json 生成");
        System.out.println(SEPARATOR);
        try {
            generateMusicJson();
        } catch (IOException e) {
            System.err.println("❌ sounds.json 生成失败: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        // 总结
        System.out.println("\n" + SEPARATOR);
        System.out.println("  ✅ 测试完成！");
        System.out.println("  已生成以下文件：");
        System.out.println("    ├ data/pasterdream/dimension_type/demo_basic.json");
        System.out.println("    ├ data/pasterdream/dimension_type/demo_nether_like.json");
        System.out.println("    ├ data/pasterdream/dimension/demo_desert_world.json");
        System.out.println("    ├ data/pasterdream/dimension/demo_biomes.json");
        System.out.println("    └ assets/pasterdream/sounds.json（已追加音乐条目）");
        System.out.println("  📍 位置: " + BASE_PATH + "/data/pasterdream/");
        System.out.println("  " + SEPARATOR);
    }
}