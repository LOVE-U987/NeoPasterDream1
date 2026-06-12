package com.pasterdream.pasterdreammod.api.ruin;

import com.pasterdream.pasterdreammod.api.PasterDreamAPI;
import com.pasterdream.pasterdreammod.api.dimension.terrain.StructurePlacementRecord;
import com.pasterdream.pasterdreammod.api.dimension.terrain.StructureTerrainNegotiator;
import com.pasterdream.pasterdreammod.api.dimension.terrain.TerrainAssessment;
import com.pasterdream.pasterdreammod.api.ruin.builder.RuinBuilder;
import com.pasterdream.pasterdreammod.api.ruin.builder.StructureSetBuilder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.neoforged.neoforge.registries.DeferredRegister;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 遗迹/结构注册 API —— 将繁琐的结构注册集中管理，提供高效简洁的注册方式
 * <p>
 * 采用 Facade 模式 + Builder 模式设计，与 {@link com.pasterdream.pasterdreammod.api.block.BlockAPI}
 * 和 {@link com.pasterdream.pasterdreammod.api.dimension.DimensionAPI} 风格一致，
 * 覆盖结构注册的完整流程：
 * <ul>
 *   <li><b>结构类型注册</b>：通过 Builder 链式配置 structure 各项参数</li>
 *   <li><b>结构集配置</b>：配置结构放置参数（间距、分离值、盐值等）</li>
 *   <li><b>模板池配置</b>：独立使用 {@link com.pasterdream.pasterdreammod.api.ruin.gen.TemplatePoolGenerator}</li>
 *   <li><b>JSON 文件自动生成</b>：自动生成 structure、structure_set 的 JSON 文件</li>
 *   <li><b>ResourceKey 管理</b>：自动创建并返回结构类型、结构实例和结构集的 ResourceKey</li>
 * </ul>
 * <p>
 * 使用示例：
 * <pre>{@code
 * // ====== 在 PasterDreamMod 构造函数中调用 ======
 * // 1. 创建遗迹结构
 * RuinResult result = RuinAPI.createRuin("dyedream_ruins")
 *     .biomeTag("pasterdream:is_dyedream")
 *     .templatePool("pasterdream:dyedream_ruins_pool")
 *     .structureClass(DyedreamRuinsStructure.class)
 *     .codec(DyedreamRuinsStructure.CODEC)
 *     .terrainAdaptation(TerrainAdaptation.BEARD_THIN)
 *     .build();
 *
 * // 2. 创建结构集
 * RuinAPI.createRuinSet("dyedream_ruins", "dyedream_ruins_set")
 *     .spacing(32).separation(8).salt(12345)
 *     .build();
 *
 * // 3. 单独生成模板池 JSON（也可以直接用 RuinBuilder 的 templatePool 配置）
 * new TemplatePoolGenerator("pasterdream", "dyedream_ruins_pool")
 *     .addSingleElement("pasterdream:dyedream_ruins/ruin_1", 3, "rigid", "minecraft:empty")
 *     .addSingleElement("pasterdream:dyedream_ruins/ruin_2", 2, "rigid", "minecraft:empty")
 *     .saveToFile("src/main/resources");
 * }</pre>
 */
public final class RuinAPI {

    /**
     * API 专属的结构类型注册器。
     * 注意：需要在 {@code PasterDreamMod} 构造函数中注册到事件总线：
     * <pre>{@code
     * RuinAPI.REGISTRY.register(modEventBus);
     * }</pre>
     */
    public static final DeferredRegister<StructureType<?>> REGISTRY =
            DeferredRegister.create(
                    net.minecraft.core.registries.Registries.STRUCTURE_TYPE,
                    PasterDreamAPI.MOD_ID
            );

    /** 结构结果缓存 */
    private static final Map<String, RuinResult> REGISTERED_RUINS = new LinkedHashMap<>();

    private RuinAPI() {
        throw new UnsupportedOperationException("RuinAPI 是纯静态门面类，不可实例化");
    }

    // ======================== Builder 工厂方法 ========================

    /**
     * 创建一个遗迹结构构建器
     * <p>
     * 采用链式调用配置结构类型的各项参数，
     * 最终通过 {@link RuinBuilder#build()} 完成注册并返回 {@link RuinResult}。
     *
     * @param name 结构注册名称（snake_case 格式，如 "dyedream_ruins"）
     * @return {@link RuinBuilder} 实例
     */
    public static RuinBuilder createRuin(String name) {
        PasterDreamAPI.LOGGER.info("[RuinAPI] 🏛️ 开始创建遗迹构建器: {}", name);
        return new RuinBuilder(PasterDreamAPI.MOD_ID, name, REGISTRY);
    }

    /**
     * 创建一个结构集构建器
     * <p>
     * 为已注册的结构创建配套的 structure_set 配置。
     * 内部会通过 {@link #getRuin(String)} 查找已注册的 RuinResult。
     *
     * @param ruinName 关联的结构注册名称（需先通过 {@link #createRuin} 注册）
     * @param setName  结构集注册名称（如 "dyedream_ruins_set"）
     * @return {@link StructureSetBuilder} 实例
     * @throws IllegalStateException 如果对应的结构尚未注册
     */
    public static StructureSetBuilder createRuinSet(String ruinName, String setName) {
        PasterDreamAPI.LOGGER.info("[RuinAPI] 🔗 开始创建结构集构建器: ruin={}, setName={}", ruinName, setName);
        RuinResult result = getRuin(ruinName);
        if (result == null) {
            PasterDreamAPI.LOGGER.error("[RuinAPI] ❌ createRuinSet 失败: 找不到结构 [{}]", ruinName);
            throw new IllegalStateException(
                    "RuinAPI.createRuinSet: 找不到结构 [" + ruinName + "]，请先调用 createRuin().build()"
            );
        }
        PasterDreamAPI.LOGGER.debug("[RuinAPI] 找到关联结构 [{}]: structureKey={}", ruinName, result.structureKey());
        return new StructureSetBuilder(PasterDreamAPI.MOD_ID, ruinName, setName);
    }

    // ======================== 查询方法 ========================

    /**
     * 获取已注册的结构结果
     *
     * @param name 结构注册名称
     * @return 结构结果，如果未找到返回 null
     */
    public static RuinResult getRuin(String name) {
        RuinResult result = REGISTERED_RUINS.get(name);
        PasterDreamAPI.LOGGER.debug("[RuinAPI] 🔍 查询遗迹: {} → {}", name, result != null ? "已找到" : "未找到");
        return result;
    }

    /**
     * 获取所有已注册结构结果的不可变视图
     *
     * @return 结构注册名到结构结果的映射
     */
    public static Map<String, RuinResult> getAllRuins() {
        int count = REGISTERED_RUINS.size();
        PasterDreamAPI.LOGGER.debug("[RuinAPI] 📊 获取所有已注册遗迹: 共 {} 个", count);
        return Collections.unmodifiableMap(REGISTERED_RUINS);
    }

    /**
     * 判断指定名称的结构是否已注册
     *
     * @param name 结构注册名称
     * @return 如果已注册返回 true
     */
    public static boolean hasRuin(String name) {
        boolean exists = REGISTERED_RUINS.containsKey(name);
        PasterDreamAPI.LOGGER.debug("[RuinAPI] 🔍 检查遗迹是否存在: {} → {}", name, exists);
        return exists;
    }

    // ======================== 内部缓存方法 ========================

    /**
     * 缓存已注册的结构结果（内部使用）
     *
     * @param result 结构结果
     */
    public static void cacheRuin(RuinResult result) {
        if (result != null && result.name() != null) {
            REGISTERED_RUINS.put(result.name(), result);
            int total = REGISTERED_RUINS.size();
            PasterDreamAPI.LOGGER.info("[RuinAPI] 📦 已缓存遗迹: {} | 缓存总数: {} | structureKey={} | hasSetKey={}",
                    result.name(), total, result.structureKey(), result.hasSetKey());
        } else {
            PasterDreamAPI.LOGGER.warn("[RuinAPI] ⚠️ 尝试缓存 null 或无名遗迹结果");
        }
    }

    // ======================== 大型结构诊断 ========================

    /**
     * 打印所有已注册结构（含大型结构）的生成诊断报告
     * <p>
     * 调用后会在控制台输出详细的放置统计信息，
     * 包括成功/失败次数、失败率、最近失败原因等。
     * 频繁失败的结构会标记警示信息。
     */
    public static void printStructureDiagnostics() {
        StructureTerrainNegotiator negotiator = StructureTerrainNegotiator.getInstance();
        negotiator.printDiagnostics();
    }

    /**
     * 打印指定结构的详细诊断信息
     *
     * @param structureName 结构注册名称
     */
    public static void printStructureDiagnostics(String structureName) {
        StructureTerrainNegotiator negotiator = StructureTerrainNegotiator.getInstance();
        negotiator.printStructureDiagnostics(structureName);
    }

    /**
     * 评估指定位置的地形是否适合放置指定的结构
     *
     * @param structureName 结构注册名称
     * @param chunkX        区块 X 坐标
     * @param chunkZ        区块 Z 坐标
     * @param level         世界实例
     * @return 地形评估结果
     */
    public static TerrainAssessment assessTerrain(String structureName, int chunkX, int chunkZ, Level level) {
        StructureTerrainNegotiator negotiator = StructureTerrainNegotiator.getInstance();
        return negotiator.assessTerrain(structureName, chunkX, chunkZ, level);
    }

    /**
     * 报告结构放置结果
     *
     * @param structureName 结构注册名称
     * @param success       是否成功放置
     * @param reason        失败原因（成功时传空字符串）
     */
    public static void reportPlacement(String structureName, boolean success, String reason) {
        StructureTerrainNegotiator negotiator = StructureTerrainNegotiator.getInstance();
        negotiator.reportPlacement(structureName, success, reason);
    }

    /**
     * 获取结构的放置统计记录
     *
     * @param structureName 结构注册名称
     * @return 放置记录，不存在返回 null
     */
    @Nullable
    public static StructurePlacementRecord getPlacementRecord(String structureName) {
        StructureTerrainNegotiator negotiator = StructureTerrainNegotiator.getInstance();
        return negotiator.getPlacementRecord(structureName);
    }
}