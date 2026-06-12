package com.pasterdream.pasterdreammod.api.ruin.builder;

import com.pasterdream.pasterdreammod.api.PasterDreamAPI;
import com.pasterdream.pasterdreammod.api.ruin.RuinResult;
import com.pasterdream.pasterdreammod.api.ruin.gen.StructureSetGenerator;

import java.io.IOException;

/**
 * 结构集构建器 —— 采用 Builder 模式链式配置结构集和 JSON 资源文件生成
 * <p>
 * 用于为已注册的结构创建配套的 structure_set 配置，包括
 * 放置间距、分离值、随机种子盐值等参数。
 * <p>
 * 使用示例：
 * <pre>{@code
 * // 先创建结构
 * RuinResult result = RuinAPI.createRuin("dyedream_ruins")
 *     .biomeTag("pasterdream:is_dyedream")
 *     .templatePool("pasterdream:dyedream_ruins_pool")
 *     .structureClass(DyedreamRuinsStructure.class)
 *     .codec(DyedreamRuinsStructure.CODEC)
 *     .build();
 *
 * // 再创建结构集
 * RuinAPI.createRuinSet("dyedream_ruins", "dyedream_ruins_set")
 *     .spacing(32)
 *     .separation(8)
 *     .salt(12345)
 *     .build();
 * }</pre>
 *
 * @see com.pasterdream.pasterdreammod.api.ruin.RuinAPI
 */
public class StructureSetBuilder {

    private final String modId;
    private final String setName;
    private final String ruinName;
    private final StructureSetGenerator setGenerator;

    /** 间距 */
    private int spacing;
    /** 分离值 */
    private int separation;
    /** 随机种子盐值 */
    private int salt;
    /** 放置类型（如 "minecraft:random_spread"） */
    private String placementType;
    /** 是否自动生成 JSON 资源文件，默认为 true */
    private boolean generateJsonFiles;
    /** 资源文件基础路径，默认为 src/main/resources */
    private String basePath;

    /**
     * 构造结构集构建器
     *
     * @param modId    模组 ID
     * @param ruinName 关联的结构注册名称（用于查找对应的 RuinResult）
     * @param setName  结构集注册名称（如 "dyedream_ruins_set"）
     */
    public StructureSetBuilder(String modId, String ruinName, String setName) {
        this.modId = modId;
        this.ruinName = ruinName;
        this.setName = setName;
        PasterDreamAPI.LOGGER.debug("[StructureSetBuilder] 创建结构集构建器: modId={}, ruin={}, setName={}", modId, ruinName, setName);
        this.setGenerator = new StructureSetGenerator(modId, setName);
        this.spacing = 32;
        this.separation = 8;
        this.salt = 0;
        this.placementType = "minecraft:random_spread";
        this.generateJsonFiles = true;
        this.basePath = "src/main/resources";
    }

    // ======================== 配置方法（链式调用） ========================

    /**
     * 设置结构生成间距
     * <p>
     * 控制两个同类型结构之间的最小区块距离。
     * 值越大，结构越稀疏。
     *
     * @param spacing 间距值（区块数）
     * @return 当前构建器实例
     */
    public StructureSetBuilder spacing(int spacing) {
        this.spacing = spacing;
        PasterDreamAPI.LOGGER.debug("[StructureSetBuilder] {} → spacing={}", setName, spacing);
        return this;
    }

    /**
     * 设置结构分离值
     * <p>
     * 控制结构之间的最小分离距离。
     * 值越大，结构之间的空隙越大。
     *
     * @param separation 分离值（区块数）
     * @return 当前构建器实例
     */
    public StructureSetBuilder separation(int separation) {
        this.separation = separation;
        PasterDreamAPI.LOGGER.debug("[StructureSetBuilder] {} → separation={}", setName, separation);
        return this;
    }

    /**
     * 设置随机种子盐值
     * <p>
     * 用于决定结构生成位置的随机数种子偏移。
     * 不同结构类型应使用不同的盐值以确保生成位置不重叠。
     *
     * @param salt 盐值
     * @return 当前构建器实例
     */
    public StructureSetBuilder salt(int salt) {
        this.salt = salt;
        PasterDreamAPI.LOGGER.debug("[StructureSetBuilder] {} → salt={}", setName, salt);
        return this;
    }

    /**
     * 设置放置类型
     * <p>
     * 指定结构的放置算法类型。
     *
     * @param placementType 放置类型（如 "minecraft:random_spread"）
     * @return 当前构建器实例
     */
    public StructureSetBuilder placementType(String placementType) {
        this.placementType = placementType;
        PasterDreamAPI.LOGGER.debug("[StructureSetBuilder] {} → placementType={}", setName, placementType);
        return this;
    }

    /**
     * 是否自动生成 JSON 资源文件
     * <p>
     * 默认为 true，会在 {@link #build()} 时自动生成
     * structure_set 的 JSON 文件。
     *
     * @param generate 是否生成 JSON
     * @return 当前构建器实例
     */
    public StructureSetBuilder generateJson(boolean generate) {
        this.generateJsonFiles = generate;
        PasterDreamAPI.LOGGER.debug("[StructureSetBuilder] {} → generateJson={}", setName, generate);
        return this;
    }

    /**
     * 设置资源文件基础路径
     * <p>
     * 默认为 {@code "src/main/resources"}。
     *
     * @param basePath 资源根目录
     * @return 当前构建器实例
     */
    public StructureSetBuilder basePath(String basePath) {
        this.basePath = basePath;
        PasterDreamAPI.LOGGER.debug("[StructureSetBuilder] {} → basePath={}", setName, basePath);
        return this;
    }

    // ======================== 构建 & 生成 ========================

    /**
     * 执行构建，完成结构集配置和资源文件生成
     * <p>
     * 完成以下工作：
     * <ol>
     *   <li>查找关联的 {@link RuinResult}</li>
     *   <li>生成并保存 structure_set JSON 文件</li>
     *   <li>返回更新的 {@link RuinResult}（包含结构集 Key）</li>
     * </ol>
     *
     * @return 更新的 {@link RuinResult}（包含结构集 Key）
     * @throws IllegalStateException 如果找不到关联的 RuinResult
     * @throws RuntimeException      如果 JSON 文件写入失败
     */
public RuinResult build() {
        PasterDreamAPI.LOGGER.info("[StructureSetBuilder] ===== 开始构建结构集: {} =====", setName);
        PasterDreamAPI.LOGGER.info("[StructureSetBuilder]   配置: ruin={}, spacing={}, separation={}, salt={}, placementType={}",
                ruinName, spacing, separation, salt, placementType);

        // 1. 查找关联的 RuinResult
        RuinResult result = com.pasterdream.pasterdreammod.api.ruin.RuinAPI.getRuin(ruinName);
        if (result == null) {
            PasterDreamAPI.LOGGER.error("[StructureSetBuilder] ❌ 构建失败 [{}]: 找不到关联的结构 [{}]，请先调用 RuinBuilder.build()",
                    setName, ruinName);
            throw new IllegalStateException(
                    "StructureSetBuilder[" + setName + "]: 找不到关联的结构 [" + ruinName + "]，请先调用 RuinBuilder.build()"
            );
        }

        // 2. 配置 StructureSetGenerator
        String structureId = modId + ":" + ruinName;
        setGenerator.structureId(structureId);
        setGenerator.placementType(placementType);
        setGenerator.spacing(spacing);
        setGenerator.separation(separation);
        setGenerator.salt(salt);

        // 3. 生成 JSON 资源文件
        if (generateJsonFiles) {
            try {
                PasterDreamAPI.LOGGER.info("[StructureSetBuilder] 生成结构集 JSON: data/{}/worldgen/structure_set/{}.json", modId, setName);
                setGenerator.saveToFile(basePath);
                PasterDreamAPI.LOGGER.info("[StructureSetBuilder] ✅ 结构集 JSON 生成完成: {}", setName);
            } catch (IOException e) {
                PasterDreamAPI.LOGGER.error("[StructureSetBuilder] ❌ 无法生成结构集 JSON [{}]: {}", setName, e.getMessage(), e);
                throw new RuntimeException("StructureSetBuilder: 无法生成结构集资源文件 [" + setName + "]", e);
            }
        } else {
            PasterDreamAPI.LOGGER.info("[StructureSetBuilder] ⏭️ 跳过 JSON 文件生成: {} (generateJson=false)", setName);
        }

        // 4. 创建新的 RuinResult（包含结构集 Key）并缓存
        RuinResult updatedResult = result.withSetKey(setName, modId);
        PasterDreamAPI.LOGGER.debug("[StructureSetBuilder] 创建 RuinResult（含结构集 Key）: ruin={}, structureKey={}, setKey={}",
                ruinName, updatedResult.structureKey(), updatedResult.setKey());
        com.pasterdream.pasterdreammod.api.ruin.RuinAPI.cacheRuin(updatedResult);

        PasterDreamAPI.LOGGER.info("[StructureSetBuilder] ✅ 结构集构建完成: {} | spacing={}, separation={}, salt={}",
                setName, spacing, separation, salt);
        return updatedResult;
    }
}