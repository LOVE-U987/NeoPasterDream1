package com.pasterdream.pasterdreammod.api.client.shading;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;

/**
 * 生物群系着色注册表 —— 管理全部群系着色条目
 * <p>
 * 两条来源：
 * <ul>
 *   <li><b>代码注册</b>：{@link #register}，由主模块在客户端初始化时调用</li>
 *   <li><b>数据包加载</b>：{@link #replaceDataEntries}，由数据包重载监听器填充（未来扩展）</li>
 * </ul>
 * 渲染器通过 {@link #interpolateColor} 获取插值后的雾色。
 *
 * @see BiomeShadingAPI
 */
public final class BiomeShadingRegistry {

    /** 代码注册的条目 */
    private static final Map<ResourceKey<Biome>, BiomeShadingEntry> ENTRIES = new HashMap<>();

    /** 数据包注册的条目（重载时整体替换，优先级高于代码注册） */
    private static final Map<ResourceKey<Biome>, BiomeShadingEntry> DATA_ENTRIES = new HashMap<>();

    /** 默认日间雾色 */
    private static final Vec3 DEFAULT_DAY = new Vec3(1.0, 0.71, 0.85);

    /** 默认黄昏雾色 */
    private static final Vec3 DEFAULT_SUNSET = new Vec3(1.0, 0.56, 0.64);

    /** 默认夜间雾色 */
    private static final Vec3 DEFAULT_NIGHT = new Vec3(0.29, 0.10, 0.36);

    private BiomeShadingRegistry() {
        throw new UnsupportedOperationException("BiomeShadingRegistry is a static facade class, not instantiable");
    }

    /**
     * 代码注册一条群系着色（优先级低于数据包）
     *
     * @param entry 群系着色条目
     */
    public static void register(BiomeShadingEntry entry) {
        ENTRIES.put(entry.biome(), entry);
    }

    /**
     * 整体替换数据包条目（由数据包重载监听器调用）
     *
     * @param entries 新数据条目
     */
    public static void replaceDataEntries(Map<ResourceKey<Biome>, BiomeShadingEntry> entries) {
        DATA_ENTRIES.clear();
        DATA_ENTRIES.putAll(entries);
    }

    /**
     * 获取指定群系的着色配置
     * <p>
     * 优先级：数据包 > 代码注册 > 默认值
     *
     * @param biome 群系 Key
     * @return 着色条目
     */
    public static BiomeShadingEntry get(ResourceKey<Biome> biome) {
        BiomeShadingEntry entry = DATA_ENTRIES.get(biome);
        if (entry != null) {
            return entry;
        }
        entry = ENTRIES.get(biome);
        if (entry != null) {
            return entry;
        }
        // fallback to defaults
        return new BiomeShadingEntry(biome, DEFAULT_DAY, DEFAULT_SUNSET, DEFAULT_NIGHT);
    }

    /**
     * 根据太阳高度插值获取群系雾色
     * <p>
     * 插值逻辑：
     * <ul>
     *   <li>太阳高度 > 0：在黄昏与日间之间插值</li>
     *   <li>太阳高度 <= 0：在黄昏与夜间之间插值</li>
     * </ul>
     *
     * @param biome     群系 Key
     * @param sunHeight 太阳高度（-1 ~ 1），负值=夜晚，0=地平线，正值=白天
     * @return 插值后的雾色
     */
    public static Vec3 interpolateColor(ResourceKey<Biome> biome, float sunHeight) {
        BiomeShadingEntry entry = get(biome);
        return interpolateTriColor(entry.dayColor(), entry.sunsetColor(), entry.nightColor(), sunHeight);
    }

    /**
     * 三色插值：根据太阳高度在日间/黄昏/夜间颜色之间平滑过渡
     *
     * @param day       日间雾色
     * @param sunset    黄昏雾色
     * @param night     夜间雾色
     * @param sunHeight 太阳高度（-1 ~ 1）
     * @return 插值后的雾色
     */
    private static Vec3 interpolateTriColor(Vec3 day, Vec3 sunset, Vec3 night, float sunHeight) {
        if (sunHeight > 0.0f) {
            float t = Math.min(sunHeight * 6.0f, 1.0f);
            return new Vec3(
                    sunset.x + (day.x - sunset.x) * t,
                    sunset.y + (day.y - sunset.y) * t,
                    sunset.z + (day.z - sunset.z) * t
            );
        } else {
            float t = Math.min(-sunHeight * 5.0f, 1.0f);
            return new Vec3(
                    sunset.x + (night.x - sunset.x) * t,
                    sunset.y + (night.y - sunset.y) * t,
                    sunset.z + (night.z - sunset.z) * t
            );
        }
    }
}
