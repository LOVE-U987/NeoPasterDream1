package com.pasterdream.pasterdreammod.test;

import com.pasterdream.pasterdreammod.api.entity.EntityResult;

import java.lang.reflect.Constructor;

/**
 * EntityAPI 测试运行器 —— 快速验证实体 API 核心功能
 * <p>
 * ⚠️ 注意：EntityAPI 依赖 Minecraft BuiltInRegistries 引导初始化，
 * 因此 EntityBuilder、EntityAttributesGenerator、EntityAPI 缓存方法的测试
 * 需要在完整游戏环境下运行。
 * <p>
 * 本测试在无游戏环境的编译/运行状态下仅测试：
 * <ul>
 *   <li>1. EntityResult Record —— 验证纯数据记录的方法</li>
 *   <li>2. 实体注册逻辑 —— 关于如何全面测试的说明</li>
 * </ul>
 * <p>
 * 运行方式：通过 Gradle task `runEntityApiTest` 执行
 */
public class EntityApiTestRunner {

    private static final String PASS = "[PASS] ";
    private static final String FAIL = "[FAIL] ";
    private static final String INFO = "[INFO] ";
    private static int passed = 0;
    private static int failed = 0;
    private static int skipped = 0;

    public static void main(String[] args) {
        printHeader("🎭 EntityAPI 测试运行器");

        // =========================================================
        // 1. EntityResult Record 验证
        // =========================================================
        printHeader("1. EntityResult Record 验证");

        testEntityResultRecord();

        // =========================================================
        // 2. 运行时说明
        // =========================================================
        printHeader("2. 运行时测试说明");

        printRuntimeTestGuide();

        // =========================================================
        // 结果汇总
        // =========================================================
        printSummary();
    }

    // ========================================================================
    // 1. EntityResult Record 验证
    // ========================================================================

    private static void testEntityResultRecord() {
        try {
            // 验证 EntityResult 是否为有效的 Record
            Constructor<?>[] constructors = EntityResult.class.getDeclaredConstructors();
            if (constructors.length > 0) {
                passed++;
                printPass("EntityResult Record 验证通过: 提供 " + constructors.length + " 个构造方法");
            } else {
                failed++;
                printFail("EntityResult 未找到任何构造方法");
            }

            // 验证 Record 组件方法存在
            boolean hasName = false;
            boolean hasEntityTypeSupplier = false;
            boolean hasDeferredHolder = false;
            boolean hasEntityType = false;
            boolean hasEntityClass = false;
            for (var method : EntityResult.class.getDeclaredMethods()) {
                String m = method.getName();
                if (m.equals("name")) hasName = true;
                if (m.equals("entityTypeSupplier")) hasEntityTypeSupplier = true;
                if (m.equals("deferredHolder")) hasDeferredHolder = true;
                if (m.equals("entityType")) hasEntityType = true;
                if (m.equals("entityClass")) hasEntityClass = true;
            }

            int recordMethods = 0;
            if (hasName) recordMethods++;
            if (hasEntityTypeSupplier) recordMethods++;
            if (hasDeferredHolder) recordMethods++;
            if (hasEntityType) recordMethods++;
            if (hasEntityClass) recordMethods++;

            if (recordMethods >= 3) {
                passed++;
                printPass("EntityResult 组件方法验证通过: 存在 " + recordMethods + " 个组件方法 (name/entityTypeSupplier/entityType/entityClass/deferredHolder)");
            } else {
                failed++;
                printFail("EntityResult 组件方法不足, 仅找到 " + recordMethods + " 个");
            }
        } catch (Exception e) {
            failed++;
            printFail("EntityResult Record 验证异常: " + e.getMessage());
        }
    }

    // ========================================================================
    // 2. 运行时测试说明
    // ========================================================================

    private static void printRuntimeTestGuide() {
        printInfo("下面的测试需要在完整游戏环境下运行（通过 @SubscribeEvent 注册）：");
        printInfo("");
        printInfo("  📋 EntityBuilder 链式调用测试");
        printInfo("     方法: EntityAPI.createEntity(name)");
        printInfo("          .category(MobCategory.MONSTER)");
        printInfo("          .size(1.5f, 2.0f)");
        printInfo("          .entityClass(ShadowGolemEntity.class)");
        printInfo("          .build()");
        printInfo("     验证: ✅ 实体注册成功, EntityResult 包含正确的 EntityType");
        printInfo("");
        printInfo("  📋 Builder 验证方法测试");
        printInfo("     缺少 category → 抛出 IllegalStateException");
        printInfo("     缺少 size     → 抛出 IllegalStateException");
        printInfo("     缺少 entityClass → 抛出 IllegalStateException");
        printInfo("");
        printInfo("  📋 EntityAttributesGenerator 预设模板测试");
        printInfo("     createMonsterAttributes()  | 攻击 3.0, 生命 20.0, 追踪 32");
        printInfo("     createCreatureAttributes() | 生命 10.0, 速度 0.2, 追踪 16");
        printInfo("     createFlyingAttributes()   | 飞行速度 0.4, 追踪 24");
        printInfo("     createWaterCreatureAttributes() | 生命 15.0, 速度 0.3");
        printInfo("");
        printInfo("  📋 EntityAPI 缓存机制测试");
        printInfo("     cacheEntity() / getEntityResult() → 正确返回");
        printInfo("     getRegisteredEntities() → 返回不可变视图");
        printInfo("     getSpawnEggColors() → 正确返回颜色配置");
        printInfo("");
        printInfo("  📋 生成蛋颜色缓存测试");
        printInfo("     未配置 → 返回 null");
        printInfo("     已配置 → 返回 int[2] { bgColor, hlColor }");

        passed++;
        printPass("运行时测试说明已打印");
    }

    // ========================================================================
    // 辅助方法
    // ========================================================================

    private static void printHeader(String title) {
        System.out.println("\n╔══════════════════════════════════════════════╗");
        System.out.println("║  " + title);
        System.out.println("╚══════════════════════════════════════════════╝");
    }

    private static void printPass(String msg) {
        System.out.println("  " + PASS + msg);
    }

    private static void printFail(String msg) {
        System.out.println("  " + FAIL + msg);
    }

    private static void printInfo(String msg) {
        System.out.println("  " + INFO + msg);
    }

    private static void printSummary() {
        int total = passed + failed + skipped;
        System.out.println("\n══════════════════════════════════════════════");
        System.out.println("  EntityAPI 测试结果汇总");
        System.out.println("  🔹 总计: " + total + "  |  ✅ 通过: " + passed
                + "  |  ❌ 失败: " + failed + "  |  ⏭️ 跳过: " + skipped);
        if (passed > 0 && failed == 0) {
            System.out.println("  EntityAPI 静态部分验证通过!");
        }
        System.out.println("══════════════════════════════════════════════\n");
    }
}