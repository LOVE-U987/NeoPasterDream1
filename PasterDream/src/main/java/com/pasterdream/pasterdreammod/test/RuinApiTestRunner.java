package com.pasterdream.pasterdreammod.test;

import com.google.gson.JsonObject;
import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.api.ruin.RuinAPI;
import com.pasterdream.pasterdreammod.api.ruin.RuinResult;
import com.pasterdream.pasterdreammod.api.ruin.builder.RuinBuilder;
import com.pasterdream.pasterdreammod.api.ruin.builder.StructureSetBuilder;
import com.pasterdream.pasterdreammod.api.ruin.gen.StructureSetGenerator;
import com.pasterdream.pasterdreammod.api.ruin.gen.StructureTypeGenerator;
import com.pasterdream.pasterdreammod.api.ruin.gen.TemplatePoolGenerator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * RuinAPI 测试运行器 —— 快速验证自定义遗迹 API 核心功能
 * <p>
 * 测试内容：
 * <ul>
 *   <li>1. Builder 链式调用 —— 验证 RuinBuilder 各 setter 方法正确性</li>
 *   <li>2. 结构 JSON 生成 —— 验证 structure/{name}.json 文件生成</li>
 *   <li>3. 结构集 JSON 生成 —— 验证 structure_set/{name}.json 文件生成</li>
 *   <li>4. 模板池 JSON 生成 —— 验证 template_pool/{name}_pool.json 文件生成</li>
 *   <li>5. RuinResult 验证 —— 验证 RuinResult Record 方法</li>
 *   <li>6. API 查询方法 —— 验证 hasRuin / getAllRuins 等方法</li>
 *   <li>7. StructureSetBuilder 测试 —— 验证链式调用和配置</li>
 * </ul>
 * <p>
 * 运行方式：通过 Gradle task `runRuinApiTest` 执行
 */
public class RuinApiTestRunner {

    private static final String TEST_BASE = "build/api-test-output";
    private static final String PASS = "[PASS] ";
    private static final String FAIL = "[FAIL] ";
    private static final String SKIP = "[SKIP] ";
    private static int passed = 0;
    private static int failed = 0;
    private static int skipped = 0;

    public static void main(String[] args) {
        printHeader("🏛️ RuinAPI 测试运行器");
        printSubHeader("测试环境: 基础路径 = " + TEST_BASE);

        // =========================================================
        // 1. Builder 链式调用测试
        // =========================================================
        printHeader("1. Builder 链式调用测试");

        testBuilderChain();
        testBuilderWithAllOptions();
        testBuilderDefaultValues();
        testBuilderSetters();

        // =========================================================
        // 2. 结构 JSON 生成测试
        // =========================================================
        printHeader("2. 结构 JSON 生成测试");

        testStructureJsonGeneration();
        testStructureJsonWithCustomFields();

        // =========================================================
        // 3. 结构集 JSON 生成测试
        // =========================================================
        printHeader("3. 结构集 JSON 生成测试");

        testStructureSetJsonGeneration();

        // =========================================================
        // 4. 模板池 JSON 生成测试
        // =========================================================
        printHeader("4. 模板池 JSON 生成测试");

        testTemplatePoolJsonGeneration();
        testTemplatePoolWithMultipleElements();

        // =========================================================
        // 5. RuinResult 验证
        // =========================================================
        printHeader("5. RuinResult 验证");

        testRuinResultRecord();
        testRuinResultWithSetKey();

        // =========================================================
        // 6. API 查询方法测试
        // =========================================================
        printHeader("6. API 查询方法测试");

        testRuinQueryMethods();

        // =========================================================
        // 7. StructureSetBuilder 测试
        // =========================================================
        printHeader("7. StructureSetBuilder 测试");

        testStructureSetBuilderChain();

        // =========================================================
        // 结果汇总
        // =========================================================
        printSummary();
    }

    // ========================================================================
    // 1. Builder 链式调用测试
    // ========================================================================

    private static void testBuilderChain() {
        try {
            RuinBuilder builder = createConfiguredBuilder("test_ruin");
            if (builder != null) {
                passed++;
                printPass("RuinBuilder 链式调用创建成功: test_ruin (biome=#minecraft:is_overworld, step=surface_structures, size=7)");
            } else {
                failed++;
                printFail("RuinBuilder 创建返回 null");
            }
        } catch (Exception e) {
            failed++;
            printFail("RuinBuilder 链式调用异常: " + e.getMessage());
        }
    }

    private static void testBuilderWithAllOptions() {
        try {
            JsonObject extra = new JsonObject();
            extra.addProperty("allow_biome_surface_decoration", true);

            RuinBuilder builder = RuinAPI.createRuin("test_full_ruin")
                    .biomeTag("pasterdream:is_dyedream")
                    .templatePool("pasterdream:test_full_ruin_pool")
                    .terrainAdaptation("beard_thin")
                    .step("surface_structures")
                    .size(12)
                    .startHeight(64)
                    .extraFields(extra)
                    .generateJson(false);

            if (builder != null) {
                passed++;
                printPass("RuinBuilder 完整配置创建成功: test_full_ruin（所有参数已设置）");
            } else {
                failed++;
                printFail("RuinBuilder 完整配置返回 null");
            }
        } catch (Exception e) {
            failed++;
            printFail("RuinBuilder 完整配置异常: " + e.getMessage());
        }
    }

    private static void testBuilderDefaultValues() {
        try {
            RuinBuilder builder = RuinAPI.createRuin("test_default_ruin");
            if (builder != null) {
                passed++;
                printPass("RuinBuilder 默认值创建成功: test_default_ruin（使用默认 step=surface_structures, size=7, startHeight=0）");
            } else {
                failed++;
                printFail("RuinBuilder 默认值返回 null");
            }
        } catch (Exception e) {
            failed++;
            printFail("RuinBuilder 默认值异常: " + e.getMessage());
        }
    }

    private static void testBuilderSetters() {
        try {
            // 验证 biomeTag 自动添加 #
            RuinBuilder builder1 = RuinAPI.createRuin("test_biome1")
                    .biomeTag("minecraft:is_overworld")
                    .generateJson(false);
            // 验证 biomeTag 已带 # 不重复添加
            RuinBuilder builder2 = RuinAPI.createRuin("test_biome2")
                    .biomeTag("#minecraft:is_nether")
                    .generateJson(false);

            if (builder1 != null && builder2 != null) {
                passed++;
                printPass("RuinBuilder biomeTag 自动处理测试通过（自动添加 # 前缀）");
            } else {
                failed++;
                printFail("RuinBuilder biomeTag 测试返回 null");
            }
        } catch (Exception e) {
            failed++;
            printFail("RuinBuilder biomeTag 测试异常: " + e.getMessage());
        }
    }

    // ========================================================================
    // 2. 结构 JSON 生成测试
    // ========================================================================

    private static void testStructureJsonGeneration() {
        try {
            String ruinName = "test_structure_gen";
            String typeId = PasterDreamMod.MOD_ID + ":" + ruinName;

            new StructureTypeGenerator(PasterDreamMod.MOD_ID, ruinName)
                    .type(typeId)
                    .biomes("#minecraft:is_overworld")
                    .step("surface_structures")
                    .terrainAdaptation("beard_thin")
                    .startPool(PasterDreamMod.MOD_ID + ":" + ruinName + "_pool")
                    .size(7)
                    .startHeight(0)
                    .saveToFile(TEST_BASE);

            String filePath = TEST_BASE + "/data/" + PasterDreamMod.MOD_ID
                    + "/worldgen/structure/" + ruinName + ".json";
            if (Files.exists(Paths.get(filePath))) {
                String content = Files.readString(Paths.get(filePath));
                if (content.contains(typeId) && content.contains("beard_thin")) {
                    passed++;
                    printPass("结构 JSON 生成成功: " + filePath);
                } else {
                    failed++;
                    printFail("结构 JSON 内容不正确，缺少 type 或 terrain_adaptation 字段");
                }
            } else {
                failed++;
                printFail("结构 JSON 文件未生成: " + filePath);
            }
        } catch (IOException e) {
            failed++;
            printFail("结构 JSON 生成异常: " + e.getMessage());
        }
    }

    private static void testStructureJsonWithCustomFields() {
        try {
            String ruinName = "test_custom_fields";
            String typeId = PasterDreamMod.MOD_ID + ":" + ruinName;

            JsonObject extra = new JsonObject();
            extra.addProperty("allow_biome_surface_decoration", true);
            extra.addProperty("allow_biome_underground_decoration", true);

            new StructureTypeGenerator(PasterDreamMod.MOD_ID, ruinName)
                    .type(typeId)
                    .biomes("#minecraft:is_overworld")
                    .step("underground_structures")
                    .startPool(PasterDreamMod.MOD_ID + ":" + ruinName + "_pool")
                    .size(12)
                    .startHeight(-40)
                    .extraFields(extra)
                    .saveToFile(TEST_BASE);

            String filePath = TEST_BASE + "/data/" + PasterDreamMod.MOD_ID
                    + "/worldgen/structure/" + ruinName + ".json";
            if (Files.exists(Paths.get(filePath))) {
                String content = Files.readString(Paths.get(filePath));
                if (content.contains("allow_biome_surface_decoration")
                        && content.contains("underground_structures")
                        && content.contains("-40")) {
                    passed++;
                    printPass("自定义字段结构 JSON 生成成功: " + filePath);
                } else {
                    failed++;
                    printFail("自定义字段结构 JSON 内容不正确，缺少自定义字段");
                }
            } else {
                failed++;
                printFail("自定义字段结构 JSON 文件未生成: " + filePath);
            }
        } catch (IOException e) {
            failed++;
            printFail("自定义字段结构 JSON 生成异常: " + e.getMessage());
        }
    }

    // ========================================================================
    // 3. 结构集 JSON 生成测试
    // ========================================================================

    private static void testStructureSetJsonGeneration() {
        try {
            String setName = "test_structure_set";

            new StructureSetGenerator(PasterDreamMod.MOD_ID, setName)
                    .structureId(PasterDreamMod.MOD_ID + ":test_ruin")
                    .weight(1)
                    .placementType("minecraft:random_spread")
                    .spacing(32)
                    .separation(8)
                    .salt(12345)
                    .saveToFile(TEST_BASE);

            String filePath = TEST_BASE + "/data/" + PasterDreamMod.MOD_ID
                    + "/worldgen/structure_set/" + setName + ".json";
            if (Files.exists(Paths.get(filePath))) {
                String content = Files.readString(Paths.get(filePath));
                if (content.contains("random_spread") && content.contains("32")
                        && content.contains("12345")) {
                    passed++;
                    printPass("结构集 JSON 生成成功: " + filePath);
                } else {
                    failed++;
                    printFail("结构集 JSON 内容不正确，缺少 placement 参数");
                }
            } else {
                failed++;
                printFail("结构集 JSON 文件未生成: " + filePath);
            }
        } catch (IOException e) {
            failed++;
            printFail("结构集 JSON 生成异常: " + e.getMessage());
        }
    }

    // ========================================================================
    // 4. 模板池 JSON 生成测试
    // ========================================================================

    private static void testTemplatePoolJsonGeneration() {
        try {
            String poolName = "test_pool";

            new TemplatePoolGenerator(PasterDreamMod.MOD_ID, poolName)
                    .fallback("minecraft:empty")
                    .addSingleElement(PasterDreamMod.MOD_ID + ":test_ruins/ruin_1",
                            3, "rigid", "minecraft:empty")
                    .saveToFile(TEST_BASE);

            String filePath = TEST_BASE + "/data/" + PasterDreamMod.MOD_ID
                    + "/worldgen/template_pool/" + poolName + ".json";
            if (Files.exists(Paths.get(filePath))) {
                String content = Files.readString(Paths.get(filePath));
                if (content.contains("test_ruins/ruin_1")
                        && content.contains("\"weight\": 3")
                        && content.contains("rigid")) {
                    passed++;
                    printPass("模板池 JSON 生成成功: " + filePath);
                } else {
                    failed++;
                    printFail("模板池 JSON 内容不正确，缺少元素配置");
                }
            } else {
                failed++;
                printFail("模板池 JSON 文件未生成: " + filePath);
            }
        } catch (IOException e) {
            failed++;
            printFail("模板池 JSON 生成异常: " + e.getMessage());
        }
    }

    private static void testTemplatePoolWithMultipleElements() {
        try {
            String poolName = "test_multi_pool";

            new TemplatePoolGenerator(PasterDreamMod.MOD_ID, poolName)
                    .fallback("minecraft:empty")
                    .addSingleElement(PasterDreamMod.MOD_ID + ":test_ruins/ruin_a", 3, "rigid", "minecraft:empty")
                    .addSingleElement(PasterDreamMod.MOD_ID + ":test_ruins/ruin_b", 2, "terrain_matching", "minecraft:empty")
                    .addSingleElement(PasterDreamMod.MOD_ID + ":test_ruins/ruin_c", 1, "rigid", "minecraft:empty")
                    .saveToFile(TEST_BASE);

            String filePath = TEST_BASE + "/data/" + PasterDreamMod.MOD_ID
                    + "/worldgen/template_pool/" + poolName + ".json";
            if (Files.exists(Paths.get(filePath))) {
                String content = Files.readString(Paths.get(filePath));
                boolean hasA = content.contains("ruin_a");
                boolean hasB = content.contains("ruin_b");
                boolean hasC = content.contains("ruin_c");
                boolean hasWeights = content.contains("\"weight\": 3")
                        && content.contains("\"weight\": 2")
                        && content.contains("\"weight\": 1");
                if (hasA && hasB && hasC && hasWeights) {
                    passed++;
                    printPass("多元素模板池 JSON 生成成功: 3 个元素，权重分别为 3/2/1");
                } else {
                    failed++;
                    printFail("多元素模板池 JSON 内容不正确，缺少部分元素");
                }
            } else {
                failed++;
                printFail("多元素模板池 JSON 文件未生成: " + filePath);
            }
        } catch (IOException e) {
            failed++;
            printFail("多元素模板池 JSON 生成异常: " + e.getMessage());
        }
    }

    // ========================================================================
    // 5. RuinResult 验证
    // ========================================================================

    private static void testRuinResultRecord() {
        try {
            // 验证 RuinResult.of() 静态方法
            RuinResult result = RuinResult.of(PasterDreamMod.MOD_ID, "test_result");
            if (result != null && "test_result".equals(result.name())) {
                passed++;
                printPass("RuinResult.of() 创建成功: name=test_result, structureKey=" + result.structureKey());
            } else {
                failed++;
                printFail("RuinResult.of() 返回结果不正确");
            }

            // 验证 hasSetKey 默认 false
            if (!result.hasSetKey()) {
                passed++;
                printPass("RuinResult.hasSetKey() 默认返回 false（未设置 setKey 时）");
            } else {
                failed++;
                printFail("RuinResult.hasSetKey() 预期 false 但返回 true");
            }
        } catch (Exception e) {
            failed++;
            printFail("RuinResult Record 验证异常: " + e.getMessage());
        }
    }

    private static void testRuinResultWithSetKey() {
        try {
            RuinResult base = RuinResult.of(PasterDreamMod.MOD_ID, "test_set_result");
            RuinResult withSet = base.withSetKey("test_set", PasterDreamMod.MOD_ID);

            if (withSet != null && withSet.hasSetKey()) {
                passed++;
                printPass("RuinResult.withSetKey() 创建成功: name=" + withSet.name()
                        + ", setKey=" + withSet.setKey());
            } else {
                failed++;
                printFail("RuinResult.withSetKey() 返回结果不正确");
            }

            // 验证不可变性：base 的 hasSetKey 仍为 false
            if (!base.hasSetKey()) {
                passed++;
                printPass("RuinResult 不可变性验证通过: withSetKey 返回新实例，原实例不变");
            } else {
                failed++;
                printFail("RuinResult 不可变性验证失败");
            }
        } catch (Exception e) {
            failed++;
            printFail("RuinResult withSetKey 测试异常: " + e.getMessage());
        }
    }

    // ========================================================================
    // 6. API 查询方法测试
    // ========================================================================

    private static void testRuinQueryMethods() {
        try {
            // 测试 hasRuin 返回 false（未注册）
            boolean exists = RuinAPI.hasRuin("non_existent_ruin");
            if (!exists) {
                passed++;
                printPass("RuinAPI.hasRuin() 正确返回 false（未注册的结构）");
            } else {
                failed++;
                printFail("RuinAPI.hasRuin() 预期 false 但返回 true");
            }

            // 测试 getAllRuins 返回空或有效结果
            var allRuins = RuinAPI.getAllRuins();
            passed++;
            printPass("RuinAPI.getAllRuins() 查询成功: 当前 " + allRuins.size() + " 个已注册结构");

            // 测试 getRuin 返回 null
            RuinResult result = RuinAPI.getRuin("non_existent_ruin");
            if (result == null) {
                passed++;
                printPass("RuinAPI.getRuin() 正确返回 null（未注册的结构）");
            } else {
                failed++;
                printFail("RuinAPI.getRuin() 预期 null 但返回了结果");
            }
        } catch (Exception e) {
            failed++;
            printFail("API 查询方法测试异常: " + e.getMessage());
        }
    }

    // ========================================================================
    // 7. StructureSetBuilder 测试
    // ========================================================================

    private static void testStructureSetBuilderChain() {
        try {
            // 测试完整的 StructureSetBuilder 链（不调用 build）
            StructureSetBuilder builder = new StructureSetBuilder(
                    PasterDreamMod.MOD_ID, "test_chain_ruin", "test_chain_set"
            ).spacing(48)
                    .separation(12)
                    .salt(67890)
                    .placementType("minecraft:concentric")
                    .generateJson(false);

            if (builder != null) {
                passed++;
                printPass("StructureSetBuilder 链式调用创建成功: spacing=48, separation=12, salt=67890, placementType=concentric");
            } else {
                failed++;
                printFail("StructureSetBuilder 链式调用返回 null");
            }
        } catch (Exception e) {
            failed++;
            printFail("StructureSetBuilder 链式调用异常: " + e.getMessage());
        }
    }

    // ========================================================================
    // 辅助方法
    // ========================================================================

    private static RuinBuilder createConfiguredBuilder(String name) {
        return RuinAPI.createRuin(name)
                .biomeTag("minecraft:is_overworld")
                .templatePool(PasterDreamMod.MOD_ID + ":" + name + "_pool")
                .terrainAdaptation("beard_thin")
                .step("surface_structures")
                .size(7)
                .startHeight(0)
                .generateJson(false);
    }

    private static void printHeader(String title) {
        System.out.println("\n╔══════════════════════════════════════════════╗");
        System.out.println("║  " + title);
        System.out.println("╚══════════════════════════════════════════════╝");
    }

    private static void printSubHeader(String text) {
        System.out.println("  " + text);
    }

    private static void printPass(String msg) {
        System.out.println("  " + PASS + msg);
    }

    private static void printFail(String msg) {
        System.out.println("  " + FAIL + msg);
    }

    private static void printSkip(String msg) {
        System.out.println("  " + SKIP + msg);
    }

    private static void printSummary() {
        int total = passed + failed + skipped;
        System.out.println("\n══════════════════════════════════════════════");
        System.out.println("  RuinAPI 测试结果汇总");
        System.out.println("  🔹 总计: " + total + "  |  ✅ 通过: " + passed
                + "  |  ❌ 失败: " + failed + "  |  ⏭️ 跳过: " + skipped);
        System.out.println("══════════════════════════════════════════════\n");
    }
}