package com.pasterdream.pasterdreammod.api.config;

import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 附属模组配置统一注册表。
 * <p>
 * 位于 PasterDreamAPI，供 PasterDreamSanity / PasterDreamMeltDream / PasterDreamSpells
 * 等附属模组在初始化时注册自己的 {@link ModConfig} 引用与可在配置界面中展示的条目。
 * PasterDream 主模组的配置界面通过本注册表读取并展示这些选项，无需对每个附属模组
 * 产生硬编码依赖，从而支持 Gradle 开关动态启用/禁用模块。
 *
 * @author PasterDream
 */
public final class PDAddonConfigRegistry {

    /** 配置项类型 */
    public enum EntryType {
        /** 布尔开关 */
        BOOLEAN,
        /** 整数输入 */
        INTEGER,
        /** 双精度浮点输入 */
        DOUBLE
    }

    /**
     * 配置界面条目描述。
     *
     * @param modId         所属模组 ID
     * @param categoryKey   配置界面分类键（由主模组映射为 {@code ConfigCategory}）
     * @param type          值类型
     * @param value         配置值引用（{@link ModConfigSpec.ConfigValue}）
     * @param min           数值最小值（布尔条目忽略）
     * @param max           数值最大值（布尔条目忽略）
     * @param translationKey 翻译键后缀，用于生成 {@code gui.pasterdream.config.<key>}；
     *                       为 null 时由主模组界面根据配置 path 自动生成
     */
    public record Entry(
            String modId,
            String categoryKey,
            EntryType type,
            ModConfigSpec.ConfigValue<?> value,
            double min,
            double max,
            @Nullable String translationKey
    ) {
    }

    /** 已注册的 COMMON 配置 ModConfig 引用：modId -> ModConfig */
    private static final Map<String, ModConfig> COMMON_CONFIGS = new HashMap<>();

    /** 已注册的配置界面条目 */
    private static final List<Entry> ENTRIES = new ArrayList<>();

    private PDAddonConfigRegistry() {
        throw new UnsupportedOperationException("PDAddonConfigRegistry 是静态注册表，不可实例化");
    }

    /**
     * 注册附属模组的 COMMON 类型 ModConfig 引用，用于配置界面保存时持久化到 TOML。
     *
     * @param modId  模组 ID
     * @param config 已注册的 COMMON 配置对象
     */
    public static void registerCommonConfig(String modId, ModConfig config) {
        COMMON_CONFIGS.put(modId, config);
    }

    /**
     * 获取已注册的 COMMON 类型 ModConfig。
     *
     * @param modId 模组 ID
     * @return ModConfig 引用；未注册时返回 null
     */
    @Nullable
    public static ModConfig getCommonConfig(String modId) {
        return COMMON_CONFIGS.get(modId);
    }

    /**
     * 注册一个布尔类型的配置界面条目。
     *
     * @param modId       模组 ID
     * @param categoryKey 配置界面分类键
     * @param value       布尔配置值
     */
    public static void registerBooleanEntry(String modId, String categoryKey, ModConfigSpec.ConfigValue<Boolean> value) {
        registerBooleanEntry(modId, categoryKey, value, null);
    }

    /**
     * 注册一个布尔类型的配置界面条目，并指定翻译键。
     *
     * @param modId          模组 ID
     * @param categoryKey    配置界面分类键
     * @param value          布尔配置值
     * @param translationKey 翻译键后缀；为 null 时由主模组界面自动生成
     */
    public static void registerBooleanEntry(String modId, String categoryKey, ModConfigSpec.ConfigValue<Boolean> value, @Nullable String translationKey) {
        ENTRIES.add(new Entry(modId, categoryKey, EntryType.BOOLEAN, value, 0.0D, 0.0D, translationKey));
    }

    /**
     * 注册一个整数类型的配置界面条目。
     *
     * @param modId       模组 ID
     * @param categoryKey 配置界面分类键
     * @param value       整数配置值
     * @param min         最小值
     * @param max         最大值
     */
    public static void registerIntegerEntry(String modId, String categoryKey, ModConfigSpec.ConfigValue<Integer> value, int min, int max) {
        registerIntegerEntry(modId, categoryKey, value, min, max, null);
    }

    /**
     * 注册一个整数类型的配置界面条目，并指定翻译键。
     *
     * @param modId          模组 ID
     * @param categoryKey    配置界面分类键
     * @param value          整数配置值
     * @param min            最小值
     * @param max            最大值
     * @param translationKey 翻译键后缀；为 null 时由主模组界面自动生成
     */
    public static void registerIntegerEntry(String modId, String categoryKey, ModConfigSpec.ConfigValue<Integer> value, int min, int max, @Nullable String translationKey) {
        ENTRIES.add(new Entry(modId, categoryKey, EntryType.INTEGER, value, min, max, translationKey));
    }

    /**
     * 注册一个双精度浮点类型的配置界面条目。
     *
     * @param modId       模组 ID
     * @param categoryKey 配置界面分类键
     * @param value       双精度浮点配置值
     * @param min         最小值
     * @param max         最大值
     */
    public static void registerDoubleEntry(String modId, String categoryKey, ModConfigSpec.ConfigValue<Double> value, double min, double max) {
        registerDoubleEntry(modId, categoryKey, value, min, max, null);
    }

    /**
     * 注册一个双精度浮点类型的配置界面条目，并指定翻译键。
     *
     * @param modId          模组 ID
     * @param categoryKey    配置界面分类键
     * @param value          双精度浮点配置值
     * @param min            最小值
     * @param max            最大值
     * @param translationKey 翻译键后缀；为 null 时由主模组界面自动生成
     */
    public static void registerDoubleEntry(String modId, String categoryKey, ModConfigSpec.ConfigValue<Double> value, double min, double max, @Nullable String translationKey) {
        ENTRIES.add(new Entry(modId, categoryKey, EntryType.DOUBLE, value, min, max, translationKey));
    }

    /**
     * 获取所有已注册的配置界面条目。
     *
     * @return 不可修改的条目列表
     */
    public static List<Entry> getEntries() {
        return Collections.unmodifiableList(ENTRIES);
    }

    /**
     * 清除所有已注册数据。主要用于测试场景，生产代码不应调用。
     */
    public static void clear() {
        COMMON_CONFIGS.clear();
        ENTRIES.clear();
    }
}
