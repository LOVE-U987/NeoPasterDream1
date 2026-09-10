package com.pasterdream.pasterdreammod.api.client.shading;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.Vec3;

/**
 * 生物群系着色 API 门面 —— 代码注册自定义群系雾色的统一入口
 * <p>
 * 采用 Facade 模式，屏蔽 {@link BiomeShadingRegistry} 细节。数据驱动的
 * 着色配置（未来扩展）由主模块的数据包重载监听器自动加载
 * {@code data/<namespace>/shading/*.json}，一般无需代码注册；
 * 本门面供需要程序化控制群系雾色的场景使用。
 * <p>
 * 使用示例：
 * <pre>{@code
 * // 在客户端初始化时注册默认群系着色
 * BiomeShadingAPI.register(new BiomeShadingEntry(
 *     PDBiomes.DYEDREAM_PLAINS,
 *     new Vec3(1.0, 0.71, 0.85),   // 日间
 *     new Vec3(1.0, 0.56, 0.64),   // 黄昏
 *     new Vec3(0.29, 0.10, 0.36)   // 夜间
 * ));
 *
 * // 在 getBrightnessDependentFogColor 中获取插值雾色
 * Vec3 fogColor = BiomeShadingAPI.interpolateColor(biome, sunHeight);
 * }</pre>
 *
 * @see BiomeShadingEntry
 * @see BiomeShadingRegistry
 */
public final class BiomeShadingAPI {

    private BiomeShadingAPI() {
    }

    /**
     * 注册一条群系着色配置
     *
     * @param entry 群系着色条目
     */
    public static void register(BiomeShadingEntry entry) {
        BiomeShadingRegistry.register(entry);
    }

    /**
     * 根据太阳高度插值获取群系雾色
     *
     * @param biome     群系 Key
     * @param sunHeight 太阳高度（-1 ~ 1），负值=夜晚，0=地平线，正值=白天
     * @return 插值后的雾色
     */
    public static Vec3 interpolateColor(ResourceKey<Biome> biome, float sunHeight) {
        return BiomeShadingRegistry.interpolateColor(biome, sunHeight);
    }

    /**
     * 获取指定群系的着色配置
     *
     * @param biome 群系 Key
     * @return 着色条目
     */
    public static BiomeShadingEntry get(ResourceKey<Biome> biome) {
        return BiomeShadingRegistry.get(biome);
    }
}
