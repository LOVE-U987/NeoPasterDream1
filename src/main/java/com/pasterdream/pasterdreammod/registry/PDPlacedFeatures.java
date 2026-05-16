package com.pasterdream.pasterdreammod.registry;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

/**
 * 已放置特征（Placed Feature）的 ResourceKey 引用注册类
 * <p>
 * 用于在代码中类型安全地引用 JSON 定义的 placed feature，
 * 配合 {@link com.pasterdream.pasterdreammod.worldgen.PDDyedreamBiomeModifier} 使用。
 * <p>
 * 对应的 JSON 定义存放在 data/pasterdream/worldgen/placed_feature/ 目录下，
 * 由数据驱动自动加载，此处仅提供 Java 侧的引用键。
 */
public class PDPlacedFeatures {

    // ==================== 矿石 ====================
    /** 琥珀糖矿 */
    public static final ResourceKey<PlacedFeature> ORE_AMBER_CANDY = createKey("ore_amber_candy");
    /** 染梦尘矿 */
    public static final ResourceKey<PlacedFeature> ORE_DYEDREAMDUST = createKey("ore_dyedreamdust");
    /** 染梦石英矿 */
    public static final ResourceKey<PlacedFeature> ORE_DYEDREAMQUARTZ = createKey("ore_dyedreamquartz");

    // ==================== 植被 ====================
    /** 染梦草地被草 */
    public static final ResourceKey<PlacedFeature> PATCH_DYEDREAM_GRASS = createKey("patch_dyedream_grass");
    /** 染梦芽集合 */
    public static final ResourceKey<PlacedFeature> PATCH_DYEDREAM_BUDS = createKey("patch_dyedream_buds");
    /** 粉色蘑菇 */
    public static final ResourceKey<PlacedFeature> PATCH_PINKAGARIC = createKey("patch_pinkagaric");
    /** 染梦睡莲 */
    public static final ResourceKey<PlacedFeature> PATCH_DYEDREAM_LILY_PAD = createKey("patch_dyedream_lily_pad");
    /** 染梦莲花 */
    public static final ResourceKey<PlacedFeature> PATCH_DYEDREAM_LOTUS = createKey("patch_dyedream_lotus");
    /** 染梦海草 */
    public static final ResourceKey<PlacedFeature> PATCH_DYEDREAM_SEAGRASS = createKey("patch_dyedream_seagrass");

    // ==================== 树木 ====================
    /** 染梦树 */
    public static final ResourceKey<PlacedFeature> DYEDREAM_TREES = createKey("dyedream_trees");

    /**
     * 创建 PlacedFeature 的 ResourceKey
     *
     * @param name 特征名称（对应 JSON 文件名）
     * @return ResourceKey<PlacedFeature>
     */
    private static ResourceKey<PlacedFeature> createKey(String name) {
        return ResourceKey.create(
                Registries.PLACED_FEATURE,
                ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, name)
        );
    }
}