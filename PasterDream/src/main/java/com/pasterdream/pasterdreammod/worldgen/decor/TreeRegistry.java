package com.pasterdream.pasterdreammod.worldgen.decor;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 染梦树注册器 —— 参照 {@link DecorationRegistry} 模式集中管理树变体的资源键
 * <p>
 * 所有染梦树变体及其对应的 configured_feature / placed_feature ResourceKey
 * 均在此处定义，作为特征引用的单一数据源。
 * 对应的 JSON 定义文件位于 data/pasterdream/worldgen/configured_feature/ 和
 * data/pasterdream/worldgen/placed_feature/ 目录下。
 */
public class TreeRegistry {

    private TreeRegistry() {}

    /**
     * 树变体条目 —— 记录变体名及其在 random_selector 中的权重
     */
    public record TreeVariant(String name, float weight) {
        /**
         * 获取该变体的 configured_feature ResourceKey
         */
        public ResourceKey<ConfiguredFeature<?, ?>> configuredKey() {
            return ResourceKey.create(
                    Registries.CONFIGURED_FEATURE,
                    ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, name)
            );
        }
    }

    // ==================== 树变体定义 ====================

    /** 繁茂染梦树 (fancy trunk + fancy foliage) */
    public static final TreeVariant FANCY = new TreeVariant("dyedream_tree_fancy", 0.25f);
    /** 茂密染梦树 (straight trunk + blob 宽冠) */
    public static final TreeVariant BUSHY = new TreeVariant("dyedream_tree_bushy", 0.25f);
    /** 巨型染梦树 (mega jungle trunk + jungle foliage) */
    public static final TreeVariant GIANT = new TreeVariant("dyedream_tree_giant", 0.25f);
    /** 垂枝染梦树 (straight trunk + cherry 垂叶) */
    public static final TreeVariant WEEPING = new TreeVariant("dyedream_tree_weeping", 0.25f);
    /** 发光染梦树 (fancy trunk + fancy foliage) */
    public static final TreeVariant GLOWING = new TreeVariant("dyedream_tree_glowing", 0.25f);

    /** 所有变体列表（注入 random_selector 用）*/
    public static final List<TreeVariant> VARIANTS = List.of(FANCY, BUSHY, GIANT, WEEPING, GLOWING);

    // ==================== 默认树 & 选择器 ====================

    /** 默认染梦树（兜底变体）configured_feature 键 */
    public static final ResourceKey<ConfiguredFeature<?, ?>> DEFAULT_TREE = ResourceKey.create(
            Registries.CONFIGURED_FEATURE,
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "dyedream_tree")
    );

    /** random_selector configured_feature 键 */
    public static final ResourceKey<ConfiguredFeature<?, ?>> TREE_SELECTOR = ResourceKey.create(
            Registries.CONFIGURED_FEATURE,
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "dyedream_tree_selector")
    );

    // ==================== Placed Feature ====================

    /** 主入口 placed_feature 键（对应 dyedream_trees.json）*/
    public static final ResourceKey<PlacedFeature> DYEDREAM_TREES = ResourceKey.create(
            Registries.PLACED_FEATURE,
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "dyedream_trees")
    );

}

