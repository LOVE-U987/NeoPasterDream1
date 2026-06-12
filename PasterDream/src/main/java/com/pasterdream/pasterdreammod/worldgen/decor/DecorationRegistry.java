package com.pasterdream.pasterdreammod.worldgen.decor;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.registries.DeferredRegister;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 装饰物注册管理中心 —— 统一管理所有装饰物特征的注册与条目追踪
 * <p>
 * 负责：
 * <ul>
 *   <li>使用 DeferredRegister 注册 {@link GenericDecorationFeature} 到特征注册表</li>
 *   <li>管理所有已注册的装饰物条目列表</li>
 *   <li>提供 register() 方法供 {@link DecorationBuilder} 调用</li>
 *   <li>JSON 数据文件生成委托给 {@link DecorationJsonGenerator}</li>
 * </ul>
 *
 * @see DecorationBuilder 装饰物构建器（入口 API）
 * @see DecorationJsonGenerator JSON 数据文件生成器
 */
public class DecorationRegistry {

    /**
     * 特征延迟注册器 —— 用于注册 {@link GenericDecorationFeature}
     */
    public static final DeferredRegister<Feature<?>> FEATURES =
            DeferredRegister.create(Registries.FEATURE, PasterDreamMod.MOD_ID);

    /**
     * 已注册的装饰物条目列表（不可变视图对外暴露）
     */
    private static final List<DecorationEntry> REGISTERED = new ArrayList<>();

    /** 已注册条目的不可变外部视图 */
    private static final List<DecorationEntry> REGISTERED_VIEW = Collections.unmodifiableList(REGISTERED);

    /** 自定义装饰物生成器注册表 */
    private static final Map<String, ICustomDecorationGenerator> CUSTOM_GENERATORS = new HashMap<>();

    /**
     * 注册自定义装饰物生成器
     *
     * @param key       生成器键名（对应配置中的 customGeneratorKey）
     * @param generator 生成器实现
     */
    public static void registerCustomGenerator(String key, ICustomDecorationGenerator generator) {
        CUSTOM_GENERATORS.put(key, generator);
        PasterDreamMod.LOGGER.info("[DecorationRegistry] 已注册自定义生成器: {}", key);
    }

    /**
     * 获取自定义装饰物生成器
     *
     * @param key 生成器键名
     * @return 生成器实例，未找到则返回 null
     */
    @Nullable
    public static ICustomDecorationGenerator getCustomGenerator(String key) {
        return CUSTOM_GENERATORS.get(key);
    }

    /*
     * 静态初始化块：将 GenericDecorationFeature 注册到 FEATURES 注册表中
     */
    static {
        FEATURES.register("generic_decor", GenericDecorationFeature::new);
    }

    /**
     * 装饰物条目记录 —— 存储一个已注册装饰物的完整信息
     *
     * @param name                 装饰物名称（也是 JSON 文件名）
     * @param type                 装饰物类型
     * @param config               装饰物完整配置
     * @param configuredFeatureKey configured_feature 的资源键
     * @param placedFeatureKey     placed_feature 的资源键
     * @param targetBiome          目标群系 ID
     * @param step                 生成阶段
     * @param rarity               稀有度（概率为 1/rarity）
     */
    public record DecorationEntry(
            String name,
            DecorationType type,
            DecorationConfig config,
            ResourceKey<ConfiguredFeature<?, ?>> configuredFeatureKey,
            ResourceKey<PlacedFeature> placedFeatureKey,
            String targetBiome,
            GenerationStep.Decoration step,
            int rarity
    ) {}

    /**
     * 注册一个新的装饰物
     * <p>
     * 创建 configured_feature 和 placed_feature 的 ResourceKey，
     * 并将装饰物条目存储到内部列表中。
     *
     * @param name        装饰物名称（对应 JSON 文件名和资源路径）
     * @param config      装饰物配置对象
     * @param targetBiome 目标群系 ID（如 "minecraft:plains"）
     * @param step        生成阶段（如 SURFACE_STRUCTURES、VEGETAL_DECORATION）
     * @param rarity      稀有度（rarity_filter 的 chance 值，越大越稀有）
     * @return 已放置特征的 ResourceKey，用于在 BiomeModifier 中引用
     */
    public static ResourceKey<PlacedFeature> register(String name, DecorationConfig config,
                                                       String targetBiome, GenerationStep.Decoration step, int rarity) {
        ResourceKey<ConfiguredFeature<?, ?>> configuredKey = ResourceKey.create(
                Registries.CONFIGURED_FEATURE,
                ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, name)
        );
        ResourceKey<PlacedFeature> placedKey = ResourceKey.create(
                Registries.PLACED_FEATURE,
                ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, name)
        );

        DecorationEntry entry = new DecorationEntry(
                name, config.type(), config, configuredKey, placedKey,
                targetBiome, step, rarity
        );
        REGISTERED.add(entry);

        PasterDreamMod.LOGGER.info("[DecorationRegistry] 已注册装饰物: {} (类型={}, 群系={}, 稀有度={})",
                name, config.type(), targetBiome, rarity);

        return placedKey;
    }

    /**
     * 获取所有已注册的装饰物条目列表
     *
     * @return 只读的装饰物条目列表
     */
    public static List<DecorationEntry> getAllDecorations() {
        return REGISTERED_VIEW;
    }

    /**
     * 自动生成所有已注册装饰物的 JSON 数据文件
     * <p>
     * 委托给 {@link DecorationJsonGenerator#generateAllJson()} 执行。
     * 保持此方法为过渡兼容，新代码应直接调用 {@code DecorationJsonGenerator.generateAllJson()}。
     */
    public static void generateAllJson() {
        DecorationJsonGenerator.generateAllJson();
    }

    /**
     * 自动生成所有已注册装饰物的 biome_modifier JSON 文件
     * <p>
     * 委托给 {@link DecorationJsonGenerator#generateBiomeModifierJson()} 执行。
     * 保持此方法为过渡兼容，新代码应直接调用 {@code DecorationJsonGenerator.generateBiomeModifierJson()}。
     */
    public static void generateBiomeModifierJson() {
        DecorationJsonGenerator.generateBiomeModifierJson();
    }

    /**
     * 清除所有已注册的装饰物条目
     * <p>
     * 主要用于测试或重新加载场景，正常使用无需调用。
     */
    public static void clear() {
        REGISTERED.clear();
        PasterDreamMod.LOGGER.debug("[DecorationRegistry] 已清除所有装饰物条目");
    }
}