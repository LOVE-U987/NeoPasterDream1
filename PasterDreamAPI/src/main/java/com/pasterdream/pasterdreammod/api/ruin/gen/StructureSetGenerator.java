package com.pasterdream.pasterdreammod.api.ruin.gen;

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
 * 结构集 JSON 生成器 —— 自动生成 Minecraft structure_set JSON 文件
 * <p>
 * 生成 {@code data/{modId}/worldgen/structure_set/{name}.json} 文件，
 * 包含结构引用和放置配置（间距、分离值、随机种子盐值等）。
 * <p>
 * 生成的 JSON 结构：
 * <ul>
 *   <li>structures — 结构列表（含权重）</li>
 *   <li>placement — 放置配置（类型、间距、分离值、盐值）</li>
 * </ul>
 */
public class StructureSetGenerator {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private final String modId;
    private final String setName;

    /** 结构 ID（如 "pasterdream:dyedream_ruins"） */
    private String structureId;
    /** 结构权重 */
    private int weight;
    /** 放置类型（如 "minecraft:random_spread"） */
    private String placementType;
    /** 间距 */
    private int spacing;
    /** 分离值 */
    private int separation;
    /** 随机种子盐值 */
    private int salt;
    /** 排除的附近结构集（可选） */
    private String excludeSet;

    /**
     * 构造结构集生成器
     *
     * @param modId   模组 ID
     * @param setName 结构集注册名称（如 "dyedream_ruins_set"）
     */
    public StructureSetGenerator(String modId, String setName) {
        this.modId = modId;
        this.setName = setName;
        this.weight = 1;
        this.placementType = "minecraft:random_spread";
        this.spacing = 32;
        this.separation = 8;
        this.salt = 0;
    }

    // ======================== 配置方法（链式调用） ========================

    /** 设置结构 ID */
    public StructureSetGenerator structureId(String structureId) { this.structureId = structureId; return this; }
    /** 设置结构权重 */
    public StructureSetGenerator weight(int weight) { this.weight = weight; return this; }
    /** 设置放置类型 */
    public StructureSetGenerator placementType(String placementType) { this.placementType = placementType; return this; }
    /** 设置间距 */
    public StructureSetGenerator spacing(int spacing) { this.spacing = spacing; return this; }
    /** 设置分离值 */
    public StructureSetGenerator separation(int separation) { this.separation = separation; return this; }
    /** 设置随机种子盐值 */
    public StructureSetGenerator salt(int salt) { this.salt = salt; return this; }
    /** 设置排除的附近结构集 */
    public StructureSetGenerator excludeSet(String excludeSet) { this.excludeSet = excludeSet; return this; }

    /**
     * 生成结构集 JSON 字符串
     *
     * @return 格式化后的 JSON 字符串
     */
    public String generateJson() {
        JsonObject root = new JsonObject();

        // structures 字段（简化版，使用对象格式 { "structure": weight }）
        JsonObject structuresObj = new JsonObject();
        if (structureId != null) {
            structuresObj.addProperty(structureId, weight);
        }
        root.add("structures", structuresObj);

        // placement 字段
        JsonObject placement = new JsonObject();
        placement.addProperty("type", placementType);
        placement.addProperty("spacing", spacing);
        placement.addProperty("separation", separation);
        placement.addProperty("salt", salt);

        if (excludeSet != null && !excludeSet.isEmpty()) {
            placement.addProperty("exclusion_zone", excludeSet);
        }

        root.add("placement", placement);

        return GSON.toJson(root);
    }

    /**
     * 将结构集 JSON 写入文件
     * <p>
     * 目标路径：{basePath}/data/{modId}/worldgen/structure_set/{setName}.json
     *
     * @param basePath 资源根目录（如 "src/main/resources"）
     * @throws IOException 如果文件写入失败
     */
    public void saveToFile(String basePath) throws IOException {
        String json = generateJson();
        Path outputDir = Paths.get(basePath, "data", modId, "worldgen", "structure_set");
        Files.createDirectories(outputDir);

        Path outputFile = outputDir.resolve(setName + ".json");
        try (FileWriter writer = new FileWriter(outputFile.toFile())) {
            writer.write(json);
        }

        PasterDreamAPI.LOGGER.info("[StructureSetGenerator] ✅ 已生成 structure_set JSON → {}", outputFile);
    }
}