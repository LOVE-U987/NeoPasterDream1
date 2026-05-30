package com.pasterdream.pasterdreammod.api.dimension.gen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.pasterdream.pasterdreammod.PasterDreamMod;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 维度 JSON 生成器 —— 自动生成 Minecraft dimension JSON 文件
 * <p>
 * 生成 {@code data/{modId}/dimension/{name}.json} 文件，
 * 包含维度类型引用、噪声生成器设置、生物群系源配置。
 * <p>
 * 支持三种生物群系源：
 * <ul>
 *   <li>multi_noise — 多噪声生物群系（最常用，类似原版主世界/下界）</li>
 *   <li>fixed — 固定单一生物群系</li>
 *   <li>checkerboard — 棋盘格生物群系</li>
 * </ul>
 */
public class DimensionGenerator {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private final String modId;
    private final String dimensionName;

    private String dimensionTypeId;
    private String noiseSettings;
    private int seaLevel;
    private boolean disableMobGeneration;
    private boolean aquifersEnabled;
    private boolean oreVeinsEnabled;
    private boolean legacyRandomSource;
    private String defaultBlock;
    private String defaultFluid;
    private int minY;
    private int height;
    private int sizeHorizontal;
    private int sizeVertical;
    private String biomeSourceType;
    private JsonArray biomes;
    private String fixedBiome;
    private int checkerboardScale;

    /**
     * 构造维度生成器
     *
     * @param modId          模组 ID
     * @param dimensionName  维度注册名称
     */
    public DimensionGenerator(String modId, String dimensionName) {
        this.modId = modId;
        this.dimensionName = dimensionName;
        this.seaLevel = 63;
        this.disableMobGeneration = false;
        this.aquifersEnabled = true;
        this.oreVeinsEnabled = false;
        this.legacyRandomSource = false;
        this.defaultBlock = "minecraft:stone";
        this.defaultFluid = "minecraft:water";
        this.minY = -64;
        this.height = 384;
        this.sizeHorizontal = 1;
        this.sizeVertical = 2;
        this.biomeSourceType = "minecraft:multi_noise";
        this.checkerboardScale = 2;
    }

    // ======================== 配置方法 ========================

    /**
     * 设置维度类型引用 ID
     *
     * @param dimensionTypeId 如 "pasterdream:dyedream_world"
     */
    public DimensionGenerator dimensionTypeId(String dimensionTypeId) {
        this.dimensionTypeId = dimensionTypeId;
        return this;
    }

    /**
     * 设置噪声设置引用 ID
     *
     * @param noiseSettings 如 "pasterdream:dyedream_world" 或 "minecraft:overworld"
     */
    public DimensionGenerator noiseSettings(String noiseSettings) {
        this.noiseSettings = noiseSettings;
        return this;
    }

    /**
     * 设置海平面高度
     */
    public DimensionGenerator seaLevel(int seaLevel) {
        this.seaLevel = seaLevel;
        return this;
    }

    public DimensionGenerator disableMobGeneration(boolean value) { this.disableMobGeneration = value; return this; }
    public DimensionGenerator aquifersEnabled(boolean value) { this.aquifersEnabled = value; return this; }
    public DimensionGenerator oreVeinsEnabled(boolean value) { this.oreVeinsEnabled = value; return this; }
    public DimensionGenerator legacyRandomSource(boolean value) { this.legacyRandomSource = value; return this; }

    /**
     * 设置默认方块（如 "minecraft:calcite"）
     */
    public DimensionGenerator defaultBlock(String defaultBlock) {
        this.defaultBlock = defaultBlock;
        return this;
    }

    /**
     * 设置默认流体（如 "minecraft:water"）
     */
    public DimensionGenerator defaultFluid(String defaultFluid) {
        this.defaultFluid = defaultFluid;
        return this;
    }

    public DimensionGenerator minY(int minY) { this.minY = minY; return this; }
    public DimensionGenerator height(int height) { this.height = height; return this; }
    public DimensionGenerator sizeHorizontal(int value) { this.sizeHorizontal = value; return this; }
    public DimensionGenerator sizeVertical(int value) { this.sizeVertical = value; return this; }

    /**
     * 使用多噪声生物群系源（最常用，类似原版主世界）
     *
     * @param biomesJson 生物群系 JSON 数组字符串
     */
    public DimensionGenerator multiNoiseBiomes(String biomesJson) {
        this.biomeSourceType = "minecraft:multi_noise";
        this.biomes = GSON.fromJson(biomesJson, JsonArray.class);
        return this;
    }

    /**
     * 添加一个多噪声生物群系条目
     *
     * @param biomeId     生物群系 ID
     * @param temperature 温度范围 [min, max]
     * @param humidity    湿度范围 [min, max]
     * @param continental 大陆性范围 [min, max]
     * @param weirdness   怪异度范围 [min, max]
     * @param erosion     侵蚀度范围 [min, max]
     */
    public DimensionGenerator addBiome(String biomeId,
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
     * 使用固定单一生物群系源
     *
     * @param biomeId 固定生物群系 ID
     */
    public DimensionGenerator fixedBiome(String biomeId) {
        this.biomeSourceType = "minecraft:fixed";
        this.fixedBiome = biomeId;
        this.biomes = null;
        return this;
    }

    /**
     * 使用棋盘格生物群系源
     *
     * @param biomesJson 生物群系 ID 数组 JSON
     * @param scale      棋盘格缩放
     */
    public DimensionGenerator checkerboardBiomes(String biomesJson, int scale) {
        this.biomeSourceType = "minecraft:checkerboard";
        this.biomes = GSON.fromJson(biomesJson, JsonArray.class);
        this.checkerboardScale = scale;
        return this;
    }

    // ======================== JSON 生成 ========================

    /**
     * 生成维度 JSON 字符串
     *
     * @return 格式化后的 JSON 字符串
     */
    public String generateJson() {
        JsonObject root = new JsonObject();

        if (dimensionTypeId != null) {
            root.addProperty("type", dimensionTypeId);
        }

        JsonObject generator = new JsonObject();
        generator.addProperty("type", "minecraft:noise");

        // 生物群系源
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

        // 噪声设置
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

        return GSON.toJson(root);
    }

    /**
     * 将维度 JSON 写入文件
     *
     * @param basePath 资源根目录（如 "src/main/resources"）
     * @throws IOException 如果文件写入失败
     */
    public void saveToFile(String basePath) throws IOException {
        String json = generateJson();
        saveJsonToFile(json, modId, dimensionName, basePath);
    }

    /**
     * 静态辅助方法 —— 将维度 JSON 保存到文件
     *
     * @param json           JSON 字符串
     * @param modId          模组 ID
     * @param dimensionName  维度注册名称
     * @param basePath       资源根目录
     * @throws IOException   如果文件写入失败
     */
    public static void saveJsonToFile(String json, String modId, String dimensionName, String basePath) throws IOException {
        Path outputDir = Paths.get(basePath, "data", modId, "dimension");
        Files.createDirectories(outputDir);

        Path outputFile = outputDir.resolve(dimensionName + ".json");
        try (FileWriter writer = new FileWriter(outputFile.toFile())) {
            writer.write(json);
        }

        PasterDreamMod.LOGGER.info("[DimensionGenerator] ✅ 已生成 dimension JSON → {}", outputFile);
    }

    // ======================== 私有辅助 ========================

    private void addRange(JsonObject parent, String key, double[] range) {
        JsonArray arr = new JsonArray();
        arr.add(range[0]);
        arr.add(range[1]);
        parent.add(key, arr);
    }
}