package com.pasterdream.pasterdreammod.api.particle.gen;

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
import java.util.ArrayList;
import java.util.List;

/**
 * 粒子定义 JSON 生成器 —— 自动生成 Minecraft particle JSON 文件
 * <p>
 * 生成 {@code assets/{modId}/particles/{name}.json} 文件，
 * 包含粒子使用的纹理列表，供 Minecraft 游戏引擎加载。
 * <p>
 * 生成的 JSON 格式：
 * <pre>{@code
 * {
 *   "textures": [
 *     "pasterdream:particle_name_1",
 *     "pasterdream:particle_name_2"
 *   ]
 * }
 * }</pre>
 *
 * @see com.pasterdream.pasterdreammod.api.particle.builder.ParticleBuilder
 */
public class ParticleGenerator {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private final String modId;
    private final String particleName;

    /** 纹理路径列表 */
    private final List<String> textures = new ArrayList<>();

    /**
     * 构造粒子定义生成器
     *
     * @param modId        模组 ID
     * @param particleName 粒子注册名称
     */
    public ParticleGenerator(String modId, String particleName) {
        this.modId = modId;
        this.particleName = particleName;
    }

    /**
     * 添加一个粒子纹理路径
     * <p>
     * 路径格式为 {@code {namespace}:{path}}，
     * 如 {@code "pasterdream:sparkle_1"}。
     * 会自动在 {@code assets/{modId}/textures/particle/} 下查找对应的 PNG 文件。
     *
     * @param texturePath 纹理路径
     * @return 当前生成器实例
     */
    public ParticleGenerator addTexture(String texturePath) {
        this.textures.add(texturePath);
        return this;
    }

    /**
     * 批量添加粒子纹理路径
     * <p>
     * 常用于粒子有多个帧（精灵表变体）的场景。
     *
     * @param texturePaths 纹理路径列表
     * @return 当前生成器实例
     */
    public ParticleGenerator addTextures(String... texturePaths) {
        for (String path : texturePaths) {
            this.textures.add(path);
        }
        return this;
    }

    /**
     * 根据基础纹理名自动生成多帧纹理路径
     * <p>
     * 例如基础名为 {@code "pasterdream:sparkle"}，帧数 {@code 4}，
     * 会自动添加 {@code pasterdream:sparkle_1} 到 {@code pasterdream:sparkle_4}。
     *
     * @param baseTexture 基础纹理名称
     * @param frameCount  帧数
     * @return 当前生成器实例
     */
    public ParticleGenerator withFrames(String baseTexture, int frameCount) {
        for (int i = 1; i <= frameCount; i++) {
            this.textures.add(baseTexture + "_" + i);
        }
        return this;
    }

    /**
     * 生成粒子定义 JSON 字符串
     *
     * @return 格式化后的 JSON 字符串
     */
    public String generateJson() {
        JsonObject root = new JsonObject();
        JsonArray textureArray = new JsonArray();

        if (textures.isEmpty()) {
            // 如果没有显式添加纹理，使用默认规则
            textureArray.add(modId + ":" + particleName);
        } else {
            for (String tex : textures) {
                textureArray.add(tex);
            }
        }

        root.add("textures", textureArray);
        return GSON.toJson(root);
    }

    /**
     * 将粒子定义 JSON 写入文件
     * <p>
     * 目标路径：{basePath}/assets/{modId}/particles/{particleName}.json
     *
     * @param basePath 资源根目录（如 "src/main/resources"）
     * @throws IOException 如果文件写入失败
     */
    public void saveToFile(String basePath) throws IOException {
        String json = generateJson();
        Path outputDir = Paths.get(basePath, "assets", modId, "particles");
        Files.createDirectories(outputDir);

        Path outputFile = outputDir.resolve(particleName + ".json");
        try (FileWriter writer = new FileWriter(outputFile.toFile())) {
            writer.write(json);
        }

        PasterDreamMod.LOGGER.info("[ParticleGenerator] ✅ 已生成粒子定义 JSON → {}", outputFile);
    }
}