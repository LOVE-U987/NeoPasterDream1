package com.pasterdream.pasterdreammod.worldgen.decor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import com.pasterdream.pasterdreammod.PasterDreamMod;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * 装饰物 JSON 数据文件生成器 —— 专用于生成 configured_feature / placed_feature / biome_modifier 的 JSON
 * <p>
 * 从 {@link DecorationRegistry} 中分离出的职责单一类，负责：
 * <ul>
 *   <li>根据已注册的装饰物条目生成 configured_feature JSON 文件</li>
 *   <li>根据已注册的装饰物条目生成 placed_feature JSON 文件</li>
 *   <li>自动聚合生成 biome_modifier JSON 文件（按群系 + 生成阶段分组）</li>
 * </ul>
 * <p>
 * 输出路径硬编码为 {@code src/main/resources/data/<modid>/worldgen/}，
 * 生成的 JSON 文件可直接被 Minecraft 数据包加载。
 *
 * @see DecorationRegistry 装饰物注册管理（持有已注册条目列表）
 * @see DecorationBuilder 装饰物构建器（入口 API）
 */
public class DecorationJsonGenerator {

    /**
     * Gson 实例 —— 用于美化输出的 JSON 文件
     */
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    // 私有构造函数，禁止实例化
    private DecorationJsonGenerator() {}

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

        var entries = DecorationRegistry.getAllDecorations();

        for (var entry : entries) {
            try {
                generateConfiguredFeatureJson(entry, configuredPath);
                generatePlacedFeatureJson(entry, placedPath);
            } catch (IOException e) {
                PasterDreamMod.LOGGER.error("[DecorationJsonGenerator] 生成 JSON 失败: name={}, error={}",
                        entry.name(), e.getMessage(), e);
            }
        }

        PasterDreamMod.LOGGER.info("[DecorationJsonGenerator] JSON 文件生成完成! 共生成 {} 个装饰物定义", entries.size());
        PasterDreamMod.LOGGER.info("  输出目录: {}", basePath.resolve("worldgen").toAbsolutePath().normalize());
    }

    /**
     * 自动生成所有已注册装饰物的 biome_modifier JSON 文件
     * <p>
     * 在 {@code src/main/resources/data/<modid>/neoforge/biome_modifier/} 目录下生成
     * biome_modifier JSON 文件，按群系和生成阶段分组。
     * 每组生成一个文件，文件名为 {@code {biome_simple_name}_{step_name}.json}。
     * <p>
     * <b>注意：</b>此方法会<b>覆盖</b>已存在的同名 JSON 文件，
     * 已在确认无误后调用。已有手动编写的 biome_modifier 文件不会被覆盖
     * （文件名使用自动生成的命名规则，不会与手动命名的文件冲突）。
     */
    public static void generateBiomeModifierJson() {
        Path basePath = Path.of("src", "main", "resources", "data", PasterDreamMod.MOD_ID,
                "neoforge", "biome_modifier");
        var entries = DecorationRegistry.getAllDecorations();

        if (entries.isEmpty()) {
            PasterDreamMod.LOGGER.info("[DecorationJsonGenerator] 无装饰物条目，跳过 biome_modifier 生成");
            return;
        }

        // 按群系 + 生成阶段分组
        java.util.Map<String, java.util.List<DecorationRegistry.DecorationEntry>> groups = new java.util.LinkedHashMap<>();
        for (var entry : entries) {
            String key = entry.targetBiome() + "|" + entry.step().name();
            groups.computeIfAbsent(key, k -> new java.util.ArrayList<>()).add(entry);
        }

        for (var group : groups.entrySet()) {
            String key = group.getKey();
            String biome = key.substring(0, key.indexOf('|'));
            String stepName = key.substring(key.indexOf('|') + 1).toLowerCase();
            var groupEntries = group.getValue();

            String simpleBiomeName = biome;
            if (biome.startsWith("#")) {
                simpleBiomeName = "tag_" + biome.substring(biome.indexOf(':') + 1);
            } else if (biome.contains(":")) {
                simpleBiomeName = biome.substring(biome.indexOf(':') + 1);
            }

            String fileName = simpleBiomeName + "_" + stepName + ".json";

            JsonObject root = new JsonObject();
            root.addProperty("type", "neoforge:add_features");
            root.addProperty("biomes", biome);
            JsonArray features = new JsonArray();
            for (var entry : groupEntries) {
                features.add(PasterDreamMod.MOD_ID + ":" + entry.name());
            }
            root.add("features", features);
            root.addProperty("step", stepName);

            try {
                Files.createDirectories(basePath);
                Path filePath = basePath.resolve(fileName);
                Files.writeString(filePath, GSON.toJson(root),
                        StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                PasterDreamMod.LOGGER.debug("  已生成 biome_modifier: {}", filePath);
            } catch (IOException e) {
                PasterDreamMod.LOGGER.error("[DecorationJsonGenerator] 生成 biome_modifier 失败: file={}, error={}",
                        fileName, e.getMessage(), e);
            }
        }

        PasterDreamMod.LOGGER.info("[DecorationJsonGenerator] biome_modifier JSON 文件生成完成! 共生成 {} 个文件", groups.size());
        PasterDreamMod.LOGGER.info("  输出目录: {}", basePath.toAbsolutePath().normalize());
    }

    // ======================== 内部辅助方法 ========================

    /**
     * 生成单个 configured_feature JSON 文件
     *
     * @param entry   装饰物条目
     * @param dirPath configured_feature 输出目录
     * @throws IOException 文件写入失败时抛出
     */
    private static void generateConfiguredFeatureJson(DecorationRegistry.DecorationEntry entry, Path dirPath) throws IOException {
        JsonObject root = new JsonObject();

        // feature 类型引用 —— 对应 DeferredRegister 中注册的 GenericDecorationFeature
        root.addProperty("type", PasterDreamMod.MOD_ID + ":generic_decor");

        // 将 DecorationConfig 通过其 CODEC 序列化为 JSON 对象
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
    private static void generatePlacedFeatureJson(DecorationRegistry.DecorationEntry entry, Path dirPath) throws IOException {
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
}