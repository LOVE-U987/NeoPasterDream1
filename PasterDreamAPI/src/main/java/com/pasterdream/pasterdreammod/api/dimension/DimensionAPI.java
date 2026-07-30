package com.pasterdream.pasterdreammod.api.dimension;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.pasterdream.pasterdreammod.api.ApiSoundRegistry;
import com.pasterdream.pasterdreammod.api.PasterDreamAPI;
import com.pasterdream.pasterdreammod.api.dimension.builder.DimensionBuilder;
import com.pasterdream.pasterdreammod.api.dimension.terrain.StructureTerrainNegotiator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import com.pasterdream.pasterdreammod.api.util.PDDebugLogger;
/**
 * 维度注册 API —— 将繁琐的维度注册集中管理，提供高效简洁的注册方式
 * <p>
 * 采用 Facade 模式 + Builder 模式设计，与 {@link com.pasterdream.pasterdreammod.api.block.BlockAPI}
 * 风格一致，覆盖维度注册的完整流程：
 * <ul>
 *   <li><b>维度类型配置</b>：通过 Builder 链式配置 dimension_type 各项参数</li>
 *   <li><b>维度实例配置</b>：配置生物群系源、噪声设置、默认方块/流体等</li>
 *   <li><b>JSON 文件自动生成</b>：自动生成 dimension_type 和 dimension 的 JSON 文件</li>
 *   <li><b>ResourceKey 管理</b>：自动创建并返回维度类型和维度的 ResourceKey</li>
 *   <li><b>特殊效果注册</b>：配套的客户端特效注册辅助，直接使用 {@code RegisterDimensionSpecialEffectsEvent}</li>
 * </ul>
 * <p>
 * <b>⚠️ Minecraft 1.21 维度 JSON 格式要求（反复踩坑警告）：</b>
 * <ul>
 *   <li>维度 JSON 必须包含 {@code noise_router} 和 {@code surface_rule} 字段，否则游戏崩溃</li>
 *   <li>虚空维度推荐使用模板：参考 {@code data/pasterdream/dimension/aaroncos_arena_world.json}</li>
 *   <li>文件必须放在 {@code data/<modid>/dimension/}，不是 {@code assets/}</li>
 *   <li>详见 Skill「pasterdream-dimension-api」</li>
 * </ul>
 * <p>
 * 注意：此类不包含任何客户端专属类型引用，确保服务端兼容。
 * <p>
 * 使用示例：
 * <pre>{@code
 * // ====== 在 PasterDreamMod 构造函数中调用 ======
 * DimensionResult dyedreamWorld = DimensionAPI.createDimension("dyedream_world")
 *     .natural()
 *     .hasSkylight()
 *     .bedWorks()
 *     .withAmbientLight(0.5)
 *     .minY(-64).height(384)
 *     .monsterSpawnLight(0, 7)
 *     .withDefaultBlock("minecraft:calcite")
 *     .withNoiseSettings("pasterdream:dyedream_world")
 *     .build();
 *
 * // 判断当前维度
 * if (DimensionAPI.isInDimension(level, dyedreamWorld)) {
 *     // 在染梦世界中...
 * }
 *
 * // ====== 在 ClientSetup 中注册特效 ======
 * @SubscribeEvent
 * public static void registerEffects(RegisterDimensionSpecialEffectsEvent event) {
 *     ResourceLocation id = ResourceLocation.fromNamespaceAndPath(modId, "dyedream_world");
 *     event.register(id, new DimensionSpecialEffects(192f, true, SkyType.NORMAL, false, false) {
 *         // 自定义雾色、天空色...
 *     });
 * }
 * }</pre>
 */
public final class DimensionAPI {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    /** 已注册的维度结果缓存 */
    private static final Map<String, DimensionResult> REGISTERED_DIMENSIONS = new LinkedHashMap<>();

    /**
     * 重置所有静态缓存，供测试使用。
     * <p>
     * 清空已注册维度结果缓存，使每次测试都在干净的缓存状态下运行。
     * 注意：此方法不会取消已生成的 JSON 文件或 DeferredRegister 中的注册项，仅清除 API 层面的缓存数据。
     */
    public static void resetForTesting() {
        REGISTERED_DIMENSIONS.clear();
    }

    private DimensionAPI() {
        throw new UnsupportedOperationException("DimensionAPI 是纯静态门面类，不可实例化");
    }

    // ======================== Builder 工厂方法 ========================

    /**
     * 创建一个维度构建器
     * <p>
     * 采用链式调用配置维度类型和维度实例的各项参数，
     * 最终通过 {@link DimensionBuilder#build()} 完成注册并返回 {@link DimensionResult}。
     *
     * @param dimensionName 维度注册名称（snake_case 格式，如 "dyedream_world"）
     * @return {@link DimensionBuilder} 实例
     */
    public static DimensionBuilder createDimension(String dimensionName) {
        return new DimensionBuilder(PasterDreamAPI.DATA_NAMESPACE, dimensionName);
    }

    // ======================== 工具方法 ========================

    /**
     * 判断目标维度是否属于指定的维度结果
     *
     * @param level 目标维度
     * @param result 维度结果
     * @return 如果是同一维度返回 true
     */
    public static boolean isInDimension(net.minecraft.world.level.Level level, DimensionResult result) {
        return result.isDimension(level);
    }

    /**
     * 获取已注册的维度结果
     *
     * @param dimensionName 维度注册名称
     * @return 包含维度结果的 {@link Optional}，如果未找到则返回空 Optional
     */
    public static Optional<DimensionResult> getRegisteredDimension(String dimensionName) {
        return Optional.ofNullable(REGISTERED_DIMENSIONS.get(dimensionName));
    }

    /**
     * 记录已注册的维度结果（内部使用）
     *
     * @param result 维度结果
     */
    public static void cacheDimension(DimensionResult result) {
        REGISTERED_DIMENSIONS.put(result.dimensionName(), result);
    }

    /**
     * 获取已注册的维度背景音乐事件。
     *
     * @param musicName 音乐名称
     * @return 包含 SoundEvent Supplier 的 {@link Optional}，未注册时返回空 Optional
     */
    public static Optional<Supplier<SoundEvent>> getMusicEvent(String musicName) {
        return ApiSoundRegistry.getDimensionMusic(musicName);
    }

    // ======================== 大型结构地形协商支持 ========================

    /**
     * 获取地形协商器实例
     * <p>
     * 通过协商器可以注册大型结构、评估地形、查看放置统计等。
     *
     * @return {@link StructureTerrainNegotiator} 单例
     */
    public static StructureTerrainNegotiator getTerrainNegotiator() {
        return StructureTerrainNegotiator.getInstance();
    }

    /**
     * 启用维度的大型结构支持
     * <p>
     * 调用后，当该维度生成区块时，
     * 如果附近有已注册的大型结构，会自动尝试调整地形以适应结构。
     *
     * @param result 之前由 {@link #createDimension} 返回的维度结果
     */
    public static void enableLargeStructureSupport(DimensionResult result) {
        StructureTerrainNegotiator negotiator = StructureTerrainNegotiator.getInstance();
        negotiator.enableDimensionSupport(result.dimensionName());
        PDDebugLogger.apiDebug("[DimensionAPI] 🏗️ 已启用维度的大型结构支持: {} ({})",
                result.dimensionName(), result.dimensionTypeId());
    }

    /**
     * 启用维度的大型结构支持（按维度名称）
     *
     * @param dimensionId 维度 ID（如 "pasterdream:dyedream_world"）
     */
    public static void enableLargeStructureSupport(String dimensionId) {
        StructureTerrainNegotiator negotiator = StructureTerrainNegotiator.getInstance();
        negotiator.enableDimensionSupport(dimensionId);
        PDDebugLogger.apiDebug("[DimensionAPI] 🏗️ 已启用维度的大型结构支持: {}", dimensionId);
    }

}