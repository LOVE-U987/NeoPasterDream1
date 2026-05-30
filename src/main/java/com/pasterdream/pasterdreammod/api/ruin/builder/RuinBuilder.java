package com.pasterdream.pasterdreammod.api.ruin.builder;

import com.google.gson.JsonObject;
import com.mojang.serialization.MapCodec;
import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.api.dimension.terrain.StructureTerrainNegotiator;
import com.pasterdream.pasterdreammod.api.dimension.terrain.TerrainRequirements;
import com.pasterdream.pasterdreammod.api.ruin.RuinResult;
import com.pasterdream.pasterdreammod.api.ruin.gen.StructureTypeGenerator;
import com.pasterdream.pasterdreammod.api.ruin.gen.TemplatePoolGenerator;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.io.IOException;

/**
 * 遗迹结构构建器 —— 采用 Builder 模式链式配置结构类型和 JSON 资源文件生成
 * <p>
 * 解决在 {@code PDStructures.java} 中手动注册 StructureType 和
 * 手写 worldgen/structure JSON 的繁琐问题。
 * 通过链式调用即可完成结构类型的注册和资源文件生成。
 * <p>
 * 使用示例：
 * <pre>{@code
 * RuinResult result = RuinAPI.createRuin("dyedream_ruins")
 *     .biomeTag("pasterdream:is_dyedream")
 *     .templatePool("pasterdream:dyedream_ruins_pool")
 *     .structureClass(DyedreamRuinsStructure.class)
 *     .codec(DyedreamRuinsStructure.CODEC)
 *     .terrainAdaptation(TerrainAdaptation.BEARD_THIN)
 *     .build();
 * }</pre>
 *
 * @see com.pasterdream.pasterdreammod.api.ruin.RuinAPI
 */
public class RuinBuilder {

    private final String modId;
    private final String structureName;
    private final DeferredRegister<StructureType<?>> registry;
    private final StructureTypeGenerator typeGenerator;
    private final TemplatePoolGenerator poolGenerator;

    /** 生物群系标签（如 "#pasterdream:is_dyedream"） */
    private String biomeTag;
    /** 起始模板池 ID（如 "pasterdream:dyedream_ruins_pool"） */
    private String templatePool;
    /** 结构类 */
    private Class<? extends Structure> structureClass;
    /** 结构 MapCodec */
    private MapCodec<? extends Structure> codec;
    /** 地形适应类型名称（如 "beard_thin", "beard_box", "none"） */
    private String terrainAdaptation;
    /** 生成阶段 */
    private String step;
    /** 结构扩展大小 */
    private int size;
    /** 起始高度 */
    private int startHeight;
    /** 是否自动生成 JSON 资源文件，默认为 true */
    private boolean generateJsonFiles;
    /** 资源文件基础路径，默认为 src/main/resources */
    private String basePath;
    /** 自定义额外 JSON 字段 */
    private JsonObject extraFields;
    /** 大型结构的地形需求（null 表示为普通结构） */
    private TerrainRequirements terrainRequirements;

    /**
     * 构造遗迹结构构建器
     *
     * @param modId         模组 ID
     * @param structureName 结构注册名称（如 "dyedream_ruins"）
     * @param registry      结构类型注册器
     */
    public RuinBuilder(String modId, String structureName, DeferredRegister<StructureType<?>> registry) {
        this.modId = modId;
        this.structureName = structureName;
        this.registry = registry;
        this.typeGenerator = new StructureTypeGenerator(modId, structureName);
        this.poolGenerator = new TemplatePoolGenerator(modId, structureName + "_pool");
        this.step = "surface_structures";
        this.size = 7;
        this.startHeight = 0;
        this.generateJsonFiles = true;
        this.basePath = "src/main/resources";
    }

    // ======================== 配置方法（链式调用） ========================

    /**
     * 设置生物群系标签
     * <p>
     * 指定结构可以在哪些生物群系中生成。
     *
     * @param biomeTagId 生物群系标签 ID（如 "pasterdream:is_dyedream" 或 "#minecraft:is_overworld"）
     * @return 当前构建器实例
     */
    public RuinBuilder biomeTag(String biomeTagId) {
        this.biomeTag = biomeTagId.startsWith("#") ? biomeTagId : "#" + biomeTagId;
        PasterDreamMod.LOGGER.debug("[RuinBuilder] {} → biomeTag={}", structureName, this.biomeTag);
        return this;
    }

    /**
     * 设置起始模板池
     * <p>
     * 指定结构起始生成位置的模板池（需要在 worldgen/template_pool/ 下定义）。
     *
     * @param poolId 模板池 ID（如 "pasterdream:dyedream_ruins_pool"）
     * @return 当前构建器实例
     */
    public RuinBuilder templatePool(String poolId) {
        this.templatePool = poolId;
        PasterDreamMod.LOGGER.debug("[RuinBuilder] {} → templatePool={}", structureName, poolId);
        return this;
    }

    /**
     * 设置结构类
     * <p>
     * 指定自定义 Structure 子类，用于注册 StructureType。
     *
     * @param structureClass Structure 子类的 Class 对象
     * @return 当前构建器实例
     */
    public RuinBuilder structureClass(Class<? extends Structure> structureClass) {
        this.structureClass = structureClass;
        PasterDreamMod.LOGGER.debug("[RuinBuilder] {} → structureClass={}", structureName, structureClass.getSimpleName());
        return this;
    }

    /**
     * 设置结构 MapCodec
     * <p>
     * 指定 Structure 子类的 CODEC（MapCodec），用于序列化/反序列化。
     *
     * @param codec 结构的 MapCodec
     * @return 当前构建器实例
     */
    public RuinBuilder codec(MapCodec<? extends Structure> codec) {
        this.codec = codec;
        PasterDreamMod.LOGGER.debug("[RuinBuilder] {} → codec=已配置", structureName);
        return this;
    }

    /**
     * 设置地形适应类型
     * <p>
     * 控制结构生成时对地形的适应方式。
     *
     * @param terrainAdaptation 地形适应类型（如 "beard_thin", "beard_box", "none"）
     * @return 当前构建器实例
     */
    public RuinBuilder terrainAdaptation(String terrainAdaptation) {
        this.terrainAdaptation = terrainAdaptation;
        PasterDreamMod.LOGGER.debug("[RuinBuilder] {} → terrainAdaptation={}", structureName, terrainAdaptation);
        return this;
    }

    /**
     * 设置生成阶段
     *
     * @param step 生成阶段（如 "surface_structures", "underground_structures"）
     * @return 当前构建器实例
     */
    public RuinBuilder step(String step) {
        this.step = step;
        PasterDreamMod.LOGGER.debug("[RuinBuilder] {} → step={}", structureName, step);
        return this;
    }

    /**
     * 设置结构扩展大小
     * <p>
     * 控制结构从起始位置向外扩展的范围。
     *
     * @param size 扩展大小
     * @return 当前构建器实例
     */
    public RuinBuilder size(int size) {
        this.size = size;
        PasterDreamMod.LOGGER.debug("[RuinBuilder] {} → size={}", structureName, size);
        return this;
    }

    /**
     * 设置起始生成高度
     *
     * @param startHeight 起始高度值
     * @return 当前构建器实例
     */
    public RuinBuilder startHeight(int startHeight) {
        this.startHeight = startHeight;
        PasterDreamMod.LOGGER.debug("[RuinBuilder] {} → startHeight={}", structureName, startHeight);
        return this;
    }

    /**
     * 设置自定义额外 JSON 字段
     * <p>
     * 适用于需要在结构 JSON 中添加额外自定义字段的场景。
     *
     * @param extraFields 额外的 JSON 对象字段
     * @return 当前构建器实例
     */
    public RuinBuilder extraFields(JsonObject extraFields) {
        this.extraFields = extraFields;
        PasterDreamMod.LOGGER.debug("[RuinBuilder] {} → extraFields=已配置 ({} 个字段)", structureName, extraFields.size());
        return this;
    }

    /**
     * 是否自动生成 JSON 资源文件
     * <p>
     * 默认为 true，会在 {@link #build()} 时自动生成
     * structure 和 template_pool 的 JSON 文件。
     *
     * @param generate 是否生成 JSON
     * @return 当前构建器实例
     */
    public RuinBuilder generateJson(boolean generate) {
        this.generateJsonFiles = generate;
        PasterDreamMod.LOGGER.debug("[RuinBuilder] {} → generateJson={}", structureName, generate);
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
    public RuinBuilder basePath(String basePath) {
        this.basePath = basePath;
        PasterDreamMod.LOGGER.debug("[RuinBuilder] {} → basePath={}", structureName, basePath);
        return this;
    }

    // ======================== 大型结构支持 ========================

    /**
     * 将此结构标记为大型结构，并指定完整的地形需求
     * <p>
     * 启用与 DimensionAPI 的地形协商机制：
     * 维度会在区块生成时尝试为结构调整地形，
     * 避免产生明显断层，并返回评估结果。
     *
     * @param reqs 地形需求
     * @return 当前构建器实例
     */
    public RuinBuilder largeStructure(TerrainRequirements reqs) {
        this.terrainRequirements = reqs;
        PasterDreamMod.LOGGER.info("[RuinBuilder] 🏗️ 标记为大型结构: {} | 需求={}", structureName, reqs);
        return this;
    }

    /**
     * 快捷方法：将此结构标记为大型结构并配置平地平台
     * <p>
     * 自动计算渐变半径 = max(5, flatRadius / 3)，并启用地形协商。
     *
     * @param flatRadius 需要的平地半径（格）
     * @return 当前构建器实例
     */
    public RuinBuilder withTerrainPlatform(int flatRadius) {
        TerrainRequirements reqs = TerrainRequirements.builder()
                .requiredFlatRadius(flatRadius)
                .terrainBlendRadius(Math.max(5, flatRadius / 3))
                .build();
        return largeStructure(reqs);
    }

    // ======================== 构建 & 生成 ========================

    /**
     * 执行构建，完成结构类型注册和资源文件生成
     * <p>
     * 完成以下工作：
     * <ol>
     *   <li>注册 StructureType（通过 DeferredRegister）</li>
     *   <li>生成并保存 structure JSON 文件</li>
     *   <li>返回 {@link RuinResult} 包含所有 ResourceKey</li>
     * </ol>
     *
     * @return {@link RuinResult} 包含结构相关的所有 ResourceKey
     * @throws IllegalStateException 如果 codec 或 structureClass 未设置
     * @throws RuntimeException      如果 JSON 文件写入失败
     */
    public RuinResult build() {
        PasterDreamMod.LOGGER.info("[RuinBuilder] ===== 开始构建遗迹结构: {} =====", structureName);
        PasterDreamMod.LOGGER.info("[RuinBuilder]   配置: biomeTag={}, templatePool={}, step={}, terrainAdaptation={}, size={}, startHeight={}",
                biomeTag, templatePool, step, terrainAdaptation, size, startHeight);

        // 验证必要参数
        if (codec == null) {
            PasterDreamMod.LOGGER.error("[RuinBuilder] ❌ 构建失败 [{}]: codec 未设置", structureName);
            throw new IllegalStateException("RuinBuilder[" + structureName + "]: codec 未设置，请调用 .codec()");
        }
        if (structureClass == null) {
            PasterDreamMod.LOGGER.warn("[RuinBuilder] ⚠️ structureClass 未设置，StructureType 仍会注册但类型安全无法保证");
        }

        // 1. 注册 StructureType（通过 DeferredRegister 懒注册）
        PasterDreamMod.LOGGER.debug("[RuinBuilder] 注册 StructureType: {}.{}", modId, structureName);
        @SuppressWarnings("unchecked")
        StructureType<Structure> type = () -> (MapCodec<Structure>) codec;
        registry.register(structureName, () -> type);
        PasterDreamMod.LOGGER.info("[RuinBuilder] ✅ 已注册 StructureType: {}.{}", modId, structureName);

        // 2. 配置并生成 JSON 资源文件
        if (generateJsonFiles) {
            configureGenerators();
            try {
                PasterDreamMod.LOGGER.info("[RuinBuilder] ===== 开始生成结构资源文件: {} =====", structureName);

                PasterDreamMod.LOGGER.debug("[RuinBuilder] 生成 structure JSON → data/{}/worldgen/structure/{}.json", modId, structureName);
                typeGenerator.saveToFile(basePath);

                PasterDreamMod.LOGGER.debug("[RuinBuilder] 生成 template_pool JSON → data/{}/worldgen/template_pool/{}_pool.json", modId, structureName);
                poolGenerator.saveToFile(basePath);

                PasterDreamMod.LOGGER.info("[RuinBuilder] ✅ 结构资源文件生成完成: {}", structureName);
            } catch (IOException e) {
                PasterDreamMod.LOGGER.error("[RuinBuilder] ❌ 无法生成结构资源文件 [{}]: {}", structureName, e.getMessage(), e);
                throw new RuntimeException("RuinBuilder: 无法生成结构资源文件 [" + structureName + "]", e);
            }
        } else {
            PasterDreamMod.LOGGER.info("[RuinBuilder] ⏭️ 跳过 JSON 文件生成: {} (generateJson=false)", structureName);
        }

        // 3. 创建并缓存 RuinResult
        RuinResult result = (terrainRequirements != null)
                ? RuinResult.of(modId, structureName, terrainRequirements)
                : RuinResult.of(modId, structureName);
        PasterDreamMod.LOGGER.debug("[RuinBuilder] 创建 RuinResult: typeKey={}, structureKey={}", result.typeKey(), result.structureKey());

        // 如果是大型结构，注册到地形协商器
        if (terrainRequirements != null) {
            StructureTerrainNegotiator negotiator = StructureTerrainNegotiator.getInstance();
            negotiator.registerLargeStructure(structureName, modId, terrainRequirements);
            PasterDreamMod.LOGGER.info("[RuinBuilder] 🔗 已注册大型结构到地形协商器: {}", structureName);
        }

        com.pasterdream.pasterdreammod.api.ruin.RuinAPI.cacheRuin(result);

        PasterDreamMod.LOGGER.info("[RuinBuilder] ✅ 遗迹结构构建完成: {} | result={}", structureName, result);
        return result;
    }

    /**
     * 配置所有内部生成器
     */
    private void configureGenerators() {
        PasterDreamMod.LOGGER.debug("[RuinBuilder] 配置内部生成器...");

        // 配置 StructureTypeGenerator
        String typeId = modId + ":" + structureName;
        typeGenerator.type(typeId);
        if (biomeTag != null) {
            typeGenerator.biomes(biomeTag);
        }
        typeGenerator.step(step);
        if (terrainAdaptation != null) {
            typeGenerator.terrainAdaptation(terrainAdaptation);
        }
        if (templatePool != null) {
            typeGenerator.startPool(templatePool);
        }
        typeGenerator.size(size);
        typeGenerator.startHeight(startHeight);
        if (extraFields != null) {
            typeGenerator.extraFields(extraFields);
        }

        PasterDreamMod.LOGGER.debug("[RuinBuilder] 生成器配置完成: typeId={}", typeId);
    }
}