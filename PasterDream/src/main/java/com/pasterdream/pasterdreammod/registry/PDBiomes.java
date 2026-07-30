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
 */
public final class PDBiomes {

    /** 温暖平原 */
    public static final ResourceKey<Biome> BIOME_DYEDREAM_0 = create("biome_dyedream_0");

    /** 炎热森林 */
    public static final ResourceKey<Biome> BIOME_DYEDREAM_1 = create("biome_dyedream_1");

    /** 寒冷冰雪 */
    public static final ResourceKey<Biome> BIOME_DYEDREAM_2 = create("biome_dyedream_2");

    /** 温暖海洋 */
    public static final ResourceKey<Biome> BIOME_DYEDREAM_3 = create("biome_dyedream_3");

    /** 晶莹深海 */
    public static final ResourceKey<Biome> BIOME_DYEDREAM_DEEP_OCEAN = create("biome_dyedream_deep_ocean");

    /** 蘑菇平原 */
    public static final ResourceKey<Biome> BIOME_DYEDREAM_MUSHROOM_PLAINS = create("biome_dyedream_mushroom_plains");

    /** 染梦海岸 */
    public static final ResourceKey<Biome> BIOME_DYEDREAM_SHORE = create("biome_dyedream_shore");

    /** 染梦河流 */
    public static final ResourceKey<Biome> BIOME_DYEDREAM_RIVER = create("biome_dyedream_river");

    /** 染梦密林 */
    public static final ResourceKey<Biome> BIOME_DYEDREAM_DENSE_FOREST = create("biome_dyedream_dense_forest");

    /** 亚伦柯斯竞技场遗迹群系（灯影渗出的 BOSS 传送门废墟） */
    public static final ResourceKey<Biome> BIOME_AARONCOS_ARENA = create("aaroncos_arena_biome");

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
