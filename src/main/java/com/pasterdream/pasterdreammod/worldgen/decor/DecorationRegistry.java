package com.pasterdream.pasterdreammod.worldgen.decor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import com.pasterdream.pasterdreammod.PasterDreamMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.registries.DeferredRegister;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 装饰物注册管理中心 —— 统一管理所有装饰物特征的注册与 JSON 生成
 * <p>
 * 负责：
 * <ul>
 *   <li>使用 DeferredRegister 注册 {@link GenericDecorationFeature} 到特征注册表</li>
 *   <li>管理所有已注册的装饰物条目列表</li>
 *   <li>提供 register() 方法供 {@link DecorationBuilder} 调用</li>
 *   <li>自动生成 configured_feature + placed_feature 的 JSON 数据文件</li>
 * </ul>
 * <p>
 * 生成的 JSON 文件会输出到项目的资源目录中，便于直接打包使用。
 */
public class DecorationRegistry {

    /**
     * 特征延迟注册器 —— 用于注册 {@link GenericDecorationFeature}
     */
    public static final DeferredRegister<Feature<?>> FEATURES =
            DeferredRegister.create(Registries.FEATURE, PasterDreamMod.MOD_ID);

    /**
     * 已注册的装饰物条目列表（不可变视图对外暴露）
     */
    private static final List<DecorationEntry> REGISTERED = new ArrayList<>();

    /** 已注册条目的不可变外部视图 */
    private static final List<DecorationEntry> REGISTERED_VIEW = Collections.unmodifiableList(REGISTERED);

    /** 自定义装饰物生成器注册表 */
    private static final Map<String, ICustomDecorationGenerator> CUSTOM_GENERATORS = new HashMap<>();

    /**
     * 注册自定义装饰物生成器
     *
     * @param key       生成器键名（对应配置中的 customGeneratorKey）
     * @param generator 生成器实现
     */
    public static void registerCustomGenerator(String key, ICustomDecorationGenerator generator) {
        CUSTOM_GENERATORS.put(key, generator);
        PasterDreamMod.LOGGER.info("[DecorationRegistry] 已注册自定义生成器: {}", key);
    }

    /**
     * 获取自定义装饰物生成器
     *
     * @param key 生成器键名
     * @return 生成器实例，未找到则返回 null
     */
    @Nullable
    public static ICustomDecorationGenerator getCustomGenerator(String key) {
        return CUSTOM_GENERATORS.get(key);
    }

    /**
     * Gson 实例 —— 用于美化输出的 JSON 文件
     */
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    /*
     * 静态初始化块：将 GenericDecorationFeature 注册到 FEATURES 注册表中
     */
    static {
        FEATURES.register("generic_decor", GenericDecorationFeature::new);
    }

    /**
     * 装饰物条目记录 —— 存储一个已注册装饰物的完整信息
     *
     * @param name                 装饰物名称（也是 JSON 文件名）
     * @param type                 装饰物类型
     * @param config               装饰物完整配置
     * @param configuredFeatureKey configured_feature 的资源键
     * @param placedFeatureKey     placed_feature 的资源键
     * @param targetBiome          目标群系 ID
     * @param step                 生成阶段
     * @param rarity               稀有度（概率为 1/rarity）
     */
    public record DecorationEntry(
            String name,
            DecorationType type,
            DecorationConfig config,
            ResourceKey<ConfiguredFeature<?, ?>> configuredFeatureKey,
            ResourceKey<PlacedFeature> placedFeatureKey,
            String targetBiome,
            GenerationStep.Decoration step,
            int rarity
    ) {}

    /**
     * 注册一个新的装饰物
     * <p>
     * 创建 configured_feature 和 placed_feature 的 ResourceKey，
     * 并将装饰物条目存储到内部列表中。
     *
     * @param name        装饰物名称（对应 JSON 文件名和资源路径）
     * @param config      装饰物配置对象
     * @param targetBiome 目标群系 ID（如 "minecraft:plains"）
     * @param step        生成阶段（如 SURFACE_STRUCTURES、VEGETAL_DECORATION）
     * @param rarity      稀有度（rarity_filter 的 chance 值，越大越稀有）
     * @return 已放置特征的 ResourceKey，用于在 BiomeModifier 中引用
     */
    public static ResourceKey<PlacedFeature> register(String name, DecorationConfig config,
                                                       String targetBiome, GenerationStep.Decoration step, int rarity) {
        ResourceKey<ConfiguredFeature<?, ?>> configuredKey = ResourceKey.create(
                Registries.CONFIGURED_FEATURE,
                ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, name)
        );
        ResourceKey<PlacedFeature> placedKey = ResourceKey.create(
                Registries.PLACED_FEATURE,
                ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, name)
        );

        DecorationEntry entry = new DecorationEntry(
                name, config.type(), config, configuredKey, placedKey,
                targetBiome, step, rarity
        );
        REGISTERED.add(entry);

        PasterDreamMod.LOGGER.info("[DecorationRegistry] 已注册装饰物: {} (类型={}, 群系={}, 稀有度={})",
                name, config.type(), targetBiome, rarity);

        return placedKey;
    }

    /**
     * 自动生成所有已注册装饰物的 JSON 数据文件
     * <p>
     * 在 {@code src/main/resources/data/<modid>/worldgen/} 目录下生成：
     * <ul>
     *   <li>{@code configured_feature/<name>.json} —— 配置特征定义</li>
     *   <li>{@code placed_feature/<name>.json} —— 已放置特征定义（含稀有度、高度图等放置参数）</li>
     * </ul>
     * <p>
     * <b>注意：</b>此方法会<b>覆盖</b>已存在的同名 JSON 文件，请在确认无误后调用。
     */
    public static void generateAllJson() {
        Path basePath = Path.of("src", "main", "resources", "data", PasterDreamMod.MOD_ID);
        Path configuredPath = basePath.resolve("worldgen").resolve("configured_feature");
        Path placedPath = basePath.resolve("worldgen").resolve("placed_feature");

        for (DecorationEntry entry : REGISTERED) {
            try {
                generateConfiguredFeatureJson(entry, configuredPath);
                generatePlacedFeatureJson(entry, placedPath);
            } catch (IOException e) {
                PasterDreamMod.LOGGER.error("[DecorationRegistry] 生成 JSON 失败: name={}, error={}",
                        entry.name(), e.getMessage(), e);
            }
        }

        PasterDreamMod.LOGGER.info("[DecorationRegistry] JSON 文件生成完成! 共生成 {} 个装饰物定义", REGISTERED.size());
        PasterDreamMod.LOGGER.info("  输出目录: {}", basePath.resolve("worldgen").toAbsolutePath().normalize());
    }

    /**
     * 生成单个 configured_feature JSON 文件
     *
     * @param entry   装饰物条目
     * @param dirPath configured_feature 输出目录
     * @throws IOException 文件写入失败时抛出
     */
    private static void generateConfiguredFeatureJson(DecorationEntry entry, Path dirPath) throws IOException {
        JsonObject root = new JsonObject();

        // feature 类型引用 —— 对应 DeferredRegister 中注册的 GenericDecorationFeature
        root.addProperty("type", PasterDreamMod.MOD_ID + ":generic_decor");

        // 将 DecorationConfig 通过其 CODEC 序列化为 JSON 对象
        // 使用 .codec() 将 MapCodec 转为 Codec 后再调用 encodeStart
        JsonElement configJson = DecorationConfig.CODEC.codec()
                .encodeStart(JsonOps.INSTANCE, entry.config())
                .getOrThrow(msg -> {
                    PasterDreamMod.LOGGER.error("配置编码失败: {}", msg);
                    return new RuntimeException("装饰物配置 JSON 编码失败: " + msg);
                });
        root.add("config", configJson);

        Files.createDirectories(dirPath);
        Path filePath = dirPath.resolve(entry.name() + ".json");
        Files.writeString(filePath, GSON.toJson(root), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        PasterDreamMod.LOGGER.debug("  已生成 configured_feature: {}", filePath);
    }

    /**
     * 生成单个 placed_feature JSON 文件
     * <p>
     * 包含标准的放置参数：稀有度过滤 → 正方形散布 → 高度图定位 → 群系过滤
     *
     * @param entry   装饰物条目
     * @param dirPath placed_feature 输出目录
     * @throws IOException 文件写入失败时抛出
     */
    private static void generatePlacedFeatureJson(DecorationEntry entry, Path dirPath) throws IOException {
        JsonObject root = new JsonObject();

        // 引用对应的 configured_feature
        root.addProperty("feature", PasterDreamMod.MOD_ID + ":" + entry.name());

        // 放置参数列表
        JsonArray placement = new JsonArray();

        // 1. 稀有度过滤器 —— 控制生成概率
        JsonObject rarityFilter = new JsonObject();
        rarityFilter.addProperty("type", "minecraft:rarity_filter");
        rarityFilter.addProperty("chance", entry.rarity());
        placement.add(rarityFilter);

        // 2. 正方形散布 —— 在区块内随机偏移
        JsonObject inSquare = new JsonObject();
        inSquare.addProperty("type", "minecraft:in_square");
        placement.add(inSquare);

        // 3. 高度图定位 —— 在地表最高阻挡方块处放置
        JsonObject heightmap = new JsonObject();
        heightmap.addProperty("type", "minecraft:heightmap");
        heightmap.addProperty("heightmap", "MOTION_BLOCKING");
        placement.add(heightmap);

        // 4. 群系过滤器 —— 仅目标群系生成
        JsonObject biomeFilter = new JsonObject();
        biomeFilter.addProperty("type", "minecraft:biome");
        placement.add(biomeFilter);

        root.add("placement", placement);

        Files.createDirectories(dirPath);
        Path filePath = dirPath.resolve(entry.name() + ".json");
        Files.writeString(filePath, GSON.toJson(root), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        PasterDreamMod.LOGGER.debug("  已生成 placed_feature: {}", filePath);
    }

    /**
     * 获取所有已注册的装饰物条目列表
     *
     * @return 只读的装饰物条目列表
     */
    public static List<DecorationEntry> getAllDecorations() {
        return REGISTERED_VIEW;
    }

    /**
     * 清除所有已注册的装饰物条目
     * <p>
     * 主要用于测试或重新加载场景，正常使用无需调用。
     */
    public static void clear() {
        REGISTERED.clear();
        PasterDreamMod.LOGGER.debug("[DecorationRegistry] 已清除所有装饰物条目");
    }
}