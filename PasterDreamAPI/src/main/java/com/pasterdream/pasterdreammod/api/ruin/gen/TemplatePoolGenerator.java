package com.pasterdream.pasterdreammod.api.ruin.gen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.pasterdream.pasterdreammod.api.PasterDreamAPI;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * 模板池 JSON 生成器 —— 自动生成 Minecraft template_pool JSON 文件
 * <p>
 * 生成 {@code data/{modId}/worldgen/template_pool/{name}.json} 文件，
 * 包含模板池的退回落和模板条目列表。
 * <p>
 * 生成的 JSON 结构：
 * <ul>
 *   <li>name — 模板池名称</li>
 *   <li>fallback — 退回落（如 "minecraft:empty"）</li>
 *   <li>elements — 模板条目列表（含权重、元素类型、位置、处理方式等）</li>
 * </ul>
 * <p>
 * 使用示例：
 * <pre>{@code
 * TemplatePoolGenerator pool = new TemplatePoolGenerator("pasterdream", "dyedream_ruins_pool")
 *     .fallback("minecraft:empty")
 *     .addSingleElement("pasterdream:dyedream_ruins/ruin_1", 3, "rigid", "minecraft:empty")
 *     .addSingleElement("pasterdream:dyedream_ruins/ruin_2", 2, "rigid", "minecraft:empty")
 *     .saveToFile("src/main/resources");
 * }</pre>
 */
public class TemplatePoolGenerator {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private final String modId;
    private final String poolName;

    /** 退回落（如 "minecraft:empty"） */
    private String fallback;
    /** 模板条目列表 */
    private final List<TemplateElement> elements;

    /**
     * 模板条目，描述模板池中的一个元素
     *
     * @param location   结构 NBT 文件路径（如 "pasterdream:dyedream_ruins/ruin_1"）
     * @param weight     权重
     * @param projection 投影类型（"rigid" 或 "terrain_matching"）
     * @param processors 处理器 ID（如 "minecraft:empty"）
     * @param elementType 元素类型（如 "minecraft:single_pool_element"）
     */
    public record TemplateElement(
            String location,
            int weight,
            String projection,
            String processors,
            String elementType
    ) {}

    /**
     * 构造模板池生成器
     *
     * @param modId    模组 ID
     * @param poolName 模板池注册名称（如 "dyedream_ruins_pool"）
     */
    public TemplatePoolGenerator(String modId, String poolName) {
        this.modId = modId;
        this.poolName = poolName;
        this.fallback = "minecraft:empty";
        this.elements = new ArrayList<>();
    }

    // ======================== 配置方法（链式调用） ========================

    /**
     * 设置退回落
     *
     * @param fallback 退回落 ID（如 "minecraft:empty"）
     * @return 当前生成器实例
     */
    public TemplatePoolGenerator fallback(String fallback) {
        this.fallback = fallback;
        return this;
    }

    /**
     * 添加一个单元素模板条目
     * <p>
     * 使用 {@code "minecraft:single_pool_element"} 作为元素类型。
     *
     * @param location   结构 NBT 文件路径（如 "pasterdream:dyedream_ruins/ruin_1"）
     * @param weight     权重（越高生成概率越大）
     * @param projection 投影类型（"rigid" | "terrain_matching"）
     * @param processors 处理器 ID（如 "minecraft:empty"）
     * @return 当前生成器实例
     */
    public TemplatePoolGenerator addSingleElement(String location, int weight,
                                                   String projection, String processors) {
        elements.add(new TemplateElement(
                location, weight, projection, processors, "minecraft:single_pool_element"
        ));
        return this;
    }

    /**
     * 添加一个自定义模板条目
     *
     * @param element 模板条目
     * @return 当前生成器实例
     */
    public TemplatePoolGenerator addElement(TemplateElement element) {
        elements.add(element);
        return this;
    }

    /**
     * 批量添加模板条目
     *
     * @param elements 模板条目列表
     * @return 当前生成器实例
     */
    public TemplatePoolGenerator addAllElements(List<TemplateElement> elements) {
        this.elements.addAll(elements);
        return this;
    }

    /**
     * 清空所有模板条目
     *
     * @return 当前生成器实例
     */
    public TemplatePoolGenerator clearElements() {
        this.elements.clear();
        return this;
    }

    // ======================== JSON 生成 ========================

    /**
     * 生成模板池 JSON 字符串
     *
     * @return 格式化后的 JSON 字符串
     */
    public String generateJson() {
        JsonObject root = new JsonObject();
        root.addProperty("name", modId + ":" + poolName);
        root.addProperty("fallback", fallback);

        JsonArray elementsArray = new JsonArray();
        for (TemplateElement elem : elements) {
            JsonObject entry = new JsonObject();
            entry.addProperty("weight", elem.weight());

            JsonObject elementObj = new JsonObject();
            elementObj.addProperty("element_type", elem.elementType());
            elementObj.addProperty("projection", elem.projection());
            elementObj.addProperty("location", elem.location());
            elementObj.addProperty("processors", elem.processors());

            entry.add("element", elementObj);
            elementsArray.add(entry);
        }
        root.add("elements", elementsArray);

        return GSON.toJson(root);
    }

    /**
     * 将模板池 JSON 写入文件
     * <p>
     * 目标路径：{basePath}/data/{modId}/worldgen/template_pool/{poolName}.json
     *
     * @param basePath 资源根目录（如 "src/main/resources"）
     * @throws IOException 如果文件写入失败
     */
    public void saveToFile(String basePath) throws IOException {
        String json = generateJson();
        Path outputDir = Paths.get(basePath, "data", modId, "worldgen", "template_pool");
        Files.createDirectories(outputDir);

        Path outputFile = outputDir.resolve(poolName + ".json");
        try (FileWriter writer = new FileWriter(outputFile.toFile())) {
            writer.write(json);
        }

        PasterDreamAPI.LOGGER.info("[TemplatePoolGenerator] ✅ 已生成 template_pool JSON → {}", outputFile);
    }
}