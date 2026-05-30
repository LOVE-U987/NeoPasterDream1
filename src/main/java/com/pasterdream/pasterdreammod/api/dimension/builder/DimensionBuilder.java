package com.pasterdream.pasterdreammod.api.dimension.builder;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.api.dimension.DimensionResult;
import com.pasterdream.pasterdreammod.api.dimension.gen.DimensionGenerator;
import com.pasterdream.pasterdreammod.api.dimension.gen.DimensionTypeGenerator;
import com.pasterdream.pasterdreammod.api.dimension.gen.SoundsJsonGenerator;
import com.pasterdream.pasterdreammod.registry.PDSounds;

import java.io.IOException;
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

    private final String modId;
    private final String dimensionName;
    private final DimensionTypeGenerator typeGenerator;
    private final DimensionGenerator dimensionGenerator;

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
        this.typeGenerator = new DimensionTypeGenerator(modId, dimensionName);
        this.dimensionGenerator = new DimensionGenerator(modId, dimensionName);
    }

    // ======================== DimensionType 配置（直接桥接到 DimensionTypeGenerator） ========================

    public DimensionBuilder ultraWarm(boolean value) { typeGenerator.ultraWarm(value); return this; }
    /** 便捷方法：默认 true */
    public DimensionBuilder ultraWarm() { return ultraWarm(true); }
    public DimensionBuilder natural(boolean value) { typeGenerator.natural(value); return this; }
    /** 便捷方法：默认 true */
    public DimensionBuilder natural() { return natural(true); }
    public DimensionBuilder piglinSafe(boolean value) { typeGenerator.piglinSafe(value); return this; }
    /** 便捷方法：默认 true */
    public DimensionBuilder piglinSafe() { return piglinSafe(true); }
    public DimensionBuilder respawnAnchorWorks(boolean value) { typeGenerator.respawnAnchorWorks(value); return this; }
    /** 便捷方法：默认 true */
    public DimensionBuilder respawnAnchorWorks() { return respawnAnchorWorks(true); }
    public DimensionBuilder bedWorks(boolean value) { typeGenerator.bedWorks(value); return this; }
    /** 便捷方法：默认 true */
    public DimensionBuilder bedWorks() { return bedWorks(true); }
    public DimensionBuilder hasRaids(boolean value) { typeGenerator.hasRaids(value); return this; }
    /** 便捷方法：默认 true */
    public DimensionBuilder hasRaids() { return hasRaids(true); }
    public DimensionBuilder hasSkylight(boolean value) { typeGenerator.hasSkylight(value); return this; }
    /** 便捷方法：默认 true */
    public DimensionBuilder hasSkylight() { return hasSkylight(true); }
    public DimensionBuilder hasCeiling(boolean value) { typeGenerator.hasCeiling(value); return this; }
    /** 便捷方法：默认 true */
    public DimensionBuilder hasCeiling() { return hasCeiling(true); }
    public DimensionBuilder coordinateScale(double value) { typeGenerator.coordinateScale(value); return this; }
    public DimensionBuilder withAmbientLight(double value) { typeGenerator.ambientLight(value); return this; }
    public DimensionBuilder logicalHeight(int value) { typeGenerator.logicalHeight(value); return this; }
    public DimensionBuilder infiniburn(String value) { typeGenerator.infiniburn(value); return this; }
    public DimensionBuilder minY(int value) { typeGenerator.minY(value); return this; }
    public DimensionBuilder height(int value) { typeGenerator.height(value); return this; }
    public DimensionBuilder monsterSpawnLight(int min, int max) { typeGenerator.monsterSpawnLight(min, max); return this; }
    public DimensionBuilder monsterSpawnBlockLightLimit(int value) { typeGenerator.monsterSpawnBlockLightLimit(value); return this; }

    // ======================== Dimension 配置（桥接到 DimensionGenerator） ========================

    /**
     * 设置维度类型引用
     * <p>
     * 默认自动使用 {@code {modId}:{dimensionName}}，
     * 一般无需手动调用此方法。
     */
    public DimensionBuilder withDimensionType(String dimensionTypeId) {
        dimensionGenerator.dimensionTypeId(dimensionTypeId);
        return this;
    }

    /**
     * 设置噪声设置引用
     * <p>
     * 如果使用自定义噪声设置，指定其 ID。
     * 使用原版主世界设置可传入 {@code "minecraft:overworld"}。
     */
    public DimensionBuilder withNoiseSettings(String noiseSettings) {
        dimensionGenerator.noiseSettings(noiseSettings);
        return this;
    }

    public DimensionBuilder seaLevel(int seaLevel) { dimensionGenerator.seaLevel(seaLevel); return this; }
    public DimensionBuilder disableMobGeneration(boolean value) { dimensionGenerator.disableMobGeneration(value); return this; }
    public DimensionBuilder aquifersEnabled(boolean value) { dimensionGenerator.aquifersEnabled(value); return this; }
    public DimensionBuilder oreVeinsEnabled(boolean value) { dimensionGenerator.oreVeinsEnabled(value); return this; }
    public DimensionBuilder legacyRandomSource(boolean value) { dimensionGenerator.legacyRandomSource(value); return this; }

    /**
     * 设置维度默认方块
     *
     * @param blockId 方块 ID（如 "minecraft:calcite"）
     */
    public DimensionBuilder withDefaultBlock(String blockId) {
        dimensionGenerator.defaultBlock(blockId);
        return this;
    }

    /**
     * 设置维度默认流体
     *
     * @param fluidId 流体 ID（如 "minecraft:water"）
     */
    public DimensionBuilder withDefaultFluid(String fluidId) {
        dimensionGenerator.defaultFluid(fluidId);
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
        dimensionGenerator.addBiome(biomeId, temperature, humidity, continental, weirdness, erosion);
        return this;
    }

    /**
     * 使用固定生物群系
     *
     * @param biomeId 固定生物群系 ID
     */
    public DimensionBuilder withFixedBiome(String biomeId) {
        dimensionGenerator.fixedBiome(biomeId);
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
     */
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
     */
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
        dimensionGenerator.dimensionTypeId(dimensionTypeId);

        // 自动设置 effectsId 为自身（允许客户端注册 DimensionSpecialEffects）
        typeGenerator.effectsId(dimensionTypeId);

        if (generateJsonFiles) {
            try {
                PasterDreamMod.LOGGER.info("[DimensionBuilder] ===== 开始生成维度资源文件: {} =====", dimensionName);

                typeGenerator.saveToFile(basePath);
                dimensionGenerator.saveToFile(basePath);

                // 如果有配置背景音乐，自动注册 SoundEvent 并生成 sounds.json
                if (musicName != null) {
                    registerMusic();
                }

                PasterDreamMod.LOGGER.info("[DimensionBuilder] ✅ 维度资源文件生成完成: {}", dimensionName);
            } catch (IOException e) {
                PasterDreamMod.LOGGER.error("[DimensionBuilder] ❌ 无法生成维度资源文件 [{}]: {}", dimensionName, e.getMessage(), e);
                throw new RuntimeException("DimensionBuilder: 无法生成维度资源文件 [" + dimensionName + "]", e);
            }
        } else if (musicName != null) {
            // 即使不生成 JSON 文件，也要注册 SoundEvent
            PDSounds.registerDimensionMusic(musicName);
            PasterDreamMod.LOGGER.info("[DimensionBuilder] 已注册背景音乐 SoundEvent: {}.music.{}, 请确保 .ogg 文件存在",
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
        Supplier<net.minecraft.sounds.SoundEvent> soundSupplier = PDSounds.registerDimensionMusic(musicName);

        // 2. 生成 sounds.json 条目（传入音量参数）
        new SoundsJsonGenerator(modId)
                .addDimensionMusic(musicName, musicVolume)
                .saveToFile(basePath);

        // 3. 检查 .ogg 文件是否存在（仅给出警告，不阻止构建）
        java.nio.file.Path oggPath = java.nio.file.Paths.get(
                basePath, "assets", modId, "sounds", "music", musicName + ".ogg");
        if (!java.nio.file.Files.exists(oggPath)) {
            PasterDreamMod.LOGGER.warn("[DimensionBuilder] ⚠️ 背景音乐 .ogg 文件未找到: {}",
                    oggPath.toAbsolutePath().normalize());
            PasterDreamMod.LOGGER.warn("[DimensionBuilder] 📌 请在以下位置放置 .ogg 音频文件:");
            PasterDreamMod.LOGGER.warn("[DimensionBuilder]    {}", oggPath.toAbsolutePath().normalize());
        }
    }
}