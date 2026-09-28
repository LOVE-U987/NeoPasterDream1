package com.pasterdream.pasterdreammod.api.client.shading;

import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
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
     * 根据天空亮度插值获取群系雾色
     * <p>
     * 入参即 vanilla {@code FogRenderer} 传给
     * {@code DimensionSpecialEffects#getBrightnessDependentFogColor} 的亮度因子
     * {@code clamp(cos(timeOfDay * 2π) * 2 + 0.5, 0, 1)}：0 = 午夜，0.5 = 地平线
     * （黄昏/黎明），1 = 正午。注意它不是 -1 ~ 1 的太阳高度。
     * <p>
     * 插值逻辑：
     * <ul>
     *   <li>亮度 >= 0.5：在黄昏与日间之间插值（黄昏 → 日间）</li>
     *   <li>亮度 &lt; 0.5：在夜色与黄昏之间插值（夜色 → 黄昏）</li>
     * </ul>
     *
     * @param biome      群系 Key
     * @param brightness 天空亮度（0 = 午夜，0.5 = 地平线，1 = 正午）
     * @return 插值后的雾色
     */
    public static Vec3 interpolateColor(ResourceKey<Biome> biome, float brightness) {
        BiomeShadingEntry entry = get(biome);
        return interpolateTriColor(entry.dayColor(), entry.sunsetColor(), entry.nightColor(), brightness);
    }

    /**
     * 三色插值：根据天空亮度在夜色/黄昏/日间颜色之间平滑过渡
     *
     * @param day        日间雾色
     * @param sunset     黄昏雾色
     * @param night      夜色雾色
     * @param brightness 天空亮度（0 = 午夜，0.5 = 地平线，1 = 正午）
     * @return 插值后的雾色
     */
    private static Vec3 interpolateTriColor(Vec3 day, Vec3 sunset, Vec3 night, float brightness) {
        float b = Mth.clamp(brightness, 0.0F, 1.0F);
        if (b >= 0.5F) {
            // 地平线 → 正午
            return lerp(sunset, day, (b - 0.5F) * 2.0F);
        }
        // 午夜 → 地平线
        return lerp(night, sunset, b * 2.0F);
    }

    /**
     * 线性插值：t = 0 取 from，t = 1 取 to
     *
     * @param from 起点颜色
     * @param to   终点颜色
     * @param t    插值系数（0 ~ 1）
     * @return 插值后的颜色
     */
    private static Vec3 lerp(Vec3 from, Vec3 to, float t) {
        return new Vec3(
                from.x + (to.x - from.x) * t,
                from.y + (to.y - from.y) * t,
                from.z + (to.z - from.z) * t
        );
    }
}
