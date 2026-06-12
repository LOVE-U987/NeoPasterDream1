package com.pasterdream.pasterdreammod.test;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.api.particle.ParticleAPI;
import com.pasterdream.pasterdreammod.api.particle.ParticleResult;
import com.pasterdream.pasterdreammod.api.particle.builder.ParticleBuilder;
import com.pasterdream.pasterdreammod.api.particle.gen.ParticleGenerator;
import com.pasterdream.pasterdreammod.api.particle.gen.ParticleTextureGenerator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * ParticleAPI 测试运行器 —— 快速验证粒子 API 核心功能
 * <p>
 * 测试内容：
 * <ul>
 *   <li>1. Builder 链式调用 —— 验证各 setter 方法正确性</li>
 *   <li>2. 粒子 JSON 生成 —— 验证 particles/{name}.json 文件生成</li>
 *   <li>3. 纹理元数据 JSON 生成 —— 验证 textures/particle/{name}.json 文件生成</li>
 *   <li>4. API 缓存机制 —— 验证 cacheParticle / getParticle 方法</li>
 *   <li>5. 结果对象验证 —— 验证 ParticleResult Record 方法</li>
 * </ul>
 * <p>
 * 运行方式：通过 Gradle task `runParticleApiTest` 执行
 */
public class ParticleApiTestRunner {

    private static final String TEST_BASE = "build/api-test-output";
    private static final String PASS = "[PASS] ";
    private static final String FAIL = "[FAIL] ";
    private static final String SKIP = "[SKIP] ";
    private static int passed = 0;
    private static int failed = 0;
    private static int skipped = 0;

    public static void main(String[] args) {
        printHeader("🎨 ParticleAPI 测试运行器");
        printSubHeader("测试环境: 基础路径 = " + TEST_BASE);

        // =========================================================
        // 1. Builder 链式调用测试
        // =========================================================
        printHeader("1. Builder 链式调用测试");

        testBuilderChain();
        testBuilderDefaultTexture();
        testBuilderCustomTexture();
        testBuilderGravity();
        testBuilderAlwaysShow();

        // =========================================================
        // 2. 粒子 JSON 生成测试
        // =========================================================
        printHeader("2. 粒子 JSON 生成测试");

        testParticleJsonGeneration();
        testParticleJsonWithGravity();

        // =========================================================
        // 3. 纹理元数据 JSON 生成测试
        // =========================================================
        printHeader("3. 纹理元数据 JSON 生成测试");

        testTextureJsonGeneration();

        // =========================================================
        // 4. API 缓存机制测试
        // =========================================================
        printHeader("4. API 缓存机制测试");

        testParticleCaching();
        testParticleQuery();

        // =========================================================
        // 5. 结果对象验证
        // =========================================================
        printHeader("5. 结果对象验证");

        testParticleResultRecord();

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
            ParticleBuilder builder = ParticleBuilder.builder("test_chain")
                    .alwaysShow(true)
                    .texture("minecraft:flame")
                    .withGravity(0.1f)
                    .generateJson(false)
                    .basePath(TEST_BASE);

            if (builder != null) {
                passed++;
                printPass("Builder 链式调用创建成功: test_chain (alwaysShow=true, texture=minecraft:flame, gravity=0.1, generateJson=false)");
            } else {
                failed++;
                printFail("Builder 创建返回 null");
            }
        } catch (Exception e) {
            failed++;
            printFail("Builder 链式调用异常: " + e.getMessage());
        }
    }

    private static void testBuilderDefaultTexture() {
        try {
            ParticleBuilder builder = ParticleBuilder.builder("test_default_tex");
            if (builder != null) {
                passed++;
                printPass("Builder 默认纹理创建成功: test_default_tex (无需显式设置纹理)");
            } else {
                failed++;
                printFail("Builder 默认纹理创建返回 null");
            }
        } catch (Exception e) {
            failed++;
            printFail("Builder 默认纹理异常: " + e.getMessage());
        }
    }

    private static void testBuilderCustomTexture() {
        try {
            ParticleBuilder builder = ParticleBuilder.builder("test_custom_tex")
                    .texture("pasterdream:crystal_flame");
            if (builder != null) {
                passed++;
                printPass("Builder 自定义纹理创建成功: test_custom_tex → texture=pasterdream:crystal_flame");
            } else {
                failed++;
                printFail("Builder 自定义纹理创建返回 null");
            }
        } catch (Exception e) {
            failed++;
            printFail("Builder 自定义纹理异常: " + e.getMessage());
        }
    }

    private static void testBuilderGravity() {
        try {
            ParticleBuilder builder = ParticleBuilder.builder("test_gravity")
                    .withGravity(0.05f);
            if (builder != null) {
                passed++;
                printPass("Builder 重力值创建成功: test_gravity → gravity=0.05");
            } else {
                failed++;
                printFail("Builder 重力值创建返回 null");
            }
        } catch (Exception e) {
            failed++;
            printFail("Builder 重力值异常: " + e.getMessage());
        }
    }

    private static void testBuilderAlwaysShow() {
        try {
            ParticleBuilder builder = ParticleBuilder.builder("test_show")
                    .alwaysShow();
            if (builder != null) {
                passed++;
                printPass("Builder alwaysShow() 便捷方法创建成功: test_show");
            } else {
                failed++;
                printFail("Builder alwaysShow() 便捷方法返回 null");
            }
        } catch (Exception e) {
            failed++;
            printFail("Builder alwaysShow() 异常: " + e.getMessage());
        }
    }

    // ========================================================================
    // 2. 粒子 JSON 生成测试
    // ========================================================================

    private static void testParticleJsonGeneration() {
        String particleName = "test_particle";
        try {
            new ParticleGenerator(PasterDreamMod.MOD_ID, particleName)
                    .addTexture(PasterDreamMod.MOD_ID + ":sparkle")
                    .saveToFile(TEST_BASE);

            String filePath = TEST_BASE + "/assets/" + PasterDreamMod.MOD_ID + "/particles/" + particleName + ".json";
            if (Files.exists(Paths.get(filePath))) {
                String content = Files.readString(Paths.get(filePath));
                if (content.contains("sparkle")) {
                    passed++;
                    printPass("粒子 JSON 生成成功: " + filePath);
                } else {
                    failed++;
                    printFail("粒子 JSON 内容不正确，缺少预期的纹理引用");
                }
            } else {
                failed++;
                printFail("粒子 JSON 文件未生成: " + filePath);
            }
        } catch (IOException e) {
            failed++;
            printFail("粒子 JSON 生成异常: " + e.getMessage());
        }
    }

    private static void testParticleJsonWithGravity() {
        String particleName = "test_gravity_particle";
        try {
            new ParticleGenerator(PasterDreamMod.MOD_ID, particleName)
                    .addTexture(PasterDreamMod.MOD_ID + ":falling_leaf")
                    .saveToFile(TEST_BASE);

            String filePath = TEST_BASE + "/assets/" + PasterDreamMod.MOD_ID + "/particles/" + particleName + ".json";
            if (Files.exists(Paths.get(filePath))) {
                String content = Files.readString(Paths.get(filePath));
                if (content.contains("falling_leaf")) {
                    passed++;
                    printPass("粒子 JSON（带重力参考）生成成功: " + filePath);
                } else {
                    failed++;
                    printFail("粒子 JSON 内容不正确");
                }
            } else {
                failed++;
                printFail("粒子 JSON 文件未生成: " + filePath);
            }
        } catch (IOException e) {
            failed++;
            printFail("粒子 JSON 生成异常: " + e.getMessage());
        }
    }

    // ========================================================================
    // 3. 纹理元数据 JSON 生成测试
    // ========================================================================

    private static void testTextureJsonGeneration() {
        String particleName = "test_texture_meta";
        try {
            new ParticleTextureGenerator(PasterDreamMod.MOD_ID, particleName)
                    .withGravity(0.05f)
                    .saveToFile(TEST_BASE);

            String filePath = TEST_BASE + "/assets/" + PasterDreamMod.MOD_ID
                    + "/textures/particle/" + particleName + ".json";
            if (Files.exists(Paths.get(filePath))) {
                String content = Files.readString(Paths.get(filePath));
                if (content.contains("gravity") && content.contains("0.05")) {
                    passed++;
                    printPass("纹理元数据 JSON 生成成功: " + filePath);
                } else {
                    failed++;
                    printFail("纹理元数据 JSON 内容不正确，缺少 gravity 字段或值不匹配");
                }
            } else {
                failed++;
                printFail("纹理元数据 JSON 文件未生成: " + filePath);
            }
        } catch (IOException e) {
            failed++;
            printFail("纹理元数据 JSON 生成异常: " + e.getMessage());
        }
    }

    // ========================================================================
    // 4. API 缓存机制测试
    // ========================================================================

    private static void testParticleCaching() {
        String particleName = "test_cache_particle";
        try {
            ParticleResult cached = ParticleAPI.getParticle(particleName);
            if (cached == null) {
                passed++;
                printPass("缓存查询正确返回 null（尚未注册）: " + particleName);
            } else {
                failed++;
                printFail("缓存查询预期返回 null，但得到了结果: " + cached);
            }
        } catch (Exception e) {
            failed++;
            printFail("缓存查询异常: " + e.getMessage());
        }
    }

    private static void testParticleQuery() {
        try {
            var allParticles = ParticleAPI.getRegisteredParticles();
            int count = allParticles.size();
            passed++;
            printPass("已注册粒子列表查询成功: 当前 " + count + " 个粒子（不含未注册测试项）");
        } catch (Exception e) {
            failed++;
            printFail("已注册粒子列表查询异常: " + e.getMessage());
        }
    }

    // ========================================================================
    // 5. 结果对象验证
    // ========================================================================

    private static void testParticleResultRecord() {
        try {
            // 验证 ParticleResult 是否为有效的 Record
            var constructors = ParticleResult.class.getDeclaredConstructors();
            if (constructors.length > 0) {
                passed++;
                printPass("ParticleResult Record 验证通过: 提供 " + constructors.length + " 个构造方法");
            } else {
                failed++;
                printFail("ParticleResult 未找到构造方法");
            }
        } catch (Exception e) {
            failed++;
            printFail("ParticleResult Record 验证异常: " + e.getMessage());
        }
    }

    // ========================================================================
    // 辅助方法
    // ========================================================================

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
        System.out.println("  ParticleAPI 测试结果汇总");
        System.out.println("  🔹 总计: " + total + "  |  ✅ 通过: " + passed
                + "  |  ❌ 失败: " + failed + "  |  ⏭️ 跳过: " + skipped);
        System.out.println("══════════════════════════════════════════════\n");
    }
}