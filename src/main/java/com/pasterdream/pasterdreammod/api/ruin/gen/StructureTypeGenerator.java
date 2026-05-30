package com.pasterdream.pasterdreammod.api.ruin.gen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.pasterdream.pasterdreammod.PasterDreamMod;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 结构类型 JSON 生成器 —— 自动生成 Minecraft structure JSON 文件
 * <p>
 * 根据 {@link com.pasterdream.pasterdreammod.api.ruin.builder.RuinBuilder}
 * 中配置的各项参数，生成标准格式的 structure JSON，
 * 写入 {@code data/{modId}/worldgen/structure/{name}.json}。
 * <p>
 * 生成的 JSON 结构包含完整的结构属性：
 * <ul>
 *   <li>type — 结构类型 ID（引用注册的 StructureType）</li>
 *   <li>biomes — 生物群系标签</li>
 *   <li>step — 生成阶段</li>
 *   <li>terrain_adaptation — 地形适应类型</li>
 *   <li>start_pool — 起始模板池引用</li>
 *   <li>size — 结构扩展大小</li>
 *   <li>start_height — 起始生成高度</li>
 *   <li>spawn_overrides — 生物生成覆盖</li>
 * </ul>
 */
public class StructureTypeGenerator {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private final String modId;
    private final String structureName;

    /** 结构类型 ID（如 "pasterdream:dyedream_ruins"） */
    private String type;
    /** 生物群系标签（如 "#pasterdream:is_dyedream"） */
    private String biomes;
    /** 生成阶段（如 "surface_structures"） */
    private String step;
    /** 地形适应类型（如 "beard_thin"） */
    private String terrainAdaptation;
    /** 起始模板池 ID（如 "pasterdream:dyedream_ruins_pool"） */
    private String startPool;
    /** 结构扩展大小 */
    private int size;
    /** 起始高度值 */
    private int startHeight;
    /** 是否将起始位置投影到地形高度图上 */
    private boolean projectStartToHeightmap;
    /** 距中心的最大距离 */
    private int maxDistanceFromCenter;
    /** 是否使用扩展 hack（仅 jigsaw 结构用） */
    private boolean useExpansionHack;
    /** 自定义额外 JSON 字段 */
    private JsonObject extraFields;

    /**
     * 构造结构类型生成器
     *
     * @param modId         模组 ID
     * @param structureName 结构注册名称（如 "dyedream_ruins"）
     */
    public StructureTypeGenerator(String modId, String structureName) {
        this.modId = modId;
        this.structureName = structureName;
        this.step = "surface_structures";
        this.size = 7;
        this.startHeight = 0;
        this.projectStartToHeightmap = true;
        this.maxDistanceFromCenter = 80;
        this.useExpansionHack = false;
    }

    // ======================== 配置方法（链式调用） ========================

    /** 设置结构类型 ID */
    public StructureTypeGenerator type(String type) { this.type = type; return this; }
    /** 设置生物群系标签 */
    public StructureTypeGenerator biomes(String biomes) { this.biomes = biomes; return this; }
    /** 设置生成阶段 */
    public StructureTypeGenerator step(String step) { this.step = step; return this; }
    /** 设置地形适应类型 */
    public StructureTypeGenerator terrainAdaptation(String terrainAdaptation) { this.terrainAdaptation = terrainAdaptation; return this; }
    /** 设置起始模板池 ID */
    public StructureTypeGenerator startPool(String startPool) { this.startPool = startPool; return this; }
    /** 设置结构扩展大小 */
    public StructureTypeGenerator size(int size) { this.size = size; return this; }
    /** 设置起始高度 */
    public StructureTypeGenerator startHeight(int startHeight) { this.startHeight = startHeight; return this; }
    /** 设置是否投影到地形高度图 */
    public StructureTypeGenerator projectStartToHeightmap(boolean value) { this.projectStartToHeightmap = value; return this; }
    /** 设置最大距离中心距离 */
    public StructureTypeGenerator maxDistanceFromCenter(int value) { this.maxDistanceFromCenter = value; return this; }
    /** 设置是否使用扩展 hack */
    public StructureTypeGenerator useExpansionHack(boolean value) { this.useExpansionHack = value; return this; }
    /** 设置自定义额外字段 */
    public StructureTypeGenerator extraFields(JsonObject extraFields) { this.extraFields = extraFields; return this; }

    /**
     * 生成结构 JSON 字符串
     *
     * @return 格式化后的 JSON 字符串
     */
    public String generateJson() {
        JsonObject root = new JsonObject();

        if (type != null) {
            root.addProperty("type", type);
        }
        if (biomes != null) {
            root.addProperty("biomes", biomes);
        }
        root.addProperty("step", step);
        if (terrainAdaptation != null) {
            root.addProperty("terrain_adaptation", terrainAdaptation);
        }

        // 生成覆盖（默认为空对象）
        root.add("spawn_overrides", new JsonObject());

        if (startPool != null) {
            root.addProperty("start_pool", startPool);
        }
        root.addProperty("size", size);

        // 起始高度
        JsonObject startHeightObj = new JsonObject();
        startHeightObj.addProperty("type", "minecraft:absolute");
        startHeightObj.addProperty("height", startHeight);
        root.add("start_height", startHeightObj);

        if (projectStartToHeightmap) {
            root.addProperty("project_start_to_heightmap", "WORLD_SURFACE_WG");
        }
        root.addProperty("max_distance_from_center", maxDistanceFromCenter);

        if (useExpansionHack) {
            root.addProperty("use_expansion_hack", true);
        }

        // 合并自定义额外字段
        if (extraFields != null && !extraFields.isEmpty()) {
            for (var entry : extraFields.entrySet()) {
                root.add(entry.getKey(), entry.getValue());
            }
        }

        return GSON.toJson(root);
    }

    /**
     * 将结构 JSON 写入文件
     * <p>
     * 目标路径：{basePath}/data/{modId}/worldgen/structure/{structureName}.json
     *
     * @param basePath 资源根目录（如 "src/main/resources"）
     * @throws IOException 如果文件写入失败
     */
    public void saveToFile(String basePath) throws IOException {
        String json = generateJson();
        Path outputDir = Paths.get(basePath, "data", modId, "worldgen", "structure");
        Files.createDirectories(outputDir);

        Path outputFile = outputDir.resolve(structureName + ".json");
        try (FileWriter writer = new FileWriter(outputFile.toFile())) {
            writer.write(json);
        }

        PasterDreamMod.LOGGER.info("[StructureTypeGenerator] ✅ 已生成 structure JSON → {}", outputFile);
    }
}