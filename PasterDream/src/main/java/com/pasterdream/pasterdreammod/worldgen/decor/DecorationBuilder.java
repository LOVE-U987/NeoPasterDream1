package com.pasterdream.pasterdreammod.worldgen.decor;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.Objects;

/**
 * 装饰物流式 Builder —— 用于链式配置装饰物参数并注册
 * <p>
 * 提供流畅的链式调用 API，所有配置方法均返回 {@code this}，
 * 最终通过 {@link #register(String)} 方法完成注册并生成 ResourceKey。
 * <p>
 * 用法示例：
 * <pre>{@code
 * ResourceKey<PlacedFeature> myPillar = DecorationBuilder.create()
 *     .type(DecorationType.PILLAR)
 *     .body(Blocks.STONE)
 *     .top(Blocks.SNOW_BLOCK)
 *     .height(5, 12)
 *     .width(3, 1)
 *     .rarity(3)
 *     .step(GenerationStep.Decoration.SURFACE_STRUCTURES)
 *     .biome("minecraft:plains")
 *     .register("my_cool_pillar");
 * }</pre>
 */
public class DecorationBuilder {

    // ======================== 装饰物配置字段 ========================

    /** 装饰物类型（默认：柱形） */
    private DecorationType type = DecorationType.PILLAR;

    /** 主体方块提供器（必填，否则 register 时会抛异常） */
    private BlockStateProvider bodyBlock;

    /** 顶部/特殊方块提供器 */
    private BlockStateProvider topBlock;

    /** 表面嵌入晶体提供器 */
    private BlockStateProvider oreBlock;

    /** 碎片方块提供器 */
    private BlockStateProvider debrisBlock;

    /** 最小高度 */
    private int minHeight = 3;

    /** 最大高度 */
    private int maxHeight = 8;

    /** 底部宽度（柱形方柱用，方块数） */
    private int baseWidth = 2;

    /** 顶部宽度（柱形用，0=尖顶） */
    private int topWidth = 1;

    /** 底部半径（尖刺/圆形用） */
    private int baseRadius = 2;

    /** 顶部半径（尖刺用，0=尖顶） */
    private int topRadius = 0;

    /** 团块总方块数 */
    private int clusterSize = 50;

    /** 团块垂直半径 */
    private int yRadius = 4;

    /** 不规则度（0~1） */
    private float irregularity = 0.3f;

    /** 门框最小间距 */
    private int gateMinWidth = 4;

    /** 门框最大间距 */
    private int gateMaxWidth = 8;

    /** 门框柱半径 */
    private int pillarRadius = 2;

    /** 横梁厚度 */
    private int beamThickness = 2;

    /** 表面嵌入晶体的概率（0~1） */
    private float crystalChance = 0.0f;

    /** 碎片散落数量 */
    private int debrisCount = 0;

    /** 碎片散落半径 */
    private int debrisRadius = 0;

    /** 额外装饰概率（门框用） */
    private float decorationChance = 0.0f;

    /** 晶体是否仅放置于最顶层（顶部高度打断） */
    private boolean crystalOnlyOnTop = true;

    /** 是否启用悬空检测 */
    private boolean checkHang = true;

    /** 是否启用悬空填充 */
    private boolean fillHang = false;

    /** 是否启用占用检测 */
    private boolean occupiedCheck = true;

    /** 是否启用区域重叠检测 */
    private boolean regionCheck = false;

    /** 区域重叠阈值（0~1） */
    private float regionThreshold = 0.3f;

    /** 是否启用地下空间检测 */
    private boolean undergroundCheck = false;

    /** 是否需要水环境 */
    private boolean waterRequired = false;

    /** 尖刺倾斜程度（0=垂直，越大越倾斜） */
    private float tiltIntensity = 0.0f;

    /** 可被替换的方块判定条件（null=全可替换） */
    private BlockPredicate replaceable;

    /** 自定义生成器键（CUSTOM 类型专用） */
    private String customGeneratorKey = "";

    // ======================== 注册相关字段 ========================

    /** 目标群系 ID */
    private String targetBiome = "";

    /** 生成阶段（默认：顶层地表修改，优先于植被生成） */
    private GenerationStep.Decoration step = GenerationStep.Decoration.TOP_LAYER_MODIFICATION;

    /** 稀有度（rarity_filter 的 chance 值，默认每次都会尝试生成） */
    private int rarity = 1;

    /**
     * 私有构造方法 —— 使用 {@link #create()} 工厂方法创建实例
     */
    private DecorationBuilder() {}

    /**
     * 创建一个新的装饰物 Builder 实例
     *
     * @return 新的 DecorationBuilder 实例
     */
    public static DecorationBuilder create() {
        return new DecorationBuilder();
    }

    /**
     * 设置装饰物类型
     *
     * @param type 装饰物类型枚举值
     * @return this（支持链式调用）
     */
    public DecorationBuilder type(DecorationType type) {
        this.type = type;
        return this;
    }

    /**
     * 设置主体方块（自动转为 simple_state_provider）
     *
     * @param block 主体方块
     * @return this（支持链式调用）
     */
    public DecorationBuilder body(Block block) {
        this.bodyBlock = BlockStateProvider.simple(block);
        return this;
    }

    /**
     * 设置主体方块提供器
     *
     * @param provider 方块状态提供器（支持加权随机、噪声等）
     * @return this（支持链式调用）
     */
    public DecorationBuilder body(BlockStateProvider provider) {
        this.bodyBlock = provider;
        return this;
    }

    /**
     * 设置顶部方块（自动转为 simple_state_provider）
     *
     * @param block 顶部方块
     * @return this（支持链式调用）
     */
    public DecorationBuilder top(Block block) {
        this.topBlock = BlockStateProvider.simple(block);
        return this;
    }

    /**
     * 设置顶部方块提供器
     *
     * @param provider 方块状态提供器
     * @return this（支持链式调用）
     */
    public DecorationBuilder top(BlockStateProvider provider) {
        this.topBlock = provider;
        return this;
    }

    /**
     * 设置表面嵌入晶体
     *
     * @param chance   晶体生成概率（0~1）
     * @param provider 晶体方块提供器
     * @return this（支持链式调用）
     */
    public DecorationBuilder crystal(float chance, BlockStateProvider provider) {
        this.crystalChance = chance;
        this.oreBlock = provider;
        return this;
    }

    /**
     * 设置碎片方块（简单方块，自动转为 simple_state_provider）
     *
     * @param block  碎片方块
     * @param count  碎片数量
     * @param radius 碎片散布半径
     * @return this（支持链式调用）
     */
    public DecorationBuilder debris(Block block, int count, int radius) {
        this.debrisBlock = BlockStateProvider.simple(block);
        this.debrisCount = count;
        this.debrisRadius = radius;
        return this;
    }

    /**
     * 设置碎片方块提供器
     *
     * @param provider 碎片方块提供器
     * @param count    碎片数量
     * @param radius   碎片散布半径
     * @return this（支持链式调用）
     */
    public DecorationBuilder debris(BlockStateProvider provider, int count, int radius) {
        this.debrisBlock = provider;
        this.debrisCount = count;
        this.debrisRadius = radius;
        return this;
    }

    /**
     * 设置高度范围
     *
     * @param min 最小高度
     * @param max 最大高度
     * @return this（支持链式调用）
     */
    public DecorationBuilder height(int min, int max) {
        this.minHeight = min;
        this.maxHeight = max;
        return this;
    }

    /**
     * 设置宽度范围（柱形用，方形截面）
     *
     * @param base 底部宽度
     * @param top  顶部宽度
     * @return this（支持链式调用）
     */
    public DecorationBuilder width(int base, int top) {
        this.baseWidth = base;
        this.topWidth = top;
        return this;
    }

    /**
     * 设置半径范围（尖刺/圆形用）
     *
     * @param base 底部半径
     * @param top  顶部半径（0=尖顶）
     * @return this（支持链式调用）
     */
    public DecorationBuilder radius(int base, int top) {
        this.baseRadius = base;
        this.topRadius = top;
        return this;
    }

    /**
     * 设置团块方块总数
     *
     * @param size 团块包含的方块总数
     * @return this（支持链式调用）
     */
    public DecorationBuilder clusterSize(int size) {
        this.clusterSize = size;
        return this;
    }

    /**
     * 设置团块垂直半径
     *
     * @param radius 垂直方向半径
     * @return this（支持链式调用）
     */
    public DecorationBuilder yRadius(int radius) {
        this.yRadius = radius;
        return this;
    }

    /**
     * 设置不规则度
     *
     * @param irregularity 不规则度（0=规则球体，1=极度不规则）
     * @return this（支持链式调用）
     */
    public DecorationBuilder irregularity(float irregularity) {
        this.irregularity = irregularity;
        return this;
    }

    /**
     * 设置门框间距范围
     *
     * @param min 最小间距
     * @param max 最大间距
     * @return this（支持链式调用）
     */
    public DecorationBuilder gateWidth(int min, int max) {
        this.gateMinWidth = min;
        this.gateMaxWidth = max;
        return this;
    }

    /**
     * 设置门框柱半径
     *
     * @param radius 每根门框柱的半径
     * @return this（支持链式调用）
     */
    public DecorationBuilder pillarRadius(int radius) {
        this.pillarRadius = radius;
        return this;
    }

    /**
     * 设置横梁厚度
     *
     * @param thickness 顶部横梁的方块厚度
     * @return this（支持链式调用）
     */
    public DecorationBuilder beamThickness(int thickness) {
        this.beamThickness = thickness;
        return this;
    }

    /**
     * 设置额外装饰概率（门框顶部额外冰块等装饰用）
     *
     * @param chance 装饰生成概率（0~1）
     * @return this（支持链式调用）
     */
    public DecorationBuilder decorationChance(float chance) {
        this.decorationChance = chance;
        return this;
    }

    /**
     * 设置晶体是否仅放置于最顶层
     * <p>
     * 启用后晶体仅出现在结构的最顶层表面，充当「高度上限帽」的角色，
     * 不会再嵌入到中间层的方块之间。禁用后晶体可以在任意层的表面出现。
     *
     * @param crystalOnlyOnTop true=仅最顶层放置晶体，false=任意层表面均可
     * @return this（支持链式调用）
     */
    public DecorationBuilder crystalOnlyOnTop(boolean crystalOnlyOnTop) {
        this.crystalOnlyOnTop = crystalOnlyOnTop;
        return this;
    }

    /**
     * 启用/禁用悬空检测
     * <p>
     * 启用后会检测结构底部是否悬空，悬空则取消生成。
     *
     * @param check true=启用，false=禁用
     * @return this（支持链式调用）
     */
    public DecorationBuilder checkHang(boolean check) {
        this.checkHang = check;
        return this;
    }

    /**
     * 启用/禁用悬空填充
     * <p>
     * 启用后会在悬空结构下方填充方块（下坠+路径填充效果）。
     *
     * @param fill true=启用，false=禁用
     * @return this（支持链式调用）
     */
    public DecorationBuilder fillHang(boolean fill) {
        this.fillHang = fill;
        return this;
    }

    /**
     * 启用/禁用占用检测
     * <p>
     * 启用后会检测生成位置是否已被其他结构占用。
     *
     * @param check true=启用，false=禁用
     * @return this（支持链式调用）
     */
    public DecorationBuilder occupiedCheck(boolean check) {
        this.occupiedCheck = check;
        return this;
    }

    /**
     * 启用/禁用区域重叠检测
     * <p>
     * 启用后会检测生成区域是否与已有结构重叠。
     *
     * @param check     true=启用，false=禁用
     * @param threshold 重叠判定阈值（0~1）
     * @return this（支持链式调用）
     */
    public DecorationBuilder regionCheck(boolean check, float threshold) {
        this.regionCheck = check;
        this.regionThreshold = threshold;
        return this;
    }

    /**
     * 设置是否需要水环境
     * <p>
     * 启用后结构仅在水体中生成。
     *
     * @param required true=需要水环境，false=不限制
     * @return this（支持链式调用）
     */
    public DecorationBuilder waterRequired(boolean required) {
        this.waterRequired = required;
        return this;
    }

    /**
     * 设置尖刺倾斜程度
     * <p>
     * 让尖刺在生成时随机向某个方向倾斜。
     * 值越大，倾斜越明显。0=完全垂直。
     *
     * @param intensity 倾斜程度（0=垂直，建议 0.1~0.5）
     * @return this（支持链式调用）
     */
    public DecorationBuilder tilt(float intensity) {
        this.tiltIntensity = intensity;
        return this;
    }

    /**
     * 设置可替换方块判定条件
     * <p>
     * 定义哪些方块可以被本装饰物替换（如空气、草方块、雪等）。
     *
     * @param predicate 方块判定条件
     * @return this（支持链式调用）
     */
    public DecorationBuilder replaceable(BlockPredicate predicate) {
        this.replaceable = predicate;
        return this;
    }

    /**
     * 设置自定义生成器键（CUSTOM 类型专用）
     * <p>
     * 关联通过 {@link DecorationRegistry#registerCustomGenerator(String, ICustomDecorationGenerator)}
     * 注册的自定义生成器。
     *
     * @param key 生成器键名
     * @return this（支持链式调用）
     */
    public DecorationBuilder customGenerator(String key) {
        this.customGeneratorKey = key;
        return this;
    }

    /**
     * 设置目标群系
     *
     * @param biomeId 群系 ID（如 "minecraft:plains"）
     * @return this（支持链式调用）
     */
    public DecorationBuilder biome(String biomeId) {
        this.targetBiome = biomeId;
        return this;
    }

    /**
     * 设置稀有度
     * <p>
     * rarity_filter 的 chance 值，值为 N 表示 1/N 的概率生成。
     *
     * @param chance 稀有度值（越大越稀有）
     * @return this（支持链式调用）
     */
    public DecorationBuilder rarity(int chance) {
        this.rarity = chance;
        return this;
    }

    /**
     * 设置生成阶段
     *
     * @param step 生成阶段枚举值
     * @return this（支持链式调用）
     */
    public DecorationBuilder step(GenerationStep.Decoration step) {
        this.step = step;
        return this;
    }

    /**
     * 注册装饰物 —— 将当前配置构建为 {@link DecorationConfig} 并提交到 {@link DecorationRegistry}
     * <p>
     * 调用前必须至少通过 {@link #body(Block)} 或 {@link #body(BlockStateProvider)} 设置主体方块，
     * 并通过 {@link #biome(String)} 设置目标群系，否则会抛出异常。
     *
     * @param name 装饰物注册名称（对应 JSON 文件名和资源路径）
     * @return 已放置特征的 ResourceKey，用于在 BiomeModifier 中引用
     * @throws NullPointerException     如果主体方块未设置
     * @throws IllegalStateException    如果目标群系未设置或高度范围无效
     */
    public ResourceKey<PlacedFeature> register(String name) {
        Objects.requireNonNull(bodyBlock,
                "[DecorationBuilder] 装饰物 '" + name + "' 的主体方块(bodyBlock)不能为空！请调用 .body(Block) 或 .body(BlockStateProvider)。");

        // 校验目标群系（为空则装饰物不会被绑定到任何群系，属于常见漏配）
        if (targetBiome == null || targetBiome.isEmpty()) {
            throw new IllegalStateException(
                    "[DecorationBuilder] 装饰物 '" + name + "' 的目标群系(targetBiome)未设置！请调用 .biome(String)。"
            );
        }

        // 校验高度范围
        if (minHeight > maxHeight) {
            throw new IllegalStateException(
                    "[DecorationBuilder] 装饰物 '" + name + "' 的高度范围无效：minHeight(" + minHeight + ") > maxHeight(" + maxHeight + ")"
            );
        }

        // 如果未设置顶部方块，使用主体方块作为默认值
        if (topBlock == null) {
            topBlock = bodyBlock;
        }

        DecorationConfig config = new DecorationConfig(
                type,
                bodyBlock,
                topBlock,
                oreBlock,
                debrisBlock,
                minHeight,
                maxHeight,
                baseWidth,
                topWidth,
                baseRadius,
                topRadius,
                clusterSize,
                yRadius,
                irregularity,
                gateMinWidth,
                gateMaxWidth,
                pillarRadius,
                beamThickness,
                crystalChance,
                debrisCount,
                debrisRadius,
                decorationChance,
                crystalOnlyOnTop,
                checkHang,
                fillHang,
                occupiedCheck,
                regionCheck,
                regionThreshold,
                undergroundCheck,
                waterRequired,
                replaceable,
                tiltIntensity,
                customGeneratorKey
        );

        PasterDreamMod.LOGGER.debug("[DecorationBuilder] 构建装饰物配置: name={}, type={}", name, type);
        return DecorationRegistry.register(name, config, targetBiome, step, rarity);
    }
}