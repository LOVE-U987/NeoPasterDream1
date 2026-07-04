package com.pasterdream.pasterdreammod.api.dimension.builder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.pasterdream.pasterdreammod.api.ApiSoundRegistry;
import com.pasterdream.pasterdreammod.api.PasterDreamAPI;
import com.pasterdream.pasterdreammod.api.dimension.DimensionResult;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 维度配置构建器 —— 采用 Builder 模式链式配置维度类型和维度实例
 * <p>
 * 解决 {@code PDDimensions.java} 中手动定义 ResourceKey 和
 * 手写 dimension_type/dimension JSON 的繁琐问题。
 * 通过链式调用即可完成完整维度的配置、注册和资源文件生成。
 * <p>
 * 使用示例：
 * <pre>{@code
 * DimensionResult dyedreamWorld = DimensionAPI.createDimension("dyedream_world")
 *     .natural()
 *     .hasSkylight()
 *     .bedWorks()
 *     .withAmbientLight(0.5)
 *     .minY(-64).height(384)
 *     .monsterSpawnLight(0, 7)
 *     .withDefaultBlock("pasterdream:dyedream_block")
 *     .withNoiseSettings("pasterdream:dyedream_world")
 *     .withMusic("dyedream_world")
 *     .build();
 * }</pre>
 *
 * @see com.pasterdream.pasterdreammod.api.dimension.DimensionAPI
 */
public class DimensionBuilder {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private final String modId;
    private final String dimensionName;

    // ======================== DimensionType 配置 ========================
    private boolean ultraWarm;
    private boolean natural = true;
    private boolean piglinSafe;
    private boolean respawnAnchorWorks;
    private boolean bedWorks = true;
    private boolean hasRaids = true;
    private boolean hasSkylight = true;
    private boolean hasCeiling;
    private double coordinateScale = 1.0;
    private double ambientLight = 0.5;
    private int logicalHeight = 384;
    private String infiniburn = "#minecraft:infiniburn_overworld";
    private int minY = -64;
    private int height = 384;
    private int monsterSpawnLightMin = 0;
    private int monsterSpawnLightMax = 7;
    private int monsterSpawnBlockLightLimit = 0;
    private String effectsId;

    // ======================== Dimension 配置 ========================
    private String dimensionTypeId;
    private String noiseSettings;
    private int seaLevel = 63;
    private boolean disableMobGeneration;
    private boolean aquifersEnabled = true;
    private boolean oreVeinsEnabled;
    private boolean legacyRandomSource;
    private String defaultBlock = "minecraft:stone";
    private String defaultFluid = "minecraft:water";
    private int sizeHorizontal = 1;
    private int sizeVertical = 2;
    private String biomeSourceType = "minecraft:multi_noise";
    private JsonArray biomes;
    private String fixedBiome;
    private int checkerboardScale = 2;

    /** 是否自动生成 JSON 资源文件，默认为 true */
    private boolean generateJsonFiles = true;
    /** 资源文件基础路径，默认为 src/main/resources */
    private String basePath = "src/main/resources";

    /** 维度背景音乐名称（如 "dyedream_world"），null 表示无自定义音乐 */
    private String musicName;

    /** 维度背景音乐音量（0.0 ~ 1.0），默认为 1.0 */
    private float musicVolume = 1.0f;

    /**
     * 构造维度构建器
     *
     * @param modId          模组 ID
     * @param dimensionName  维度注册名称（如 "dyedream_world"）
     */
    public DimensionBuilder(String modId, String dimensionName) {
        this.modId = modId;
        this.dimensionName = dimensionName;
    }

    // ======================== DimensionType 配置 ========================

    public DimensionBuilder ultraWarm(boolean value) { this.ultraWarm = value; return this; }
    /** 便捷方法：默认 true */
    public DimensionBuilder ultraWarm() { return ultraWarm(true); }
    public DimensionBuilder natural(boolean value) { this.natural = value; return this; }
    /** 便捷方法：默认 true */
    public DimensionBuilder natural() { return natural(true); }
    public DimensionBuilder piglinSafe(boolean value) { this.piglinSafe = value; return this; }
    /** 便捷方法：默认 true */
    public DimensionBuilder piglinSafe() { return piglinSafe(true); }
    public DimensionBuilder respawnAnchorWorks(boolean value) { this.respawnAnchorWorks = value; return this; }
    /** 便捷方法：默认 true */
    public DimensionBuilder respawnAnchorWorks() { return respawnAnchorWorks(true); }
    public DimensionBuilder bedWorks(boolean value) { this.bedWorks = value; return this; }
    /** 便捷方法：默认 true */
    public DimensionBuilder bedWorks() { return bedWorks(true); }
    public DimensionBuilder hasRaids(boolean value) { this.hasRaids = value; return this; }
    /** 便捷方法：默认 true */
    public DimensionBuilder hasRaids() { return hasRaids(true); }
    public DimensionBuilder hasSkylight(boolean value) { this.hasSkylight = value; return this; }
    /** 便捷方法：默认 true */
    public DimensionBuilder hasSkylight() { return hasSkylight(true); }
    public DimensionBuilder hasCeiling(boolean value) { this.hasCeiling = value; return this; }
    /** 便捷方法：默认 true */
    public DimensionBuilder hasCeiling() { return hasCeiling(true); }
    public DimensionBuilder coordinateScale(double value) { this.coordinateScale = value; return this; }
    public DimensionBuilder withAmbientLight(double value) { this.ambientLight = value; return this; }
    public DimensionBuilder logicalHeight(int value) { this.logicalHeight = value; return this; }
    public DimensionBuilder infiniburn(String value) { this.infiniburn = value; return this; }
    public DimensionBuilder minY(int value) { this.minY = value; return this; }
    public DimensionBuilder height(int value) { this.height = value; return this; }
    public DimensionBuilder monsterSpawnLight(int min, int max) {
        this.monsterSpawnLightMin = min;
        this.monsterSpawnLightMax = max;
        return this;
    }
    public DimensionBuilder monsterSpawnBlockLightLimit(int value) {
        this.monsterSpawnBlockLightLimit = value;
        return this;
    }

    // ======================== Dimension 配置 ========================

    /**
     * 设置维度类型引用
     * <p>
     * 默认自动使用 {@code {modId}:{dimensionName}}，
     * 一般无需手动调用此方法。
     */
    public DimensionBuilder withDimensionType(String dimensionTypeId) {
        this.dimensionTypeId = dimensionTypeId;
        return this;
    }

    /**
     * 设置噪声设置引用
     * <p>
     * 如果使用自定义噪声设置，指定其 ID。
     * 使用原版主世界设置可传入 {@code "minecraft:overworld"}。
     */
    public DimensionBuilder withNoiseSettings(String noiseSettings) {
        this.noiseSettings = noiseSettings;
        return this;
    }

    public DimensionBuilder seaLevel(int seaLevel) { this.seaLevel = seaLevel; return this; }
    public DimensionBuilder disableMobGeneration(boolean value) { this.disableMobGeneration = value; return this; }
    public DimensionBuilder aquifersEnabled(boolean value) { this.aquifersEnabled = value; return this; }
    public DimensionBuilder oreVeinsEnabled(boolean value) { this.oreVeinsEnabled = value; return this; }
    public DimensionBuilder legacyRandomSource(boolean value) { this.legacyRandomSource = value; return this; }

    /**
     * 设置维度默认方块
     *
     * @param blockId 方块 ID（如 "minecraft:calcite"）
     */
    public DimensionBuilder withDefaultBlock(String blockId) {
        this.defaultBlock = blockId;
        return this;
    }

    /**
     * 设置维度默认流体
     *
     * @param fluidId 流体 ID（如 "minecraft:water"）
     */
    public DimensionBuilder withDefaultFluid(String fluidId) {
        this.defaultFluid = fluidId;
        return this;
    }

    /**
     * 添加生物群系到多噪声生物群系源
     *
     * @param biomeId     生物群系 ID
     * @param temperature 温度范围 [min, max]
     * @param humidity    湿度范围 [min, max]
     * @param continental 大陆性范围 [min, max]
     * @param weirdness   怪异度范围 [min, max]
     * @param erosion     侵蚀度范围 [min, max]
     */
    public DimensionBuilder addBiome(String biomeId,
                                     double[] temperature, double[] humidity,
                                     double[] continental, double[] weirdness,
                                     double[] erosion) {
        if (this.biomes == null) {
            this.biomes = new JsonArray();
        }
        this.biomeSourceType = "minecraft:multi_noise";

        JsonObject entry = new JsonObject();
        entry.addProperty("biome", biomeId);

        JsonObject params = new JsonObject();
        addRange(params, "temperature", temperature);
        addRange(params, "humidity", humidity);
        addRange(params, "continentalness", continental);
        addRange(params, "weirdness", weirdness);
        addRange(params, "erosion", erosion);
        params.addProperty("depth", 0);
        params.addProperty("offset", 0);

        entry.add("parameters", params);
        this.biomes.add(entry);
        return this;
    }

    /**
     * 使用固定生物群系
     *
     * @param biomeId 固定生物群系 ID
     */
    public DimensionBuilder withFixedBiome(String biomeId) {
        this.biomeSourceType = "minecraft:fixed";
        this.fixedBiome = biomeId;
        this.biomes = null;
        return this;
    }

    /**
     * 是否自动生成 JSON 资源文件
     * <p>
     * 默认为 true，会在 {@code build()} 时自动生成
     * dimension_type 和 dimension 的 JSON 文件。
     */
    public DimensionBuilder generateJson(boolean generate) {
        this.generateJsonFiles = generate;
        return this;
    }

    /**
     * 设置资源文件基础路径
     * <p>
     * 默认为 {@code "src/main/resources"}。
     */
    public DimensionBuilder basePath(String basePath) {
        this.basePath = basePath;
        return this;
    }

    // ======================== 背景音乐配置 ========================

    /**
     * 为维度注册背景音乐（默认音量 1.0）
     * <p>
     * 自动完成以下工作：
     * <ol>
     *   <li>注册 {@code SoundEvent}（ID 为 {@code music.{musicName}}）</li>
     *   <li>生成 {@code sounds.json} 条目（含 {@code "stream": true, "volume": 1.0}）</li>
     *   <li>提示 .ogg 文件的放置路径</li>
     * </ol>
     * <p>
     * 你需要在 {@code assets/{modId}/sounds/music/{musicName}.ogg}
     * 放置实际的音频文件（建议使用高品质的 .ogg 格式）。
     * <p>
     * 之后在你的生物群系 JSON 中引用此音乐：
     * <pre>{@code
     * {
     *   "music": {
     *     "event": "pasterdream:music.dyedream_world",
     *     "min_delay": 12000,
     *     "max_delay": 24000,
     *     "replace_current_music": false
     *   }
     * }
     * }</pre>
     *
     * @param musicName 背景音乐名称（如 "dyedream_world"）
     * @return 当前构建器实例
     * @deprecated 建议在主模组的声音注册类（如 PDSounds）中统一管理音乐注册，
     *             此方法仅用于开发阶段快速原型验证。
     */
    @Deprecated
    public DimensionBuilder withMusic(String musicName) {
        return withMusic(musicName, 1.0f);
    }

    /**
     * 为维度注册背景音乐（自定义音量）
     * <p>
     * 在 {@link #withMusic(String)} 的基础上增加音量参数。
     * 音量值会写入 {@code sounds.json}，推荐统一设置为 0.3（30%），
     * 以保持各群系 BGM 的相对音量比例一致。
     *
     * @param musicName 背景音乐名称（如 "dyedream_world"）
     * @param volume    音量值（0.0 ~ 1.0），推荐 0.3
     * @return 当前构建器实例
     * @deprecated 建议在主模组的声音注册类（如 PDSounds）中统一管理音乐注册，
     *             此方法仅用于开发阶段快速原型验证。
     */
    @Deprecated
    public DimensionBuilder withMusic(String musicName, float volume) {
        this.musicName = musicName;
        this.musicVolume = Math.max(0.0f, Math.min(1.0f, volume));
        return this;
    }

    // ======================== 构建 & 生成 ========================

    /**
     * 执行构建，完成维度注册和资源文件生成
     * <p>
     * 完成以下工作：
     * <ol>
     *   <li>生成并保存 dimension_type JSON 文件</li>
     *   <li>生成并保存 dimension JSON 文件</li>
     *   <li>返回 {@link DimensionResult} 包含所有 ResourceKey</li>
     * </ol>
     *
     * @return {@link DimensionResult} 包含维度相关的所有 ResourceKey
     * @throws RuntimeException 如果 JSON 文件写入失败
     */
    public DimensionResult build() {
        String dimensionTypeId = modId + ":" + dimensionName;

        // 自动设置 dimensionTypeId
        this.dimensionTypeId = dimensionTypeId;

        // 自动设置 effectsId 为自身（允许客户端注册 DimensionSpecialEffects）
        this.effectsId = dimensionTypeId;

        if (generateJsonFiles) {
            try {
                PasterDreamAPI.LOGGER.debug("[DimensionBuilder] ===== 开始生成维度资源文件: {} =====", dimensionName);

                saveDimensionTypeJson();
                saveDimensionJson();

                // 如果有配置背景音乐，自动注册 SoundEvent 并生成 sounds.json
                if (musicName != null) {
                    registerMusic();
                }

                PasterDreamAPI.LOGGER.debug("[DimensionBuilder] ✅ 维度资源文件生成完成: {}", dimensionName);
            } catch (IOException e) {
                PasterDreamAPI.LOGGER.error("[DimensionBuilder] ❌ 无法生成维度资源文件 [{}]: {}", dimensionName, e.getMessage(), e);
                throw new RuntimeException("DimensionBuilder: 无法生成维度资源文件 [" + dimensionName + "]", e);
            }
        } else if (musicName != null) {
            // 即使不生成 JSON 文件，也要注册 SoundEvent
            ApiSoundRegistry.registerDimensionMusic(musicName);
            PasterDreamAPI.LOGGER.debug("[DimensionBuilder] 已注册背景音乐 SoundEvent: {}.music.{}, 请确保 .ogg 文件存在",
                    modId, musicName);
        }

        DimensionResult result = new DimensionResult(dimensionName, dimensionTypeId);

        // 缓存到 DimensionAPI 中，方便后续查询
        com.pasterdream.pasterdreammod.api.dimension.DimensionAPI.cacheDimension(result);

        return result;
    }

    /**
     * 注册背景音乐：注册 SoundEvent + 生成 sounds.json（含音量参数）+ 检查 .ogg 文件
     */
    private void registerMusic() throws IOException {
        // 1. 注册 SoundEvent（DeferredRegister）
        Supplier<net.minecraft.sounds.SoundEvent> soundSupplier = ApiSoundRegistry.registerDimensionMusic(musicName);

        // 2. 生成 sounds.json 条目（传入音量参数）
        saveSoundsJson();

        // 3. 检查 .ogg 文件是否存在（仅给出警告，不阻止构建）
        Path oggPath = Paths.get(basePath, "assets", modId, "sounds", "music", musicName + ".ogg");
        if (!Files.exists(oggPath)) {
            PasterDreamAPI.LOGGER.warn("[DimensionBuilder] ⚠️ 背景音乐 .ogg 文件未找到: {}",
                    oggPath.toAbsolutePath().normalize());
            PasterDreamAPI.LOGGER.warn("[DimensionBuilder] 📌 请在以下位置放置 .ogg 音频文件:");
            PasterDreamAPI.LOGGER.warn("[DimensionBuilder]    {}", oggPath.toAbsolutePath().normalize());
        }
    }

    /**
     * 生成并保存 dimension_type JSON 文件
     * <p>
     * 目标路径：{@code {basePath}/data/{modId}/dimension_type/{dimensionName}.json}
     *
     * @throws IOException 如果文件写入失败
     */
    private void saveDimensionTypeJson() throws IOException {
        JsonObject root = new JsonObject();

        root.addProperty("ultrawarm", ultraWarm);
        root.addProperty("natural", natural);
        root.addProperty("piglin_safe", piglinSafe);
        root.addProperty("respawn_anchor_works", respawnAnchorWorks);
        root.addProperty("bed_works", bedWorks);
        root.addProperty("has_raids", hasRaids);
        root.addProperty("has_skylight", hasSkylight);
        root.addProperty("has_ceiling", hasCeiling);
        root.addProperty("coordinate_scale", coordinateScale);
        root.addProperty("ambient_light", ambientLight);
        root.addProperty("logical_height", logicalHeight);
        root.addProperty("infiniburn", infiniburn);
        root.addProperty("min_y", minY);
        root.addProperty("height", height);

        JsonObject spawnLight = new JsonObject();
        spawnLight.addProperty("type", "minecraft:uniform");
        spawnLight.addProperty("min_inclusive", monsterSpawnLightMin);
        spawnLight.addProperty("max_inclusive", monsterSpawnLightMax);
        root.add("monster_spawn_light_level", spawnLight);
        root.addProperty("monster_spawn_block_light_limit", monsterSpawnBlockLightLimit);

        if (effectsId != null) {
            root.addProperty("effects", effectsId);
        }

        Path outputDir = Paths.get(basePath, "data", modId, "dimension_type");
        Files.createDirectories(outputDir);

        Path outputFile = outputDir.resolve(dimensionName + ".json");
        try (FileWriter writer = new FileWriter(outputFile.toFile())) {
            GSON.toJson(root, writer);
        }

        PasterDreamAPI.LOGGER.debug("[DimensionBuilder] ✅ 已生成 dimension_type JSON → {}", outputFile);
    }

    /**
     * 生成并保存 dimension JSON 文件
     * <p>
     * 目标路径：{@code {basePath}/data/{modId}/dimension/{dimensionName}.json}
     *
     * @throws IOException 如果文件写入失败
     */
    private void saveDimensionJson() throws IOException {
        JsonObject root = new JsonObject();

        if (dimensionTypeId != null) {
            root.addProperty("type", dimensionTypeId);
        }

        JsonObject generator = new JsonObject();
        generator.addProperty("type", "minecraft:noise");

        JsonObject biomeSource = new JsonObject();
        biomeSource.addProperty("type", biomeSourceType);

        if ("minecraft:fixed".equals(biomeSourceType) && fixedBiome != null) {
            biomeSource.addProperty("biome", fixedBiome);
        } else if ("minecraft:checkerboard".equals(biomeSourceType) && biomes != null) {
            biomeSource.add("biomes", biomes);
            biomeSource.addProperty("scale", checkerboardScale);
        } else if (biomes != null) {
            biomeSource.add("biomes", biomes);
        }

        generator.add("biome_source", biomeSource);

        JsonObject settings = new JsonObject();
        if (noiseSettings != null) {
            settings.addProperty("name", noiseSettings);
        }
        settings.addProperty("sea_level", seaLevel);
        settings.addProperty("disable_mob_generation", disableMobGeneration);
        settings.addProperty("aquifers_enabled", aquifersEnabled);
        settings.addProperty("ore_veins_enabled", oreVeinsEnabled);
        settings.addProperty("legacy_random_source", legacyRandomSource);

        JsonObject defaultBlockObj = new JsonObject();
        defaultBlockObj.addProperty("Name", defaultBlock);
        settings.add("default_block", defaultBlockObj);

        JsonObject defaultFluidObj = new JsonObject();
        defaultFluidObj.addProperty("Name", defaultFluid);
        JsonObject fluidProps = new JsonObject();
        fluidProps.addProperty("level", "0");
        defaultFluidObj.add("Properties", fluidProps);
        settings.add("default_fluid", defaultFluidObj);

        settings.add("spawn_target", new JsonArray());

        JsonObject noise = new JsonObject();
        noise.addProperty("min_y", minY);
        noise.addProperty("height", height);
        noise.addProperty("size_horizontal", sizeHorizontal);
        noise.addProperty("size_vertical", sizeVertical);
        settings.add("noise", noise);

        generator.add("settings", settings);
        root.add("generator", generator);

        Path outputDir = Paths.get(basePath, "data", modId, "dimension");
        Files.createDirectories(outputDir);

        Path outputFile = outputDir.resolve(dimensionName + ".json");
        try (FileWriter writer = new FileWriter(outputFile.toFile())) {
            GSON.toJson(root, writer);
        }

        PasterDreamAPI.LOGGER.debug("[DimensionBuilder] ✅ 已生成 dimension JSON → {}", outputFile);
    }

    /**
     * 生成并保存 sounds.json 文件
     * <p>
     * 目标路径：{@code {basePath}/assets/{modId}/sounds.json}
     *
     * @throws IOException 如果文件读写失败
     */
    private void saveSoundsJson() throws IOException {
        String soundKey = "music." + musicName;
        String soundPath = modId + ":music/" + musicName;

        JsonObject entry = new JsonObject();
        entry.addProperty("category", "music");
        entry.addProperty("subtitle", "subtitle." + modId + "." + soundKey);

        JsonArray sounds = new JsonArray();
        JsonObject soundObj = new JsonObject();
        soundObj.addProperty("name", soundPath);
        soundObj.addProperty("stream", true);
        soundObj.addProperty("volume", musicVolume);
        sounds.add(soundObj);
        entry.add("sounds", sounds);

        JsonObject merged = loadExistingSoundsJson();
        merged.add(soundKey, entry);

        Path outputFile = Paths.get(basePath, "assets", modId, "sounds.json");
        Files.createDirectories(outputFile.getParent());
        try (FileWriter writer = new FileWriter(outputFile.toFile())) {
            GSON.toJson(merged, writer);
        }

        PasterDreamAPI.LOGGER.debug("[DimensionBuilder] ✅ 已更新 sounds.json → {}", outputFile);
    }

    /**
     * 加载已有的 sounds.json 文件
     *
     * @return 已有的 sounds.json 对象，不存在时返回空对象
     */
    private JsonObject loadExistingSoundsJson() {
        Path existingFile = Paths.get(basePath, "assets", modId, "sounds.json");
        if (Files.exists(existingFile)) {
            try (FileReader reader = new FileReader(existingFile.toFile())) {
                JsonElement parsed = JsonParser.parseReader(reader);
                if (parsed != null && parsed.isJsonObject()) {
                    return parsed.getAsJsonObject();
                }
            } catch (IOException e) {
                PasterDreamAPI.LOGGER.warn("[DimensionBuilder] 读取已有 sounds.json 失败: {}", e.getMessage());
            }
        }
        return new JsonObject();
    }

    /**
     * 向 JSON 对象中添加范围数组
     *
     * @param parent 目标 JSON 对象
     * @param key    键名
     * @param range  范围数组 [min, max]
     */
    private void addRange(JsonObject parent, String key, double[] range) {
        JsonArray arr = new JsonArray();
        arr.add(range[0]);
        arr.add(range[1]);
        parent.add(key, arr);
    }
}