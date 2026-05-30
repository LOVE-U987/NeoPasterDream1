package com.pasterdream.pasterdreammod.api.dimension.gen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.pasterdream.pasterdreammod.PasterDreamMod;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * sounds.json 生成器 —— 自动管理 {@code assets/{modId}/sounds.json} 中的音乐条目
 * <p>
 * Minecraft 的背景音乐需要在 {@code sounds.json} 中注册，
 * 并且 {@code SoundEvent} 需要声明为 {@code "stream": true}
 * 才能正确处理长音频的流式加载。
 * <p>
 * 本生成器会读取已有的 sounds.json，追加或更新音乐条目，保留所有现有配置。
 */
public class SoundsJsonGenerator {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private final String modId;
    /** 音乐条目缓存：soundKey -> SoundEntry 配置 */
    private final Map<String, JsonObject> musicEntries = new LinkedHashMap<>();

    /**
     * 构造声音 JSON 生成器
     *
     * @param modId 模组 ID
     */
    public SoundsJsonGenerator(String modId) {
        this.modId = modId;
    }

    /**
     * 添加一个维度背景音乐条目（默认音量 1.0）
     * <p>
     * 会自动将 {@code musicName} 映射为标准 Minecraft 音乐键格式：
     * <ul>
     *   <li>soundKey: {@code music.{musicName}}</li>
     *   <li>soundPath: {@code music/{musicName}}</li>
     *   <li>将 {@code "stream": true} 以确保长音频流畅播放</li>
     * </ul>
     *
     * @param musicName 音乐名称（如 "dyedream_world"）
     */
    public SoundsJsonGenerator addDimensionMusic(String musicName) {
        return addDimensionMusic(musicName, 1.0f);
    }

    /**
     * 添加一个维度背景音乐条目（自定义音量）
     * <p>
     * 在 {@link #addDimensionMusic(String)} 的基础上增加了音量参数支持。
     * 音量值将写入 {@code sounds.json} 的 sounds 数组条目中，
     * Minecraft 在播放时会将该音量与 SoundInstance 的音量相乘。
     * <p>
     * 推荐将所有 BGM 统一设置为 0.3（30%），以保持各群系BGM的相对音量比例。
     *
     * @param musicName 音乐名称（如 "dyedream_world"）
     * @param volume    音量值（0.0 ~ 1.0），推荐 0.3
     */
    public SoundsJsonGenerator addDimensionMusic(String musicName, float volume) {
        String soundKey = "music." + musicName;
        String soundPath = modId + ":music/" + musicName;

        JsonObject entry = new JsonObject();
        // category：背景音乐固定为 "music"
        entry.addProperty("category", "music");
        // subtitle：可选，可留空
        entry.addProperty("subtitle", "subtitle." + modId + "." + soundKey);

        // sounds 数组
        com.google.gson.JsonArray sounds = new com.google.gson.JsonArray();
        JsonObject soundObj = new JsonObject();
        soundObj.addProperty("name", soundPath);
        soundObj.addProperty("stream", true);
        // 写入音量参数
        soundObj.addProperty("volume", volume);
        sounds.add(soundObj);
        entry.add("sounds", sounds);

        musicEntries.put(soundKey, entry);
        PasterDreamMod.LOGGER.info("[SoundsJsonGenerator] 已缓存音乐条目: {} → {} (音量={})", soundKey, soundPath, volume);
        return this;
    }

    /**
     * 添加自定义声音条目（非音乐用途，默认音量 1.0）
     *
     * @param soundKey 声音键（如 "life_crystal"）
     * @param soundPath 声音文件路径（如 "pasterdream:life_crystal"）
     * @param category 声音分类（如 "block", "hostile", "ambient"）
     * @param subtitle 字幕键（如 "subtitle.pasterdream.life_crystal"）
     * @param stream 是否流式加载（音乐必须 true，短音效 false）
     */
    public SoundsJsonGenerator addCustomSound(String soundKey, String soundPath,
                                               String category, String subtitle,
                                               boolean stream) {
        return addCustomSound(soundKey, soundPath, category, subtitle, stream, 1.0f);
    }

    /**
     * 添加自定义声音条目（自定义音量）
     *
     * @param soundKey 声音键（如 "life_crystal"）
     * @param soundPath 声音文件路径（如 "pasterdream:life_crystal"）
     * @param category 声音分类（如 "block", "hostile", "ambient"）
     * @param subtitle 字幕键（如 "subtitle.pasterdream.life_crystal"）
     * @param stream 是否流式加载（音乐必须 true，短音效 false）
     * @param volume 音量值（0.0 ~ 1.0）
     */
    public SoundsJsonGenerator addCustomSound(String soundKey, String soundPath,
                                               String category, String subtitle,
                                               boolean stream, float volume) {
        JsonObject entry = new JsonObject();
        entry.addProperty("category", category);
        entry.addProperty("subtitle", subtitle);

        com.google.gson.JsonArray sounds = new com.google.gson.JsonArray();
        JsonObject soundObj = new JsonObject();
        soundObj.addProperty("name", soundPath);
        soundObj.addProperty("stream", stream);
        soundObj.addProperty("volume", volume);
        sounds.add(soundObj);
        entry.add("sounds", sounds);

        musicEntries.put(soundKey, entry);
        return this;
    }

    /**
     * 生成最终的 sounds.json 内容
     * <p>
     * 会保留已有 sounds.json 中的所有条目，再追加本次添加的条目。
     *
     * @return 完整的 sounds.json 字符串
     */
    public String generateJson() {
        JsonObject root = new JsonObject();

        // 按添加顺序写入，保持可读性
        for (Map.Entry<String, JsonObject> entry : musicEntries.entrySet()) {
            root.add(entry.getKey(), entry.getValue());
        }

        return GSON.toJson(root);
    }

    /**
     * 将 sounds.json 写入文件
     * <p>
     * 目标路径：{@code {basePath}/assets/{modId}/sounds.json}
     *
     * @param basePath 资源根目录（如 "src/main/resources"）
     * @throws IOException 如果文件读写失败
     */
    public void saveToFile(String basePath) throws IOException {
        // 读取已有 sounds.json 并入
        JsonObject merged = loadExisting(basePath);

        // 添加/覆盖当前缓存的条目
        for (Map.Entry<String, JsonObject> entry : musicEntries.entrySet()) {
            merged.add(entry.getKey(), entry.getValue());
        }

        // 写入文件
        Path outputFile = Paths.get(basePath, "assets", modId, "sounds.json");
        Files.createDirectories(outputFile.getParent());
        try (FileWriter writer = new FileWriter(outputFile.toFile())) {
            GSON.toJson(merged, writer);
        }

        PasterDreamMod.LOGGER.info("[SoundsJsonGenerator] ✅ 已更新 sounds.json → {}", outputFile);
    }

    // ======================== 私有辅助 ========================

    /**
     * 加载已有的 sounds.json 文件
     */
    private JsonObject loadExisting(String basePath) {
        Path existingFile = Paths.get(basePath, "assets", modId, "sounds.json");
        if (Files.exists(existingFile)) {
            try (FileReader reader = new FileReader(existingFile.toFile())) {
                JsonElement parsed = JsonParser.parseReader(reader);
                if (parsed != null && parsed.isJsonObject()) {
                    return parsed.getAsJsonObject();
                }
            } catch (IOException e) {
                PasterDreamMod.LOGGER.warn("[SoundsJsonGenerator] 读取已有 sounds.json 失败: {}", e.getMessage());
            }
        }
        return new JsonObject();
    }

    /**
     * 静态便捷方法 —— 一键注册维度背景音乐并更新 sounds.json（默认音量 1.0）
     *
     * @param modId      模组 ID
     * @param musicName  音乐名称（如 "dyedream_world"）
     * @param basePath   资源根目录
     * @throws IOException 如果文件写入失败
     */
    public static void registerDimensionMusic(String modId, String musicName, String basePath) throws IOException {
        registerDimensionMusic(modId, musicName, basePath, 1.0f);
    }

    /**
     * 静态便捷方法 —— 一键注册维度背景音乐并更新 sounds.json（自定义音量）
     *
     * @param modId      模组 ID
     * @param musicName  音乐名称（如 "dyedream_world"）
     * @param basePath   资源根目录
     * @param volume     音量值（0.0 ~ 1.0），推荐 0.3
     * @throws IOException 如果文件写入失败
     */
    public static void registerDimensionMusic(String modId, String musicName, String basePath, float volume) throws IOException {
        new SoundsJsonGenerator(modId)
                .addDimensionMusic(musicName, volume)
                .saveToFile(basePath);
    }
}