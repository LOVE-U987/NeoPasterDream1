package com.pasterdream.pasterdreammod.client;

import com.pasterdream.pasterdreammod.api.client.shading.BiomeShadingAPI;
import com.pasterdream.pasterdreammod.api.client.shading.BiomeShadingEntry;
import com.pasterdream.pasterdreammod.registry.PDBiomes;
import net.minecraft.world.phys.Vec3;

/**
 * 生物群系着色默认注册 —— 在客户端初始化时注册全部群系的三色雾气配置
 * <p>
 * 从 {@link ClientSetup#getBrightnessDependentFogColor} 中提取的硬编码值，
 * 集中管理在此处，便于维护和扩展。
 * <p>
 * 染梦河流与染梦平原共享相同的着色配置。
 */
public final class PDShadingRegistration {

    /** 染梦平原/河流：粉色系 */
    private static final Vec3 PLAINS_DAY = new Vec3(1.0, 0.71, 0.85);
    private static final Vec3 PLAINS_SUNSET = new Vec3(1.0, 0.56, 0.64);
    private static final Vec3 PLAINS_NIGHT = new Vec3(0.29, 0.10, 0.36);

    /** 染梦森林：绿色系 */
    private static final Vec3 FOREST_DAY = new Vec3(0.66, 0.90, 0.64);
    private static final Vec3 FOREST_SUNSET = new Vec3(0.83, 0.64, 0.45);
    private static final Vec3 FOREST_NIGHT = new Vec3(0.10, 0.23, 0.16);

    /** 染梦冰雪冻原：冰蓝色系 */
    private static final Vec3 FROZEN_DAY = new Vec3(0.71, 0.85, 1.0);
    private static final Vec3 FROZEN_SUNSET = new Vec3(0.64, 0.71, 0.83);
    private static final Vec3 FROZEN_NIGHT = new Vec3(0.10, 0.16, 0.36);

    /** 染梦冰冻海洋：浅蓝系 */
    private static final Vec3 COLD_OCEAN_DAY = new Vec3(0.64, 0.83, 0.90);
    private static final Vec3 COLD_OCEAN_SUNSET = new Vec3(0.83, 0.64, 0.64);
    private static final Vec3 COLD_OCEAN_NIGHT = new Vec3(0.04, 0.16, 0.23);

    /** 染梦深海：紫色系 */
    private static final Vec3 DEEP_OCEAN_DAY = new Vec3(0.76, 0.64, 0.90);
    private static final Vec3 DEEP_OCEAN_SUNSET = new Vec3(0.83, 0.53, 0.74);
    private static final Vec3 DEEP_OCEAN_NIGHT = new Vec3(0.12, 0.04, 0.28);

    /** 染梦蘑菇平原：橙色系 */
    private static final Vec3 MUSHROOM_DAY = new Vec3(1.0, 0.82, 0.64);
    private static final Vec3 MUSHROOM_SUNSET = new Vec3(0.90, 0.64, 0.45);
    private static final Vec3 MUSHROOM_NIGHT = new Vec3(0.28, 0.16, 0.04);

    /** 染梦海岸：蓝紫色系 */
    private static final Vec3 SHORE_DAY = new Vec3(0.71, 0.85, 1.0);
    private static final Vec3 SHORE_SUNSET = new Vec3(0.83, 0.71, 0.83);
    private static final Vec3 SHORE_NIGHT = new Vec3(0.16, 0.23, 0.36);

    /** 染梦密林：深绿色系 */
    private static final Vec3 DENSE_FOREST_DAY = new Vec3(0.56, 0.71, 0.56);
    private static final Vec3 DENSE_FOREST_SUNSET = new Vec3(0.71, 0.56, 0.64);
    private static final Vec3 DENSE_FOREST_NIGHT = new Vec3(0.08, 0.16, 0.10);

    /** 冷域冻原：冰蓝色系 */
    private static final Vec3 COLD_DOMAIN_DAY = new Vec3(0.68, 0.82, 1.0);
    private static final Vec3 COLD_DOMAIN_SUNSET = new Vec3(0.62, 0.68, 0.88);
    private static final Vec3 COLD_DOMAIN_NIGHT = new Vec3(0.06, 0.10, 0.26);

    /** 注册全部群系默认着色 */
    public static void registerDefaults() {
        // 染梦平原
        BiomeShadingAPI.register(new BiomeShadingEntry(
                PDBiomes.DYEDREAM_PLAINS, PLAINS_DAY, PLAINS_SUNSET, PLAINS_NIGHT));

        // 染梦河流（与平原一致）
        BiomeShadingAPI.register(new BiomeShadingEntry(
                PDBiomes.DYEDREAM_RIVER, PLAINS_DAY, PLAINS_SUNSET, PLAINS_NIGHT));

        // 染梦森林
        BiomeShadingAPI.register(new BiomeShadingEntry(
                PDBiomes.DYEDREAM_FOREST, FOREST_DAY, FOREST_SUNSET, FOREST_NIGHT));

        // 染梦冰雪冻原
        BiomeShadingAPI.register(new BiomeShadingEntry(
                PDBiomes.DYEDREAM_FROZEN_TUNDRA, FROZEN_DAY, FROZEN_SUNSET, FROZEN_NIGHT));

        // 染梦冰冻海洋
        BiomeShadingAPI.register(new BiomeShadingEntry(
                PDBiomes.DYEDREAM_COLD_OCEAN, COLD_OCEAN_DAY, COLD_OCEAN_SUNSET, COLD_OCEAN_NIGHT));

        // 染梦深海
        BiomeShadingAPI.register(new BiomeShadingEntry(
                PDBiomes.DYEDREAM_DEEP_OCEAN, DEEP_OCEAN_DAY, DEEP_OCEAN_SUNSET, DEEP_OCEAN_NIGHT));

        // 染梦蘑菇平原
        BiomeShadingAPI.register(new BiomeShadingEntry(
                PDBiomes.DYEDREAM_MUSHROOM_PLAINS, MUSHROOM_DAY, MUSHROOM_SUNSET, MUSHROOM_NIGHT));

        // 染梦海岸
        BiomeShadingAPI.register(new BiomeShadingEntry(
                PDBiomes.DYEDREAM_SHORE, SHORE_DAY, SHORE_SUNSET, SHORE_NIGHT));

        // 染梦密林
        BiomeShadingAPI.register(new BiomeShadingEntry(
                PDBiomes.DYEDREAM_DENSE_FOREST, DENSE_FOREST_DAY, DENSE_FOREST_SUNSET, DENSE_FOREST_NIGHT));

        // 冷域冻原
        BiomeShadingAPI.register(new BiomeShadingEntry(
                PDBiomes.COLD_DOMAIN_TUNDRA, COLD_DOMAIN_DAY, COLD_DOMAIN_SUNSET, COLD_DOMAIN_NIGHT));
    }

    private PDShadingRegistration() {
        throw new UnsupportedOperationException("PDShadingRegistration is a static utility class, not instantiable");
    }
}
