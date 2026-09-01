package com.pasterdream.pasterdreammod.registry;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;

/**
 * 生物群系 ResourceKey 常量类 —— 染梦维度全部群系的统一 Key 定义
 * <p>
 * 群系本体由数据包 JSON（data/pasterdream/worldgen/biome/）定义，
 * 此处仅提供代码侧引用的 {@link ResourceKey} 常量，供环境粒子、雾色渲染、
 * 群系音乐、世界生成等系统统一使用，避免在多个类中重复定义。
 * <p>
 * <b>命名规范</b>：采用 {@code {dimension}_{biome_type}} 格式，
 * 旧名称（如 {@code biome_dyedream_0}）已标记为废弃，将在 0.9.10 版本移除。
 */
public final class PDBiomes {

    // ==================== 新名称 (Primary) ====================

    /** 染梦平原 */
    public static final ResourceKey<Biome> DYEDREAM_PLAINS = create("dyedream_plains");

    /** 染梦森林 */
    public static final ResourceKey<Biome> DYEDREAM_FOREST = create("dyedream_forest");

    /** 染梦冰雪冻原 */
    public static final ResourceKey<Biome> DYEDREAM_FROZEN_TUNDRA = create("dyedream_frozen_tundra");

    /** 染梦冰冻海洋 */
    public static final ResourceKey<Biome> DYEDREAM_COLD_OCEAN = create("dyedream_cold_ocean");

    /** 染梦深海 */
    public static final ResourceKey<Biome> DYEDREAM_DEEP_OCEAN = create("dyedream_deep_ocean");

    /** 染梦蘑菇平原 */
    public static final ResourceKey<Biome> DYEDREAM_MUSHROOM_PLAINS = create("dyedream_mushroom_plains");

    /** 染梦海岸 */
    public static final ResourceKey<Biome> DYEDREAM_SHORE = create("dyedream_shore");

    /** 染梦河流 */
    public static final ResourceKey<Biome> DYEDREAM_RIVER = create("dyedream_river");

    /** 染梦密林 */
    public static final ResourceKey<Biome> DYEDREAM_DENSE_FOREST = create("dyedream_dense_forest");

    /** 暗影荒原 */
    public static final ResourceKey<Biome> SHADOW_WASTES = create("shadow_wastes");

    /** 暗影森林 */
    public static final ResourceKey<Biome> SHADOW_FOREST = create("shadow_forest");

    /** 暗影荒漠 */
    public static final ResourceKey<Biome> SHADOW_BARRENS = create("shadow_barrens");

    /** 风之旅途浮岛 */
    public static final ResourceKey<Biome> WIND_JOURNEY_ISLANDS = create("wind_journey_islands");

    /** 风之旅途沙漠 */
    public static final ResourceKey<Biome> WIND_JOURNEY_DEERT = create("wind_journey_desert");

    /** 冷域冻原 */
    public static final ResourceKey<Biome> COLD_DOMAIN_TUNDRA = create("cold_domain_tundra");

    /** 亚伦柯斯竞技场 */
    public static final ResourceKey<Biome> AARONCOS_ARENA = create("aaroncos_arena");

    /** 亚伦柯斯竞技场虚空 */
    public static final ResourceKey<Biome> AARONCOS_ARENA_VOID = create("aaroncos_arena_void");

    // ==================== 旧名称 (Deprecated Aliases) ====================
    // 将在 0.11.0 版本移除

    /** @deprecated 使用 {@link #DYEDREAM_PLAINS} 代替 */
    @Deprecated(since = "0.11.0", forRemoval = true)
    public static final ResourceKey<Biome> BIOME_DYEDREAM_0 = create("biome_dyedream_0");

    /** @deprecated 使用 {@link #DYEDREAM_FOREST} 代替 */
    @Deprecated(since = "0.11.0", forRemoval = true)
    public static final ResourceKey<Biome> BIOME_DYEDREAM_1 = create("biome_dyedream_1");

    /** @deprecated 使用 {@link #DYEDREAM_FROZEN_TUNDRA} 代替 */
    @Deprecated(since = "0.11.0", forRemoval = true)
    public static final ResourceKey<Biome> BIOME_DYEDREAM_2 = create("biome_dyedream_2");

    /** @deprecated 使用 {@link #DYEDREAM_COLD_OCEAN} 代替 */
    @Deprecated(since = "0.11.0", forRemoval = true)
    public static final ResourceKey<Biome> BIOME_DYEDREAM_3 = create("biome_dyedream_3");

    /** @deprecated 使用 {@link #DYEDREAM_DEEP_OCEAN} 代替 */
    @Deprecated(since = "0.11.0", forRemoval = true)
    public static final ResourceKey<Biome> BIOME_DYEDREAM_DEEP_OCEAN = create("biome_dyedream_deep_ocean");

    /** @deprecated 使用 {@link #DYEDREAM_MUSHROOM_PLAINS} 代替 */
    @Deprecated(since = "0.11.0", forRemoval = true)
    public static final ResourceKey<Biome> BIOME_DYEDREAM_MUSHROOM_PLAINS = create("biome_dyedream_mushroom_plains");

    /** @deprecated 使用 {@link #DYEDREAM_SHORE} 代替 */
    @Deprecated(since = "0.11.0", forRemoval = true)
    public static final ResourceKey<Biome> BIOME_DYEDREAM_SHORE = create("biome_dyedream_shore");

    /** @deprecated 使用 {@link #DYEDREAM_RIVER} 代替 */
    @Deprecated(since = "0.11.0", forRemoval = true)
    public static final ResourceKey<Biome> BIOME_DYEDREAM_RIVER = create("biome_dyedream_river");

    /** @deprecated 使用 {@link #DYEDREAM_DENSE_FOREST} 代替 */
    @Deprecated(since = "0.11.0", forRemoval = true)
    public static final ResourceKey<Biome> BIOME_DYEDREAM_DENSE_FOREST = create("biome_dyedream_dense_forest");

    /** @deprecated 使用 {@link #AARONCOS_ARENA} 代替 */
    @Deprecated(since = "0.11.0", forRemoval = true)
    public static final ResourceKey<Biome> BIOME_AARONCOS_ARENA = create("aaroncos_arena_biome");

    /** @deprecated 使用 {@link #AARONCOS_ARENA_VOID} 代替 */
    @Deprecated(since = "0.11.0", forRemoval = true)
    public static final ResourceKey<Biome> BIOME_AARONCOS_ARENA_DIM = create("aaroncos_arena_dim_biome");

    /** @deprecated 使用 {@link #COLD_DOMAIN_TUNDRA} 代替 */
    @Deprecated(since = "0.11.0", forRemoval = true)
    public static final ResourceKey<Biome> BIOME_COLD_DOMAIN = create("cold_domain_biome");

    /**
     * 创建模组命名空间下的群系 ResourceKey
     *
     * @param name 群系注册名（不含命名空间）
     * @return 群系 ResourceKey
     */
    private static ResourceKey<Biome> create(String name) {
        return ResourceKey.create(Registries.BIOME,
                ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, name));
    }

    /** 常量持有类，禁止实例化 */
    private PDBiomes() {
    }
}
