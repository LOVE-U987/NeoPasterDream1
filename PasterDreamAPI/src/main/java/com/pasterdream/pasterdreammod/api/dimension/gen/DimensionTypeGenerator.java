package com.pasterdream.pasterdreammod.api.dimension.gen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.pasterdream.pasterdreammod.api.PasterDreamAPI;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 维度类型 JSON 生成器 —— 自动生成 Minecraft dimension_type JSON 文件
 * <p>
 * 根据 {@link com.pasterdream.pasterdreammod.api.dimension.builder.DimensionBuilder}
 * 中配置的各项参数，生成标准格式的 dimension_type JSON，
 * 写入 {@code data/{modId}/dimension_type/{name}.json}。
 * <p>
 * 生成的 JSON 结构包含完整的维度类型属性：
 * <ul>
 *   <li>天空光照、天花板、自然属性</li>
 *   <li>坐标缩放、环境光照、逻辑高度</li>
 *   <li>Y 轴范围（min_y / height）</li>
 *   <li>怪物生成光照等级</li>
 *   <li>特殊效果引用（用于客户端自定义天空/雾气）</li>
 * </ul>
 */
public class DimensionTypeGenerator {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private final String modId;
    private final String dimensionName;

    private boolean ultraWarm;
    private boolean natural;
    private boolean piglinSafe;
    private boolean respawnAnchorWorks;
    private boolean bedWorks;
    private boolean hasRaids;
    private boolean hasSkylight;
    private boolean hasCeiling;
    private double coordinateScale;
    private double ambientLight;
    private int logicalHeight;
    private String infiniburn;
    private int minY;
    private int height;
    private int monsterSpawnLightMin;
    private int monsterSpawnLightMax;
    private int monsterSpawnBlockLightLimit;
    private String effectsId;

    /**
     * 构造维度类型生成器
     *
     * @param modId          模组 ID
     * @param dimensionName  维度注册名称（如 "dyedream_world"）
     */
    public DimensionTypeGenerator(String modId, String dimensionName) {
        this.modId = modId;
        this.dimensionName = dimensionName;
        this.coordinateScale = 1.0;
        this.ambientLight = 0.5;
        this.logicalHeight = 384;
        this.infiniburn = "#minecraft:infiniburn_overworld";
        this.minY = -64;
        this.height = 384;
        this.monsterSpawnLightMin = 0;
        this.monsterSpawnLightMax = 7;
        this.monsterSpawnBlockLightLimit = 0;
    }

    // ======================== 配置方法（链式调用） ========================

    public DimensionTypeGenerator ultraWarm(boolean value) { this.ultraWarm = value; return this; }
    public DimensionTypeGenerator natural(boolean value) { this.natural = value; return this; }
    public DimensionTypeGenerator piglinSafe(boolean value) { this.piglinSafe = value; return this; }
    public DimensionTypeGenerator respawnAnchorWorks(boolean value) { this.respawnAnchorWorks = value; return this; }
    public DimensionTypeGenerator bedWorks(boolean value) { this.bedWorks = value; return this; }
    public DimensionTypeGenerator hasRaids(boolean value) { this.hasRaids = value; return this; }
    public DimensionTypeGenerator hasSkylight(boolean value) { this.hasSkylight = value; return this; }
    public DimensionTypeGenerator hasCeiling(boolean value) { this.hasCeiling = value; return this; }
    public DimensionTypeGenerator coordinateScale(double value) { this.coordinateScale = value; return this; }
    public DimensionTypeGenerator ambientLight(double value) { this.ambientLight = value; return this; }
    public DimensionTypeGenerator logicalHeight(int value) { this.logicalHeight = value; return this; }
    public DimensionTypeGenerator infiniburn(String value) { this.infiniburn = value; return this; }
    public DimensionTypeGenerator minY(int value) { this.minY = value; return this; }
    public DimensionTypeGenerator height(int value) { this.height = value; return this; }
    public DimensionTypeGenerator monsterSpawnLight(int min, int max) {
        this.monsterSpawnLightMin = min;
        this.monsterSpawnLightMax = max;
        return this;
    }
    public DimensionTypeGenerator monsterSpawnBlockLightLimit(int value) {
        this.monsterSpawnBlockLightLimit = value;
        return this;
    }
    public DimensionTypeGenerator effectsId(String value) { this.effectsId = value; return this; }

    /**
     * 生成维度类型 JSON 字符串
     *
     * @return 格式化后的 JSON 字符串
     */
    public String generateJson() {
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

        return GSON.toJson(root);
    }

    /**
     * 将维度类型 JSON 写入文件
     * <p>
     * 目标路径：{basePath}/data/{modId}/dimension_type/{dimensionName}.json
     *
     * @param basePath 资源根目录（如 "src/main/resources"）
     * @throws IOException 如果文件写入失败
     */
    public void saveToFile(String basePath) throws IOException {
        String json = generateJson();
        saveJsonToFile(json, modId, dimensionName, basePath);
    }

    /**
     * 静态辅助方法 —— 将维度类型 JSON 保存到文件
     *
     * @param json           JSON 字符串
     * @param modId          模组 ID
     * @param dimensionName  维度注册名称
     * @param basePath       资源根目录
     * @throws IOException   如果文件写入失败
     */
    public static void saveJsonToFile(String json, String modId, String dimensionName, String basePath) throws IOException {
        Path outputDir = Paths.get(basePath, "data", modId, "dimension_type");
        Files.createDirectories(outputDir);

        Path outputFile = outputDir.resolve(dimensionName + ".json");
        try (FileWriter writer = new FileWriter(outputFile.toFile())) {
            writer.write(json);
        }

        PasterDreamAPI.LOGGER.info("[DimensionTypeGenerator] ✅ 已生成 dimension_type JSON → {}", outputFile);
    }
}