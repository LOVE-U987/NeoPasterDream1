package com.pasterdream.pasterdreammod.api.particle.gen;

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
 * 粒子纹理元数据生成器 —— 生成粒子纹理的辅助描述文件
 * <p>
 * 生成 {@code assets/{modId}/textures/particle/{name}.json} 文件，
 * 记录粒子的纹理配置元数据，包括纹理基础路径、重力参考值等信息。
 * <p>
 * 此文件为非标准的元数据文件，主要用于开发参考和文档记录。
 * 实际的粒子纹理 PNG 文件需手动放置在 {@code textures/particle/} 目录下。
 * <p>
 * 生成的 JSON 格式：
 * <pre>{@code
 * {
 *   "particle": "pasterdream:sparkle",
 *   "gravity": 0.05,
 *   "texture_dir": "assets/pasterdream/textures/particle/"
 * }
 * }</pre>
 *
 * @see com.pasterdream.pasterdreammod.api.particle.builder.ParticleBuilder
 */
public class ParticleTextureGenerator {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private final String modId;
    private final String particleName;

    /** 重力值（可空），用于文档参考 */
    private Float gravity;

    /**
     * 构造粒子纹理元数据生成器
     *
     * @param modId        模组 ID
     * @param particleName 粒子注册名称
     */
    public ParticleTextureGenerator(String modId, String particleName) {
        this.modId = modId;
        this.particleName = particleName;
    }

    /**
     * 设置粒子重力值（记录到元数据文件中）
     * <p>
     * 此值仅用于记录和文档提示，实际的粒子重力效果
     * 需要在自定义 Particle 类中通过 {@code this.gravity} 实现。
     *
     * @param gravity 重力值
     * @return 当前生成器实例
     */
    public ParticleTextureGenerator withGravity(Float gravity) {
        this.gravity = gravity;
        return this;
    }

    /**
     * 生成粒子纹理元数据 JSON 字符串
     *
     * @return 格式化后的 JSON 字符串
     */
    public String generateJson() {
        JsonObject root = new JsonObject();

        // 粒子完整 ID
        root.addProperty("particle", modId + ":" + particleName);

        // 纹理目录路径提示
        root.addProperty("texture_dir", "assets/" + modId + "/textures/particle/");

        // 纹理基础名称
        root.addProperty("texture_base", modId + ":" + particleName);

        // 重力值（如果有配置）
        if (gravity != null) {
            root.addProperty("gravity", gravity);
        }

        return GSON.toJson(root);
    }

    /**
     * 将粒子纹理元数据 JSON 写入文件
     * <p>
     * 目标路径：{basePath}/assets/{modId}/textures/particle/{particleName}.json
     *
     * @param basePath 资源根目录（如 "src/main/resources"）
     * @throws IOException 如果文件写入失败
     */
    public void saveToFile(String basePath) throws IOException {
        String json = generateJson();
        Path outputDir = Paths.get(basePath, "assets", modId, "textures", "particle");
        Files.createDirectories(outputDir);

        Path outputFile = outputDir.resolve(particleName + ".json");
        try (FileWriter writer = new FileWriter(outputFile.toFile())) {
            writer.write(json);
        }

        PasterDreamMod.LOGGER.info("[ParticleTextureGenerator] ✅ 已生成粒子纹理元数据 → {}", outputFile);
    }
}