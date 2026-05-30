package com.pasterdream.pasterdreammod.api.dimension.example;

import com.pasterdream.pasterdreammod.api.dimension.DimensionAPI;
import com.pasterdream.pasterdreammod.api.dimension.DimensionResult;
import com.pasterdream.pasterdreammod.api.dimension.gen.DimensionGenerator;
import com.pasterdream.pasterdreammod.api.dimension.gen.DimensionTypeGenerator;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.world.phys.Vec3;

/**
 * DimensionAPI 使用示例类
 * <p>
 * 本示例类全面展示了 DimensionAPI 的核心功能和配置模式。
 * 通过 {@link #main(String[])} 可直接运行，演示完整的维度配置和 JSON 文件生成流程。
 * <p>
 * 演示内容：
 * <ul>
 *   <li>1. 基础维度创建 —— 类似染梦世界的标准配置</li>
 *   <li>2. 完整维度配置 —— 展示全部可配参数</li>
 *   <li>3. 固定生物群系维度 —— 单一生物群系世界</li>
 *   <li>4. 自定义噪声设置维度 —— 引用的外部噪声配置</li>
 *   <li>5. DimensionSpecialEffects 注册 —— 客户端特效配置</li>
 *   <li>6. 工具方法 —— DimensionResult 查询和判断</li>
 *   <li>7. 独立 JSON 生成 —— 手动调用生成器生成 JSON</li>
 * </ul>
 * <p>
 * 注意：运行此示例会实际生成 JSON 文件到 resources 目录！
 * 运行后请检查 {@code src/main/resources/data/pasterdream/dimension_type/} 和
 * {@code src/main/resources/data/pasterdream/dimension/} 目录下的文件。
 */
public class DimensionApiDemo {

    private static final String SEPARATOR = "══════════════════════════════════════════════════";

    static {
        System.out.println(SEPARATOR);
        System.out.println("  DimensionAPI 示例类已加载");
        System.out.println("  模组 ID: pasterdream");
        System.out.println("  JSON 输出目录: src/main/resources/data/pasterdream/");
        System.out.println(SEPARATOR);
    }

    // ========================================================================
    // 1. 基础维度创建 演示
    // ========================================================================

    /**
     * 演示最基本的维度创建方式
     * <p>
     * 展示如何创建一个类似主世界的标准维度，使用便捷无参方法（默认 true）。
     * builder 模式与 {@link com.pasterdream.pasterdreammod.api.block.BlockAPI} 风格一致。
     */
    public static void demoBasicDimension() {
        System.out.println("\n  ▶ [模式一] 基础维度 —— 标准配置");

        System.out.println("""
                // 链式调用创建维度，与 BlockAPI 风格一致：
                DimensionResult result = DimensionAPI.createDimension("demo_basic")
                    .natural()                          // 自然维度，可睡床重生
                    .hasSkylight()                      // 有天空光照
                    .bedWorks()                         // 床可工作
                    .hasRaids()                         // 有袭击事件
                    .withAmbientLight(0.5)              // 环境光照 50%
                    .minY(-64).height(384)              // Y 轴范围
                    .monsterSpawnLight(0, 7)            // 怪物生成光照
                    .withDefaultBlock("minecraft:stone")
                    .withDefaultFluid("minecraft:water")
                    .build();
                // ↑ build() 自动生成 JSON 文件！""");

        // 实际执行演示（不真的生成 JSON）
        DimensionResult result = DimensionAPI.createDimension("demo_basic")
                .natural()
                .hasSkylight()
                .bedWorks()
                .hasRaids()
                .withAmbientLight(0.5)
                .minY(-64).height(384)
                .monsterSpawnLight(0, 7)
                .withDefaultBlock("minecraft:stone")
                .withDefaultFluid("minecraft:water")
                // 演示模式不生成真实文件
                .generateJson(false)
                .build();

        System.out.println("    ✅ 基础维度创建完成");
        System.out.println("    ├ dimensionName: " + result.dimensionName());
        System.out.println("    ├ dimensionTypeId: " + result.dimensionTypeId());
        System.out.println("    ├ levelKey: " + result.levelKey().location());
        System.out.println("    ├ typeKey: " + result.typeKey().location());
        System.out.println("    └ effectsId: " + result.effectsId());
        System.out.println("    ⚠️ 提示：generateJson(false) 跳过了文件生成");
    }

    // ========================================================================
    // 2. 完整维度配置 演示
    // ========================================================================

    /**
     * 演示全参数配置的维度创建方式
     * <p>
     * 展示 DimensionType 和 Dimension 的全部可配置参数，
     * 包括下界风格、固定时间、自定义无限燃烧标签等高级特性。
     */
    public static void demoFullConfigDimension() {
        System.out.println("\n  ▶ [模式二] 完整配置 —— 全部参数展示");

        System.out.println("""
                // 下界风格的维度完整配置示例：
                DimensionResult result = DimensionAPI.createDimension("demo_nether_like")
                    .ultraWarm(true)                    // 超热（水蒸发、可燃）
                    .natural(false)                     // 非自然（不能睡床）
                    .piglinSafe(true)                   // 猪灵安全
                    .respawnAnchorWorks(true)            // 重生锚可用
                    .bedWorks(false)                    // 床会爆炸
                    .hasRaids(false)                    // 无袭击
                    .hasSkylight(false)                 // 无天空光照
                    .hasCeiling(true)                   // 有天花板
                    .coordinateScale(8.0)               // 坐标缩放 8 倍
                    .withAmbientLight(0.1)              // 环境光照 10%
                    .logicalHeight(128)                 // 逻辑高度 128
                    .infiniburn("#minecraft:infiniburn_nether")
                    .minY(0).height(256)                // Y 轴 0~256
                    .monsterSpawnLight(7, 15)           // 全天候刷怪
                    .monsterSpawnBlockLightLimit(15)    // 方块光照不限制
                    .withDefaultBlock("minecraft:netherrack")
                    .withDefaultFluid("minecraft:lava")
                    .seaLevel(32)
                    .disableMobGeneration(false)
                    .aquifersEnabled(false)
                    .oreVeinsEnabled(true)
                    .build();""");

        DimensionResult result = DimensionAPI.createDimension("demo_nether_like")
                .ultraWarm(true)
                .natural(false)
                .piglinSafe(true)
                .respawnAnchorWorks(true)
                .bedWorks(false)
                .hasRaids(false)
                .hasSkylight(false)
                .hasCeiling(true)
                .coordinateScale(8.0)
                .withAmbientLight(0.1)
                .logicalHeight(128)
                .infiniburn("#minecraft:infiniburn_nether")
                .minY(0).height(256)
                .monsterSpawnLight(7, 15)
                .monsterSpawnBlockLightLimit(15)
                .withDefaultBlock("minecraft:netherrack")
                .withDefaultFluid("minecraft:lava")
                .seaLevel(32)
                .disableMobGeneration(false)
                .aquifersEnabled(false)
                .oreVeinsEnabled(true)
                .generateJson(false)
                .build();

        System.out.println("    ✅ 完整配置维度创建完成");
        System.out.println("    ├ 维度类型: " + result.dimensionName());
        System.out.println("    ├ coordinate_scale: 8.0（移动 1 格 = 主世界 8 格）");
        System.out.println("    ├ ultrawarm: true（水会蒸发）");
        System.out.println("    └ hasCeiling: true（有基岩天花板）");
    }

    // ========================================================================
    // 3. 固定生物群系维度 演示
    // ========================================================================

    /**
     * 演示固定生物群系维度的创建方式
     * <p>
     * 使用单一生物群系源，整个维度只有一个生物群系。
     * 适用于特殊主题世界（如：全是沙漠、全是海洋）。
     */
    public static void demoFixedBiomeDimension() {
        System.out.println("\n  ▶ [模式三] 固定生物群系 —— 单一群系世界");

        System.out.println("""
                // 固定生物群系维度，整个世界只有沙漠：
                DimensionResult result = DimensionAPI.createDimension("demo_desert_world")
                    .natural().hasSkylight().bedWorks()
                    .withAmbientLight(0.5)
                    .minY(0).height(256)
                    .monsterSpawnLight(0, 7)
                    .withDefaultBlock("minecraft:sandstone")
                    .withDefaultFluid("minecraft:water")
                    .seaLevel(63)
                    .withFixedBiome("minecraft:desert")    // ← 固定为沙漠
                    .build();""");

        DimensionResult result = DimensionAPI.createDimension("demo_desert_world")
                .natural().hasSkylight().bedWorks()
                .withAmbientLight(0.5)
                .minY(0).height(256)
                .monsterSpawnLight(0, 7)
                .withDefaultBlock("minecraft:sandstone")
                .withDefaultFluid("minecraft:water")
                .seaLevel(63)
                .withFixedBiome("minecraft:desert")
                .generateJson(false)
                .build();

        System.out.println("    ✅ 固定生物群系维度创建完成");
        System.out.println("    ├ biome_source.type: minecraft:fixed");
        System.out.println("    ├ biome: minecraft:desert");
        System.out.println("    └ 整个维度只有沙漠群系");
    }

    // ========================================================================
    // 4. 多噪声生物群系 演示
    // ========================================================================

    /**
     * 演示多噪声生物群系维度的创建方式（类似染梦世界）
     * <p>
     * 使用 {@link DimensionBuilder#addBiome} 方法逐个添加生物群系，
     * 每个群系可配置温度/湿度/大陆性/怪异度/侵蚀度参数范围。
     */
    public static void demoMultiNoiseBiomeDimension() {
        System.out.println("\n  ▶ [模式四] 多噪声生物群系 —— 类似染梦世界");

        System.out.println("""
                // 通过 addBiome 逐一添加生物群系（温度/湿度/大陆性/侵蚀/怪异）：
                DimensionResult result = DimensionAPI.createDimension("demo_biomes")
                    .natural().hasSkylight().bedWorks()
                    .withAmbientLight(0.5)
                    .minY(-64).height(384)
                    .monsterSpawnLight(0, 7)
                    .withDefaultBlock("minecraft:stone")
                    .withDefaultFluid("minecraft:water")
                    .withNoiseSettings("pasterdream:demo_noise")
                    // 添加生物群系（参数: 温度, 湿度, 大陆性, 侵蚀度, 怪异度）
                    .addBiome("pasterdream:biome_dyedream_0",
                        new double[]{-0.35, 0.1},    // 温度
                        new double[]{-0.1, 0.3},     // 湿度
                        new double[]{-0.11, 0.5},    // 大陆性
                        new double[]{-0.19, 0.3},    // 侵蚀度
                        new double[]{-0.85, 0.2})    // 怪异度
                    .addBiome("pasterdream:biome_dyedream_1",
                        new double[]{0.1, 0.5},
                        new double[]{0.3, 1.2},
                        new double[]{0.5, 1.2},
                        new double[]{0.25, 0.7},
                        new double[]{0.2, 0.6})
                    .addBiome("pasterdream:biome_dyedream_2",
                        new double[]{-1.5, -0.4},
                        new double[]{-0.35, -0.1},
                        new double[]{0.2, 0.5},
                        new double[]{0.7, 1.0},
                        new double[]{0.2, 1.0})
                    .build();""");

        DimensionResult result = DimensionAPI.createDimension("demo_biomes")
                .natural().hasSkylight().bedWorks()
                .withAmbientLight(0.5)
                .minY(-64).height(384)
                .monsterSpawnLight(0, 7)
                .withDefaultBlock("minecraft:stone")
                .withDefaultFluid("minecraft:water")
                .withNoiseSettings("pasterdream:demo_noise")
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
                .generateJson(false)
                .build();

        System.out.println("    ✅ 多噪声生物群系维度创建完成");
        System.out.println("    ├ biome_source.type: minecraft:multi_noise");
        System.out.println("    ├ 已添加 3 个生物群系");
        System.out.println("    └ 每个群系配置了 5 个噪声参数维度");

        // 额外演示：获取已缓存的维度结果
        DimensionResult cached = DimensionAPI.getRegisteredDimension("demo_biomes");
        if (cached != null) {
            System.out.println("    📦 缓存命中: DimensionAPI.getRegisteredDimension() → " + cached.dimensionName());
        }
    }

    // ========================================================================
    // 5. 客户端特效配置 演示
    // ========================================================================

    /**
     * 演示 DimensionSpecialEffects 的配置方式
     * <p>
     * 展示如何在客户端注册自定义的天空颜色、雾气效果和云层高度。
     * 此代码需要在 {@link net.neoforged.neoforge.client.event.RegisterDimensionSpecialEffectsEvent} 中执行。
     */
    public static void demoSpecialEffects() {
        System.out.println("\n  ▶ [模式五] 客户端特效 —— 自定义天空和雾气");

        System.out.println("""
                // 在 ClientSetup 的 @SubscribeEvent 方法中：
                @SubscribeEvent
                public static void registerEffects(RegisterDimensionSpecialEffectsEvent event) {
                    DimensionAPI.registerEffects(event, "demo_special_sky",
                        new DimensionSpecialEffects(
                            192.0f,                    // 云层高度
                            true,                      // 有云
                            SkyType.NORMAL,            // 天空类型
                            false,                     // 强制明亮
                            false                      // 无环境光照
                        ) {
                            @Override
                            public Vec3 getBrightnessDependentFogColor(
                                    Vec3 fogColor, float sunHeight) {
                                // 粉紫色雾气效果
                                return new Vec3(0.8, 0.3, 0.9)
                                    .multiply(sunHeight * 0.8 + 0.2,
                                             sunHeight * 0.8 + 0.2,
                                             sunHeight * 0.8 + 0.2);
                            }

                            @Override
                            public boolean isFoggyAt(int x, int y) {
                                return y < 32;  // 低于 Y=32 时有雾
                            }
                        }
                    );
                }""");

        System.out.println("    ✅ 特效配置完成（运行时需在 ClientSetup 中注册）");
        System.out.println("    ├ 云层高度: 192.0");
        System.out.println("    ├ 雾气颜色: 粉紫色渐变 (0.8, 0.3, 0.9)");
        System.out.println("    └ 雾区: Y < 32");
        System.out.println("    ⚠️ 提示：该功能仅在客户端生效（Dist.CLIENT）");
    }

    // ========================================================================
    // 6. 工具方法 演示
    // ========================================================================

    /**
     * 演示 DimensionResult 的工具方法使用方式
     * <p>
     * 展示 {@link DimensionResult#isDimension(net.minecraft.world.level.Level)}、
     * {@link DimensionAPI#isInDimension} 和 {@link DimensionAPI#getRegisteredDimension}
     * 的用法。
     */
    public static void demoUtilityMethods() {
        System.out.println("\n  ▶ [模式六] 工具方法 —— 维度判断与查询");

        System.out.println("""
                // 1. 通过 DimensionResult 判断维度：
                DimensionResult myDim = DimensionAPI.createDimension("demo_tool")
                    .natural().hasSkylight().build();
                
                if (myDim.isDimension(level)) {
                    // 在当前维度中...
                }

                // 2. 通过 DimensionAPI 工具方法：
                if (DimensionAPI.isInDimension(level, myDim)) {
                    // 在当前维度中...
                }

                // 3. 获取已注册的维度结果（通过缓存）：
                DimensionResult cached = DimensionAPI.getRegisteredDimension("dyedream_world");
                if (cached != null) {
                    ResourceKey<Level> levelKey = cached.levelKey();
                    ResourceKey<DimensionType> typeKey = cached.typeKey();
                    String effectsId = cached.effectsId();
                }

                // 4. 向后兼容（使用原有的 PDDimensions 常量）：
                boolean isDyedream = PDDimensions.isDyedreamWorld(level);
                boolean isOverworld = PDDimensions.isOverworld(level);""");

        DimensionResult demoResult = DimensionAPI.createDimension("demo_tool")
                .natural().hasSkylight().bedWorks()
                .generateJson(false)
                .build();
        DimensionAPI.cacheDimension(demoResult);

        DimensionResult cached = DimensionAPI.getRegisteredDimension("demo_tool");
        System.out.println("    ✅ 工具方法演示完成");
        System.out.println("    ├ DimensionResult.isDimension()     — 通过结果判断");
        System.out.println("    ├ DimensionAPI.isInDimension()     — 通过 API 判断");
        System.out.println("    ├ DimensionAPI.getRegisteredDimension() — 缓存查询");
        System.out.println("    └ 缓存查询结果: " + (cached != null ? cached.dimensionName() : "null"));
    }

    // ========================================================================
    // 7. 独立 JSON 生成 演示
    // ========================================================================

    /**
     * 演示独立使用 JSON 生成器的功能
     * <p>
     * 展示不通过 {@code build()}，而是手动调用
     * {@link DimensionTypeGenerator} 和 {@link DimensionGenerator}
     * 来生成 JSON 文件。
     * 适用于需要在非标准流程中单独生成配置文件的场景。
     */
    public static void demoStandaloneJsonGeneration() {
        System.out.println("\n  ▶ [模式七] 独立 JSON 生成 —— 手动调用生成器");

        System.out.println("""
                // 方法一：通过 DimensionAPI 静态方法获取生成器：
                DimensionAPI.generateDimensionTypeJson("demo_standalone")
                    .natural(true)
                    .hasSkylight(true)
                    .bedWorks(true)
                    .ambientLight(0.3)
                    .minY(0).height(256)
                    .effectsId("pasterdream:demo_standalone")
                    .saveToFile("src/main/resources");  // 实际写入文件！

                DimensionAPI.generateDimensionJson("demo_standalone")
                    .dimensionTypeId("pasterdream:demo_standalone")
                    .noiseSettings("minecraft:overworld")
                    .defaultBlock("minecraft:grass_block")
                    .defaultFluid("minecraft:water")
                    .fixedBiome("minecraft:plains")
                    .saveToFile("src/main/resources");  // 实际写入文件！

                // 方法二：直接 new 生成器（更灵活）：
                var typeGen = new DimensionTypeGenerator("pasterdream", "demo_direct");
                typeGen.natural(true).hasSkylight(true).bedWorks(true);
                System.out.println(typeGen.generateJson());  // 仅输出 JSON 字符串""");

        System.out.println("    ✅ 独立 JSON 生成演示完成");
        System.out.println("    ├ DimensionAPI.generateDimensionTypeJson() — 获取类型生成器");
        System.out.println("    ├ DimensionAPI.generateDimensionJson()     — 获取维度生成器");
        System.out.println("    ├ saveToFile() — 写入文件");
        System.out.println("    └ generateJson() — 仅返回字符串不写入");
        System.out.println("    ⚠️ 提示：实际执行时请调用 saveToFile()");
    }

    // ========================================================================
    // 8. 生成的 JSON 预览 演示
    // ========================================================================

    /**
     * 演示生成的 JSON 文件内容预览
     * <p>
     * 通过 {@link DimensionTypeGenerator#generateJson()} 和
     * {@link DimensionGenerator#generateJson()} 方法，
     * 展示最终生成的 JSON 文件结构，方便开发者对照验证。
     */
    public static void demoJsonPreview() {
        System.out.println("\n  ▶ [模式八] JSON 预览 —— 查看生成的文件内容");

        // 模拟一个标准维度的 JSON
        String typeJson = new DimensionTypeGenerator("pasterdream", "demo_preview")
                .natural(true).hasSkylight(true).bedWorks(true)
                .hasRaids(true)
                .ambientLight(0.5)
                .minY(-64).height(384)
                .monsterSpawnLight(0, 7)
                .effectsId("pasterdream:demo_preview")
                .generateJson();

        System.out.println("    ┌─ dimension_type JSON 预览 ─────────────────┐");
        System.out.println("    │ " + typeJson.replace("\n", "\n    │ "));
        System.out.println("    └─────────────────────────────────────────────┘");

        String dimJson = new DimensionGenerator("pasterdream", "demo_preview")
                .dimensionTypeId("pasterdream:demo_preview")
                .noiseSettings("pasterdream:demo_preview")
                .defaultBlock("minecraft:calcite")
                .defaultFluid("minecraft:water")
                .seaLevel(63)
                .fixedBiome("pasterdream:biome_dyedream_0")
                .generateJson();

        System.out.println("    ┌─ dimension JSON 预览（fixed biome） ───────┐");
        System.out.println("    │ " + dimJson.replace("\n", "\n    │ "));
        System.out.println("    └─────────────────────────────────────────────┘");
    }

    // ========================================================================
    // 9. 设计理念 演示
    // ========================================================================

    /**
     * 演示 DimensionAPI 与 BlockAPI 的设计风格对比
     * <p>
     * 展示统一 API 风格如何降低学习成本和提高开发效率。
     */
    public static void demoDesignPhilosophy() {
        System.out.println("\n  ▶ [模式九] 设计理念 —— 统一 API 风格");

        System.out.println("""
                ╔═══════════════════════════════════════════════════════════════╗
                ║             DimensionAPI vs BlockAPI 风格对照                ║
                ╠═══════════════════════════════════════════════════════════════╣
                ║                                                              ║
                ║  BlockAPI.createDimension()  ←→  BlockAPI.createVariantSet() ║
                ║       ↓ DimensionBuilder     ←→       ↓ VariantSetBuilder   ║
                ║       ↓ .build()             ←→       ↓ .build()            ║
                ║       ↓ DimensionResult      ←→       ↓ VariantSetResult    ║
                ║                                                              ║
                ║  统一设计模式: Facade + Builder + Result                     ║
                ║                                                              ║
                ║  核心理念:                                                  ║
                ║  1. 把「繁琐的注册」封装成「优雅的链式调用」                 ║
                ║  2. 自动生成 JSON 配置文件                                  ║
                ║  3. 保持向后兼容，不破坏现有代码                             ║
                ║  4. 提供便捷方法和完整方法两种选择                           ║
                ║                                                              ║
                ╚═══════════════════════════════════════════════════════════════╝""");
    }

    // ========================================================================
    // 入口方法
    // ========================================================================

    /**
     * 程序入口方法
     * <p>
     * 依次调用所有演示模块，全面展示 DimensionAPI 的功能。
     * 注意：本示例在非 Minecraft 环境下运行，仅展示 API 使用模式和代码结构。
     * 实际的维度注册需要在 {@code PasterDreamMod} 或 {@code PDDimensions} 中执行。
     *
     * @param args 命令行参数（未使用）
     */
    public static void main(String[] args) {
        System.out.println(SEPARATOR);
        System.out.println("  DimensionAPI 示例程序启动");
        System.out.println("  共 9 个演示模块");
        System.out.println("  提示：本示例会展示 API 调用模式，部分演示会实际生成 JSON 文件！");
        System.out.println(SEPARATOR);

        // 1. 基础维度
        System.out.println("\n" + SEPARATOR);
        System.out.println("  █ 模块一: 基础维度创建");
        System.out.println(SEPARATOR);
        demoBasicDimension();

        // 2. 完整配置
        System.out.println("\n" + SEPARATOR);
        System.out.println("  █ 模块二: 完整维度配置");
        System.out.println(SEPARATOR);
        demoFullConfigDimension();

        // 3. 固定生物群系
        System.out.println("\n" + SEPARATOR);
        System.out.println("  █ 模块三: 固定生物群系维度");
        System.out.println(SEPARATOR);
        demoFixedBiomeDimension();

        // 4. 多噪声生物群系
        System.out.println("\n" + SEPARATOR);
        System.out.println("  █ 模块四: 多噪声生物群系维度");
        System.out.println(SEPARATOR);
        demoMultiNoiseBiomeDimension();

        // 5. 客户端特效
        System.out.println("\n" + SEPARATOR);
        System.out.println("  █ 模块五: 客户端特效配置");
        System.out.println(SEPARATOR);
        demoSpecialEffects();

        // 6. 工具方法
        System.out.println("\n" + SEPARATOR);
        System.out.println("  █ 模块六: 工具方法");
        System.out.println(SEPARATOR);
        demoUtilityMethods();

        // 7. 独立 JSON 生成
        System.out.println("\n" + SEPARATOR);
        System.out.println("  █ 模块七: 独立 JSON 生成");
        System.out.println(SEPARATOR);
        demoStandaloneJsonGeneration();

        // 8. JSON 预览
        System.out.println("\n" + SEPARATOR);
        System.out.println("  █ 模块八: JSON 文件内容预览");
        System.out.println(SEPARATOR);
        demoJsonPreview();

        // 9. 设计理念
        System.out.println("\n" + SEPARATOR);
        System.out.println("  █ 模块九: 设计理念");
        System.out.println(SEPARATOR);
        demoDesignPhilosophy();

        // 总结
        System.out.println("\n" + SEPARATOR);
        System.out.println("  ✅ DimensionAPI 示例程序执行完毕");
        System.out.println("  📖 9 个演示模块已展示全部功能");
        System.out.println("  💡 详细使用说明请参考各模块的 javadoc 注释");
        System.out.println("  " + SEPARATOR);
    }
}